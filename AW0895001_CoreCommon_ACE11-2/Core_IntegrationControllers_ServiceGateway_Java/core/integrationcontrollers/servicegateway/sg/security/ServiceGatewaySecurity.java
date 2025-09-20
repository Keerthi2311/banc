package core.integrationcontrollers.servicegateway.sg.security;

/**
 * Valida las autorizaciones de consumidores, operaciones y consumos seguros.
 * @author ypalomeq
 * @version 1.0
 * ypalomeq@bancolombia.com.co
 *  */
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import core.integrationcontrollers.servicegateway.sg.commons.CommonConstant;
import core.integrationcontrollers.servicegateway.sg.dto.Authorization;
import core.integrationcontrollers.servicegateway.sg.dto.DataParameterWSRR;
import core.integrationcontrollers.servicegateway.sg.dto.InternalRoute;
import core.integrationcontrollers.servicegateway.sg.dto.Policy;
import core.integrationcontrollers.servicegateway.sg.dto.SLA;
import core.integrationcontrollers.servicegateway.sg.dto.SLD;

public class ServiceGatewaySecurity {

	private ServiceGatewaySecurity() {
	}

	public static DataParameterWSRR consummerAuthorization(SLD sld, String idConsumer, String nameOperation, String idWscaTls) {
		// validar si el consumidor existe en sla recuperado de la cache
		DataParameterWSRR parametersResponse = validateConsumerAuthorization(sld, idConsumer, nameOperation);
		if (parametersResponse.isConsumerAuthorized()) {
			validateRoutes(nameOperation, sld, parametersResponse, idConsumer);
			validateSecurityConsumerTLS(sld, idWscaTls, parametersResponse);
		}

		return parametersResponse;
	}

	/**
	 * Valida el consumidor que viene en el mensaje de peticion contra los SLA
	 * devueltos en la consulta al registry
	 * 
	 * @param SDL
	 * @param String
	 * @return retorna objeto DataParameterWSRR en la cual indica si el
	 *         consumirdor se encuentra autorizado
	 * */
	private static DataParameterWSRR validateConsumerAuthorization(SLD sld, String idConsumer, String nameOperation) {
		DataParameterWSRR parametersResponse = new DataParameterWSRR();
		parametersResponse.setConsumerAuthorization(false);
		List<SLA> slaList = sld.getSla();
		if (slaList == null) {
			return parametersResponse;
		}
		for (SLA sla : slaList) {
			if (idConsumer.equals(sla.getApplicationId()) && isAuthorizationSet(sla, nameOperation)) {
				parametersResponse.setConsumerAuthorization(true);
				if (sla.getSourceDataDomain() != null && !sla.getSourceDataDomain().isEmpty() && sla.getSourceSystem() != null
						&& !sla.getSourceSystem().isEmpty()) {
					parametersResponse.setDataDomainOrigin(sla.getSourceDataDomain());
					parametersResponse.setSystemOrigin(sla.getSourceSystem());
					break;
				}
			}
		}
		return parametersResponse;
	}
	
	private static boolean isAuthorizationSet(SLA sla, String operation) {
		if (sla.getAuthorization() == null || sla.getAuthorization().isEmpty()) {
			return true;
		}
		for (Authorization a : sla.getAuthorization()) {
			if (a.getActive() && !a.getOperations().contains(operation)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Valida la operacion que viene en el mensaje de peticion contra los SLA
	 * devueltos en la consulta al registry
	 * 
	 * @param String
	 * @param SLD
	 * @param DataParameterWSRR
	 * @return retorna objeto DataParameterWSRR en la cual indica si el servicio
	 *         esta autorizado para invocar esa operacion
	 * */
	private static void validateRoutes(String operation, SLD sld, DataParameterWSRR parametersResponse, String consumer) {
		List<InternalRoute> routes = sld.getInternalRoutes();
		if (routes == null) return;
		
		boolean authorize = false;
		String requestQmgr = "";
		String queueResponse = "";
		String queueRequest = "";
		String requestQmgrCM = "";
		String queueResponseCM = "";
		String queueRequestCM = "";
		String queueAux = "";
		boolean caTosm = false;
		boolean caTocm = false;
		boolean cmTosm = false;
		
		for (InternalRoute route : routes) {
			authorize = routeAuthorization(route, operation, consumer);
			
			if (authorize) {
				if (route.getSourceComponent().getComponentType().equalsIgnoreCase(CommonConstant.CHANNELADAPTER.getValue())) {
					if (route.getInternalComponentEndpoint().getComponentType().equalsIgnoreCase(CommonConstant.SERVICEMEDIATOR.getValue())) {
						requestQmgr = route.getInternalComponentEndpoint().getRequestQMgr();
						queueResponse = route.getInternalComponentEndpoint().getResponseQName();
						queueRequest = route.getInternalComponentEndpoint().getRequestQName();

						caTosm = true;
					} else if (route.getInternalComponentEndpoint().getComponentType().equalsIgnoreCase(CommonConstant.CONSUMERMEDIATOR.getValue())) {
						caTocm = true;
					}
				} else if (route.getSourceComponent().getComponentType().equalsIgnoreCase(CommonConstant.CONSUMERMEDIATOR.getValue())
						&& route.getInternalComponentEndpoint().getComponentType().equalsIgnoreCase(CommonConstant.SERVICEMEDIATOR.getValue())) {
					queueAux = route.getSourceComponent().getRequestQName();

					requestQmgrCM = route.getInternalComponentEndpoint().getRequestQMgr();
					queueResponseCM = route.getInternalComponentEndpoint().getResponseQName();
					queueRequestCM = route.getInternalComponentEndpoint().getRequestQName();
				
					cmTosm = true;
				}
			}
		}

		if (caTocm && cmTosm) {
			parametersResponse.setQmanager(requestQmgrCM);
			parametersResponse.setQueueResponse(queueResponseCM);
			parametersResponse.setQueueCMSTI(queueRequestCM);
			parametersResponse.setQueueRequest(queueAux);
			authorize = true;
		} else if (caTosm) {
			parametersResponse.setQmanager(requestQmgr);
			parametersResponse.setQueueResponse(queueResponse);
			parametersResponse.setQueueRequest(queueRequest);
			authorize = true;
		} else {
			authorize = false;
		}

		parametersResponse.setOperationAuthorized(authorize);
	}
	
	private static boolean routeAuthorization(InternalRoute route, String operation, String consumer) {
		List<String> operations = Arrays.asList(route.getOperationId().split(";"));
		List<String> consumers = Arrays.asList(route.getConsumerId().split(";"));
		
		if (("*".equals(route.getOperationId()) || operations.contains(operation))
				&& ("*".equals(route.getConsumerId()) || consumers.contains(consumer))) {
			return true;
		}
		
		return false;
	}
	
	private static void validateSecurityConsumerTLS(SLD sld, String idWscaTls, DataParameterWSRR parametersResponse) {
		if (sld.getSecurityPoliciy() != null) {
			for (Policy policy : sld.getSecurityPoliciy()) {
				if (policy.getName().equals(CommonConstant.CA_TS_WS_TLS.getValue())) {
					parametersResponse.setHasTLSPolicy(true);
					for (Map.Entry<String, String> hashPolicy : policy.getSecurityPolicies().entrySet()) {
						if (hashPolicy.getKey().equals(CommonConstant.HASH_VALUE.getValue()) && hashPolicy.getValue().equals(idWscaTls)) {
							parametersResponse.setAutorizationConsumerTLS(true);
							return;
						}
					}
				}
			}
		}
	}
}
