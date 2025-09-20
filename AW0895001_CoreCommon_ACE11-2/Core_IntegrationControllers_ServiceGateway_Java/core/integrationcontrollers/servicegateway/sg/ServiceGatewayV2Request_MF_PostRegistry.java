package core.integrationcontrollers.servicegateway.sg;

/**
 * 
 * @author ypalomeq
 * @author dasagude
 * @sice 2018-11-20
 * @version 1.0
 * ypalomeq@bancolombia.com.co
 * */

import java.text.MessageFormat;
import java.util.List;

import com.bancolombia.integracion.cache.imp.globalcache.GlobalCacheProperties;
import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;

import core.common.util.ILUtil;
import core.integrationcontrollers.servicegateway.sg.cache.ServiceGatewayCache;
import core.integrationcontrollers.servicegateway.sg.commons.CommonConstant;
import core.integrationcontrollers.servicegateway.sg.dto.DataParameterWSRR;
import core.integrationcontrollers.servicegateway.sg.dto.SLA;
import core.integrationcontrollers.servicegateway.sg.dto.SLD;
import core.integrationcontrollers.servicegateway.sg.dto.ServiceVersion;
import core.integrationcontrollers.servicegateway.sg.mapping.MappingException;
import core.integrationcontrollers.servicegateway.sg.mapping.ServiceGatewayMapping;
import core.integrationcontrollers.servicegateway.sg.security.ServiceGatewaySecurity;
import core.integrationcontrollers.servicegateway.sg.utility.Utilities;

public class ServiceGatewayV2Request_MF_PostRegistry extends MbJavaComputeNode {
	private String unauthorizedError = (String) getUserDefinedAttribute("UDP_UNAUTHORIZED_CLIENT_MSG");
	
	static final String OUT_TERMINAL = "out";
	static final String MESSAGE_ERROR = "MessageError";

	MbMessage env = null;

	public void evaluate(MbMessageAssembly assembly) throws MbException {
		boolean newSDL = false;
		boolean newSLA = false;
		DataParameterWSRR response = null;
		ServiceGatewayMapping mappingMessas = new ServiceGatewayMapping(assembly);
		env = assembly.getGlobalEnvironment();
		MbOutputTerminal out = getOutputTerminal(OUT_TERMINAL);
		// Un elemento para resultados temporales
		MbElement tmp;

		MbElement validationCache = env.getRootElement().getFirstElementByPath("ValidationCache");

		tmp = validationCache.getFirstElementByPath("authorize");
		boolean authorized = (tmp != null) ? Boolean.parseBoolean(tmp.getValueAsString()) : false;
		tmp = validationCache.getFirstElementByPath("existInCache");
		boolean inCache = (tmp != null) ? Boolean.parseBoolean(tmp.getValueAsString()) : false;
		try {
			if (!authorized) {
				ServiceVersion serviceVersion = new ServiceVersion();
				
				mappingMessas.mappingMessages(serviceVersion);
				tmp = env.getRootElement().getFirstElementByPath("RequestOrigin").getLastChild().getFirstElementByPath("esbXML/Header");
				String systemId = tmp.getFirstElementByPath("systemId").getValueAsString();
				String operation = tmp.getFirstElementByPath("requestData/destination/operation").getValueAsString();
				String namespace = tmp.getFirstElementByPath("requestData/destination/namespace").getValueAsString();
				String service = tmp.getFirstElementByPath("requestData/destination/name").getValueAsString();
				
				String valueHashSecurity = Utilities.getMessageContext(tmp, CommonConstant.SECURITY_CONSUMER.getValue());
				
				if (!serviceVersion.getName().equals(service)) {
					String error = (String) getUserDefinedAttribute("UPD_INVALID_SERVICE_NAME_MSG");
					throw new MappingException(MessageFormat.format(error, service, serviceVersion.getName()));
				}
				
				if (inCache) {
					SLD sldIncache = null;
					ServiceVersion serviceVersionInCache = (ServiceVersion) ServiceGatewayCache.getCache(namespace);
					for (SLD sldObject : serviceVersionInCache.getSld()) {

						if (sldObject.getName().equals(serviceVersion.getSld().get(0).getName())) {
							sldIncache = sldObject;
							newSLA = true;
							break;
						}
					}

					if (sldIncache == null) {
						sldIncache = serviceVersion.getSld().get(0);
						response = ServiceGatewaySecurity.consummerAuthorization(sldIncache, systemId, operation, valueHashSecurity);
						newSDL = true;
					} else {
						response = ServiceGatewaySecurity.consummerAuthorization(serviceVersion.getSld().get(0), systemId, operation,
								valueHashSecurity);
					}

					List<SLA> listSLAInCache = sldIncache.getSla();

					if (response.isConsumerAuthorized() && (newSLA || newSDL)) {

						if (mappingMessas.countDatagraph() > 0 && Utilities.isAuthorizedTLS(response)) {
							SLA slaNonCache = serviceVersion.getSld().get(0).getSla().get(0);
							MbElement inUpdate = validationCache.getFirstElementByPath("isUpdateCache");
							if (inUpdate != null && inCache) {
								if (newSDL) {
									serviceVersionInCache.getSld().add(sldIncache);
								} else {
									listSLAInCache.add(slaNonCache);
									sldIncache.setSla(listSLAInCache);
								}
							}
						} else {
							throw new MappingException(unauthorizedError);
						}
					} else {
						if (!response.isOperationAuthorized() && !Utilities.isAuthorizedTLS(response)) {
							throw new MappingException(unauthorizedError);
						}
					}
				} else {
					response = ServiceGatewaySecurity
							.consummerAuthorization(serviceVersion.getSld().get(0), systemId, operation, valueHashSecurity);
					addOrUpdateCache(response, serviceVersion, namespace, GlobalCacheProperties.CREATE_CACHE.getPropertiesValue());
				}

				Utilities.mappingValues(response, env);
			}
		} catch (MappingException e) {
			ILUtil.throwILSystemException(e, "REQUEST", e.getExceptionId(), "COMMAND", env.getRootElement());
		}

		out.propagate(assembly);
	}

	private void addOrUpdateCache(DataParameterWSRR response, ServiceVersion serviceVersion, String keyCache, String option) throws MbException,
			MappingException {
		if (response.isConsumerAuthorized() && response.isOperationAuthorized() && Utilities.isAuthorizedTLS(response)) {
			if (serviceVersion != null && serviceVersion.getSld() != null && !serviceVersion.getSld().isEmpty()) {
				if (option.equals(GlobalCacheProperties.CREATE_CACHE.getPropertiesValue())) {

					ServiceVersion serviceVersionTemp = (ServiceVersion) ServiceGatewayCache.getCache(keyCache);

					if (serviceVersionTemp == null) {
						ServiceGatewayCache.create(keyCache, serviceVersion);
					} else {
						serviceVersionTemp.getSld().add(serviceVersion.getSld().get(0));
						ServiceGatewayCache.update(keyCache, serviceVersion);
					}
				} else if (option.equals(GlobalCacheProperties.UPDATE_CACHE.getPropertiesValue())) {
					ServiceGatewayCache.update(keyCache, serviceVersion);
				}
			}
		} else {
			throw new MappingException(unauthorizedError);
		}
	}

}