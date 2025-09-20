package com.grupobancolombia.integracion.sti.mapping.operation;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.lang.String;

import com.grupobancolombia.integracion.sti.homologation.HomologationPEQ;
import com.grupobancolombia.integracion.sti.homologation.HomologationPER;
import com.grupobancolombia.integracion.sti.iast.IASTMessageBase;
import com.grupobancolombia.integracion.sti.mapping.ListElement;
import com.grupobancolombia.integracion.sti.mapping.ListSetting;
import com.grupobancolombia.integracion.sti.mapping.ListElement.TypeOptionHomologation;
import com.grupobancolombia.integracion.sti.rule.CommonConstant;
import com.grupobancolombia.integracion.sti.rule.IASTtoILTranformationRules;
import com.grupobancolombia.integracion.sti.rule.ILtoIASTTranformationRules;
import com.ibm.broker.config.proxy.ConfigManagerProxyLoggedException;
import com.ibm.broker.config.proxy.ConfigManagerProxyPropertyNotInitializedException;
import com.ibm.broker.plugin.MbBLOB;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbNamespaceBindings;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.broker.plugin.MbXMLNSC;
import com.ibm.broker.plugin.MbXPath;

import core.common.util2.ConfigurableServiceMultiton;
import core.common.peq.EquivalenceManagerService;
//import core.common.peq.EquivalenceManagerService;
import core.common.peq.IExecutionContextPEQ;
import core.common.peq.dto.Criteria;
import core.common.peq.dto.PEQDTO;
import core.common.per.IExecutionContextPER;
import core.common.per.ResponseCodeManagerService;
import core.common.per.dto.RequestSPRDTO;
import core.common.per.dto.ResponseSPRDTO;

/**
 * Message transformation utility
 * 
 * @author Jorge Alberto Tchira Salazar
 * @version 1.0
 * @since 2017-03-24
 */
public class TransformationProccessor {

	/**
	 * Success status
	 */
	public final static String SUCCESS = "Success";

	/**
	 * Business exception status
	 */
	public final static String BUSINESS = "BusinessException";

	/**
	 * Operation response suffix
	 */
	public final static String RESPONSE = "Response";

	/**
	 * User Define Policy Service Project name
	 */
	public final static String POLICY_PROJECT = "DefaultPolicies";

	/**
	 * User Define Policy Type Service name
	 */
	public final static String POLICY_TYPE = "UserDefined";
	
	/**
	 * User Define Configurable Service IAST name
	 */
	public final static String IAST_CONFIGURABLE_SERVICE = "UDCS_CMSTI_CATALOG";

	/**
	 * SystemId IL path
	 */
	public final static String SYSTEM_ID_KEY_PATH = "/tns:esbXML/Header/systemId";

	/**
	 * MessageId IL path
	 */
	public final static String MESSAGE_ID_PATH = "/tns:esbXML/Header/messageId";

	/**
	 * Timestamp IL path
	 */
	public final static String MESSAGE_TIMESTAMP_PATH = "/tns:esbXML/Header/interactionData/timestamp";

	/**
	 * UDP timeout name
	 */
	public final static String TIME_OUT_UDP_NAME = "TIMEOUT_CONTROL_SEG";

	/**
	 * CS timer key suffix
	 */
	public final static String CS_TEMP_KEY_SUFFIX = "TEMP";

	/**
	 * MessageContext IL path
	 */
	public final static String MESSAGE_CONTEXT_PATH = "/tns:esbXML/Header/messageContext/property";

	/**
	 * Service namespace prefix constant
	 */
	public static final String SERVICE_NAMESPACE_PREFIX = "tns3";

	/**
	 * IL namespace constant
	 */
	public static final String ESBXMLIL_NAMESPACE = "http://grupobancolombia.com/intf/IL/esbXML/V3.0";

	/**
	 * IL namespace prefix constant
	 */
	public static final String ESBXMLIL_NAMESPACE_PREFIX = "tns";

	/**
	 * Process IL to IAST message transformation
	 * 
	 * @param esbXMLIL
	 *            the MbElement esbXMLIL reference
	 * @param blob
	 *            the MbElement blob reference
	 * @param messageBase
	 *            The IASTMessageBase with the data header
	 * @param elements
	 *            A TransformationElement(Element that describe a
	 *            transformation) List
	 * @param serviceNamespace
	 *            the service namespace
	 * 
	 * @return MbElement with a IAST blob reference
	 * @throws InterruptedException
	 * @throws IllegalArgumentException
	 * @throws ConfigManagerProxyPropertyNotInitializedException
	 * @throws ConfigManagerProxyLoggedException
	 */
	@SuppressWarnings("unchecked")
	public static MbElement processILtoIAST(MbElement esbXMLIL, MbElement blob,
			IASTMessageBase messageBase, List<TransformationElement> elements,
			String serviceNamespace, HomologationPEQ homologationPEQ, IExecutionContextPEQ context) throws MbException {
		
		List<Criteria> criterias = new ArrayList<Criteria>();
		PEQDTO request = new PEQDTO();

		// recorre los elementos en busqueda de campos para homologar en PEQ
		for (TransformationElement transformationElement : elements) {
		String elementValue = "";
			// valida el parametro true en la homologacion PEQ
			if (transformationElement.isHomologatePEQ()) {

				if (transformationElement.getPath() != null) {
					
					//Implementacion Nueva
					try {

						MbXPath xp1 = buildNamespace(transformationElement.getPath(), serviceNamespace);
						MbElement element = null;
						List<MbElement> result = (List<MbElement>) esbXMLIL.evaluateXPath(xp1);
						if(result.size() > 0){
							element = (MbElement) result.get(0);
							elementValue = element.getValueAsString();
						}
											
					}catch(MbException e){
						throw new IllegalArgumentException("El siguiente path: " +  transformationElement.getPath()
								+ "Se encuentra mal estructurado o no es un expresion MbXPath");
					}catch (IndexOutOfBoundsException e) {
						throw new IllegalArgumentException("El siguiente path: " +  transformationElement.getPath()
								+ "Se encuentra mal estructurado o no es un expresion MbXPath");
					}
					if(elementValue != null && !elementValue.isEmpty()){
						Criteria criteria = new Criteria();
						criteria.setTipology(transformationElement.getTypology());
						criteria.setOriginValue(elementValue);
	
						criterias.add(criteria);
					}
				}
			}

		}
		// valida si hay criterios de busqueda, si, realiza el llamado al PEQ
		if (!criterias.isEmpty()) {
			buildParameterHomologation(request,esbXMLIL ,homologationPEQ, criterias);
		

			request = EquivalenceManagerService.getEquivalences(esbXMLIL,request,context);
			criterias = request.getCriterias();

			// recorremos los criterios si fueron exitosos, de lo contrario se
			// crea un excepcion
			validateResponsePEQ(criterias);
		}
		StringBuffer sbPayload = new StringBuffer();
		int con_peq = 0;
		for (TransformationElement transformationElement : elements) {

			transformationElement.setMessageFormat(MessageFormat.IAST);

			/*
			 * si el elemento a transformar requirio homologacion a PEQ, asigna
			 * el valor del PEQ como defecto y elimina el xpath
			 */
			if (transformationElement.isHomologatePEQ()) {
				MbXPath xp1 = buildNamespace(transformationElement.getPath(), serviceNamespace);
				List<MbElement> result = (List<MbElement>) esbXMLIL.evaluateXPath(xp1);
				if(result.size() > 0 && result.get(0).getValueAsString() != null){
				transformationElement.setValue(criterias.get(con_peq)
						.getDestinationValue());
				transformationElement.setPath(null);
				con_peq++;
				}
			}

			if (transformationElement.getRuleOption() == RuleOption.GENERICSTRING) {
				transformationElement.setValue(transformationElement
						.getCharacterRefill());
			}

			if(transformationElement.isListRequest()){
				MbXPath xp1 = buildNamespace(transformationElement.getPath(), serviceNamespace);
				List<MbElement> result = (List<MbElement>) esbXMLIL
						.evaluateXPath(xp1);
				
				List<ListElement> listFieldRequest = transformationElement.getListFiedRequest();
				for (int i = 0; i < result.size(); i++) {
					MbElement element = result.get(i);
						for (ListElement listElement : listFieldRequest) {
							List<MbElement> resulta = (List<MbElement>) element.evaluateXPath(listElement.getNameField());
							transformationElement.setValue(resulta.get(0).getValueAsString());
							transformationElement.setRuleOption(listElement.getRuleOption());
							transformationElement.setLength(listElement.getLenghtFiled());
							if(listElement.isHomologation() && listElement.getTipologyOrServiceConfiguration() != null){
								if (!listElement.getTipologyOrServiceConfiguration().isEmpty()){
									Criteria criteria = new Criteria();
									criteria.setTipology(listElement.getTipologyOrServiceConfiguration());
									criteria.setOriginValue(transformationElement.getValue());
									criterias.add(criteria);
									if(criterias != null){
										buildParameterHomologation(request,esbXMLIL ,homologationPEQ, criterias);


										request = EquivalenceManagerService.getEquivalences(esbXMLIL,request,context);
										

										criterias = request.getCriterias();
										// recorremos los criterios si fueron exitosos, de lo contrario se
										// crea un excepcion
										validateResponsePEQ(criterias);
										
										//Si la validacion no arroja ningun tipo de error enviamos el valor que se va a guardar
										transformationElement.setValue(criterias.get(con_peq)
												.getDestinationValue());
									}
							}
							
						}
							sbPayload.append(processILtoIASTElement(esbXMLIL, transformationElement, serviceNamespace));
					}

				}

			}else{
				sbPayload.append(processILtoIASTElement(esbXMLIL, transformationElement, serviceNamespace));
			}
			
			
		}

		if (sbPayload.length() > 32000) {
			// Lanzar Excepcion
			// throw new Exception("Invalid length");
		}
		StringBuffer sbIAST = new StringBuffer();
		messageBase.setMessagePayLoadLength(String.valueOf(sbPayload.length()));
		try {
			processIASTHeaders(messageBase, esbXMLIL);
		} catch (ConfigManagerProxyLoggedException e) {
			throw new MbUserException(TransformationProccessor.class,
					"processILtoIAST()", "", "", e.toString(), null);
		} catch (ConfigManagerProxyPropertyNotInitializedException e) {
			throw new MbUserException(TransformationProccessor.class,
					"processILtoIAST()", "", "", e.toString(), null);
		} catch (IllegalArgumentException e) {
			throw new MbUserException(TransformationProccessor.class,
					"processILtoIAST()", "", "", e.toString(), null);
		} catch (InterruptedException e) {
			throw new MbUserException(TransformationProccessor.class,
					"processILtoIAST()", "", "", e.toString(), null);
		}
		sbIAST.append(messageBase.getHeader());
		sbIAST.append(sbPayload);
		// Get BLOB reference and assign the plane plot resulted
		MbElement reference = blob.getFirstElementByPath("BLOB");
		reference.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,
				MbBLOB.ROOT_ELEMENT_NAME, sbIAST.toString().getBytes());

		return blob;

	}

	/**
	 * Process IAST to IL message transformation
	 * 
	 * @param blob
	 *            the MbElement blob reference
	 * @param esbXMLIL
	 *            the MbElement esbXMLIL reference
	 * @param elements
	 *            A TransformationElement(Element that describe a
	 *            transformation) List
	 * @param serviceNamespace
	 *            the service namespace
	 * 
	 * @return MbElement with the esbXMLIL reference
	 */
	public static MbElement processIASTtoIL(MbElement blob, MbElement root,
			List<TransformationElement> elements, String serviceNamespace,
			IExecutionContextPER contextPER, IExecutionContextPEQ contextPEQ,
			HomologationPER homologationPER, HomologationPEQ homologationPEQ)
			throws MbException {

		String codePage 		= "";
		String message 			= "";
		byte[] blobByteArray 	= (byte[]) (blob.getLastChild().getValue());
		
		if (root.getFirstElementByPath("MQMD/CodedCharSetId") != null)
            codePage = root.getFirstElementByPath("MQMD/CodedCharSetId").getValueAsString();
        else
            codePage = root.getFirstElementByPath("Properties/CodedCharSetId").getValueAsString();
       	//Para pruebas locales si tienen error sobre la siguiente clase: com.ibm.mq.headers. 
		//Importar el jar de las siguientes dos alternativas:
		//1.Ruta de instalación de MQ: C:\Program Files\IBM\MQ\java\lib\com.ibm.mq.headers.jar
		//2.Descargar desde artifactory: https://artifactory.apps.bancolombia.com/ui/repos/tree/General/common-maven%2Fcom%2Fibm%2Fcom.ibm.mq.headers.jar
		//Para cualquiera de las dos opciónes se debe agregar en las propiedades del proyecto Core_CommonIAST_Java
		//en la sección java buildpath en la pestaña libraries e incluirla seleccionando add External Jars.
		try {
			message = new String(blobByteArray, com.ibm.mq.headers.CCSID.getCodepage(Integer.parseInt(codePage)));
		} catch (NumberFormatException | UnsupportedEncodingException e1) {
			throw new MbUserException(TransformationProccessor.class, "processIASTtoIL()", "", "", e1.toString(), null);
		}

		String codResponse = message.substring(117, 121);
		String descResponse = message.substring(121, 170);
		MbElement esbXMLIL = root.getLastChild().getFirstChild();
		String operation = esbXMLIL.getFirstElementByPath(
				"Header/requestData/destination/operation").getValueAsString();
		String operationResponse = operation + RESPONSE;
		String payload = message.substring(180);

		String elementValue = null;
		List<Criteria> criterias = new ArrayList<Criteria>();
		PEQDTO requestPEQ = new PEQDTO();

		// recorre los elementos en busqueda de campos para homologar en PEQ
		for (TransformationElement transformationElement : elements) {

			// valida el parametro true en la homologacion PEQ
			if (transformationElement.isHomologatePEQ()) {

				if (transformationElement.getIastInitialPosition() != null
						&& transformationElement.getIastFinalPosition() != null) {
					try{
					
						elementValue = payload.substring(transformationElement
							.getIastInitialPosition(), transformationElement
						.getIastFinalPosition());
					
					} catch(IndexOutOfBoundsException e){
						throw new  IllegalArgumentException("Error las posiciones de las cuales intenta obtener información en la trama plana sin incorrectas" +
								" Posicion Inicial: " + transformationElement.getIastInitialPosition() + " Posicion Final: " + transformationElement.getIastFinalPosition());
					}
					if(!elementValue.trim().isEmpty()){
						Criteria criteria = new Criteria();
						criteria.setTipology(transformationElement.getTypology());
						criteria.setOriginValue(elementValue.trim());

						criterias.add(criteria);
	
					}
				}
			}

		}
		// valida si hay criterios de busqueda, si, realiza el llamado al PEQ
		if (!criterias.isEmpty()) {
			String serviceName = esbXMLIL.getFirstElementByPath("Header/requestData/destination/name").getValueAsString();
			final String socOrigenPEQ = "WSRR:" + serviceName + ":DominioDatosOrigen";
			final String appOrigenPEQ = "WSRR:" + serviceName + ":SistemaOrigen";
			
			requestPEQ.setDestinationApp(getMessageContextValue(esbXMLIL, socOrigenPEQ));
			requestPEQ.setDestinationSociety(getMessageContextValue(esbXMLIL, appOrigenPEQ));
			
			requestPEQ.setOriginApp(homologationPEQ.getAppDestPEQ());
			requestPEQ.setOriginSociety(homologationPEQ.getSociedadDestPEQ());
			requestPEQ.setCriterias(criterias);


			requestPEQ = EquivalenceManagerService.getEquivalences(esbXMLIL, requestPEQ,contextPEQ);
			
			criterias = requestPEQ.getCriterias();

			// recorremos los criterios si fueron exitosos, de lo contrario se
			// crea un excepcion
		}

		// El codigo fue exitoso
		if (codResponse.equals("0000")) {

			ResponseStatusSucess(root, operationResponse);
			int con_peq = 0;
			for (TransformationElement transformationElement : elements) {
				if (transformationElement.getRuleOption() != RuleOption.ERRORCODE
						&& transformationElement.getRuleOption() != RuleOption.ERRORDESC) {
					String value = "";

					/*
					 * si el elemento a transformar requirio homologacion a PEQ,
					 * asigna el valor del PEQ como defecto y elimina los campos
					 * de posicion inicial y final
					 */
					if(transformationElement.getIastFinalPosition() != null && transformationElement.getIastFinalPosition() != null){
                        value = payload.substring(transformationElement.getIastInitialPosition(), transformationElement.getIastFinalPosition());  
                  }
					
					
					if (transformationElement.isHomologatePEQ() && !value.trim().isEmpty()) { 
                        transformationElement.setValue(criterias.get(con_peq) 
                                        .getDestinationValue()); 
                        transformationElement.setIastInitialPosition(null); 
                        transformationElement.setIastFinalPosition(null); 
                        con_peq++; 
						value = "";
						
						
					}

					if (transformationElement.getIastInitialPosition() != null
							&& transformationElement.getIastFinalPosition() != null) {

						value = payload.substring(transformationElement
								.getIastInitialPosition(),
								transformationElement.getIastFinalPosition());
					} else if (transformationElement.getValue() != null) {
						value = transformationElement.getValue();
					} else {
						throw new  IllegalArgumentException("Invalid TransformationElement arguments, Error las posiciones de las cuales intenta obtener información en la trama plana sin incorrectas" +
								" Posicion Inicial: " + transformationElement.getIastInitialPosition() + " Posicion Final: " + transformationElement.getIastFinalPosition());
					}

					transformationElement.setValue(value);
					transformationElement.setMessageFormat(MessageFormat.IL);
					processIASTtoILElement(esbXMLIL,transformationElement,serviceNamespace);
				}
			}

		} else {
			for (TransformationElement transformationElement : elements) {
				if (RuleOption.ERRORCODE.equals(transformationElement
						.getRuleOption())) {

					if (transformationElement.getValue() == null
							|| transformationElement.getValue().isEmpty()) {
						codResponse = payload.substring(
								transformationElement.getIastInitialPosition(),
								transformationElement.getIastFinalPosition())
								.trim();
					} else {
						codResponse = transformationElement.getValue().trim();
					}
					// realiza la consulta al SPR, si no encuentra un valor a
					// homolgar
					// retorna el valor del campo anterior (codResponse)
					//if(/*homologationPER*/"NOTNULL" != null){
					  if(homologationPER != null){	
						RequestSPRDTO requestSPR = new RequestSPRDTO();
						requestSPR.setCodigoIdioma(homologationPER.getCodIdioma());
						requestSPR.setCodigoProveedorServicio(homologationPER
								.getCodProveedorServicio());
						requestSPR.setCodigoRespuestaProveedor(codResponse
								.toString());
						requestSPR.setEstadoRespuesta(homologationPER
								.getEstadoRespuesta());

						ResponseSPRDTO response = ResponseCodeManagerService.Run(esbXMLIL,requestSPR,contextPER);
						///ResponseSPRDTO response = new ResponseSPRDTO();

						// si el valor de SPr es diferente a null se asigna ese
						// valor
						if (response.getCodigoCanonico() != null) {
							codResponse = response.getCodigoCanonico();
							descResponse = response.getDescripcionNegocio();
							break;
						}else{
							codResponse = message.substring(117, 121);
							descResponse = message.substring(121, 170);
						}
					}else{
						if(codResponse == null || codResponse.isEmpty()){
							codResponse = message.substring(117, 121);
						}
					}

				} else if (RuleOption.ERRORDESC.equals(transformationElement.getRuleOption())) {
						descResponse = payload.substring(transformationElement
							.getIastInitialPosition(), transformationElement
							.getIastFinalPosition());
						if(homologationPER != null){
							if(descResponse.trim().isEmpty()){
								codResponse = message.substring(117, 121);
								descResponse = message.substring(121, 170);
							}
						
					}else{
						if(descResponse == null || descResponse.isEmpty()){
							descResponse = message.substring(121, 170);
						}

					}
					
				}
			}

			ResponseStatusBusiness(esbXMLIL, operationResponse,
					serviceNamespace, codResponse, descResponse);
		}
		return esbXMLIL;
	}

	/**
	 * Set the default headers
	 * 
	 * @param messageBase
	 *            The IASTMessageBase with the data header
	 * @param esbXMLIL
	 *            the MbElement esbXMLIL reference
	 * 
	 * @return IASTMessageBase with the default data populated
	 */
	@SuppressWarnings("unchecked")
	public static IASTMessageBase processIASTHeaders(
			IASTMessageBase messageBase, MbElement esbXMLIL)
			throws MbException, ConfigManagerProxyLoggedException,
			ConfigManagerProxyPropertyNotInitializedException,
			IllegalArgumentException, InterruptedException {
		IASTMessageBase message = null;
		if (messageBase != null) {
			//nueva implementacion obtener los namespaces
			// Set Customer Application Id
			MbNamespaceBindings ns = new MbNamespaceBindings();
			ns.addBinding(ESBXMLIL_NAMESPACE_PREFIX, ESBXMLIL_NAMESPACE);
			MbXPath xp1 = new MbXPath(SYSTEM_ID_KEY_PATH,ns);
			
			List<MbElement> result = (List<MbElement>) esbXMLIL
					.evaluateXPath(xp1);
			MbElement element = (MbElement) result.get(0);
			messageBase.setCustomerAppId(ConfigurableServiceMultiton.getValue(POLICY_TYPE, POLICY_PROJECT, IAST_CONFIGURABLE_SERVICE, element.getValueAsString()));
			
			//nueva implementacion obtener los namespaces
			// Set Customer Application Id
			ns = new MbNamespaceBindings();
			ns.addBinding(ESBXMLIL_NAMESPACE_PREFIX, ESBXMLIL_NAMESPACE);
			xp1 = new MbXPath(MESSAGE_ID_PATH,ns);

			result = (List<MbElement>) esbXMLIL.evaluateXPath(xp1);
			element = (MbElement) result.get(0);
			String messageId = element.getValueAsString();
			if (messageId != null) {
				int length = messageId.length();
				if (length > 16) {
					messageId = messageId.substring(length - 16);
				}
			}
			messageBase.setMessageId(messageId);

			// Set timer
			StringBuffer sbKey = new StringBuffer();
			sbKey.append(messageBase.getServerId());
			sbKey.append(messageBase.getTransactionId());
			sbKey.append(CS_TEMP_KEY_SUFFIX);
			
			String csTimer = ConfigurableServiceMultiton.getValue(POLICY_TYPE, POLICY_PROJECT, IAST_CONFIGURABLE_SERVICE, sbKey.toString());

			if (csTimer == null || csTimer.trim().equals(CommonConstant.EMPTY)) {
				String udpTimer = getMessageContextValue(esbXMLIL,
						TIME_OUT_UDP_NAME);

				messageBase.setMessageTimer(udpTimer);
			} else {
				messageBase.setMessageTimer(csTimer);
			}
			ns = new MbNamespaceBindings();
			ns.addBinding(ESBXMLIL_NAMESPACE_PREFIX, ESBXMLIL_NAMESPACE);
			xp1 = new MbXPath(MESSAGE_TIMESTAMP_PATH,ns);
			

			result = (List<MbElement>) esbXMLIL.evaluateXPath(xp1);
			element = (MbElement) result.get(0);
			String timestamp = element.getValueAsString();
			int index = timestamp.indexOf(".");
			if (index != -1) {
				timestamp = timestamp.substring(0, index);

			}
			if (timestamp != null) {
				TransformationElement transformacionElement = new TransformationElement();
				transformacionElement.setRuleOption(RuleOption.DATETIME);
				transformacionElement.setMessageFormat(MessageFormat.IAST);
				transformacionElement.setValue(timestamp);
				
				timestamp = evaluateRule(transformacionElement);
				messageBase.setMessageRequestHour(timestamp.substring(8,
						timestamp.length()));
			}
			// transaction sequence

			messageBase.setTransactionSequence(messageBase.getMessageId()
					+ timestamp.substring(timestamp.length() - 4));

		} else {

		}
		return message;
	}

	/**
	 * Process IAST to IL element
	 * 
	 * @param esbXMLIL
	 *            the MbElement esbXMLIL reference
	 * @param outPath
	 *            IL output value path
	 * @param rule
	 *            RuleOption that defines the type rule to be applied
	 * @param outputFormat
	 *            The IL format to process the element
	 * @param value
	 *            to transform
	 * @param length
	 *            of the final value
	 * @param booleanFormat
	 *            if the value to transform represents a boolean this param
	 *            represents its final format
	 * @param serviceNamespace
	 *            the service namespace
	 * 
	 * @return String with the value transformed
	 */
	@SuppressWarnings("unchecked")
	public static void processIASTtoILElement(MbElement esbXMLIL,TransformationElement transformationElement, 
			String serviceNamespace) throws MbException {

		MbXPath xp1 = buildNamespace(transformationElement.getPath(), serviceNamespace);
		MbElement element = null;
		try{
			List<MbElement> result = (List<MbElement>) esbXMLIL.evaluateXPath(xp1);
			element = (MbElement) result.get(0);
		}catch(MbException e){
			throw new IllegalArgumentException("El siguiente path: " +  transformationElement.getPath()
					+ "Se encuentra mal estructurado o no es un expresion MbXPath");
		}catch(IndexOutOfBoundsException e){
			throw new IllegalArgumentException("Invalid TransformationElement arguments, Error las posiciones de las cuales intenta obtener información en la trama plana sin incorrectas" +
					" Posicion Inicial: " + transformationElement.getIastInitialPosition() + " Posicion Final: " + transformationElement.getIastFinalPosition());
		}

		String valueTransformed = evaluateRule(transformationElement);
		element.setValue(valueTransformed);
	}

	/**
	 * Process IL to IAST element
	 * 
	 * @param esbXMLIL
	 *            the MbElement esbXMLIL reference
	 * @param path
	 *            IL value path
	 * @param rule
	 *            RuleOption that defines the type rule to be applied
	 * @param outputFormat
	 *            The IAST format to process the element
	 * @param value
	 *            to transform
	 * @param length
	 *            of the final value
	 * @param booleanFormat
	 *            if the value to transform represents a boolean this param
	 *            represents its final format
	 * @param serviceNamespace
	 *            the service namespace
	 * 
	 * @return String with the value transformed
	 */
	public static String processILtoIASTElement(MbElement esbXMLIL,TransformationElement transformationElement,
			String serviceNamespace) throws MbException {

		String elementValue = null;
		if (transformationElement.getPath() != null) {
			try {
				
				elementValue = getValuePath(transformationElement.getPath(), serviceNamespace, esbXMLIL);
			} catch (Exception e) {
				elementValue = "";
			}
			if (RuleOption.GENERICSTRING == transformationElement.getRuleOption()) {
				elementValue = elementValue + "¬" + transformationElement.getValue();
				transformationElement.setValue(elementValue != null ? elementValue : null);
			}
		}
		if(transformationElement.getValue() == null){
			transformationElement.setValue(elementValue != null ? elementValue : null);
			
		}
		return evaluateRule(transformationElement);
	}

	/**
	 * Evaluate and perform the rule desired
	 * 
	 * @param rule
	 *            RuleOption that defines the type rule to be applied
	 * @param outputFormat
	 *            The IAST format to process the element
	 * @param value
	 *            to transform
	 * @param length
	 *            of the final value
	 * @param booleanFormat
	 * 
	 * @return String with the value transformed
	 */
	public static String evaluateRule(TransformationElement transformationElement) {

		String valueTransformed = null;
		ArrayList<String> managerFieldError = new ArrayList<String>();
		managerFieldError.add("Path: " + transformationElement.getPath());
		managerFieldError.add("Value: " + transformationElement.getValue());
		if (MessageFormat.IAST.equals(transformationElement.getMessageFormat())) {
			
			switch (transformationElement.getRuleOption()) {
			case GENERICSTRING:
				String param[] = transformationElement.getValue().split("¬");
				valueTransformed = ILtoIASTTranformationRules.CharacterRefill(
						param != null ? param[0] : " ", transformationElement.getLength(), param != null ? param[1] : " ", managerFieldError);
				break;
			case RIGHTSTRING:
				valueTransformed = ILtoIASTTranformationRules
						.transformILtoIASTRightStringRule(transformationElement.getValue(), transformationElement.getLength(), managerFieldError);
				break;
			case STRING:
				valueTransformed = ILtoIASTTranformationRules
						.transformILtoIASTStringRule(transformationElement.getValue(), transformationElement.getLength(), managerFieldError);
				//valueTransformed = ILtoIASTTranformationRules.transformILtoIASTStringRule(transformationElement);
				break;
			case INTEGER:
				valueTransformed = ILtoIASTTranformationRules
						.transformILtoIASTIntegerRule(transformationElement.getValue(), transformationElement.getLength(), managerFieldError);
				break;
			case LONG:
				valueTransformed = ILtoIASTTranformationRules
						.transformILtoIASTLongRule(transformationElement.getValue(), transformationElement.getLength(), managerFieldError);
				break;
			case DECIMAL:
				valueTransformed = ILtoIASTTranformationRules
						.transformILtoIASTDecimalRule(transformationElement.getValue(), transformationElement.getLength(), managerFieldError);
				break;
			case DATETIME:
				valueTransformed = ILtoIASTTranformationRules
						.transformILtoIASTDateTimeRule(transformationElement.getValue(), managerFieldError);
				break;
			case DATE:
				valueTransformed = ILtoIASTTranformationRules
						.transformILtoIASTDateRule(transformationElement.getValue(), managerFieldError);
				break;
			case TIME:
				valueTransformed = ILtoIASTTranformationRules
						.transformILtoIASTTimeRule(transformationElement.getValue(),managerFieldError);
				break;
			case BOOLEAN:
				valueTransformed = ILtoIASTTranformationRules
						.transformILtoIASTBooleanRule(transformationElement.getValue(), transformationElement.getBooleanFormat(), managerFieldError);
				break;
			default:
				throw new IllegalArgumentException("Invalid rule");
			}

		} else if (MessageFormat.IL.equals(transformationElement.getMessageFormat())) {
			managerFieldError.add(" Initial Position: " + transformationElement.getIastInitialPosition());
			managerFieldError.add(" Final Position: " + transformationElement.getIastFinalPosition());
			switch (transformationElement.getRuleOption()) {
			case GENERICSTRING:
				valueTransformed = IASTtoILTranformationRules
						.transformIASTtoILStringRule(transformationElement.getValue());
				break;
			case RIGHTSTRING:
				valueTransformed = IASTtoILTranformationRules
						.transformIASTtoILStringRule(transformationElement.getValue());
				break;
			case STRING:
				valueTransformed = IASTtoILTranformationRules
						.transformIASTtoILStringRule(transformationElement.getValue());
				break;
			case INTEGER:
				valueTransformed = IASTtoILTranformationRules
						.transformIASTtoILIntegerRule(transformationElement.getValue(), managerFieldError);
				break;
			case LONG:
				valueTransformed = IASTtoILTranformationRules
						.transformIASTtoILLongRule(transformationElement.getValue(), managerFieldError);
				break;
			case DECIMAL:
				valueTransformed = IASTtoILTranformationRules
						.transformIASTtoILDecimalRule(transformationElement.getValue(), managerFieldError);
				break;
			case DATETIME:
				valueTransformed = IASTtoILTranformationRules
						.transformIASTtoILDateTimeRule(transformationElement.getValue(), managerFieldError);
				break;
			case DATE:
				valueTransformed = IASTtoILTranformationRules
						.transformIASTtoILDateRule(transformationElement.getValue(), managerFieldError);
				break;
			case TIME:
				valueTransformed = IASTtoILTranformationRules
						.transformIASTtoILTimeRule(transformationElement.getValue(), managerFieldError);
				break;
			case BOOLEAN:
				valueTransformed = IASTtoILTranformationRules
						.transformIASTtoILBooleanRule(transformationElement.getValue(), transformationElement.getBooleanFormat(), managerFieldError);
				break;
			default:
				throw new IllegalArgumentException("Invalid rule");
			}
		} else {
			throw new IllegalArgumentException("Invalid format");
		}

		return valueTransformed;
	}

	/**
	 * Find a value of a specific key in the esbXMLIL message
	 * 
	 * @param esbXMLIL
	 *            the MbElement esbXMLIL reference
	 * @param key
	 *            to find the value
	 * 
	 * @return String with the value transformed
	 */
	@SuppressWarnings("unchecked")
	public static String getMessageContextValue(MbElement esbXMLIL, String key)
			throws MbException {
		String value = null;
		if (esbXMLIL != null && key != null && !key.isEmpty()) {
			MbNamespaceBindings ns = new MbNamespaceBindings();
			ns.addBinding(ESBXMLIL_NAMESPACE_PREFIX, ESBXMLIL_NAMESPACE);
			MbXPath xp1 = new MbXPath(MESSAGE_CONTEXT_PATH, ns);
			
			List<MbElement> result = (List<MbElement>) esbXMLIL
					.evaluateXPath(xp1);

			for (MbElement mbElement : result) {
				MbElement mbKey = mbElement.getFirstChild();
				if (key.equals(mbKey.getValueAsString())) {
					value = mbElement.getLastChild().getValueAsString();
				}
			}
		}

		return value;

	}

	/**
	 * Prepare header response Business
	 * 
	 * @param message
	 *            output
	 * @throws MbException
	 */
	public static void ResponseStatusBusiness(MbElement outMessage,
			String operatioResponse, String namespace, String codError,
			String descError) throws MbException {
		// referencia al Header y Body
		MbElement esbXMLIL_Header = outMessage.getFirstElementByPath("Header");
		MbElement esbXMLIL_Body = outMessage;

		// Setea el nombre de la operacion de respuesta
		esbXMLIL_Header.getFirstElementByPath(
				"requestData/destination/operation").setValue(operatioResponse);

		// crear al estructura responseData
		esbXMLIL_Header = esbXMLIL_Header.getFirstElementByPath("requestData")
				.createElementAfter(MbElement.TYPE_NAME, "responseData", null);
		esbXMLIL_Header.createElementAsFirstChild(MbXMLNSC.FOLDER,
				"providerData", null);
		esbXMLIL_Header = esbXMLIL_Header.createElementAsLastChild(
				MbXMLNSC.FOLDER, "status", null);
		esbXMLIL_Header.createElementAsFirstChild(MbXMLNSC.FIELD, "statusCode",
				BUSINESS);

		// crear estructura body de BUSINESS

		esbXMLIL_Body = esbXMLIL_Body.getLastChild(); 
		
		esbXMLIL_Body = esbXMLIL_Body.createElementAsLastChild(
				MbElement.TYPE_NAME, "businessException", null);
		esbXMLIL_Body.setNamespace(namespace);
		esbXMLIL_Body = esbXMLIL_Body.createElementAsLastChild(MbXMLNSC.FOLDER,
				"genericException", null);
		esbXMLIL_Body.createElementAsFirstChild(MbXMLNSC.FIELD, "code",
				codError);
		esbXMLIL_Body.createElementAsLastChild(MbXMLNSC.FIELD, "description",
				descError);

	}

	/**
	 * Prepare header response Succes
	 * 
	 * @param message
	 *            output
	 * @throws MbException
	 */
	public static void ResponseStatusSucess(MbElement outMessage,
			String operatioResponse) throws MbException {
		// referencia al Header
		MbElement esbXMLIL_Header = outMessage
				.getFirstElementByPath("XMLNSC/esbXML/Header");

		// Setea el nombre de la operacion de respuesta
		esbXMLIL_Header.getFirstElementByPath(
				"requestData/destination/operation").setValue(operatioResponse);

		// crear al estructura responseData
		esbXMLIL_Header = esbXMLIL_Header.getFirstElementByPath("requestData")
				.createElementAfter(MbElement.TYPE_NAME, "responseData", null);
		esbXMLIL_Header.createElementAsFirstChild(MbXMLNSC.FOLDER,
				"providerData", null);
		esbXMLIL_Header = esbXMLIL_Header.createElementAsLastChild(
				MbXMLNSC.FOLDER, "status", null);
		esbXMLIL_Header.createElementAsFirstChild(MbXMLNSC.FIELD, "statusCode",
				SUCCESS);
	}

	/**
	 * Uses CoreCommons utility to get a value from a configurable service
	 * 
	 * @param configurableService
	 *            name
	 * @param key
	 *            to get a value
	 */
	public static String getConfigurableServiceValue(
			String configurableService, String key) {

		return ConfigurableServiceMultiton.getValue(POLICY_TYPE, POLICY_PROJECT, configurableService, key);
	}

	public static PEQDTO getData() {
		PEQDTO request = new PEQDTO();
		request.setDestinationApp("SAP-FI");
		request.setDestinationSociety("1000");
		request.setOriginApp("BVG");
		request.setOriginSociety("1000");

		List<Criteria> criterias = new ArrayList<Criteria>();

		Criteria criteria = new Criteria();
		criteria.setTipology("CENTROBE");
		criteria.setOriginValue("00001");

		Criteria criteria1 = new Criteria();
		criteria1.setTipology("CENTROBE");
		criteria1.setOriginValue("00002");

		criterias.add(criteria);
		criterias.add(criteria1);
		request.setCriterias(criterias);

		return request;

	}

	/***
	 * @author ypalomeq
	 * @since 2017-10-06
	 * @desc This function allows to traverse the lists delivered from STI, in
	 *       format IAST obtaining the values of the fields in each position.
	 * 
	 * @param listField
	 * 			  List of the fields that mapping of 
	 *            lista de los campos lo que se mapeo de trama plana a XML
	 * @param payLoad
	 *            payload in which you have all the information in the lists
	 * @param elements
	 *            List of elements that are filled with the information of the
	 *            fields, to be sent to the rules established by the
	 *            consumermediatorSTI and make their respective transformation
	 *            to XML
	 * 
	 * @param parameters
	 *            It has the configuration of each list that is sent from STI
	 *            for example: length of the list, number of fields, initial
	 *            position
	 */
	public static void mapList(List<ListElement> listField, String payLoad,
			List<TransformationElement> elements, ListSetting parameters) {

		if (parameters.getPath() != null && !parameters.getPath().isEmpty()) {
			
			try {
			String list = payLoad.substring(parameters.getPositionInitiaList());
			String cursor = "";
			int aux = 0, position = 0;
			int lenPayload = payLoad.length();
			int positionInitial = lenPayload
					- parameters.getPositionInitiaList();
			for (int i = 0; i < parameters.getNumberRegister(); i++) {
				if (i == 0) {
					cursor = list.substring(0, positionInitial);

					aux = parameters.getLenghtList();
				} else {
					position = aux;
					aux = aux + parameters.getLenghtList();
					cursor = list.substring(position, aux);

				}
				if (!cursor.isEmpty()) {
					position = payLoad.indexOf(cursor);

					int numField = parameters.getNumberFlied();
					for (int j = 0; j < numField; j++) {

						String field = parameters.getPath();
						String pathField = field.replace("$", "") + "["
								+ (i + 1) + "]/?";
						if (j == 0) {
							field = parameters.getPath().replace("$?", "");
							pathField = field + "/?";
						}
						int lenField = listField.get(j).getPositionFinal() - listField.get(j).getPositionInitial();
						if(listField.get(j).getDefaultValue() == null){
						
							  if (!listField.get(j).isHomologation()) {

									if (listField.get(j).getListNumericProcces() != null) {
										if (listField.get(j).getListNumericProcces()
												.getNumCifraDecimal() > 2) {
		
											String value = payLoad.substring(position + listField.get(j).getPositionInitial(),
															position + listField.get(j).getPositionFinal());
		
											value = transformIASTtoILDecimal(value,listField.get(j).getListNumericProcces().getNumCifraDecimal());
		
											elements.add(new TransformationElement(listField.get(j).getRuleOption(),
													value, null, lenField,
													pathField + listField.get(j).getNameField(),
													null, null));
										} else if (listField.get(j).getListNumericProcces().getPositionFinalSin() > 0) {
		
											String valueSing = payLoad.substring(position + listField.get(j).getListNumericProcces()
																									 .getPositionInitialSing(),
																		         position + listField.get(j).getListNumericProcces()
																		         					 .getPositionFinalSin());
											if (!(valueSing.equals("-")) || !(valueSing.equals("+"))){
												if (isNumeric(valueSing))
												{
													valueSing="+";
												}else{
													valueSing="-";
												}
											}
											
											String value = "";
											if (!(valueSing.equals("-")) && valueSing
													.equals("+")) {
												value = payLoad.substring(position + listField.get(j).getPositionInitial(),
																		  position + listField.get(j).getPositionFinal());
											} else {
												value = valueSing + payLoad.substring(position + listField.get(j)
																						.getPositionInitial(),
																		position + listField.get(j)
																						.getPositionFinal());
											}
											elements.add(new TransformationElement(listField.get(j).getRuleOption(),
													value, null, lenField,
													pathField + listField.get(j).getNameField(),
													null, null));
										}
									} else {
										elements.add(new TransformationElement(listField.get(j).getRuleOption(),
												null,
												null,
												lenField,
												pathField + listField.get(j).getNameField(),
												(position + listField.get(j).getPositionInitial()),
												(position + listField.get(j).getPositionFinal())));
									}
								} else {
									if (listField.get(j).getTypeHomologation() == TypeOptionHomologation.SERVICIECONFIGURATION) {

										String key =  payLoad.substring(
															    position + listField.get(j).getPositionInitial(),
																position + listField.get(j).getPositionFinal());
										
										if(listField.get(j).getPrefixHomologation() != null){
											
											if(key.matches("[0-9]+")){
												key = String.valueOf(Integer.parseInt(key));
											}
											key = listField.get(j).getPrefixHomologation() + key;
										}
										
										String value = ConfigurableServiceMultiton.getValue(POLICY_TYPE, POLICY_PROJECT, 
														listField.get(j).getTipologyOrServiceConfiguration(),
														key.trim());
										
										elements.add(new TransformationElement(listField.get(j).getRuleOption(),
												value, null, lenField, 
												pathField + listField.get(j).getNameField(), 
												null,
												null));
									} else {
										elements
												.add(new TransformationElement(listField.get(j).getRuleOption(),
														null,
														null,
														lenField,
														pathField + listField.get(j).getNameField(),
														(position + listField.get(j).getPositionInitial()),
														(position + listField.get(j).getPositionFinal()),
														true,
														listField.get(j).getTipologyOrServiceConfiguration()));
		
									}
								}

					}else{
						elements.add(new TransformationElement(listField.get(j).getRuleOption(),
								listField.get(j).getDefaultValue(),
								null, lenField, 
								pathField + listField.get(j).getNameField(), 
								null,
								null));
					}
						
				}
			
			}
		}
		}catch(IndexOutOfBoundsException e){
			throw new IllegalArgumentException("Se presento un error al momento de obtener las posiciones de la trama, Posicion Inicial");
		}
	}
}

	/***
	 * 
	 * @author ypalomeq
	 * @since 2017-10-06
	 * @desc this function allows more of two decimal
	 * @param value
	 *            parameter that contains the value of the field
	 * @param numDecimal
	 *            parameter that contains the figures decimal of the field value
	 */

	public static String transformIASTtoILDecimal(String value, int numDecimal) {

		StringBuffer sb = new StringBuffer();

		if (value.length() > numDecimal) {
			int numEntero = value.length() - numDecimal;
			int cifraEntera = Integer.parseInt(value.substring(0, numEntero));
			String cifraDecimal = value.substring(numEntero, numDecimal
					+ numEntero);

			if (numEntero + numDecimal <= value.length()) {
				sb.append(cifraEntera);
				sb.append(CommonConstant.DECIMAL_SEPARATOR.getValue());
				sb.append(cifraDecimal);
			} else {
				throw new IllegalArgumentException(
						"Invalid parameters request y response");
			}
		}
		return sb.toString();
	}
	
	public static MbXPath buildNamespace(String path, String namespace) throws MbException{
		
		MbNamespaceBindings ns = new MbNamespaceBindings();
		ns.addBinding(SERVICE_NAMESPACE_PREFIX, namespace);
		ns.addBinding(ESBXMLIL_NAMESPACE_PREFIX, ESBXMLIL_NAMESPACE);
		MbXPath xp1 = new MbXPath(path,ns);
		

		return xp1;
		
	}
	
	public static String getValuePath(String path, String namespace, MbElement esbXMLIL) throws MbException{
        
        MbNamespaceBindings ns = new MbNamespaceBindings();
        ns.addBinding(SERVICE_NAMESPACE_PREFIX, namespace);
        ns.addBinding(ESBXMLIL_NAMESPACE_PREFIX, ESBXMLIL_NAMESPACE);
        MbXPath xp1 = new MbXPath(path,ns);
        
        @SuppressWarnings("unchecked")
        List<MbElement> result = (List<MbElement>) esbXMLIL
                .evaluateXPath(xp1);
        String resultValue = "";
        if (result.size() > 0 ) {
         MbElement element = (MbElement) result.get(0);
         resultValue = element.getValueAsString();
        } 
        return resultValue;
 
    }

	public static boolean isNumeric(String strNum) {
		try {
			double d = Double.parseDouble(strNum);
		} catch (NumberFormatException | NullPointerException nfe) {
			return false;
		}
		return true;
	}
	
	public static void validateResponsePEQ(List<Criteria> criterias) throws MbUserException{
		for (Criteria criteria : criterias) {
			if (criteria.getResponseCode() == null ||
					!criteria.getResponseCode().equals("00")) {
				throw new MbUserException("Descripcion PEQ: "
						+ criteria.getResponseDesc(), "Codigo PEQ: "
						+ criteria.getResponseCode(), "REQUEST", "COMMAND",
						"Error en homologacion al PEQ", null);
			}
		}

	}
	public static void buildParameterHomologation(PEQDTO request, MbElement esbXMLIL, HomologationPEQ homologationPEQ, List<Criteria> criterias) throws MbException{
		String serviceName = esbXMLIL.getFirstElementByPath(
				"Header/requestData/destination/name").getValueAsString();
		final String socOrigenPEQ = "WSRR:" + serviceName + ":DominioDatosOrigen";
		final String appOrigenPEQ = "WSRR:" + serviceName + ":SistemaOrigen";

		
		request.setOriginApp(getMessageContextValue(esbXMLIL, socOrigenPEQ));
		request.setOriginSociety(getMessageContextValue(esbXMLIL,appOrigenPEQ));
		request.setDestinationApp(homologationPEQ.getAppDestPEQ());
		request.setDestinationSociety(homologationPEQ.getSociedadDestPEQ());
		request.setCriterias(criterias);

	}
	
	/**
     * Process byte array to string
     * 
     * @param blob
     *            the MbElement blob reference
     * @param root
     *            the MbElement esbXMLIL reference
     * 
     * @return String with message
     * @throws MbException 
     * @throws NumberFormatException 
     */
    public static String byteArrayToString(MbElement blob, MbElement root) throws MbException {
        byte[] blobByteArray    = (byte[]) (blob.getLastChild().getValue());
        String message ="";
        String codePage;
        if (root.getFirstElementByPath("MQMD/CodedCharSetId") != null)
            codePage = root.getFirstElementByPath("MQMD/CodedCharSetId").getValueAsString();
        else
            codePage = root.getFirstElementByPath("Properties/CodedCharSetId").getValueAsString();
        
        try {
            message = new String(blobByteArray, com.ibm.mq.headers.CCSID.getCodepage(Integer.parseInt(codePage)));
        } catch (NumberFormatException | UnsupportedEncodingException e) {
            throw new MbUserException(TransformationProccessor.class, "byteArrayToString()", "", "", e.toString(), null);
        }
        return message;
    }
    
}
