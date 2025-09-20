package core.integrationcontrollers.servicegateway.sg.cache;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bancolombia.integracion.cache.imp.guava.GuavaBasedCacheProperties;
import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;

import core.integrationcontrollers.servicegateway.sg.commons.CommonConstant;
import core.integrationcontrollers.servicegateway.sg.dto.DataParameterWSRR;
import core.integrationcontrollers.servicegateway.sg.dto.SLA;
import core.integrationcontrollers.servicegateway.sg.dto.SLD;
import core.integrationcontrollers.servicegateway.sg.dto.ServiceVersion;
import core.integrationcontrollers.servicegateway.sg.security.ServiceGatewaySecurity;
import core.integrationcontrollers.servicegateway.sg.utility.Utilities;

public class ValidationServiceGatewayCache extends MbJavaComputeNode {

	static final String VALIDATION_CACHE = "ValidationCache";
	static final String OUT_TERMINAL = "out";
	static final String OUT_ALTERNATE = "alternate";
	final Logger logger = Logger.getLogger(ValidationServiceGatewayCache.class.getName());
	
	private String  UDP_UNAUTHORIZED_CLIENT_MSG;

	@Override
	public void onInitialize() throws MbException {
		super.onInitialize();
		
		Properties properties = new Properties();
		properties.put(GuavaBasedCacheProperties.MAX_ELEMNTS.getPropertieValue(), (String) getUserDefinedAttribute("UDP_CACHE_MAX_ELEMNTS"));
		properties.put(GuavaBasedCacheProperties.MAX_CONCURRENT_THREADS.getPropertieValue(),
				(String) getUserDefinedAttribute("UDP_CACHE_MAX_CONCURRENT_THREADS"));
		properties.put(GuavaBasedCacheProperties.EXPIRY_TIME.getPropertieValue(), (String) getUserDefinedAttribute("UDP_CACHE_EXPIRY_TIME_SECONDS"));
		properties.put(GuavaBasedCacheProperties.EXPIRY_ACCESS.getPropertieValue(), -1);
		
		try {
			ServiceGatewayCache.init(properties);
		} catch (IllegalAccessException | InstantiationException | ClassNotFoundException | SecurityException | NoSuchMethodException
				| IllegalArgumentException | InvocationTargetException e) {
			logger.log(Level.SEVERE, "error inicializando el ServiceGateway", e);
			//lanza un excepcion que inposibilita el inicio del flujo o el nodo de integracion
			throw new MbUserException(this.getClass().getName(), "onInitialize", 
					"Error inicializando la cache del SG", e.getMessage(), Arrays.toString(e.getStackTrace()), null);
		}
		
		UDP_UNAUTHORIZED_CLIENT_MSG = (String) getUserDefinedAttribute("UDP_UNAUTHORIZED_CLIENT_MSG");
	}

	public void evaluate(MbMessageAssembly assembly) throws MbException {
		MbMessage env = assembly.getGlobalEnvironment();
		MbElement header = env.getRootElement().getFirstElementByPath("RequestOrigin").getLastChild().getFirstElementByPath("esbXML/Header");
		String systemId = header.getFirstElementByPath("systemId").getValueAsString();
		String serviceName = header.getFirstElementByPath("requestData/destination/name").getValueAsString();
		String operation = header.getFirstElementByPath("requestData/destination/operation").getValueAsString();
		String namespace = header.getFirstElementByPath("requestData/destination/namespace").getValueAsString();
		String idWscaTls = Utilities.getMessageContext(header, CommonConstant.SECURITY_CONSUMER.getValue());
		
		MbOutputTerminal out = getOutputTerminal(OUT_TERMINAL);
		
		// find to consumer in cache
		ServiceVersion serviceVersion = null;
		SLD sld = null;

		serviceVersion = (ServiceVersion) ServiceGatewayCache.getCache(namespace);
		
		if (serviceVersion != null && serviceVersion.getName().equals(serviceName)) {

			sld = findAuthorizedSld(systemId, serviceVersion);

			MbElement validationCacheElement = env.getRootElement().createElementAsFirstChild(MbElement.TYPE_NAME, VALIDATION_CACHE, "");
			validationCacheElement.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "existInCache", true);

			if (sld != null) {
				DataParameterWSRR response = ServiceGatewaySecurity.consummerAuthorization(sld, systemId, operation, idWscaTls);

				if (response.isConsumerAuthorized() && response.isOperationAuthorized() && Utilities.isAuthorizedTLS(response)) {
					Utilities.mappingValues(response, env);

					validationCacheElement.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "authorize", true);
				} else {
					core.common.util.ILUtil.throwILSystemException("REQUEST", "ILSEv2", "1203", "COMMAND",
							UDP_UNAUTHORIZED_CLIENT_MSG, env.getRootElement(), assembly
									.getExceptionList().getRootElement());
				}
			} else {
				validationCacheElement.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "isUpdateCache", true);
				validationCacheElement.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "authorize", false);
				out = getOutputTerminal(OUT_ALTERNATE);
			}

		} else {
			MbElement validationCacheElement = env.getRootElement().createElementAsFirstChild(MbElement.TYPE_NAME, VALIDATION_CACHE, "");
			validationCacheElement.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "isUpdateCache", false);
			validationCacheElement.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "existInCache", false);

			out = getOutputTerminal(OUT_ALTERNATE);
		}
		
		// Propaga el mensaje
		out.propagate(assembly);
	}

	private SLD findAuthorizedSld(String systemId, ServiceVersion service) {
		for (SLD sldObject : service.getSld()) {
			for (SLA slaObject : sldObject.getSla()) {
				if (slaObject.getApplicationId().equals(systemId)) {
					return sldObject;
				}
			}
		}
		return null;
	}
}
