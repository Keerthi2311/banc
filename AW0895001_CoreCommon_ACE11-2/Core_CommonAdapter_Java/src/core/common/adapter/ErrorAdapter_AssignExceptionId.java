package core.common.adapter;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbPolicy;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.broker.plugin.MbXMLNSC;
import com.ibm.broker.plugin.MbXPath;
// Add MbNamespaceBindings class, MbXPath.addNamespacePrefix Deprecated - Fernando Mondragón Amaya 06/02/2017
import com.ibm.broker.plugin.MbNamespaceBindings;

import core.common.util2.ConfigurableServiceMultiton;

/**
 * 
 * @author Edgar González
 */
public class ErrorAdapter_AssignExceptionId extends MbJavaComputeNode {

	private static final String SHOWN_FAULT_CODES = "ShownFaultCodes";

	private static final String ELEM_ERROR_ADAPTER_V2 = "ErrorAdapterV2";

	private static final String INTERACTION_TYPE_RESPONSE = "RESPONSE";

	private static final String UTF_8_ENCODING = "UTF-8";

	private static final String EXCEPTION_ID = "exceptionId";

	private static final String CONFIGURABLE_SERVICE_ERROR_CODE = "2205";

	private static String COMPABILITY_EXCEPTION_PREFIX = "CompatibilityException.";

	private static String CATALOG_PC_TTYPE = "UDP_CATALOG_POLICY_TTYPE";
	private static String CATALOG_PC_PROJECT = "UDP_CATALOG_POLICY_PROJECT";
	private static String CATALOG_PC_NAME = "UDP_CATALOG_POLICY_NAME";

	private static String CATALOG_ILSEV2 = "ILSEv2";

	private static String PATH_SYSTEMEX = "/il:esbXML/Header/responseData/status/systemException";
	private static final String NS_IL = "http://grupobancolombia.com/intf/IL/esbXML/V3.0";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.broker.javacompute.MbJavaComputeNode#evaluate(com.ibm.broker.
	 * plugin.MbMessageAssembly)
	 */
	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbMessage inMessage = inAssembly.getMessage();

		// create new message
		MbMessage outMessage = new MbMessage(inMessage);
		MbMessageAssembly outAssembly = new MbMessageAssembly(inAssembly,
				outMessage);

		try {

			// Obtiene el elemento systemException del mensaje por medio de
			// Xpath
			MbElement systemException = getElementFromXpath(inMessage,
					PATH_SYSTEMEX, "il", NS_IL);

			String faultCode = null;
			String faultDetail = null;
			String exceptionId = null;
			String interactionType = null;

			// CCCC_CoreCommons2_CCE04 Start
			boolean faultMessageAlreadyProcessed = false;
			// CCCC_CoreCommons2_CCE04 End

			if (systemException != null) {
				faultCode = (String) systemException.getFirstElementByPath(
						"faultcode").getValue();
				if (systemException.getFirstElementByPath("detail") != null) {
					faultDetail = systemException.getFirstElementByPath(
							"detail").getValueAsString();
				}
			}

			// Obtiene el Configurable Service con el catalogo de excepciones
			MbPolicy catalogCS = getCatalogCS();

			// En caso de fallar el acceso al recurso
			// UDCS_ERRORADAPTERV2_EQUIVALENCES
			if (catalogCS == null) {
				// Asignar exceptionId = 2205 (correspondiente a
				// NonRecoverableException)
				// e interactionType="RESPONSE".
				exceptionId = CONFIGURABLE_SERVICE_ERROR_CODE;
			} else {

				// Obtiene el mensaje de configuracion asociado al faultCode
				MbPolicy props = catalogCS;
				String key = COMPABILITY_EXCEPTION_PREFIX + faultCode;
				String content = props.getPropertyValueAsString(key);

				if (content == null) {
					// CCCC_CoreCommons2_CCE04 Start
					/**
					 * En caso de no encontrarse la homologacion de este codigo,
					 * se procede a validar si este mensaje tiene un faultcode
					 * ya procesado por otro errorAdapter aplicando la funcion
					 * VALIDATE_SHOWN_FAULTCODE. Si ya ha sido procesado, el
					 * mensaje se propaga directamente a la salida.
					 */
					faultMessageAlreadyProcessed = validateShownFaultCode(
							faultCode, props);
					// CCCC_CoreCommons2_CCE04 End
					if (!faultMessageAlreadyProcessed) {
						// Asignar exceptionId = 2205 (correspondiente a
						// NonRecoverableException)
						// e interactionType="RESPONSE".
						exceptionId = CONFIGURABLE_SERVICE_ERROR_CODE;
					}
				} else {
					MbElement msgXml = parsePropertyContent(content);

					// Obtiene el exceptionId asignado en el mensaje de
					// configuración
					MbElement exceptionIdAttr = msgXml.getFirstChild()
							.getFirstElementByPath(EXCEPTION_ID);
					exceptionId = exceptionIdAttr.getValueAsString();

					// Obtiene el interaction asignado en el mensaje de
					// configuración
					MbElement interactionAttr = msgXml.getFirstChild()
							.getFirstElementByPath("interaction");
					interactionType = interactionAttr.getValueAsString();
				}
			}
			// Si el mensaje aun no ha sido procesado
			if (!faultMessageAlreadyProcessed) {
				// Si ocurrio un error en el procesamiento del Servicio
				// Configurable
				if (CONFIGURABLE_SERVICE_ERROR_CODE.equals(exceptionId)) {
					interactionType = INTERACTION_TYPE_RESPONSE;
					faultDetail = "Error accediendo a recurso UDCS_ERRORADAPTERV2_EQUIVALENCES en la homologación "
							+ "de la excepción de un catálogo anterior: "
							+ faultCode + ". Detalle: " + faultDetail;
				}
				// Se asignan las variables de ambiente
				MbMessage environmentMessage = outAssembly
						.getGlobalEnvironment();
				populateEnvironmentVariables(faultDetail, exceptionId,
						interactionType, environmentMessage);
				MbOutputTerminal out = getOutputTerminal("out");
				out.propagate(outAssembly);
			}
			// Si el mensaje ya fue procesado
			else {
				// CCCC_CoreCommons2_CCE04 Start

				// Se propaga el mensaje directamente a la salida
				MbOutputTerminal alternate = getOutputTerminal("alternate");
				alternate.propagate(outAssembly);

				// CCCC_CoreCommons2_CCE04 End
			}

		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		finally {
			// clear the outMessage
			outMessage.clearMessage();
		}
	}

	/**
	 * Valida si el faultCode recibido se encuentra en la lista de
	 * shownFaultCodes configurados en el servicio configurable
	 * 
	 * @param faultCode
	 * @param props
	 * @return
	 * @throws MbException
	 * @throws UnsupportedEncodingException
	 */
	private boolean validateShownFaultCode(String faultCode, MbPolicy props)
			throws MbException, UnsupportedEncodingException {
		String content;
		boolean faultMessageAlreadyProcessed = false;
		content = props.getPropertyValueAsString(SHOWN_FAULT_CODES);
		if (content != null) {
			MbElement msgXml = parsePropertyContent(content);
			MbElement shownFaultCodes = msgXml.getFirstChild();
			MbElement shownFaultCode = shownFaultCodes.getFirstChild();
			while (shownFaultCode != null) {
				if (faultCode.equals(shownFaultCode.getValueAsString())) {
					faultMessageAlreadyProcessed = true;
					break;
				}
				shownFaultCode = shownFaultCode.getNextSibling();
			}
		}
		return faultMessageAlreadyProcessed;
	}

	/**
	 * Asigna las variables de ambiente para ser procesadas por el
	 * ErrorAdapterV2
	 * 
	 * @param faultDetail
	 * @param exceptionId
	 * @param interactionType
	 * @param environmentMessage
	 * @throws MbException
	 */
	private void populateEnvironmentVariables(String faultDetail,
			String exceptionId, String interactionType,
			MbMessage environmentMessage) throws MbException {
		MbElement variables = environmentMessage.getRootElement()
				.getFirstElementByPath("Variables");
		if (variables == null) {
			variables = environmentMessage.getRootElement()
					.createElementAsLastChild(MbElement.TYPE_NAME);
			variables.setName("Variables");
		}

		MbElement errorAdapterV2Elem = variables
				.createElementAsLastChild(MbElement.TYPE_NAME);
		errorAdapterV2Elem.setName(ELEM_ERROR_ADAPTER_V2);

		errorAdapterV2Elem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,
				EXCEPTION_ID, exceptionId);
		errorAdapterV2Elem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,
				"interactionType", interactionType);

		errorAdapterV2Elem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,
				"exceptionDetail", faultDetail);

		errorAdapterV2Elem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,
				"exceptionCatalog", CATALOG_ILSEV2);
	}

	/**
	 * 
	 * @param content
	 * @return
	 * @throws MbException
	 * @throws UnsupportedEncodingException
	 */
	private MbElement parsePropertyContent(String content) throws MbException,
			UnsupportedEncodingException {
		MbMessage mbMsg = new MbMessage();
		MbElement msgXml = null;
		msgXml = mbMsg.getRootElement().createElementAsLastChildFromBitstream(
				content.getBytes(UTF_8_ENCODING), MbXMLNSC.PARSER_NAME, null,
				null, null, 1208, 0, 0);
		return msgXml;
	}

	/**
	 * Obtiene el Servicio Configurable
	 * 
	 * @return
	 */
	private MbPolicy getCatalogCS() {
		MbPolicy myUDCS = null;
		try {
			String policyType = get_UDP_Broker(CATALOG_PC_TTYPE);
			String policyProject = get_UDP_Broker(CATALOG_PC_PROJECT);
			String policyName = get_UDP_Broker(CATALOG_PC_NAME);
			myUDCS = ConfigurableServiceMultiton.getPolicy(policyType,
					policyProject, policyName);
		} catch (Exception e) {
		}
		return myUDCS;
	}


/*
	private MbPolicy getCatalogCS() {
		MbPolicy myUDCS = null;
		try {
			String policyType = get_UDP_Broker(CATALOG_PC_TTYPE);
			String policyProject = get_UDP_Broker(CATALOG_PC_PROJECT);
			String policyName = get_UDP_Broker(CATALOG_PC_NAME);
			myUDCS = ConfigurableServiceMultiton.getPolicy(policyType,
					policyProject, policyName);
		} catch (Exception e) {
		}
		return myUDCS;
	}
 */
	
	
	/**
	 * Obtiene un elemento del mensaje a partir de una expresión Xpath
	 * 
	 * @param message
	 * @param xpath
	 * @param prefix
	 * @param ns
	 * @return
	 * @throws MbException
	 */
	@SuppressWarnings({ "unchecked" }) //Fernando Mondragón Amaya 06/02/2017
	private MbElement getElementFromXpath(MbMessage message, String xpath, String prefix, String ns) throws MbException {
		MbXPath header = new MbXPath(xpath);
		MbNamespaceBindings nspace = new MbNamespaceBindings();
		//** header.addNamespacePrefix(prefix, ns);
		nspace.addBinding(prefix, ns);
		header.setNamespaceBindings(nspace);
		List<MbElement> result = null;
		MbElement firstEle = null;
		try {
			result = (List<MbElement>) message.evaluateXPath(header);
			firstEle = result.get(0);
		} catch (Exception e) {
			MbUserException mbue = new MbUserException(this,"getElementFromXpath()", "ILSEv2", "2205", e.toString(),
					null);
			throw mbue;
		}
		return firstEle;
	}

	/**
	 * Obtiene el valor de una propiedad definida por el usuario (UDP)
	 * 
	 * @param s_udp
	 * @return
	 * @throws MbException
	 */
	public String get_UDP_Broker(String s_udp) throws MbException {
		String ls_udp_valor = "";
		Object obj_udp = null;
		obj_udp = getUserDefinedAttribute(s_udp);
		if (obj_udp != null) {
			ls_udp_valor = obj_udp.toString();
		}
		return ls_udp_valor;
	}

}
