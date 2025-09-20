package core.integrationcontrollers.servicegateway.sg.mapping;

/**
 * Mapea la respuesta de
 * @author ypalomeq
 * @version 1.0
 * ypalomeq@bancolombia.com.co
 * */
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbNamespaceBindings;
import com.ibm.broker.plugin.MbXPathVariables;

import core.integrationcontrollers.servicegateway.sg.dto.Authorization;
import core.integrationcontrollers.servicegateway.sg.dto.Certificate;
import core.integrationcontrollers.servicegateway.sg.dto.ConsumerIdentifier;
import core.integrationcontrollers.servicegateway.sg.dto.InternalComponentEndpoint;
import core.integrationcontrollers.servicegateway.sg.dto.InternalRoute;
import core.integrationcontrollers.servicegateway.sg.dto.Policy;
import core.integrationcontrollers.servicegateway.sg.dto.SLA;
import core.integrationcontrollers.servicegateway.sg.dto.SLD;
import core.integrationcontrollers.servicegateway.sg.dto.ServiceVersion;
import core.integrationcontrollers.servicegateway.sg.dto.TypeImplementation;

public class ServiceGatewayMapping {
	private static final String URI_ROOT = "bsrURIRoot";
	static final String GPX63_IDENTIFIERS = "gpx63_identifiers";
	static final String GEP63_AGREEDENDPOINTS = "gep63_agreedEndpoints";
	static final String GEP63_CONSUMES = "gep63_consumes";
	static final String GEP63_SERVICENAMESPACE = "gep63_serviceNamespace";
	static final String GEP63_SOURCEDATADOMAIN = "gep63_sourceDataDomain";
	static final String GEP63_SOURCESYSTEM = "gep63_sourceSystem";
	static final String GPX63_CERTIFICATES = "gpx63_certificates";
	static final String GPX63_CONSUMERAUTHORIZATIONS = "gpx63_consumerAuthorizations";
	static final String BCPOL_TYPE = "bcpol_type";
	static final String BCPOL_VALUE = "bcpol_value";

	// Certificates
	static final String BCPOL_ISSUER = "bcpol_issuer";
	static final String BCPOL_SUBJECT = "bcpol_subject";
	static final String BCPOL_SERIALNUMBER = "bcpol_serialNumber";
	static final String BCPOL_EXPIRYDATE = "bcpol_expiryDate";
	static final String BCPOL_PUBLICKEY = "bcpol_publicKey";
	static final String BCPOL_PUBLICKEYALGORITHM = "bcpol_publicKeyAlgorithm";
	static final String BCPOL_URL = "bcpol_url";
	static final String BCPOL_CERTIFICATETYPE = "bcpol_certificateType";

	// Authorization
	static final String BCPOL_PRIORITY = "bcpol_priority";
	static final String BCPOL_AVERAGEMESSAGESPERSECOND = "bcpol_averageMessagesPerSecond";
	static final String BCPOL_AVERAGERESPONSETIMEMILLIS = "bcpol_averageResponseTimeMillis";
	static final String BCPOL_ACTIVE = "bcpol_active";
	static final String BCPOL_OPERATION = "bcpol_operation";
	static final String BCPOL_IDOPERATION = "bcpol_idOperation";

	// ServiceVersion
	static final String GEP63_SERVICEIDENTIFIER = "gep63_serviceIdentifier";

	// SLD
	static final String GEP63_IMPLEMENTATIONS = "gep63_implementations";

	// SecurityPolicy
	static final String GEP63_SECURITYPOLICY = "gep63_securityPolicy";

	// Internal Route
	static final String BCGOB_INTERNALENDPOINTS = "bcgob_internalEndpoints";
	static final String BCSEP_OPERATIONID = "bcsep_operationId";
	static final String BCSEP_CONSUMERID = "bcsep_consumerId";
	static final String BCSEP_SOURCECOMPONENT = "bcsep_sourceComponent";
	static final String BCSEP_DESTINATION = "bcsep_destination";
	static final String BCFRW_ENDPOINTS = "bcfrw_endpoints";

	// Internal Component Endpoint
	static final String BCSEP_URL = "bcsep_URL";
	static final String SM63_REQUESTQNAME = "sm63_requestQName";
	static final String SM63_REQUESTQMGR = "sm63_requestQMgr";
	static final String SM63_RESPONSEQNAME = "sm63_responseQName";
	static final String SM63_RESPONSEQMGR = "sm63_responseQMgr";

	// Namespace y prefijos
	static final String P244 = "p244";
	static final String SDO = "sdo";
	static final String SDO_1 = "sdo_1";
	static final String NSSDO_1 = "http://www.ibm.com/xmlns/prod/serviceregistry/6/0/sdo";
	static final String NSP244 = "http://www.ibm.com/xmlns/prod/serviceregistry/6/0/ws/sdo";
	static final String PREFENVIRONMENT = "http://www.ibm.com/xmlns/prod/serviceregistry/lifecycle/v6r3/LifecycleDefinition";
	static final String PREFMQVERSION = "http://www.grupobancolombia.com/datapower/mqversion";
	static final String NSCOMMON = "commonj.sdo";
	static final String COMMONPATH = "/soapenv:Envelope/soapenv:Body/p244:executeNamedQueryWithParametersResponse";
	// Constantes
	static final String HTTPENDPOINT = "HttpEndpoint";

	private MbMessageAssembly inAssembly;
	private MbNamespaceBindings mbNamespaceBindings;
	private MbXPathVariables variablesXPATH = null;
	private Map<String, String> variables = null;

	public ServiceGatewayMapping(MbMessageAssembly inAssembly) {
		this.inAssembly = inAssembly;
	}

	/**
	 * Realiza el mapeo y convierte la respuesta del registry a un objeto java.
	 * 
	 * @author ypalomeq
	 * @version 1.0
	 * @return retorma un MbMessage ypalomeq@bancolombia.com.co
	 * @throws ParseException
	 * @throws MappingException 
	 * */
	public MbMessage mappingMessages(ServiceVersion serviceVersion) throws MbException, MappingException {
		MbElement requestOrigin = inAssembly.getGlobalEnvironment().getRootElement().getFirstElementByPath("RequestOrigin").getLastChild().getLastChild();
		String namespaceIn = requestOrigin.getFirstElementByPath("Header/requestData/destination/namespace").getValueAsString();

		SLD sldNew = new SLD();

		// se va al elemnto results del mensaje
		MbElement bodyResp = inAssembly.getMessage().getRootElement().getLastChild().getLastChild().getFirstChild();

		SLA slaNew = null;
		List<SLA> listSLA = new ArrayList<>();
		int d = countDatagraph();
		if (d == 0) throw new MappingException("No se encontro una version del servicio a la que este autorizado en Registry.");
		for (int in = 1; in <= d; in++) {
			slaNew = new SLA();
			String pattGetRoot = "string(results/sdo:datagraph[" + in + "]/sdo_1:WSRR/sdo_1:root)";

			String bsrURIRoot = buildFieldNamespace(pattGetRoot, null, null, bodyResp, null);

			String datagraphStr = "results/sdo:datagraph[" + in + "]/sdo_1:WSRR";
			@SuppressWarnings("unchecked")
			List<MbElement> listWSRR = (List<MbElement>) bodyResp.evaluateXPath(datagraphStr, mbNamespaceBindings);
			MbElement dataGraph = listWSRR.get(0);

			String applicationId = requestOrigin.getFirstElementByPath("Header/systemId").getValueAsString();

			slaNew.setApplicationId(applicationId);
			String xpathIdentifier = "string(results/sdo:datagraph[" + in
					+ "]/sdo_1:WSRR/sdo_1:artefacts[@bsrURI=$bsrURIRoot]/sdo_1:userDefinedRelationships[@name=$identifiers]/@targets)";
			variablesXPATH = new MbXPathVariables();
			variablesXPATH.assign("identifiers", GPX63_IDENTIFIERS);
			variablesXPATH.assign(URI_ROOT, bsrURIRoot);
			String sourceIdentifier = buildFieldNamespace(xpathIdentifier, null, null, bodyResp, variablesXPATH);

			slaNew.setConsumerIdentifier(getSourceIdentifier(sourceIdentifier, dataGraph));

			String xpathEndpoints = "string(results/sdo:datagraph[" + in
					+ "]/sdo_1:WSRR/sdo_1:artefacts[@bsrURI=$bsrURIRoot]/sdo_1:userDefinedRelationships[@name=$agreedEndpoints]/@targets)";
			variablesXPATH = new MbXPathVariables();
			variablesXPATH.assign(URI_ROOT, bsrURIRoot);
			variablesXPATH.assign("agreedEndpoints", GEP63_AGREEDENDPOINTS);

			String[] agreedEndpoints = buildFieldNamespace(xpathEndpoints, null, null, bodyResp, variablesXPATH).split(" ");

			for (String agreed : agreedEndpoints) {

				String xpathSldName = "string(results/sdo:datagraph[" + in + "]/sdo_1:WSRR/sdo_1:artefacts[@bsrURI=$agreed]/@name)";
				variables = new LinkedHashMap<>();
				variables.put("agreed", agreed);

				String sldName = buildFieldNamespace(xpathSldName, null, null, bodyResp, buildVariables(variables));

				String xpathNamespace = "string(results/sdo:datagraph[" + in
						+ "]/sdo_1:WSRR/sdo_1:artefacts[@bsrURI=$agreed]/sdo_1:userDefinedProperties[@name=$gep63_serviceNamespace]/@value)";
				variables = new LinkedHashMap<>();
				variables.put("agreed", agreed);
				variables.put("gep63_serviceNamespace", GEP63_SERVICENAMESPACE);

				String nameSpace = buildFieldNamespace(xpathNamespace, null, null, bodyResp, buildVariables(variables));

				if (nameSpace.replace("Enlace/", "").equals(namespaceIn)) {

					sldNew.setName(sldName);
					sldNew.setNamespace(namespaceIn);

					getDatosSLA(bsrURIRoot, dataGraph, slaNew);

					String xpathCertificate = "string(results/sdo:datagraph[" + in
							+ "]/sdo_1:WSRR/sdo_1:artefacts[@bsrURI=$bsrURIRoot]/sdo_1:userDefinedRelationships[@name=$gpx63_certificates]/@targets)";
					variablesXPATH = new MbXPathVariables();
					variablesXPATH.assign(URI_ROOT, bsrURIRoot);
					variablesXPATH.assign("gpx63_certificates", GPX63_CERTIFICATES);

					String certificate = buildFieldNamespace(xpathCertificate, null, null, bodyResp, variablesXPATH);
					slaNew.setCertificate(getCertificates(certificate, dataGraph));

					String xpathConsumerAuthorizations = "string(results/sdo:datagraph[" + in
							+ "]/sdo_1:WSRR/sdo_1:artefacts[@bsrURI=$bsrURIRoot]/sdo_1:userDefinedRelationships[@name=$gpx63_consumerAuthorizations]/@targets)";
					variablesXPATH = new MbXPathVariables();
					variablesXPATH.assign(URI_ROOT, bsrURIRoot);
					variablesXPATH.assign("gpx63_consumerAuthorizations", GPX63_CONSUMERAUTHORIZATIONS);

					String consumerAuthorization = buildFieldNamespace(xpathConsumerAuthorizations, null, null, bodyResp, variablesXPATH);

					slaNew.setAuthorization(getConsumerAuthorization(consumerAuthorization, sourceIdentifier, dataGraph));

					getServiceVersion(agreed, nameSpace, dataGraph, serviceVersion);

					String xpathImplementation = "string(results/sdo:datagraph[" + in
							+ "]/sdo_1:WSRR/sdo_1:artefacts[@bsrURI=$agreed]/sdo_1:userDefinedRelationships[@name=$gep63_implementations]/@targets)";
					variablesXPATH = new MbXPathVariables();
					variablesXPATH.assign("agreed", agreed);
					variablesXPATH.assign("gep63_implementations", GEP63_IMPLEMENTATIONS);

					String implementation = buildFieldNamespace(xpathImplementation, null, null, bodyResp, variablesXPATH);

					if (implementation != null && !implementation.isEmpty()) {
						String[] implementations = implementation.split(" ");
						List<TypeImplementation> listImplementation = new ArrayList<>();

						for (String impl : implementations) {
							String xpathInternalEndpoints = "string(//results/sdo:datagraph[" + in
									+ "]/sdo_1:WSRR/sdo_1:artefacts[@bsrURI=$impl]/sdo_1:userDefinedRelationships[@name=$bcgob_internalEndpoints]/@targets)";
							variablesXPATH = new MbXPathVariables();
							variablesXPATH.assign("impl", impl);
							variablesXPATH.assign("bcgob_internalEndpoints", BCGOB_INTERNALENDPOINTS);

							String internalEndpoints = buildFieldNamespace(xpathInternalEndpoints, null, null, bodyResp, variablesXPATH);

							if (internalEndpoints != null && !internalEndpoints.isEmpty()) {
								String[] internalEndpoint = internalEndpoints.split(" ");
								List<InternalRoute> listInternalRoute = new ArrayList<>();

								for (String internalEnd : internalEndpoint) {

									InternalComponentEndpoint internalComponentEndpoint = new InternalComponentEndpoint();
									InternalComponentEndpoint sourceComponentEndpoint = new InternalComponentEndpoint();
									InternalRoute internalRoute = new InternalRoute();

									getInternalRoute(internalRoute, internalEnd, dataGraph);

									variables = new LinkedHashMap<>();
									variables.put("internalEnd", internalEnd);
									variables.put("bcsep_destination", BCSEP_DESTINATION);
									variables.put("bcsep_sourceComponent", BCSEP_SOURCECOMPONENT);
									variables.put("bcfrw_endpoints", BCFRW_ENDPOINTS);

									String xpathDestination = "string(sdo_1:artefacts[@bsrURI=$internalEnd]/sdo_1:userDefinedRelationships[@name=$bcsep_destination]/@targets)";

									String targeBcfrw = getInternalComponentEndpoint(variables, xpathDestination, internalComponentEndpoint, dataGraph);

									String xpathSourceComponent = "string(sdo_1:artefacts[@bsrURI=$internalEnd]/sdo_1:userDefinedRelationships[@name=$bcsep_sourceComponent]/@targets)";

									String targetSource = getInternalComponentEndpoint(variables, xpathSourceComponent, sourceComponentEndpoint, dataGraph);

									setComponentData(targeBcfrw, internalComponentEndpoint, bodyResp, in, dataGraph);
									setComponentData(targetSource, sourceComponentEndpoint, bodyResp, in, dataGraph);

									internalRoute.setInternalComponentEndpoint(internalComponentEndpoint);
									internalRoute.setSourceComponent(sourceComponentEndpoint);
									listInternalRoute.add(internalRoute);
								}
								sldNew.setInternalRoutes(listInternalRoute);
							}

							TypeImplementation typeImplementation = new TypeImplementation();
							String xpathPrimaryType = "string(results/sdo:datagraph[" + in + "]/sdo_1:WSRR/sdo_1:artefacts[@bsrURI=$impl]/@primaryType)";
							variablesXPATH = new MbXPathVariables();
							variablesXPATH.assign("impl", impl);

							String primaryType = buildFieldNamespace(xpathPrimaryType, null, null, bodyResp, variablesXPATH);
							int inIndex = primaryType.indexOf("#");
							typeImplementation.setImplementation(primaryType.substring(inIndex + 1, primaryType.length()));

							listImplementation.add(typeImplementation);
						}
						sldNew.setImplementation(listImplementation);
					}

					getSecurityPolicies(agreed, dataGraph, sldNew);

				}
			}

			slaNew.setSld(sldNew);
			listSLA.add(slaNew);
			sldNew.setSla(listSLA);

			serviceVersion.getSld().add(sldNew);
		}

		return inAssembly.getMessage();

	}

	/**
	 * setea los valores de colas y qmgr que tiene un componente origen o
	 * destino
	 * 
	 * @author dviana
	 * @version 1.0
	 * @return dviana@bancolombia.com.co
	 * @throws MbException
	 * */

	private void setComponentData(String targeBcfrw, InternalComponentEndpoint componentEndpoint, MbElement bodyResp, int in, MbElement dataGraph)
			throws MbException {

		String valuTargeBcfrw = targeBcfrw.split(" ")[0];
		if (componentEndpoint.getEndPointType().equals(HTTPENDPOINT)) {

			String xpathHost = "string(results/sdo:datagraph[" + in
					+ "]/sdo_1:WSRR/sdo_1:artefacts[@bsrURI=$targeBcfrw]/sdo_1:userDefinedProperties[@name=$bcsep_URL]/@value)";
			variablesXPATH = new MbXPathVariables();
			variablesXPATH.assign("targeBcfrw", valuTargeBcfrw);
			variablesXPATH.assign("bcsep_URL", BCSEP_URL);

			componentEndpoint.setHost(buildFieldNamespace(xpathHost, null, null, bodyResp, variablesXPATH));

		} else {

			String xpathRequestQName = "string(results/sdo:datagraph[" + in
					+ "]/sdo_1:WSRR/sdo_1:artefacts[@bsrURI=$targeBcfrw]/sdo_1:userDefinedProperties[@name=$sm63_requestQName]/@value)";
			variablesXPATH = new MbXPathVariables();
			variablesXPATH.assign("sm63_requestQName", SM63_REQUESTQNAME);
			variablesXPATH.assign("targeBcfrw", valuTargeBcfrw);

			componentEndpoint.setRequestQName(buildFieldNamespace(xpathRequestQName, null, null, bodyResp, variablesXPATH));

			String xpathRequestQMgr = "string(results/sdo:datagraph[" + in
					+ "]/sdo_1:WSRR/sdo_1:artefacts[@bsrURI=$targeBcfrw]/sdo_1:userDefinedProperties[@name=$sm63_requestQMgr]/@value)";
			variablesXPATH = new MbXPathVariables();
			variablesXPATH.assign("targeBcfrw", valuTargeBcfrw);
			variablesXPATH.assign("sm63_requestQMgr", SM63_REQUESTQMGR);

			componentEndpoint.setRequestQMgr(buildFieldNamespace(xpathRequestQMgr, null, null, bodyResp, variablesXPATH));

			String xpathResponseQName = "string(results/sdo:datagraph[" + in
					+ "]/sdo_1:WSRR/sdo_1:artefacts[@bsrURI=$targeBcfrw]/sdo_1:userDefinedProperties[@name=$sm63_responseQName]/@value)";
			variablesXPATH.assign("targeBcfrw", valuTargeBcfrw);
			variablesXPATH.assign("sm63_responseQName", SM63_RESPONSEQNAME);

			componentEndpoint.setResponseQName(buildFieldNamespace(xpathResponseQName, null, null, bodyResp, variablesXPATH));

			String xpathResponseQMgr = "string(results/sdo:datagraph[" + in
					+ "]/sdo_1:WSRR/sdo_1:artefacts[@bsrURI=$targeBcfrw]/sdo_1:userDefinedProperties[@name=$sm63_responseQMgr]/@value)";
			variablesXPATH = new MbXPathVariables();
			variablesXPATH.assign("targeBcfrw", valuTargeBcfrw);
			variablesXPATH.assign("sm63_responseQMgr", SM63_RESPONSEQMGR);

			componentEndpoint.setResponseQMgr(buildFieldNamespace(xpathResponseQMgr, null, null, bodyResp, variablesXPATH));

			String xpathMQVersion = "string(results/sdo:datagraph[" + in
					+ "]/sdo_1:WSRR/sdo_1:artefacts[@bsrURI=$targeBcfrw]/sdo_1:classificationURIs[contains(text(), $prefMQVersion)])";
			variablesXPATH = new MbXPathVariables();
			variablesXPATH.assign("targeBcfrw", valuTargeBcfrw);
			variablesXPATH.assign("prefMQVersion", PREFMQVERSION);

			String mqVersion = buildFieldNamespace(xpathMQVersion, null, null, dataGraph, variablesXPATH);
			int indexMQ = mqVersion.indexOf("#");
			componentEndpoint.setMqVersion(mqVersion.substring(indexMQ + 1, mqVersion.length()));

		}
	}

	/**
	 * obtiene todos los consumidores y los guarda en una lista
	 * 
	 * @author ypalomeq
	 * @version 1.0
	 * @return retorna una lista con todos los consumidores que tiene un SLA
	 *         ypalomeq@bancolombia.com.co
	 * @throws MbException
	 * */
	private List<ConsumerIdentifier> getSourceIdentifier(String sourceIdentifier, MbElement dataGraph) throws MbException {
		List<ConsumerIdentifier> listConsumerIdentifier = null;
		if (sourceIdentifier != null && !sourceIdentifier.isEmpty()) {
			String[] sourceIdentifiers = sourceIdentifier.split(" ");

			for (String identifier : sourceIdentifiers) {

				ConsumerIdentifier consumerIdentifier = new ConsumerIdentifier();
				listConsumerIdentifier = new ArrayList<>();

				String xpathsIdentifiersType = "string(sdo_1:artefacts[@bsrURI=$identifier]/sdo_1:userDefinedProperties[@name=$bcpol_type]/@value)";

				variables = new LinkedHashMap<>();

				variables.put("identifier", identifier);
				variables.put("bcpol_type", BCPOL_TYPE);
				variables.put("bcpol_value", BCPOL_VALUE);
				variablesXPATH = buildVariables(variables);
				consumerIdentifier.setType(buildFieldNamespace(xpathsIdentifiersType, null, null, dataGraph, variablesXPATH));

				String xpathsIdentifiersValue = "string(sdo_1:artefacts[@bsrURI=$identifier]/sdo_1:userDefinedProperties[@name=$bcpol_value]/@value)";

				consumerIdentifier.setValue(buildFieldNamespace(xpathsIdentifiersValue, null, null, dataGraph, buildVariables(variables)));

				listConsumerIdentifier.add(consumerIdentifier);
			}
		}
		return listConsumerIdentifier;
	}

	/**
	 * obtiene todos la informacion de cada uno de los certificados
	 * 
	 * @author ypalomeq
	 * @version 1.0
	 * @return retorna una lista de los certificacidos
	 *         ypalomeq@bancolombia.com.co
	 * @throws MbException
	 * @throws MappingException
	 * */
	private List<Certificate> getCertificates(String certificate, MbElement dataGraph) throws MbException, MappingException {

		List<Certificate> listCertificate = null;
		MbXPathVariables xpathVariables = null;
		if (certificate != null && !certificate.isEmpty()) {
			String[] certificates = certificate.split(" ");
			for (String cert : certificates) {

				variables = new LinkedHashMap<>();

				listCertificate = new ArrayList<>();

				Certificate certificateNew = new Certificate();

				String xpathIssuer = "string(sdo_1:artefacts[@bsrURI=$cert]/sdo_1:userDefinedProperties[@name=$bcpol_issuer]/@value)";
				variables.put("cert", cert);
				variables.put("bcpol_issuer", BCPOL_ISSUER);
				variables.put("bcpol_subject", BCPOL_SUBJECT);
				variables.put("bcpol_serialNumber", BCPOL_SERIALNUMBER);
				variables.put("bcpol_expiryDate", BCPOL_EXPIRYDATE);
				variables.put("bcpol_publicKey", BCPOL_PUBLICKEY);
				variables.put("bcpol_publicKeyAlgorithm", BCPOL_PUBLICKEYALGORITHM);
				variables.put("bcpol_url", BCPOL_URL);
				variables.put("bcpol_certificateType", BCPOL_CERTIFICATETYPE);

				if (xpathVariables == null) {
					xpathVariables = buildVariables(variables);
				}
				certificateNew.setIssuer(buildFieldNamespace(xpathIssuer, null, null, dataGraph, xpathVariables));

				String xpathSubject = "string(sdo_1:artefacts[@bsrURI=$cert]/sdo_1:userDefinedProperties[@name=$bcpol_subject]/@value)";

				certificateNew.setSubject(buildFieldNamespace(xpathSubject, null, null, dataGraph, xpathVariables));

				String xpathSerialNumber = "string(sdo_1:artefacts[@bsrURI=$cert]/sdo_1:userDefinedProperties[@name=$bcpol_serialNumber]/@value)";
				certificateNew.setSerialNumber(buildFieldNamespace(xpathSerialNumber, null, null, dataGraph, buildVariables(variables)));

				String xpathExpiryDate = "string(sdo_1:artefacts[@bsrURI=$cert]/sdo_1:userDefinedProperties[@name=$bcpol_expiryDate]/@value)";
				String dateExpiry = buildFieldNamespace(xpathExpiryDate, null, null, dataGraph, xpathVariables);

				if (!dateExpiry.isEmpty()) {
					DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					Date date;
					try {
						date = format.parse(dateExpiry);
					} catch (ParseException e) {
						MappingException ex = new MappingException("Certificado con fecha de expiracion invalida.", e);
						ex.setExceptionId("2200");
						throw ex;
					}
					certificateNew.setExpiryDate(date);
				}

				String xpathPublicKey = "string(sdo_1:artefacts[@bsrURI=$cert]/sdo_1:userDefinedProperties[@name=$bcpol_publicKey]/@value)";
				certificateNew.setPublicKey(buildFieldNamespace(xpathPublicKey, null, null, dataGraph, xpathVariables));

				String xpathPublicKeyAlgorithm = "string(sdo_1:artefacts[@bsrURI=$cert]/sdo_1:userDefinedProperties[@name=$bcpol_publicKeyAlgorithm]/@value)";
				certificateNew.setPublicKeyAlgorithm(buildFieldNamespace(xpathPublicKeyAlgorithm, null, null, dataGraph, xpathVariables));

				String xpathUrl = "string(sdo_1:artefacts[@bsrURI=$cert]/sdo_1:userDefinedProperties[@name=$bcpol_url]/@value)";
				certificateNew.setUrl(buildFieldNamespace(xpathUrl, null, null, dataGraph, xpathVariables));

				String xpathCertificateType = "string(sdo_1:artefacts[@bsrURI=$cert]/sdo_1:userDefinedProperties[@name=$bcpol_certificateType]/@value)";
				certificateNew.setCertificateType(buildFieldNamespace(xpathCertificateType, null, null, dataGraph, xpathVariables));

				listCertificate.add(certificateNew);

			}
		}
		return listCertificate;
	}

	/**
	 * obtiene todos las autorizaciones que tiene un consumidor
	 * 
	 * @author ypalomeq
	 * @version 1.0
	 * @return retorna lista de autorizaciones de consumidores.
	 *         ypalomeq@bancolombia.com.co
	 * @throws MbException
	 * */
	private List<Authorization> getConsumerAuthorization(String consumerAuthorization, String sourceIdentifier, MbElement dataGraph) throws MbException {

		List<Authorization> listAuthorization = null;
		MbXPathVariables xpathVariables = null;

		if (consumerAuthorization != null && !consumerAuthorization.isEmpty()) {
			String[] consumerAuthorizations = consumerAuthorization.split(" ");
			listAuthorization = new ArrayList<>();

			variables = new LinkedHashMap<>();

			for (String auth : consumerAuthorizations) {

				variables.put("auth", auth);
				variables.put("bcpol_priority", BCPOL_PRIORITY);
				variables.put("bcpol_averageMessagesPerSecond", BCPOL_AVERAGEMESSAGESPERSECOND);
				variables.put("bcpol_averageResponseTimeMillis", BCPOL_AVERAGERESPONSETIMEMILLIS);
				variables.put("bcpol_active", BCPOL_ACTIVE);
				variables.put("bcpol_operation", BCPOL_OPERATION);
				variables.put("bcpol_idOperation", BCPOL_IDOPERATION);

				if (xpathVariables == null) {
					xpathVariables = buildVariables(variables);
				}

				Authorization authorization = new Authorization();

				String xpathPriority = "string(sdo_1:artefacts[@bsrURI=$auth]/sdo_1:userDefinedProperties[@name=$bcpol_priority]/@value)";

				int priority = 0;
				String bcpolPriority = buildFieldNamespace(xpathPriority, null, null, dataGraph, xpathVariables);
				if (bcpolPriority.matches("[0-9]+")) {
					priority = Integer.parseInt(bcpolPriority);
				}
				authorization.setPriority(priority);

				String xpathPerSecond = "string(sdo_1:artefacts[@bsrURI=$auth]/sdo_1:userDefinedProperties[@name=$bcpol_averageMessagesPerSecond]/@value)";
				double averageMessagesPerSecond = 0.0;

				String averagePerSecond = buildFieldNamespace(xpathPerSecond, null, null, dataGraph, xpathVariables);

				if (averagePerSecond.matches("^(\\d{1}\\.)?(\\d+\\.?)+(,\\d{2})?$")) {
					averageMessagesPerSecond = Double.parseDouble(averagePerSecond);
				}

				authorization.setAverageMessagesPerSecond(averageMessagesPerSecond);

				String xpathTimeMillis = "string(sdo_1:artefacts[@bsrURI=$auth]/sdo_1:userDefinedProperties[@name=$bcpol_averageResponseTimeMillis]/@value)";

				String bcpolAverageResponseTimeMillis = buildFieldNamespace(xpathTimeMillis, null, null, dataGraph, xpathVariables);

				double responseTimeMillis = 0.0;

				if (bcpolAverageResponseTimeMillis.matches("^(\\d{1}\\.)?(\\d+\\.?)+(,\\d{2})?$")) {
					responseTimeMillis = Double.parseDouble(bcpolAverageResponseTimeMillis);
				}

				authorization.setAverageResponseTimeMillis(responseTimeMillis);

				String xpathActive = "string(sdo_1:artefacts[@bsrURI=$auth]/sdo_1:userDefinedProperties[@name=$bcpol_active]/@value)";

				boolean bcpolActive = new Boolean(buildFieldNamespace(xpathActive, null, null, dataGraph, xpathVariables));
				authorization.setActive(bcpolActive);

				String xpathOperation = "string(sdo_1:artefacts[@bsrURI=$auth]/sdo_1:userDefinedRelationships[@name=$bcpol_operation]/@targets)";

				String operation = buildFieldNamespace(xpathOperation, null, null, dataGraph, xpathVariables);

				if (sourceIdentifier != null && !operation.isEmpty()) {
					String[] operations = operation.split(" ");
					List<String> listOperations = new ArrayList<>();
					for (String ope : operations) {
						String xpathIdOperation = "string(sdo_1:artefacts[@bsrURI=$ope]//sdo_1:userDefinedProperties[@name=$bcpol_idOperation]/@value)";
						variables.put("ope", ope);
						MbXPathVariables vars = buildVariables(variables);
						String idOperation = buildFieldNamespace(xpathIdOperation, null, null, dataGraph, vars);
						listOperations.add(idOperation);
					}
					authorization.setOperations(listOperations);
				}
				listAuthorization.add(authorization);
			}
		}
		return listAuthorization;
	}

	/**
	 * obtiene las clasificaciones matriculadas en el registry
	 * 
	 * @author ypalomeq
	 * @version 1.0 ypalomeq@bancolombia.com.co
	 * @throws MbException
	 * */
	private void getDatosSLA(String bsrURIRoot, MbElement dataGraph, SLA slaNew) throws MbException {

		variables = new LinkedHashMap<>();
		variables.put(URI_ROOT, bsrURIRoot);
		variables.put("gep63_sourceSystem", GEP63_SOURCESYSTEM);
		variables.put("gep63_sourceDataDomain", GEP63_SOURCEDATADOMAIN);
		variables.put("prefEnvironment", PREFENVIRONMENT);

		MbXPathVariables xpathVariables = buildVariables(variables);

		String xpathDatDomain = "string(sdo_1:artefacts[@bsrURI=$bsrURIRoot]/sdo_1:userDefinedProperties[@name=$gep63_sourceDataDomain]/@value)";

		slaNew.setSourceDataDomain(buildFieldNamespace(xpathDatDomain, null, null, dataGraph, xpathVariables));

		String xpathSourSys = "string(sdo_1:artefacts[@bsrURI=$bsrURIRoot]/sdo_1:userDefinedProperties[@name=$gep63_sourceSystem]/@value)";

		slaNew.setSourceSystem(buildFieldNamespace(xpathSourSys, null, null, dataGraph, xpathVariables));

		String xpathEnv = "string(sdo_1:artefacts[@bsrURI=$bsrURIRoot]/sdo_1:classificationURIs[contains(text(),$prefEnvironment)])";

		String envIn = buildFieldNamespace(xpathEnv, null, null, dataGraph, xpathVariables);
		int indexEnv = envIn.indexOf("_");
		slaNew.setEnvironment(envIn.substring(indexEnv + 1, envIn.length()));

	}

	/**
	 * obtiene la version del sservicio
	 * 
	 * @author ypalomeq
	 * @version 1.0 ypalomeq@bancolombia.com.co
	 * @throws MbException
	 * @throws MappingException 
	 * */
	private void getServiceVersion(String agreed, String nameSpace, MbElement dataGraph, ServiceVersion serviceVersion) throws MbException, MappingException {

		String xpathServiceIdentifier = "string(sdo_1:artefacts[@bsrURI=$agreed]/sdo_1:userDefinedProperties[@name=$gep63_serviceIdentifier]/@value)";
		variablesXPATH = new MbXPathVariables();
		variablesXPATH.assign("agreed", agreed);
		variablesXPATH.assign("gep63_serviceIdentifier", GEP63_SERVICEIDENTIFIER);
		
		String name = buildFieldNamespace(xpathServiceIdentifier, null, null, dataGraph, variablesXPATH);
		if (name == null) throw new MappingException("Nombre del servicio invalido.");
		
		serviceVersion.setName(name);
		serviceVersion.setVersion(nameSpace.substring(nameSpace.length() - 3, nameSpace.length()));
	}

	/**
	 * obtiene las rutas de cada uno de los servicios
	 * 
	 * @author ypalomeq
	 * @version 1.0 ypalomeq@bancolombia.com.co
	 * @throws MbException
	 * */
	private void getInternalRoute(InternalRoute internalRoute, String internalEnd, MbElement dataGraph) throws MbException {

		variables = new LinkedHashMap<>();
		variables.put("internalEnd", internalEnd);
		variables.put("bcsep_operationId", BCSEP_OPERATIONID);
		variables.put("bcsep_consumerId", BCSEP_CONSUMERID);

		MbXPathVariables xpathVariables = buildVariables(variables);
		String xpathInternalRouteName = "string(sdo_1:artefacts[@bsrURI=$internalEnd]/@name)";

		internalRoute.setName(buildFieldNamespace(xpathInternalRouteName, null, null, dataGraph, xpathVariables));

		String xpathOperationId = "string(sdo_1:artefacts[@bsrURI=$internalEnd]/sdo_1:userDefinedProperties[@name=$bcsep_operationId]/@value)";

		internalRoute.setOperationId(buildFieldNamespace(xpathOperationId, null, null, dataGraph, xpathVariables));

		String xpathConsumerId = "string(sdo_1:artefacts[@bsrURI=$internalEnd]/sdo_1:userDefinedProperties[@name=$bcsep_consumerId]/@value)";

		internalRoute.setConsumerId(buildFieldNamespace(xpathConsumerId, null, null, dataGraph, xpathVariables));
	}

	/**
	 * obtiene las rutas de cada uno de los servicios y las colas
	 * 
	 * @author ypalomeq
	 * @version 1.0 ypalomeq@bancolombia.com.co
	 * @throws MbException
	 * */
	private String getInternalComponentEndpoint(Map<String, String> variables, String xpathComponent, InternalComponentEndpoint componentEndpoint,
			MbElement dataGraph) throws MbException {

		String targeBcfrw = "";

		MbXPathVariables xpathVariables = buildVariables(variables);

		String destination = buildFieldNamespace(xpathComponent, null, null, dataGraph, xpathVariables);
		variables.put("destination", destination);

		String destinationPath = "string(sdo_1:artefacts[@bsrURI=$destination]/@name)";

		targeBcfrw = getComponentData(destinationPath, xpathVariables, componentEndpoint, dataGraph, targeBcfrw, destination);

		xpathVariables = buildVariables(variables);

		return targeBcfrw;
	}

	private String getComponentData(String xpath, MbXPathVariables xpathVariables, InternalComponentEndpoint internalComponentEndpoint, MbElement dataGraph,
			String targeBcfrw, String destination) throws MbException {

		xpathVariables = buildVariables(variables);

		String valueDestination = buildFieldNamespace(xpath, null, null, dataGraph, xpathVariables);

		internalComponentEndpoint.setName(valueDestination);

		String xpathTipoComponente = "string(sdo_1:artefacts[@bsrURI=$destination]/@primaryType)";

		String componentType = buildFieldNamespace(xpathTipoComponente, null, null, dataGraph, xpathVariables);
		int inIndexType = componentType.indexOf("#");

		internalComponentEndpoint.setComponentType(componentType.substring(inIndexType + 1, componentType.length()));

		String xpathTargeBcfrw = "string(sdo_1:artefacts[@bsrURI=$destination]/sdo_1:userDefinedRelationships[@name=$bcfrw_endpoints]/@targets)";
		variables.put("destination", destination);
		xpathVariables = buildVariables(variables);

		targeBcfrw = buildFieldNamespace(xpathTargeBcfrw, null, null, dataGraph, xpathVariables);

		String xpathTipoEndpoint = "string(sdo_1:artefacts[@bsrURI=$targeBcfrw]/@primaryType)";
		String valueTarge = targeBcfrw.split(" ")[0];
		variables.put("targeBcfrw", valueTarge);
		xpathVariables = buildVariables(variables);
		String tipoEndpoint = buildFieldNamespace(xpathTipoEndpoint, null, null, dataGraph, xpathVariables);
		int indexEndpoint = tipoEndpoint.indexOf("#");
		internalComponentEndpoint.setEndPointType(tipoEndpoint.substring(indexEndpoint + 1, tipoEndpoint.length()));

		return targeBcfrw;
	}

	/**
	 * obtiene las politicas de seguridad de los servicios
	 * 
	 * @author ypalomeq
	 * @version 1.0 ypalomeq@bancolombia.com.co
	 * @throws MbException
	 * */
	private void getSecurityPolicies(String agreed, MbElement dataGraph, SLD sldNew) throws MbException {

		variables = new LinkedHashMap<>();
		variables.put("gep63_securityPolicy", GEP63_SECURITYPOLICY);
		variables.put("agreed", agreed);
		MbXPathVariables xPathVariables = buildVariables(variables);
		List<Policy> listPolicy = new ArrayList<>();
		String xpathSecurityPolicies = "string(sdo_1:artefacts[@bsrURI=$agreed]/sdo_1:userDefinedRelationships[@name=$gep63_securityPolicy]/@targets)";

		String securityPolicy = buildFieldNamespace(xpathSecurityPolicies, null, null, dataGraph, xPathVariables);

		if (securityPolicy != null && !securityPolicy.isEmpty()) {
			String[] securityPolicies = securityPolicy.split(" ");
			for (String ope : securityPolicies) {
				variables = new LinkedHashMap<>();
				variables.put("ope", ope);
				xPathVariables = buildVariables(variables);

				String xpathNumBcpol = "string(count(sdo_1:artefacts[@bsrURI=$ope]/sdo_1:userDefinedProperties))";

				String userDefined = buildFieldNamespace(xpathNumBcpol, null, null, dataGraph, xPathVariables);

				Policy policy = new Policy();

				String xpathName = "string(sdo_1:artefacts[@bsrURI=$ope]/@name)";

				policy.setName(buildFieldNamespace(xpathName, null, null, dataGraph, xPathVariables));

				Map<String, String> securityPolicys = new HashMap<>();

				int numUserDefined = 0;

				if (userDefined.matches("[0-9]+")) {
					numUserDefined = Integer.parseInt(userDefined);
				}

				for (int j = 1; j <= numUserDefined; j++) {

					String xpathNamePolicy = "string(sdo_1:artefacts[@bsrURI=$ope]/sdo_1:userDefinedProperties[" + j + "]/@name)";

					String key = buildFieldNamespace(xpathNamePolicy, null, null, dataGraph, xPathVariables);

					String valueKey = key.substring(key.indexOf("_") + 1, key.length());

					String xpathValue = "string(sdo_1:artefacts[@bsrURI=$ope]/sdo_1:userDefinedProperties[" + j + "]/@value)";

					String value = buildFieldNamespace(xpathValue, null, null, dataGraph, xPathVariables);

					securityPolicys.put(valueKey, value);

				}
				policy.setSecurityPolicies(securityPolicys);
				listPolicy.add(policy);

			}
			sldNew.setSecurityPoliciy(listPolicy);
		}
	}

	/**
	 * Evalua cada una de las expresiones xpath y devuelve su valor
	 * 
	 * @author ypalomeq
	 * @version 1.0
	 * @return retorna un String con el valor obtenido
	 *         ypalomeq@bancolombia.com.co
	 * @throws MbException
	 * */
	private String buildFieldNamespace(String expresionPath, String[] prefix, String[] namespaces, MbElement inXML, MbXPathVariables vars) throws MbException {
		String resultValue = "";
		if (mbNamespaceBindings == null) {
			mbNamespaceBindings = new MbNamespaceBindings();
			for (int i = 0; i < prefix.length; i++) {
				mbNamespaceBindings.addBinding(prefix[i], namespaces[i]);
			}
		}

		if (vars == null) {
			resultValue = (String) inXML.evaluateXPath(expresionPath, mbNamespaceBindings);
		} else {
			resultValue = (String) inXML.evaluateXPath(expresionPath, mbNamespaceBindings, vars);
		}

		return resultValue;
	}

	/**
	 * Construye las variables xpath
	 * 
	 * @author ypalomeq
	 * @version 1.0
	 * @return retorna un objeto MbXPathVariables con las variables asignadas.
	 *         ypalomeq@bancolombia.com.co
	 * */
	private MbXPathVariables buildVariables(Map<String, String> varsK) {
		MbXPathVariables vars = new MbXPathVariables();
		Set<String> v = varsK.keySet();

		for (String kv : v) {
			vars.assign(kv, varsK.get(kv));
		}

		return vars;
	}

	/**
	 * Construye las variables xpath
	 * 
	 * @author ypalomeq
	 * @version 1.0
	 * @return retorna un objeto MbXPathVariables con las variables asignadas.
	 *         ypalomeq@bancolombia.com.co
	 * @throws MbException
	 * */
	public int countDatagraph() throws MbException {
		String xpathDatagraph = "string(count(results/sdo:datagraph))";
		String commonPrefix[] = { SDO, SDO_1 };
		String commonNamespace[] = { NSCOMMON, NSSDO_1 };

		// se va al elemnto results del mensaje
		MbElement bodyResp = inAssembly.getMessage().getRootElement().getLastChild().getLastChild().getFirstChild();

		String countDataGraph = buildFieldNamespace(xpathDatagraph, commonPrefix, commonNamespace, bodyResp, null);
		int numDatagraph = Integer.parseInt(countDataGraph);

		return numDatagraph;

	}
}
