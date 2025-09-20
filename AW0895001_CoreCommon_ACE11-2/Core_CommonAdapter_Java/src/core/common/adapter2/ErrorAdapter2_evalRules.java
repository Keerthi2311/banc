package core.common.adapter2;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbPolicy;
import com.ibm.broker.plugin.MbTimestamp;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.broker.plugin.MbXMLNSC;

import core.common.util2.ConfigurableServiceMultiton;

public class ErrorAdapter2_evalRules extends MbJavaComputeNode {

	private static final String WMB_EXCEPTION_DEFAULT = "2200";
	private static final String ELEM_EXCEPTION_EVENT = "ExceptionEvent";
	private static final String ELEM_BODY = "Body";
	private static final String ELEM_DETAIL = "detail";
	private static final String ELEM_FAULTACTOR = "faultactor";
	private static final String ELEM_OPERATION = "operation";
	private static final String ELEM_NAMESPACE = "namespace";
	private static final String ELEM_NAME = "name";
	private static final String ELEM_FAULTSTRING = "faultstring";
	private static final String SYSTEM_EXCEPTION = "SystemException";
	private static final String ELEM_STATUS_CODE = "statusCode";
	private static final String ELEM_PROVIDER_DATA = "providerData";
	private static final String ELEM_RESPONSE_DATA = "responseData";
	private static final String ELEM_REQUEST_DATA = "requestData";
	private static final String ENV_ELEM_INTERACTION_ID = "interactionId";
	private static final String ELEM_MESSAGE_ID = "messageId";
	private static final String ELEM_SYSTEM_ID = "systemId";
	private static final String ELEM_HEADER = "Header";
	private static final String SEVERITY_5 = "5";
	private static final String ELEM_FAULTCODE = "faultcode";
	private static final String ELEM_SYSTEM_EXCEPTION = "systemException";
	private static final String ELEM_STATUS = "status";
	private static final String SI_ISE_NON_RECOVERABLE_EXCEPTION = "Server.Integration.InternalServerError.NonRecoverableException";
	private static final String UNDEFINED_NAME = "UNDEFINED_NAME";
	private static final String UNDEFINED_NAMESPACE = "http://grupobancolombia.com/undefined";
	private static final String UNAUTHENTICATED = "UNAUTHENTICATED";
	private static final String KEY_NOT_FOUND_IN_CATALOG = "keyNotFoundInCatalog";
	private static final String UDP_RETHROW_FAILURE = "UDP_RETHROW_FAILURE";
	private static final String SHOWN_FAULTCODE = "shownFaultcode";
	private static final String SHOWN_ID = "shownId";
	private static final String PROPAGATE = "propagate";
	private static final String INTERNAL_FAULTCODE = "internalFaultcode";
	private static final String SEVERITY = "severity";
	private static final String UTF_8_ENCODING = "UTF-8";
	private static final String EXCEPTION_DETAIL = "exceptionDetail";
	private static final String OPERATION_TYPE = "operationType";
	private static final String INTERACTION_TYPE = "interactionType";
	private static final String EXCEPTION_ID = "exceptionId";
	private static final String EXCEPTION_CATALOG = "exceptionCatalog";
	private static final String ELEM_VARIABLES = "Variables";
	private static final String ELEM_EXCEPTION_MESSAGE = "ExceptionMessage";
	private static final String ELEM_ERROR_ADAPTER_V2 = "ErrorAdapterV2";
	private static final String CONFIGURABLE_SERVICE_ERROR_CODE = "2205";
	private static String EXCEPTION_MAPPING_PREFIX = "ExceptionMapping.";
	private static String WMB_EXCEPTION_TYPE_PREFIX = "WMBExceptionType.";
	private static String CATALOG_PC_TTYPE = "UDP_CATALOG_POLICY_TTYPE";
	private static String CATALOG_PC_PROJECT = "UDP_CATALOG_POLICY_PROJECT";
	private static String CATALOG_PC_NAME = "UDP_CATALOG_POLICY_NAME";
	private static String CATALOG_BIPMSGS = "BIPmsgs";
	private static String UDP_INTERACTION_TYPE = "UDP_INTERACTION_TYPE";
	private static String UDP_OPERATION_TYPE = "UDP_OPERATION_TYPE";
	private static final String NS_IL = "http://grupobancolombia.com/intf/IL/esbXML/V3.0";
	private static final String UNDEFINED_SYSTEM_ID = "UNDEFINED";

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

		// Se obtienen las variables del Environment
		MbMessage environmentMessage = outAssembly.getGlobalEnvironment();
		MbElement variables = environmentMessage.getRootElement()
				.getFirstElementByPath(ELEM_VARIABLES);

		if (variables == null) {
			variables = environmentMessage.getRootElement()
					.createElementAsLastChild(MbElement.TYPE_NAME);
			variables.setName(ELEM_VARIABLES);
		}

		MbElement errorAdapterV2 = null;
		if (variables != null) {
			errorAdapterV2 = variables
					.getFirstElementByPath(ELEM_ERROR_ADAPTER_V2);
		}

		String exceptionCatalog = null;
		String exceptionId = null;
		String interactionType = null;
		String operationType = null;
		String exceptionDetail = null;
		String internalFaultcode = null;
		String severity = "";
		String shownId = "";
		String shownFaultcode = "";
		String faultcode = "";
		MbElement exceptionList = null;
		boolean propagate = true;
		String keyNotFoundInCatalog = "false";
		boolean configServiceError = false;

		try {

			Map<String, String> innerExceptionDetail = extractInnerException(inAssembly
					.getExceptionList().getRootElement());

			try {
				exceptionCatalog = errorAdapterV2.getFirstElementByPath(
						EXCEPTION_CATALOG).getValueAsString();
			} catch (Exception e) {
				exceptionCatalog = innerExceptionDetail.get("Catalog");
			}

			try {
				exceptionId = errorAdapterV2
						.getFirstElementByPath(EXCEPTION_ID).getValueAsString();
			} catch (Exception e) {
				exceptionId = innerExceptionDetail.get("Number");
			}

			try {
				interactionType = errorAdapterV2.getFirstElementByPath(
						INTERACTION_TYPE).getValueAsString();
			} catch (Exception e) {
			}

			try {
				operationType = errorAdapterV2.getFirstElementByPath(
						OPERATION_TYPE).getValueAsString();
			} catch (Exception e) {
			}

			// BCSWF00082302, BCSWF00082306, BCSWF00082345 Start
			if (interactionType == null || interactionType.isEmpty()) {
				interactionType = get_UDP_Broker(UDP_INTERACTION_TYPE);
			}
			if (operationType == null || operationType.isEmpty()) {
				operationType = get_UDP_Broker(UDP_OPERATION_TYPE);
			}
			// BCSWF00082302, BCSWF00082306, BCSWF00082345 End

			try {
				exceptionDetail = errorAdapterV2.getFirstElementByPath(
						EXCEPTION_DETAIL).getValueAsString();
			} catch (Exception e) {
				exceptionDetail = innerExceptionDetail.get("ExceptionDetail");
				// Se reemplazan los caracteres especiales < y > en el caso
				// especial
				// de un XML parsing exception, para que se forme correctamente
				// el mensaje
				exceptionDetail = exceptionDetail.replaceAll("<", "&lt;");
				exceptionDetail = exceptionDetail.replaceAll(">", "&gt;");
			}

			try {
				exceptionList = (MbElement) errorAdapterV2
						.getFirstElementByPath("exceptionList").getValue();
			} catch (Exception e) {
				exceptionList = inAssembly.getExceptionList().getRootElement();
			}

			MbElement messageHeader = getMessageHeader(inMessage
					.getRootElement().getLastChild());

			/**
			 * Obtiene el Configurable Service con el catalogo de excepciones
			 */
			MbPolicy catalogCS = getCatalogCS();

			/**
			 * En caso de fallar el acceso al recurso
			 * UDCS_ERRORADAPTERV2_EQUIVALENCES
			 */
			if (catalogCS == null) {
				// Asignar exceptionId = 2205
				exceptionId = CONFIGURABLE_SERVICE_ERROR_CODE;
				configServiceError = true;
			} else {

				/**
				 * Si exceptionCatalog != ILSEv2, aplicar funcion
				 * EQUIVALENCE_WMB_EXCEPTION
				 */
				if (exceptionCatalog == null
						|| exceptionCatalog.equals(CATALOG_BIPMSGS)) {

					propagate = true;

					String exceptionType = innerExceptionDetail.get("Name");
					exceptionId = extractWMBExceptionId(catalogCS,
							interactionType, exceptionType);
				}

				Map<String, String> properties = extractILSEV2Values(catalogCS,
						exceptionId, interactionType, operationType);

				exceptionId = properties.get(EXCEPTION_ID);
				internalFaultcode = properties.get(INTERNAL_FAULTCODE);
				severity = properties.get(SEVERITY);
				propagate = !"false"
						.equalsIgnoreCase(properties.get(PROPAGATE));
				shownId = properties.get(SHOWN_ID);
				shownFaultcode = properties.get(SHOWN_FAULTCODE);

				// CCCC_CoreCommons2_CCE04 Start
				keyNotFoundInCatalog = properties.get(KEY_NOT_FOUND_IN_CATALOG);
				// CCCC_CoreCommons2_CCE04 End
			}

			if (exceptionId.equals(CONFIGURABLE_SERVICE_ERROR_CODE)) {

				internalFaultcode = SI_ISE_NON_RECOVERABLE_EXCEPTION;
				severity = SEVERITY_5;

				if (configServiceError
						|| "true".equalsIgnoreCase(keyNotFoundInCatalog)) {
					String configurableServiceName = get_UDP_Broker(CATALOG_PC_NAME); // UDCS_ERRORADAPTERV2_EQUIVALENCES
					exceptionDetail = "Error accediendo a recurso "
							+ configurableServiceName + " en la homologacion "
							+ "de la excepcion de un catalogo anterior: "
							+ faultcode + ". Detalle: " + exceptionDetail;
				}
				shownId = "500";
				shownFaultcode = "Server.InternalServerError";
			}

			// Se asignan las variables de ambiente
			MbElement exceptionEventElem = populateEnvironmentVariables(
					variables, exceptionId, exceptionDetail, internalFaultcode,
					severity, exceptionList, propagate);

			// Procesamiento del ExceptionMessage
			createExceptionMessage(messageHeader, variables, exceptionDetail,
					severity, shownId, shownFaultcode, exceptionEventElem);

			MbOutputTerminal out = getOutputTerminal("out");
			out.propagate(outAssembly);

			/**
			 * Finalmente, si la propiedad UDP_RETHROW_FAILURE es true, debera
			 * propagar el mensaje por la terminal de fallo una vez haya
			 * propagado el mensaje al evento o a la salida del flujo.
			 */
			if ("true".equalsIgnoreCase(get_UDP_Broker(UDP_RETHROW_FAILURE))) {
				MbOutputTerminal failure = getOutputTerminal("failure");
				failure.propagate(outAssembly);
			}
			// CCCC_CoreCommons2_CCE04 End
		} catch (MbException e1) {
			throw e1;
		} catch (Exception e) {
			MbUserException mbue = new MbUserException(this, "evaluate()",
					"ILSEv2", "2205", e.toString(), null);
			throw mbue;
		}

		finally {
			// clear the outMessage
			outMessage.clearMessage();
		}

	}

	private MbElement getMessageHeader(MbElement message) throws MbException {
		while (message != null) {
			if (message.getName().matches(ELEM_HEADER)) {
				return message;
			} else {
				try {
					MbElement element = getMessageHeader(message
							.getFirstChild());
					if (element != null
							&& element.getName().matches(ELEM_HEADER)) {
						message = element;
						break;
					} else {
						message = message.getNextSibling();
					}
				} catch (Exception ex) {
					MbUserException mbue = new MbUserException(this,
							"evaluate()", "ILSEv2", "2205", ex.toString(), null);
					throw mbue;
				}
			}
		}
		return message;
	}

	/**
	 * Crea el mensaje de excepcion en la ruta
	 * $Environment/Variables/ExceptionMessage para que se propague el mensaje
	 * original al ESBTracingManager
	 * 
	 * @param noNamespacesMessage
	 * @param variables
	 * @param exceptionDetail
	 * @param severity
	 * @param shownId
	 * @param shownFaultcode
	 * @param exceptionEventElem
	 * @throws MbException
	 */
	private void createExceptionMessage(MbElement messageHeader,
			MbElement variables, String exceptionDetail, String severity,
			String shownId, String shownFaultcode, MbElement exceptionEventElem)
			throws MbException {
		String interactionId;
		// aaltamir. Create the XML declaration parent node
		// CCCC_CoreCommons2_CCE04 Start
		/**
		 * Se crea el mensaje de excepcion en las variables de ambiente, para
		 * que el mensaje original se propague al ESBTM
		 */
		MbElement exceptionMessage = variables
				.createElementAsLastChild(MbElement.TYPE_NAME);
		exceptionMessage.setName(ELEM_EXCEPTION_MESSAGE);

		MbElement parserElement = exceptionMessage
				.createElementAsLastChild(MbXMLNSC.PARSER_NAME);

		// CCCC_CoreCommons2_CCE04 End
		MbElement xmlDecl = parserElement
				.createElementAsFirstChild(MbXMLNSC.XML_DECLARATION);
		xmlDecl.setName("XmlDeclaration");
		xmlDecl.createElementAsFirstChild(MbXMLNSC.ATTRIBUTE, "Version", "1.0");
		xmlDecl.createElementAsFirstChild(MbXMLNSC.ATTRIBUTE, "Encoding",
				"utf-8");

		MbElement esbXml = parserElement
				.createElementAsLastChild(MbElement.TYPE_NAME);
		esbXml.setName("esbXML");
		esbXml.setNamespace(NS_IL);

		esbXml = fixEsbXMLHeader(esbXml, messageHeader);
		esbXml = esbXml.getFirstElementByPath(ELEM_HEADER);

		if (esbXml != null) {
			try {
				if (esbXml.getFirstElementByPath(ELEM_SYSTEM_ID) != null
						&& esbXml.getFirstElementByPath(ELEM_MESSAGE_ID) != null) {
					interactionId = esbXml
							.getFirstElementByPath(ELEM_SYSTEM_ID)
							.getValueAsString()
							+ ":"
							+ esbXml.getFirstElementByPath(ELEM_MESSAGE_ID)
									.getValueAsString();
					exceptionEventElem.createElementAsLastChild(
							MbElement.TYPE_NAME_VALUE, ENV_ELEM_INTERACTION_ID,
							interactionId);
				}
			} catch (Exception ex) {
			}
		}

		MbElement requestData = esbXml.getFirstElementByPath(ELEM_REQUEST_DATA);

		MbElement responseData = esbXml
				.getFirstElementByPath(ELEM_RESPONSE_DATA);
		if (responseData == null) {
			responseData = requestData.createElementAfter(MbElement.TYPE_NAME);
			responseData.setName(ELEM_RESPONSE_DATA);
		}

		if (responseData.getFirstElementByPath(ELEM_PROVIDER_DATA) == null) {
			responseData.createElementAsFirstChild(MbElement.TYPE_NAME)
					.setName(ELEM_PROVIDER_DATA);
		}

		if (responseData.getFirstElementByPath(ELEM_STATUS) != null) {
			responseData.getFirstElementByPath(ELEM_STATUS).detach();
		}

		MbElement status = responseData
				.createElementAsLastChild(MbElement.TYPE_NAME);
		status.setName(ELEM_STATUS);

		status.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE,
				ELEM_STATUS_CODE, SYSTEM_EXCEPTION);

		MbElement systemException = status
				.createElementAsLastChild(MbElement.TYPE_NAME);
		systemException.setName(ELEM_SYSTEM_EXCEPTION);

		systemException.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,
				ELEM_FAULTCODE, shownFaultcode);

		systemException.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,
				ELEM_FAULTSTRING, shownId + " " + shownFaultcode);

		MbElement destination = requestData
				.getFirstElementByPath("destination");

		String destName = destination.getFirstElementByPath(ELEM_NAME)
				.getValueAsString();
		String destNamespace = destination
				.getFirstElementByPath(ELEM_NAMESPACE).getValueAsString();
		String destOper = destination.getFirstElementByPath(ELEM_OPERATION)
				.getValueAsString();

		systemException.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,
				ELEM_FAULTACTOR, destNamespace + "/" + destName + "/"
						+ destOper);

		systemException.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,
				ELEM_DETAIL, exceptionDetail);

		MbElement body = esbXml.getParent().createElementAsLastChild(
				MbElement.TYPE_NAME);
		body.setName(ELEM_BODY);
	}

	/**
	 * Asigna las variables que seran enviadas al evento de monitoreo
	 * 
	 * @param variables
	 * @param exceptionId
	 * @param exceptionDetail
	 * @param internalFaultcode
	 * @param severity
	 * @param exceptionList
	 * @param propagate
	 * @return
	 * @throws MbException
	 */
	private MbElement populateEnvironmentVariables(MbElement variables,
			String exceptionId, String exceptionDetail,
			String internalFaultcode, String severity, MbElement exceptionList,
			boolean propagate) throws MbException {
		MbElement exceptionEventElem = variables
				.getFirstElementByPath(ELEM_EXCEPTION_EVENT);
		if (exceptionEventElem == null) {
			exceptionEventElem = variables
					.createElementAsLastChild(MbElement.TYPE_NAME);
			exceptionEventElem.setName(ELEM_EXCEPTION_EVENT);

		}

		exceptionEventElem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,
				EXCEPTION_ID, exceptionId);
		exceptionEventElem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,
				INTERNAL_FAULTCODE, internalFaultcode);
		exceptionEventElem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,
				SEVERITY, severity);
		exceptionEventElem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,
				EXCEPTION_DETAIL, exceptionDetail);
		if (exceptionList != null) {
			MbElement excListEvent = exceptionEventElem
					.createElementAsLastChild(MbElement.TYPE_NAME);
			excListEvent.setName("exceptionList");
			excListEvent.addAsLastChild(exceptionList);
		}

		MbElement errorAdapterElem = variables
				.getFirstElementByPath(ELEM_ERROR_ADAPTER_V2);
		if (errorAdapterElem == null) {
			errorAdapterElem = variables
					.createElementAsLastChild(MbElement.TYPE_NAME);
			errorAdapterElem.setName(ELEM_ERROR_ADAPTER_V2);
		}
		errorAdapterElem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,
				PROPAGATE, propagate);
		return exceptionEventElem;
	}

	/**
	 * 
	 * @param content
	 * @return
	 * @throws MbException
	 * @throws UnsupportedEncodingException
	 */
	private MbElement parsePropertyContent(String content) throws MbException {
		MbMessage mbMsg = new MbMessage();
		MbElement msgXml = null;
		try {
			msgXml = mbMsg.getRootElement()
					.createElementAsLastChildFromBitstream(
							content.getBytes(UTF_8_ENCODING),
							MbXMLNSC.PARSER_NAME, null, null, null, 1208, 0, 0);
		} catch (UnsupportedEncodingException e) {
			MbUserException mbue = new MbUserException(this, "evaluate()",
					"ILSEv2", "2205", e.toString(), null);
			throw mbue;
		}
		return msgXml;
	}

	/**
	 * Obtiene los valores del servicio configurable asociados al exceptionId,
	 * interactionType y operationType
	 * 
	 * @param service
	 * @param exceptionId
	 * @param interactionType
	 * @param operationType
	 * @return
	 * @throws MbException
	 * @throws UnsupportedEncodingException
	 */
	private Map<String, String> extractILSEV2Values(MbPolicy service,
			String exceptionId, String interactionType, String operationType)
			throws MbException {
		Map<String, String> results = new HashMap<String, String>();

		String internalFaultCode = null;
		String shownId = null;
		String shownFaultcode = null;
		String severity = null;
		String propagate = "true";
		String keyNotFoundInCatalog = "false";

		if (operationType == null || operationType.isEmpty()) {
			operationType = "COMMAND";
		}

		String key = EXCEPTION_MAPPING_PREFIX + exceptionId;
		String content = service.getPropertyValueAsString(key);

		if (content == null) {
			exceptionId = CONFIGURABLE_SERVICE_ERROR_CODE;
			// CCCC_CoreCommons2_CCE04 Start
			keyNotFoundInCatalog = "true";
			// CCCC_CoreCommons2_CCE04 End
		} else {

			MbElement msgXml = parsePropertyContent(content);
			// Obtiene el exceptionId asignado en el mensaje de
			// configuraci�n
			MbElement element = msgXml.getFirstChild().getFirstElementByPath(
					INTERNAL_FAULTCODE);
			internalFaultCode = element.getValueAsString();

			element = msgXml.getFirstChild().getFirstElementByPath(SHOWN_ID);
			shownId = element.getValueAsString();

			element = msgXml.getFirstChild().getFirstElementByPath(
					SHOWN_FAULTCODE);
			shownFaultcode = element.getValueAsString();

			element = msgXml.getFirstChild().getFirstElementByPath(
					interactionType);

			if (element != null) {
				MbElement propagateElem = element
						.getFirstElementByPath(PROPAGATE);
				if (propagateElem != null) {
					propagate = propagateElem.getValueAsString();
				}
				element = element.getFirstElementByPath(operationType);
				if (element != null) {
					severity = element.getValueAsString();
				}
			}
		}
		results.put(EXCEPTION_ID, exceptionId);
		results.put(INTERNAL_FAULTCODE, internalFaultCode);
		results.put(SHOWN_ID, shownId);
		results.put(SHOWN_FAULTCODE, shownFaultcode);
		results.put(SEVERITY, severity);
		results.put(PROPAGATE, propagate);

		// CCCC_CoreCommons2_CCE04 Start
		results.put(KEY_NOT_FOUND_IN_CATALOG, keyNotFoundInCatalog);
		// CCCC_CoreCommons2_CCE04 End

		return results;
	}

	/**
	 * Obtiene el exceptionId del Servicio Configurable asociado al originType y
	 * al exceptionType
	 * 
	 * @param service
	 * @param originType
	 * @param exceptionType
	 * @return
	 * @throws MbException
	 * @throws UnsupportedEncodingException
	 */
	private String extractWMBExceptionId(MbPolicy service,
			String interactionType, String exceptionType) throws MbException {
		String exceptionId = null;
		// Obtiene el mensaje de configuracion asociado al faultCode
		String key = WMB_EXCEPTION_TYPE_PREFIX + exceptionType + "."
				+ interactionType;
		String content = service.getPropertyValueAsString(key);

		// Cuando no se encuentra en el servicio configurable
		if (content == null || content.isEmpty()) {
			// Mapeo por Defecto
			exceptionId = WMB_EXCEPTION_DEFAULT;
		} else {
			exceptionId = content;
		}
		return exceptionId;
	}

	/**
	 * Extrae la excepci�n m�s interna del ExceptionList
	 * 
	 * @param exList
	 * @return
	 */
	private Map<String, String> extractInnerException(MbElement exList) {
		Map<String, String> result = new HashMap<String, String>();
		MbElement exception = null;
		String number = null;
		String text = null;
		String exceptionDetail = null;
		String severity = null;
		String name = null;
		String type = null;
		String catalog = null;
		String inserts = "";
		try {
			exception = exList.getLastChild();
			MbElement element = null;
			while (exception != null
					&& exception.getName().contains("Exception")) {
				name = exception.getName();
				element = exception.getFirstElementByPath("Type");
				if (element != null) {
					type = element.getValueAsString();
				}
				element = exception.getFirstElementByPath("Number");
				if (element != null) {
					number = element.getValueAsString();
				}
				element = exception.getFirstElementByPath("Severity");
				if (element != null) {
					severity = element.getValueAsString();
				}
				element = exception.getFirstElementByPath("Text");
				if (element != null) {
					text = element.getValueAsString();
				}
				element = exception.getFirstElementByPath("Catalog");
				if (element != null) {
					catalog = element.getValueAsString();
				}

				inserts = "";
				MbElement insert = exception.getFirstElementByPath("Insert");
				while (insert != null && insert.getName().equals("Insert")) {
					MbElement typeElem = insert.getFirstElementByPath("Type");
					MbElement valueElem = insert.getFirstElementByPath("Text");
					inserts += "Type: ";
					if (typeElem != null) {
						inserts += typeElem.getValueAsString();
					}
					inserts += " Text: ";
					if (valueElem != null) {
						inserts += valueElem.getValueAsString();
					}
					inserts += ";";
					insert = insert.getNextSibling();
				}
				exceptionDetail = "[" + number + "][" + text + "][" + inserts
						+ "]";

				exception = exception.getLastChild();
			}
		} catch (MbException e) {
		}
		result.put("Name", name);
		result.put("Type", type);
		result.put("Number", number);
		result.put("Severity", severity);
		result.put("Catalog", catalog);
		result.put("ExceptionDetail", exceptionDetail);
		result.put("Inserts", inserts);
		return result;
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

	/**
	 * Crea un Elemento esbXML con los valores por defecto
	 * 
	 * @param header
	 * @return
	 * @throws MbException
	 */
	private MbElement fixEsbXMLHeader(MbElement outHeader, MbElement inHeader)
			throws MbException {

		outHeader = outHeader.createElementAsFirstChild(MbElement.TYPE_NAME);
		outHeader.setName(ELEM_HEADER);

		Calendar calendar = MbTimestamp.getInstance();
		MbTimestamp mbTimestamp = new MbTimestamp(
				calendar.get(MbTimestamp.YEAR),
				calendar.get(MbTimestamp.MONTH),
				calendar.get(MbTimestamp.DATE), calendar.get(MbTimestamp.HOUR),
				calendar.get(MbTimestamp.MINUTE),
				calendar.get(MbTimestamp.SECOND),
				calendar.get(MbTimestamp.MILLISECOND));

		MbElement systemId = outHeader
				.createElementAsLastChild(MbElement.TYPE_NAME);
		systemId.setName(ELEM_SYSTEM_ID);
		try {
			if (inHeader != null
					&& inHeader.getFirstElementByPath(ELEM_SYSTEM_ID) != null
					&& inHeader.getFirstElementByPath(ELEM_SYSTEM_ID)
							.getValueAsString() != null) {
				systemId.setValue(inHeader
						.getFirstElementByPath(ELEM_SYSTEM_ID)
						.getValueAsString());
			}
			// CC05 Start
			else {
				// valor por defecto para el systemId = UNDEFINED
				systemId.setValue(UNDEFINED_SYSTEM_ID);
			}
			// CC05 End
		} catch (Exception ex) {
		}

		MbElement messageId = outHeader
				.createElementAsLastChild(MbElement.TYPE_NAME);
		messageId.setName(ELEM_MESSAGE_ID);
		try {
			if (inHeader != null
					&& inHeader.getFirstElementByPath(ELEM_MESSAGE_ID) != null
					&& inHeader.getFirstElementByPath(ELEM_MESSAGE_ID)
							.getValueAsString() != null) {
				messageId.setValue(inHeader.getFirstElementByPath(
						ELEM_MESSAGE_ID).getValueAsString());
			}
			// CC05 Start
			else {
				DateFormat dateFormat = null;
				// BCSWF00082298 Start
				dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
				messageId.setValue(dateFormat.format(mbTimestamp.getTime()));
				// BCSWF00082298 End
			}
			// CC05 End
		} catch (Exception ex) {
		}

		// Se crea el elemento interactionData y se mueve el apuntador al mismo
		MbElement interactionData = outHeader
				.createElementAsLastChild(MbElement.TYPE_NAME);
		interactionData.setName("interactionData");
		// Se crea el apuntador de entrada en la ruta Header.interactionData
		MbElement inElement = null;
		try {
			inElement = inHeader != null ? inHeader
					.getFirstElementByPath("interactionData") : null;
		} catch (Exception ex) {
		}

		if (inElement != null
				&& inElement.getFirstElementByPath("senderSystemId") != null
				&& inElement.getFirstElementByPath("senderSystemId")
						.getValueAsString() != null) {
			MbElement senderSystemId = interactionData
					.createElementAsLastChild(MbElement.TYPE_NAME_VALUE);
			senderSystemId.setName("senderSystemId");
			senderSystemId.setValue(inElement.getFirstElementByPath(
					"senderSystemId").getValueAsString());
		}

		if (inElement != null
				&& inElement.getFirstElementByPath("receiverSystemId") != null
				&& inElement.getFirstElementByPath("receiverSystemId")
						.getValueAsString() != null) {
			MbElement receiverSystemId = interactionData
					.createElementAsLastChild(MbElement.TYPE_NAME_VALUE);
			receiverSystemId.setName("receiverSystemId");
			receiverSystemId.setValue(inElement.getFirstElementByPath(
					"receiverSystemId").getValueAsString());
		}

		MbElement timestamp = interactionData
				.createElementAsLastChild(MbElement.TYPE_NAME_VALUE);
		timestamp.setName("timestamp");

		// CC Armando R 02 Mayo 2016 Se corrige el formato de fecha pero solo
		// para el timestamp
		// es con T
		DateFormat dateFormat = null;
		dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		// 31 Enero 2018 Se cambia mbTimestamp.getTime() por new Date() ya que
		// la clase mbTimestamp no soporta el formato de horario GMT y por lo
		// cual se esta perdiendo la zona horaria (formato de 24 horas).
		timestamp.setValue(dateFormat.format(new Date()));
		// timestamp.setValue(dateFormat.format(mbTimestamp.getTime()));

		/*
		 * if (inElement != null && inElement.getFirstElementByPath("timestamp")
		 * != null && inElement.getFirstElementByPath("timestamp")
		 * .getValueAsString() != null) {
		 * timestamp.setValue(inElement.getFirstElementByPath("timestamp")
		 * .getValueAsString()); } else { timestamp.setValue(mbTimestamp); }
		 */

		if (inElement != null
				&& inElement.getFirstElementByPath("externalDestination") != null
				&& inElement.getFirstElementByPath("externalDestination")
						.getValueAsString() != null) {
			MbElement externalDest = interactionData
					.createElementAsLastChild(MbElement.TYPE_NAME_VALUE);
			externalDest.setName("externalDestination");
			externalDest.setValue(inElement.getFirstElementByPath(
					"externalDestination").getValueAsString());
		}

		if (inElement != null
				&& inElement.getFirstElementByPath("externalMsgId") != null
				&& inElement.getFirstElementByPath("externalMsgId")
						.getValueAsString() != null) {
			MbElement externalMsg = interactionData
					.createElementAsLastChild(MbElement.TYPE_NAME_VALUE);
			externalMsg.setName("externalMsgId");
			externalMsg.setValue(inElement.getFirstElementByPath(
					"externalMsgId").getValueAsString());
		}

		// Se mueve el apuntador del mensaje de entrada a requestData
		inElement = null;
		try {
			inElement = inHeader != null ? inHeader
					.getFirstElementByPath(ELEM_REQUEST_DATA) : null;
		} catch (Exception ex) {
		}

		// Se crea el requestData de salida y se mueve el apuntador
		MbElement outRequestData = outHeader
				.createElementAsLastChild(MbElement.TYPE_NAME);
		outRequestData.setName(ELEM_REQUEST_DATA);

		// Se crea el requestData.userId de salida y se mueve el apuntador
		MbElement userId = outRequestData
				.createElementAsLastChild(MbElement.TYPE_NAME);
		userId.setName("userId");

		// Se crea el requestData.userId.userName de salida y se mueve el
		// apuntador
		MbElement userName = userId
				.createElementAsLastChild(MbElement.TYPE_NAME_VALUE);
		userName.setName("userName");

		// Se mueve el apuntador del mensaje de entrada a requestData.userId
		try {
			inElement = inElement != null ? inElement
					.getFirstElementByPath("userId") : null;
		} catch (Exception ex) {
		}

		if (inElement != null
				&& inElement.getFirstElementByPath("userName") != null
				&& inElement.getFirstElementByPath("userName")
						.getValueAsString() != null) {
			userName.setValue(inElement.getFirstElementByPath("userName")
					.getValueAsString());
		}
		// CC05 Start
		else {
			userName.setValue(UNAUTHENTICATED);
		}
		// CC05 End

		// Si existe el userToken en el mensaje de entrada, se crea en el
		// mensaje de salida
		if (inElement != null
				&& inElement.getFirstElementByPath("userToken") != null
				&& inElement.getFirstElementByPath("userToken").getValue() != null) {
			// Se crea el requestData.userId.userToken de salida y se mueve el
			// apuntador
			MbElement userToken = userId
					.createElementAsLastChild(MbElement.TYPE_NAME_VALUE);
			userToken.setName("userToken");
			userToken.setValue(inElement.getFirstElementByPath("userToken")
					.getValue());
		}

		// Se mueve el apuntador de entrada al elemento Header.requestdata
		inElement = null;
		try {
			inElement = inHeader != null ? inHeader
					.getFirstElementByPath(ELEM_REQUEST_DATA) : null;
		} catch (Exception ex) {
		}

		// Se crea el destination. Al terminar este metodo el apuntador de
		// salida
		// queda en requestData
		createDestinationAndAssignValues(outRequestData, inElement);

		// Se crea las clasificaciones. Al terminar este metodo el apuntador de
		// salida
		// queda en requestData
		createClassificationsIfAny(inHeader, outRequestData);

		// Se crea el responseData. Al terminar este metodo el apuntador de
		// salida
		// queda en Header
		createResponseDataIfAny(inHeader, outRequestData);

		// Se crea el routingStack, si existe
		createRoutingStackIfAny(outHeader, inHeader);

		// Se crea el messageContext, si existe
		createMessageContextIfAny(outHeader, inHeader);

		// Se mueve el apuntador de salida a Header.requestData.destination
		outHeader = outHeader.getParent();

		return outHeader;
	}

	/**
	 * @param outHeader
	 * @param inHeader
	 * @throws MbException
	 */
	private void createMessageContextIfAny(MbElement outHeader,
			MbElement inHeader) throws MbException {
		MbElement inElement;
		if (inHeader != null
				&& inHeader.getFirstElementByPath("messageContext") != null) {
			MbElement outMessageContext = null;
			inElement = inHeader.getFirstElementByPath("messageContext");
			MbElement inProperty = inElement.getFirstChild();
			while (inProperty != null) {
				MbElement outProperty = null;
				if (inProperty.getFirstElementByPath("key") != null
						|| inProperty.getFirstElementByPath("value") != null) {
					// BCSWF00082234 Start
					if (outMessageContext == null) {
						outMessageContext = outHeader
								.createElementAsLastChild(MbElement.TYPE_NAME);
						outMessageContext.setName("messageContext");
					}
					// BCSWF00082234 End
					outProperty = outMessageContext
							.createElementAsLastChild(MbElement.TYPE_NAME);
					outProperty.setName("property");
					MbElement outKey = outProperty
							.createElementAsLastChild(MbElement.TYPE_NAME_VALUE);
					outKey.setName("key");
					MbElement outValue = outProperty
							.createElementAsLastChild(MbElement.TYPE_NAME_VALUE);
					outValue.setName("value");
					outKey.setValue(inProperty.getFirstElementByPath("key") != null ? inProperty
							.getFirstElementByPath("key").getValue() : "");
					outValue.setValue(inProperty.getFirstElementByPath("value") != null ? inProperty
							.getFirstElementByPath("value").getValue() : "");
				}
				inProperty = inProperty.getNextSibling();
			}

		}
	}

	/**
	 * Crea un elemento RoutingStack si es que existe en el mensaje de entrada
	 * 
	 * @param outHeader
	 * @param inHeader
	 * @throws MbException
	 */
	private void createRoutingStackIfAny(MbElement outHeader, MbElement inHeader)
			throws MbException {
		MbElement inElement = null;
		if (inHeader != null
				&& inHeader.getFirstElementByPath("routingStack") != null) {
			inElement = inHeader.getFirstElementByPath("routingStack");
			MbElement inRoute = inElement.getFirstChild();
			MbElement routingStack = null;

			while (inRoute != null) {
				MbElement outRoute = null;
				MbElement inReplyTo = inRoute.getFirstElementByPath("ReplyTo");
				if (inReplyTo != null) {
					MbElement outReplyTo = null;
					if (inReplyTo.getFirstElementByPath("address") != null) {
						if (routingStack == null) {
							routingStack = outHeader
									.createElementAsLastChild(MbElement.TYPE_NAME);
							routingStack.setName("routingStack");
						}
						outRoute = routingStack
								.createElementAsLastChild(MbElement.TYPE_NAME);
						outRoute.setName("route");
						outReplyTo = outRoute
								.createElementAsLastChild(MbElement.TYPE_NAME);
						outReplyTo.setName("ReplyTo");

						MbElement outAddress = outReplyTo
								.createElementAsLastChild(MbElement.TYPE_NAME_VALUE);
						outAddress.setName("address");
						outAddress.setValue(inReplyTo.getFirstElementByPath(
								"address").getValue());
					}
					if (outReplyTo != null
							&& inReplyTo.getFirstElementByPath("correlationId") != null) {
						MbElement correlationId = outReplyTo
								.createElementAsLastChild(MbElement.TYPE_NAME_VALUE);
						correlationId.setName("correlationId");
						correlationId.setValue(inReplyTo.getFirstElementByPath(
								"correlationId").getValue());
					}
					if (outReplyTo != null
							&& inReplyTo.getFirstElementByPath(ELEM_MESSAGE_ID) != null) {
						MbElement outMessageId = outReplyTo
								.createElementAsLastChild(MbElement.TYPE_NAME_VALUE);
						outMessageId.setName(ELEM_MESSAGE_ID);
						outMessageId.setValue(inReplyTo.getFirstElementByPath(
								ELEM_MESSAGE_ID).getValue());
					}

				}
				MbElement inFaultTo = inRoute.getFirstElementByPath("FaultTo");
				if (outRoute != null && inFaultTo != null) {
					MbElement outFaultTo = null;
					if (inFaultTo.getFirstElementByPath("address") != null) {
						outFaultTo = outRoute
								.createElementAsLastChild(MbElement.TYPE_NAME);
						outFaultTo.setName("FaultTo");

						MbElement outAddress = outFaultTo
								.createElementAsLastChild(MbElement.TYPE_NAME_VALUE);
						outAddress.setName("address");
						outAddress.setValue(inFaultTo.getFirstElementByPath(
								"address").getValue());
					}
					if (outFaultTo != null
							&& inFaultTo.getFirstElementByPath("correlationId") != null) {
						MbElement correlationId = outFaultTo
								.createElementAsLastChild(MbElement.TYPE_NAME_VALUE);
						correlationId.setName("correlationId");
						correlationId.setValue(inFaultTo.getFirstElementByPath(
								"correlationId").getValue());
					}
					if (outFaultTo != null
							&& inFaultTo.getFirstElementByPath(ELEM_MESSAGE_ID) != null) {
						MbElement outMessageId = outFaultTo
								.createElementAsLastChild(MbElement.TYPE_NAME_VALUE);
						outMessageId.setName(ELEM_MESSAGE_ID);
						outMessageId.setValue(inFaultTo.getFirstElementByPath(
								ELEM_MESSAGE_ID).getValue());
					}

				}
				inRoute = inRoute.getNextSibling();
			}

		}
	}

	/**
	 * @param inHeader
	 * @param element
	 * @throws MbException
	 */
	private void createResponseDataIfAny(MbElement inHeader,
			MbElement outRequestData) throws MbException {
		// Se mueve el apuntador de entrada al elemento Header.responseData
		MbElement inElement = null;
		try {
			inElement = inHeader != null ? inHeader
					.getFirstElementByPath(ELEM_RESPONSE_DATA) : null;
		} catch (Exception ex) {
		}

		createProviderDataIfAny(inElement, outRequestData);
		MbElement outResponseData = null;

		// si existe el elemento de entrada responseData, se crea en el header
		// de salida
		if (inElement != null) {
			// Se mueve el apuntador de entrada al elemento
			// Header.responseData.status
			inElement = inElement != null ? inElement
					.getFirstElementByPath(ELEM_STATUS) : null;
			MbElement status = null;
			if (inElement != null
					&& inElement.getFirstElementByPath(ELEM_STATUS_CODE) != null
					&& inElement.getFirstElementByPath(ELEM_STATUS_CODE)
							.getValue() != null) {
				outResponseData = checkResponseDataIfCreated(outRequestData,
						outResponseData);

				status = outResponseData
						.createElementAsLastChild(MbElement.TYPE_NAME);
				status.setName(ELEM_STATUS);
				MbElement statusCode = status
						.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE);
				statusCode.setName(ELEM_STATUS_CODE);
				statusCode.setValue(inElement.getFirstElementByPath(
						ELEM_STATUS_CODE).getValue());
			}
			if (status != null
					&& inElement != null
					&& inElement.getFirstElementByPath(ELEM_SYSTEM_EXCEPTION) != null) {
				MbElement inSystemException = inElement
						.getFirstElementByPath(ELEM_SYSTEM_EXCEPTION);
				MbElement systemException = status
						.createElementAsLastChild(MbElement.TYPE_NAME);
				systemException.setName(ELEM_SYSTEM_EXCEPTION);

				MbElement faultcode = systemException
						.createElementAsLastChild(MbElement.TYPE_NAME_VALUE);
				faultcode.setName(ELEM_FAULTCODE);
				if (inSystemException.getFirstElementByPath(ELEM_FAULTCODE) != null
						&& inSystemException.getFirstElementByPath(
								ELEM_FAULTCODE).getValue() != null) {
					faultcode.setValue(inSystemException.getFirstElementByPath(
							ELEM_FAULTCODE).getValue());
				}

				MbElement faultstring = systemException
						.createElementAsLastChild(MbElement.TYPE_NAME_VALUE);
				faultstring.setName(ELEM_FAULTSTRING);
				if (inSystemException.getFirstElementByPath(ELEM_FAULTSTRING) != null
						&& inSystemException.getFirstElementByPath(
								ELEM_FAULTSTRING).getValue() != null) {
					faultstring
							.setValue(inSystemException.getFirstElementByPath(
									ELEM_FAULTSTRING).getValue());
				}

				if (inSystemException.getFirstElementByPath(ELEM_FAULTACTOR) != null) {
					MbElement faultactor = systemException
							.createElementAsLastChild(MbElement.TYPE_NAME_VALUE);
					faultactor.setName(ELEM_FAULTACTOR);
					if (inSystemException
							.getFirstElementByPath(ELEM_FAULTACTOR).getValue() != null) {
						faultactor.setValue(inSystemException
								.getFirstElementByPath(ELEM_FAULTACTOR)
								.getValue());
					}
				}
				if (inSystemException.getFirstElementByPath(ELEM_DETAIL) != null) {
					MbElement detail = systemException
							.createElementAsLastChild(MbElement.TYPE_NAME_VALUE);
					detail.setName(ELEM_DETAIL);
					if (inSystemException.getFirstElementByPath(ELEM_DETAIL)
							.getValue() != null) {
						detail.setValue(inSystemException
								.getFirstElementByPath(ELEM_DETAIL).getValue());
					}
				}

			}

			if (inElement != null
					&& inElement.getFirstElementByPath("businessExceptionName") != null
					&& inElement.getFirstElementByPath("businessExceptionName")
							.getValue() != null) {
				MbElement businessException = status
						.createElementAsLastChild(MbElement.TYPE_NAME);
				businessException.setName("businessExceptionName");
				businessException.setValue(inElement.getFirstElementByPath(
						"businessExceptionName").getValue());
			}
			if (inElement != null
					&& inElement.getFirstElementByPath("providerExceptionName") != null
					&& inElement.getFirstElementByPath("providerExceptionName")
							.getValue() != null) {
				MbElement providerExceptionName = status
						.createElementAsLastChild(MbElement.TYPE_NAME);
				providerExceptionName.setName("providerExceptionName");
				providerExceptionName.setValue(inElement.getFirstElementByPath(
						"providerExceptionName").getValue());
			}

		}

	}

	/**
	 * @param inHeader
	 * @param element
	 * @throws MbException
	 */
	private void createProviderDataIfAny(MbElement inElement,
			MbElement requestData) throws MbException {
		MbElement responseData = null;
		// si existe el elemento de entrada responseData, se crea en el header
		// de salida
		if (inElement != null) {
			// Se mueve el apuntador de entrada al elemento
			// Header.responseData.providerData
			try {
				inElement = inElement != null ? inElement
						.getFirstElementByPath(ELEM_PROVIDER_DATA) : null;
			} catch (Exception ex) {
			}

			MbElement providerData = null;
			if (inElement != null
					&& inElement.getFirstElementByPath(ELEM_SYSTEM_ID) != null
					&& inElement.getFirstElementByPath(ELEM_SYSTEM_ID)
							.getValueAsString() != null) {
				responseData = checkResponseDataIfCreated(requestData,
						responseData);
				providerData = responseData
						.createElementAsFirstChild(MbElement.TYPE_NAME);
				providerData.setName(ELEM_PROVIDER_DATA);

				MbElement systemId = providerData
						.createElementAsFirstChild(MbElement.TYPE_NAME);
				systemId.setName(ELEM_SYSTEM_ID);
				systemId.setValue(inElement.getFirstElementByPath(
						ELEM_SYSTEM_ID).getValueAsString());
			}

			if (inElement != null
					&& inElement.getFirstElementByPath(ELEM_MESSAGE_ID) != null
					&& inElement.getFirstElementByPath(ELEM_MESSAGE_ID)
							.getValueAsString() != null) {
				responseData = checkResponseDataIfCreated(requestData,
						responseData);
				if (providerData == null) {
					providerData = responseData
							.createElementAsFirstChild(MbElement.TYPE_NAME);
					providerData.setName(ELEM_PROVIDER_DATA);
				}

				MbElement messageId = providerData
						.createElementAsLastChild(MbElement.TYPE_NAME);
				messageId.setName(ELEM_MESSAGE_ID);
				messageId.setValue(inElement.getFirstElementByPath(
						ELEM_MESSAGE_ID).getValueAsString());
			}

		}
	}

	/**
	 * @param element
	 * @param responseData
	 * @return
	 * @throws MbException
	 */
	private MbElement checkResponseDataIfCreated(MbElement requestData,
			MbElement responseData) throws MbException {
		if (requestData.getNextSibling() == null
				|| !requestData.getNextSibling().getName()
						.equals(ELEM_RESPONSE_DATA)) {
			responseData = requestData.createElementAfter(MbElement.TYPE_NAME);
			responseData.setName(ELEM_RESPONSE_DATA);
		} else if (responseData == null) {
			responseData = requestData.getNextSibling();
		}
		return responseData;
	}

	/**
	 * @param inHeader
	 * @param element
	 * @throws MbException
	 */
	private void createClassificationsIfAny(MbElement inHeader,
			MbElement outRequestData) throws MbException {
		MbElement inElement = null;
		// Si existe al menos un elemento classification, se agregan al header
		// de salida

		// Se mueve el apuntador de entrada a Header.requestData
		try {
			inElement = inHeader != null ? inHeader
					.getFirstElementByPath(ELEM_REQUEST_DATA) : null;
			inElement = inElement != null ? inElement
					.getFirstElementByPath("classifications") : null;
		} catch (Exception ex) {
		}
		// Se mueve el apuntador de entrada a Header.requestData.classifications
		if (inElement != null
				&& inElement.getFirstElementByPath("classification") != null) {
			MbElement classification = inElement
					.getFirstElementByPath("classification");
			MbElement outClassifs = outRequestData
					.createElementAsLastChild(MbElement.TYPE_NAME);
			outClassifs.setName("classifications");

			MbElement outClassif = outClassifs
					.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE);
			outClassif.setName("classification");
			outClassif.setValue(classification.getValue());

			classification = classification.getNextSibling();
			while (classification != null && classification.getValue() != null) {
				outClassif = outClassif
						.createElementAfter(MbElement.TYPE_NAME_VALUE);
				outClassif.setName("classification");
				outClassif.setValue(classification.getValue());
				classification = classification.getNextSibling();
			}
		}
	}

	/**
	 * @param element
	 * @param inElement
	 * @throws MbException
	 */
	private void createDestinationAndAssignValues(MbElement outRequestData,
			MbElement inElement) throws MbException {
		// Se mueve el apuntador de entrada al elemento requestdata.destination
		try {
			inElement = inElement != null ? inElement
					.getFirstElementByPath("destination") : null;
		} catch (Exception ex) {
		}

		// Se crea el requestData.destination de salida y se mueve el apuntador
		MbElement destination = outRequestData
				.createElementAsLastChild(MbElement.TYPE_NAME);
		destination.setName("destination");

		// Se crea el requestData.destination.name de salida y se mueve el
		// apuntador
		MbElement name = destination
				.createElementAsLastChild(MbElement.TYPE_NAME_VALUE);
		name.setName(ELEM_NAME);

		if (inElement != null
				&& inElement.getFirstElementByPath(ELEM_NAME) != null
				&& inElement.getFirstElementByPath(ELEM_NAME)
						.getValueAsString() != null) {
			name.setValue(inElement.getFirstElementByPath(ELEM_NAME)
					.getValueAsString());
		}
		// CC05 Start
		else {
			name.setValue(UNDEFINED_NAME);
		}
		// CC05 End

		// Se crea el requestData.destination.namespace de salida y se mueve el
		// apuntador
		MbElement namespace = destination
				.createElementAsLastChild(MbElement.TYPE_NAME_VALUE);
		namespace.setName(ELEM_NAMESPACE);

		if (inElement != null
				&& inElement.getFirstElementByPath(ELEM_NAMESPACE) != null
				&& inElement.getFirstElementByPath(ELEM_NAMESPACE)
						.getValueAsString() != null) {
			namespace.setValue(inElement.getFirstElementByPath(ELEM_NAMESPACE)
					.getValueAsString());
		}
		// CC05 Start
		else {
			namespace.setValue(UNDEFINED_NAMESPACE);
		}
		// CC05 End

		// Se crea el requestData.destination.operation de salida y se mueve el
		// apuntador
		MbElement operation = destination
				.createElementAsLastChild(MbElement.TYPE_NAME_VALUE);
		operation.setName(ELEM_OPERATION);

		if (inElement != null
				&& inElement.getFirstElementByPath(ELEM_OPERATION) != null
				&& inElement.getFirstElementByPath(ELEM_OPERATION)
						.getValueAsString() != null) {
			operation.setValue(inElement.getFirstElementByPath(ELEM_OPERATION)
					.getValueAsString());
		}
		// CC05 Start
		else {
			operation.setValue(UNAUTHENTICATED);
		}
		// CC05 End
	}

}
