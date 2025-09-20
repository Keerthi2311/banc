package core.common.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.broker.plugin.MbXMLNSC;

public class ILUtil {

	private static final String ELEM_ERROR_ADAPTER_V2 = "ErrorAdapterV2";
	private static final String ELEM_EXCEPTION_ID = "exceptionId";
	private static final String ELEM_INTERACTION_TYPE = "interactionType";
	private static final String ELEM_EXCEPTION_DETAIL = "exceptionDetail";
	private static final String ELEM_EXCEPTION_CATALOG = "exceptionCatalog";
	private static final String OPERATION_TYPE = "operationType";
	private static final String ELEM_EXCEPTION_LIST = "exceptionList";
	private static final String REQUESTDATA_PATH = "Header/requestData";
	private static final String DESTINATION_PATH = "Header/interactionData/externalDestination";
	private static final String DESTINATION_DATA_PATH = "Destination/MQ/DestinationData";
	private static final String JMS_DESTINATION_DATA_PATH = "Destination/JMSDestinationList/DestinationData";
	private static final String INTERACTION_SENDER_SYSID_PATH = "Header/interactionData/senderSystemId";
	private static final String INTERACTION_RECEIVER_SYSID_PATH = "Header/interactionData/receiverSystemId";
	private static final String INTERACTION_TIMESTAMP_PATH = "Header/interactionData/timestamp";
	private static final String INTERACTION_PATH = "Header/interactionData";
	private static final String SUCCESS_STATUS = "Success";
	private static final int MQRO_EXPIRATION_WITH_FULL_DATA = 14680064;
	private static final String MQ_DELIMITER = "@";
	private static final String MQ_URI_PREFIX = "wmq:/msg/queue/";
	private static final byte[] MQCI_NONE = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	private static final int MQIRIPREFIX = 15;
	private static final int NUMBER_TWO = 2;
	private static final int HEX_BASE = 16;

	public enum INTERACTION_TYPE {
		REQUEST, RESPONSE, ONEWAY
	};

	public enum DESTINATION_TYPE {
		MQ, HTTP, SOAP, JMS, LABEL
	};

	/**
	 * Crear un encabezado MQMD valido. Para ser utilizado cuando el flujo
	 * comienza con un nodo que no utiliza el transporte MQ
	 * 
	 * @param root
	 *            input parameter
	 * @return MbElement resulting MbElement with header
	 * @throws MbException
	 */
	public static MbElement createMQMD(MbMessage root) throws MbException {

		MbElement rootElement = root.getRootElement();
		MbElement refProperties = rootElement.getFirstChild();
		refProperties.createElementAfter("MQHMD");

		return rootElement;
	}

	/**
	 * Asigna las variables de ambiente para ser procesadas por el
	 * ErrorAdapterV2
	 * 
	 * @param interactionType
	 *            Interaction Type to be set
	 * @param exceptionCatalog
	 *            Exception Catalog to be set
	 * @param exceptionId
	 *            Exception Id to be set
	 * @param operationType
	 *            Operation Type to be set
	 * @param exceptionDetail
	 *            Exception Detail to be set
	 * @param environment
	 *            Environment to be set
	 * @param exceptionList
	 *            Exception List to be set
	 * @throws MbException
	 */
	
	public static void throwILSystemException(Exception ex, String interactionType, String exceptionId,
			String operationType, MbElement environment) throws MbException {

		MbElement refVariables = environment.getFirstElementByPath("Variables");
		if (refVariables == null) {
			refVariables = environment.createElementAsLastChild(MbElement.TYPE_NAME);
			refVariables.setName("Variables");
		}
		
		MbElement errorAdapterV2Elem = refVariables.createElementAsLastChild(MbElement.TYPE_NAME);
		errorAdapterV2Elem.setName(ELEM_ERROR_ADAPTER_V2);
		
		errorAdapterV2Elem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, ELEM_INTERACTION_TYPE, interactionType);
		errorAdapterV2Elem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, ELEM_EXCEPTION_CATALOG, "ILSEv2");
		errorAdapterV2Elem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, ELEM_EXCEPTION_ID, exceptionId);
		errorAdapterV2Elem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, OPERATION_TYPE, operationType);
		errorAdapterV2Elem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, ELEM_EXCEPTION_DETAIL, ex.getMessage());
		
		StackTraceElement origin = ex.getStackTrace()[0];
		throw new MbUserException(origin.getClassName(), origin.getMethodName(),
				ex.getMessage(), "", Arrays.toString(ex.getStackTrace()), null);
	}	


	public static void throwILSystemException(String interactionType,
			String exceptionCatalog, String exceptionId, String operationType,
			String exceptionDetail, MbElement environment,
			MbElement exceptionList) throws MbException {

		MbElement refVariables = environment.getFirstElementByPath("Variables");
		if (refVariables == null) {
			refVariables = environment.createElementAsLastChild(MbElement.TYPE_NAME);
			refVariables.setName("Variables");
		}
		
		
		MbElement errorAdapterV2Elem = refVariables.createElementAsLastChild(MbElement.TYPE_NAME);
		errorAdapterV2Elem.setName(ELEM_ERROR_ADAPTER_V2);
		
		errorAdapterV2Elem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, ELEM_INTERACTION_TYPE, interactionType);
		
		errorAdapterV2Elem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, ELEM_EXCEPTION_CATALOG, exceptionCatalog);
		
		errorAdapterV2Elem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, ELEM_EXCEPTION_ID, exceptionId);
		
		errorAdapterV2Elem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, OPERATION_TYPE, operationType);
		
		errorAdapterV2Elem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, ELEM_EXCEPTION_DETAIL, exceptionDetail);
		
		MbElement refExceptionList = errorAdapterV2Elem.createElementAsLastChild(MbElement.TYPE_NAME);
		refExceptionList.setName(ELEM_EXCEPTION_LIST);
		refExceptionList.copyElementTree(exceptionList);
		
		throw new MbUserException(errorAdapterV2Elem,
				"throwILSystemException()", "", "", "", null);
	}
	

	/**
	 * Procedure for logging severidad, detalle, tipoEvento, operationType and
	 * encoded message
	 * 
	 * @param severidad
	 *            String to be printed
	 * @param detalle
	 *            String to be printed
	 * @param tipoEvento
	 *            String to be printed
	 * @param operationType
	 *            String to be printed
	 * @param root
	 *            String to be printed
	 * @throws MbException
	 *             String to be printed
	 * 
	 */
	public static void log(String severidad, String detalle, String tipoEvento,
			String operationType, MbMessage root) throws MbException {

		// DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// Date dateobj = new Date();

		StringBuilder logger = new StringBuilder();
		// logger.append(df.format(dateobj));
		// logger.append(" ");
		logger.append(severidad);
		logger.append("-");
		logger.append(tipoEvento);
		logger.append("-");
		logger.append(operationType);
		logger.append(": ");
		logger.append(detalle);
		System.err.println(logger.toString());

		logger = new StringBuilder();
		MbMessage tmp = new MbMessage(root);
		MbElement xml = tmp.getRootElement().getLastChild();
		// We only want to log the Header
		MbElement body = xml.getFirstElementByPath("esbXML/Body");
		body.getFirstChild().detach();

		String encoded = encodeBase64(tmp.getBuffer());
		logger.append(encoded);
		System.err.println(logger.toString());
	}

	public static String encodeBase64(byte[] root) {
		return DatatypeConverter.printBase64Binary(root);
	}

	/**
	 * Procedimiento para generar un mensaje de respuesta de excepciin de
	 * negocio. Se envian como parimetros la cabecera esbXML de entrada y el
	 * mensaje de salida (elemento il:esbXML).
	 * 
	 * 
	 * @param esbXML
	 *            input tree
	 * @param beName
	 *            business exception name value
	 * @param code
	 *            code value
	 * @param description
	 *            description value
	 * @return MbElement value
	 * @throws MbException
	 * 
	 */
	public static MbElement createBuisnessExceptionResponse(MbElement esbXML,
			String beName, String code, String description) throws MbException {

		// Get BodyNamespace
		String nameSpace = esbXML.evaluateXPath(
				"string(//Header/requestData/destination/namespace)")
				.toString();

		// Modify operation
		String ops = esbXML.evaluateXPath(
				"string(//Header/requestData/destination/operation)")
				.toString();
		MbElement operation = esbXML
				.getFirstElementByPath("Header/requestData/destination/operation");
		operation.setValue(ops + "Response");

		// Create <responseData>
		MbElement requestData = esbXML
				.getFirstElementByPath("Header/requestData");
		System.err.println("requestData: " + requestData);
		MbElement responseData = requestData.createElementAfter(
				MbElement.TYPE_NAME, "responseData", null);
		MbElement providerData = responseData.createElementAsFirstChild(
				MbElement.TYPE_NAME, "providerData", null);
		MbElement status = providerData.createElementAfter(MbElement.TYPE_NAME,
				"status", null);
		MbElement statusCode = status.createElementAsFirstChild(
				MbElement.TYPE_NAME, "statusCode", null);
		statusCode.setValue("BusinessException");
		MbElement businessExceptionName = statusCode.createElementAfter(
				MbElement.TYPE_NAME, "businessExceptionName", null);
		businessExceptionName.setValue(beName);

		// Remove element Body
		esbXML.getLastChild().detach();

		// Create new elementBody
		MbElement bodyBE = esbXML.createElementAsLastChild(MbElement.TYPE_NAME,
				"Body", null);
		bodyBE = bodyBE.createElementAsLastChild(MbElement.TYPE_NAME, beName, null);
		bodyBE.setNamespace(nameSpace);
		
		MbElement genericException = bodyBE.createElementAsFirstChild(
				MbElement.TYPE_NAME, "genericException", null);

		// <genericException>
		MbElement codeBE = genericException.createElementAsFirstChild(
				MbElement.TYPE_NAME, "code", null);
		codeBE.setValue(code);

		// <code>12700040</code>
		MbElement descriptionBE = genericException.createElementAsLastChild(
				MbElement.TYPE_NAME, "description", null);
		descriptionBE.setValue(description);

		// <description>La Solicitud no se encuentra</description>
		return esbXML;
	}

	/**
	 * Procedimiento para actualizar el elemento interactionData en la cabecera
	 * esbXML-IL antes de generar un evento de informaciin. Se envia entre los
	 * parimetros de entrada la cabecera esbXML de salida (elemento il:esbXML).
	 * 
	 * Procedure to update the interactionData element in esbXML -IL header
	 * before generating an information event. It is sent from the input
	 * parameters the output esbXML (il element esbXML) header .
	 * 
	 * @param header
	 * @param interaction
	 * @param providerSystemId
	 * @throws MbException
	 */
	public static void updateInteractionData(MbElement header,
			INTERACTION_TYPE interaction, String providerSystemId)
			throws MbException {
		MbElement refInteractionData = header
				.getFirstElementByPath(INTERACTION_PATH);
		MbElement refSenderSysId = header
				.getFirstElementByPath(INTERACTION_SENDER_SYSID_PATH);
		MbElement refReceiverSyId = header
				.getFirstElementByPath(INTERACTION_RECEIVER_SYSID_PATH);
		MbElement refTimeStamp = header
				.getFirstElementByPath(INTERACTION_TIMESTAMP_PATH);

		// if(("REQUEST").equals(interaction.name()) ||
		// ("ONEWAY").equals(interaction.name())){
		if (interaction == INTERACTION_TYPE.REQUEST
				|| interaction == INTERACTION_TYPE.ONEWAY) {

			if (refSenderSysId != null) {
				refSenderSysId.detach();
			}

			if (refReceiverSyId != null) {
				MbElement receiverSystemId = refInteractionData
						.createElementAsLastChild(MbElement.TYPE_NAME_VALUE);
				receiverSystemId.setName("receiverSystemId");
				receiverSystemId.setValue(providerSystemId);
			} else {
				MbElement receiverSystemId = refInteractionData
						.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE);
				receiverSystemId.setName("receiverSystemId");
				receiverSystemId.setValue(providerSystemId);
			}

		}

		// if(("RESPONSE").equals(interaction.name())){
		if (interaction == INTERACTION_TYPE.RESPONSE) {

			if (refReceiverSyId != null) {
				refReceiverSyId.detach();
			}

			if (refSenderSysId != null) {
				/*MbElement senderSystemId = refInteractionData
						.createElementAsLastChild(MbElement.TYPE_NAME);
				senderSystemId.setName("senderSystemId");*/
				// senderSystemId.setValue(providerSystemId);
				refSenderSysId.setValue(providerSystemId);
			} else {
				MbElement senderSystemId = refTimeStamp.createElementBefore(MbElement.TYPE_NAME_VALUE);
				senderSystemId.setName("senderSystemId");
				senderSystemId.setValue(providerSystemId);
			}
		}

		String timeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
				.format(new Date());
		refTimeStamp.setName("timestamp");
		refTimeStamp.setValue(timeStamp);
	}

	/**
	 * Procedimiento para generar el elemento responseData, para respuesta
	 * exitosa, en la cabecera esbXML-IL. Se envia entre los parimetros de
	 * entrada la cabecera esbXML de salida (elemento il:esbXML).
	 * 
	 * @param header
	 * @throws MbException
	 */
	public static void setResponseData(MbElement header) throws MbException {
		// Actualizar el campo "operation"
		MbElement refRequestData = header
				.getFirstElementByPath(REQUESTDATA_PATH);
		MbElement operation = refRequestData.getFirstElementByPath("destination/operation");
		String o = operation.getValueAsString();
		operation.setValue(o + "Response");
		
		MbElement responseData = refRequestData.createElementAfter(
				MbElement.TYPE_NAME, "responseData", null);

		MbElement refProviderData = responseData.createElementAsFirstChild(
				MbElement.TYPE_NAME, "providerData", null);
		MbElement status = refProviderData.createElementAfter(
				MbElement.TYPE_NAME, "status", null);

		MbElement statusCode = status
				.createElementAsFirstChild(MbElement.TYPE_NAME);
		statusCode.setName("statusCode");
		statusCode.setValue(SUCCESS_STATUS);
	}

	/**
	 * Procedimiento para asignar el valor del externalDestination en la
	 * cabecera esbXML-IL. Se envia entre los parimetros de entrada la cabecera
	 * esbXML de salida (elemento il:esbXML).
	 * 
	 * @return String the external destination
	 * @throws MbException
	 */
	public static String getExternalDestination(MbElement header)
			throws MbException {

		String refExternal = header.getFirstElementByPath(DESTINATION_PATH)
				.getValueAsString();
		return refExternal;

	}

	/**
	 * Procedure for setting the expiry
	 * 
	 * @param outMQMD
	 * @param timeout
	 * @param timeoutQueue
	 * @throws MbException
	 */
	public static void expirySetUp(MbElement outMQMD, int timeout,
			String timeoutQueue) throws MbException {

		if (timeout > 0) {
			MbElement refMQExpiry = outMQMD.getFirstElementByPath("Expiry");
			refMQExpiry.setValue(timeout * 10);

			MbElement refMQReport = outMQMD.getFirstElementByPath("Report");
			refMQReport.setValue(MQRO_EXPIRATION_WITH_FULL_DATA);

			MbElement refMQReplyToQ = outMQMD.getFirstElementByPath("ReplyToQ");
			refMQReplyToQ.setValue(timeoutQueue);
		}
	}

	/**
	 * Procedimiento para aiadir un destino a la lista de destino. Se envia
	 * parimetros entre una salida de referencia para el medio ambiente local.
	 * Transporte parimetro debe ser uno de "MQ" , "HTTP ", "SOAP" , "JMS"
	 * "LABEL"
	 * 
	 * @param localEnv
	 * @param destino
	 * @param tipo
	 * @throws MbException
	 */
	@SuppressWarnings("unchecked")
	public static void addToDestinationList(MbElement localEnv, String destino,
			DESTINATION_TYPE tipo) throws MbException {
		// Removed the string transporte as this is not in document
		List<Object> destinationDataList = new ArrayList<>();
		if (tipo == DESTINATION_TYPE.MQ) {
			destinationDataList = (List<Object>) localEnv.evaluateXPath(DESTINATION_DATA_PATH);
			int i = destinationDataList.size();

			if (i == 0) {
				MbElement destinationCheck = localEnv
						.getFirstElementByPath("Destination");
				boolean hasDestination = false;
				if (destinationCheck != null)
					hasDestination = true;

				MbElement mqCheck = localEnv
						.getFirstElementByPath("Destination/MQ");
				boolean hasMQ = false;
				if (mqCheck != null)
					hasMQ = true;

				// If destination not yet existing, create
				if (!hasDestination) {
					MbElement destination = localEnv.createElementAsLastChild(
							MbElement.TYPE_NAME, "Destination", null);
					MbElement mq = destination.createElementAsLastChild(
							MbElement.TYPE_NAME, "MQ", null);
					MbElement destinationData = mq.createElementAsLastChild(
							MbElement.TYPE_NAME, "DestinationData", null);
					MbElement queueName = destinationData
							.createElementAsLastChild(MbElement.TYPE_NAME);
					queueName.setName("queueName");
					queueName.setValue(destino);
				} else if (!hasMQ && hasDestination) {
					MbElement mq = destinationCheck.createElementAsLastChild(
							MbElement.TYPE_NAME, "MQ", null);
					MbElement destinationData = mq.createElementAsLastChild(
							MbElement.TYPE_NAME, "DestinationData", null);
					MbElement queueName = destinationData
							.createElementAsLastChild(MbElement.TYPE_NAME);
					queueName.setName("queueName");
					queueName.setValue(destino);
				} else if (hasMQ && hasDestination) {
					MbElement destinationData = mqCheck
							.createElementAsLastChild(MbElement.TYPE_NAME,
									"DestinationData", null);
					MbElement queueName = destinationData
							.createElementAsLastChild(MbElement.TYPE_NAME);
					queueName.setName("queueName");
					queueName.setValue(destino);
				}
			} else {
				MbElement destinationData = localEnv
						.getFirstElementByPath(DESTINATION_DATA_PATH);
				MbElement destinationData1 = destinationData
						.createElementAfter(MbElement.TYPE_NAME,
								"DestinationData", null);
				MbElement queueName = destinationData1
						.createElementAsLastChild(MbElement.TYPE_NAME);
				queueName.setName("queueName");
				queueName.setValue(destino);
			}
		} else if (tipo == DESTINATION_TYPE.HTTP) {

			MbElement destinationCheck = localEnv
					.getFirstElementByPath("Destination");
			boolean hasDestination = false;
			if (destinationCheck != null)
				hasDestination = true;

			MbElement httpCheck = localEnv
					.getFirstElementByPath("Destination/HTTP");
			boolean hasHTTP = false;
			if (httpCheck != null)
				hasHTTP = true;

			if (!hasDestination) {
				MbElement destination = localEnv.createElementAsLastChild(
						MbElement.TYPE_NAME, "Destination", null);
				MbElement http = destination.createElementAsLastChild(
						MbElement.TYPE_NAME, "HTTP", null);
				MbElement requestURL = http
						.createElementAsLastChild(MbElement.TYPE_NAME);
				requestURL.setName("RequestURL");
				requestURL.setValue(destino);
			} else if (!hasHTTP && hasDestination) {
				MbElement http = destinationCheck.createElementAsLastChild(
						MbElement.TYPE_NAME, "HTTP", null);
				MbElement requestURL = http
						.createElementAsLastChild(MbElement.TYPE_NAME);
				requestURL.setName("RequestURL");
				requestURL.setValue(destino);
			} else if (hasHTTP && hasDestination) {
				MbElement requestURL = httpCheck
						.createElementAsLastChild(MbElement.TYPE_NAME);
				requestURL.setName("RequestURL");
				requestURL.setValue(destino);
			}
		} else if (tipo == DESTINATION_TYPE.SOAP) {
			MbElement HTTPCheck = localEnv
					.getFirstElementByPath("Destination/SOAP/Request/Transport/HTTP");
			boolean hasHTTPCheck = false;
			if (HTTPCheck != null)
				hasHTTPCheck = true;

			MbElement transportCheck = localEnv
					.getFirstElementByPath("Destination/SOAP/Request/Transport");
			boolean hasTransportCheck = false;
			if (transportCheck != null)
				hasTransportCheck = true;

			MbElement requestCheck = localEnv
					.getFirstElementByPath("Destination/SOAP/Request");
			boolean hasRequestCheck = false;
			if (requestCheck != null)
				hasRequestCheck = true;

			MbElement soapCheck = localEnv
					.getFirstElementByPath("Destination/SOAP");
			boolean hasSoapCheck = false;
			if (soapCheck != null)
				hasSoapCheck = true;

			MbElement destinationCheck = localEnv
					.getFirstElementByPath("Destination");
			boolean hasDestinationCheck = false;
			if (destinationCheck != null)
				hasDestinationCheck = true;

			if (hasHTTPCheck) {
				MbElement webServiceURL = HTTPCheck
						.createElementAsLastChild(MbElement.TYPE_NAME);
				webServiceURL.setName("WebServiceURL");
				webServiceURL.setValue(destino);
			} else if (hasTransportCheck) {
				MbElement http = transportCheck.createElementAsLastChild(
						MbElement.TYPE_NAME, "HTTP", null);
				MbElement webServiceURL = http
						.createElementAsLastChild(MbElement.TYPE_NAME);
				webServiceURL.setName("WebServiceURL");
				webServiceURL.setValue(destino);
			} else if (hasRequestCheck) {
				MbElement transport = requestCheck.createElementAsLastChild(
						MbElement.TYPE_NAME, "Transport", null);
				MbElement http = transport.createElementAsLastChild(
						MbElement.TYPE_NAME, "HTTP", null);
				MbElement webServiceURL = http
						.createElementAsLastChild(MbElement.TYPE_NAME);
				webServiceURL.setName("WebServiceURL");
				webServiceURL.setValue(destino);
			} else if (hasSoapCheck) {
				MbElement request = soapCheck.createElementAsLastChild(
						MbElement.TYPE_NAME, "Request", null);
				MbElement transport = request.createElementAsLastChild(
						MbElement.TYPE_NAME, "Transport", null);
				MbElement http = transport.createElementAsLastChild(
						MbElement.TYPE_NAME, "HTTP", null);
				MbElement webServiceURL = http
						.createElementAsLastChild(MbElement.TYPE_NAME);
				webServiceURL.setName("WebServiceURL");
				webServiceURL.setValue(destino);
			} else if (hasDestinationCheck) {
				MbElement soap = destinationCheck.createElementAsLastChild(
						MbElement.TYPE_NAME, "SOAP", null);
				MbElement request = soap.createElementAsLastChild(
						MbElement.TYPE_NAME, "Request", null);
				MbElement transport = request.createElementAsLastChild(
						MbElement.TYPE_NAME, "Transport", null);
				MbElement http = transport.createElementAsLastChild(
						MbElement.TYPE_NAME, "HTTP", null);
				MbElement webServiceURL = http
						.createElementAsLastChild(MbElement.TYPE_NAME);
				webServiceURL.setName("WebServiceURL");
				webServiceURL.setValue(destino);
			} else if (!hasDestinationCheck) {
				MbElement destination = localEnv.createElementAsLastChild(
						MbElement.TYPE_NAME, "Destination", null);
				MbElement soap = destination.createElementAsLastChild(
						MbElement.TYPE_NAME, "SOAP", null);
				MbElement request = soap.createElementAsLastChild(
						MbElement.TYPE_NAME, "Request", null);
				MbElement transport = request.createElementAsLastChild(
						MbElement.TYPE_NAME, "Transport", null);
				MbElement http = transport.createElementAsLastChild(
						MbElement.TYPE_NAME, "HTTP", null);
				MbElement webServiceURL = http
						.createElementAsLastChild(MbElement.TYPE_NAME);
				webServiceURL.setName("WebServiceURL");
				webServiceURL.setValue(destino);
			}
		} else if (tipo == DESTINATION_TYPE.LABEL) {

			MbElement destinationData = localEnv
					.getFirstElementByPath("Destination/RouterList/DestinationData");
			boolean hasDestinationDataCheck = false;
			if (destinationData != null)
				hasDestinationDataCheck = true;

			MbElement routerList = localEnv
					.getFirstElementByPath("Destination/RouterList");
			boolean hasRouterList = false;
			if (routerList != null)
				hasRouterList = true;

			MbElement refDestination = localEnv
					.getFirstElementByPath("Destination");
			boolean hasRefDestination = false;
			if (refDestination != null)
				hasRefDestination = true;

			if (hasDestinationDataCheck) {
				MbElement label = destinationData
						.createElementAsLastChild(MbElement.TYPE_NAME);
				label.setName("label");
				label.setValue(destino);
			} else if (hasRouterList) {
				MbElement destinationData1 = routerList
						.createElementAsFirstChild(MbElement.TYPE_NAME,
								"DestinationData", null);
				MbElement label = destinationData1
						.createElementAsLastChild(MbElement.TYPE_NAME);
				label.setName("label");
				label.setValue(destino);
			} else if (hasRefDestination) {
				MbElement refRouterList = refDestination
						.createElementAsFirstChild(MbElement.TYPE_NAME,
								"RouterList", null);
				MbElement destinationData1 = refRouterList
						.createElementAsFirstChild(MbElement.TYPE_NAME,
								"DestinationData", null);
				MbElement label = destinationData1
						.createElementAsLastChild(MbElement.TYPE_NAME);
				label.setName("label");
				label.setValue(destino);
			} else if (!hasDestinationDataCheck) {
				MbElement destination = localEnv.createElementAsFirstChild(
						MbElement.TYPE_NAME, "Destination", null);
				MbElement refRouterList = destination
						.createElementAsFirstChild(MbElement.TYPE_NAME,
								"RouterList", null);
				MbElement destinationData1 = refRouterList
						.createElementAsFirstChild(MbElement.TYPE_NAME,
								"DestinationData", null);
				MbElement label = destinationData1
						.createElementAsLastChild(MbElement.TYPE_NAME);
				label.setName("label");
				label.setValue(destino);
			}
		} else if (tipo == DESTINATION_TYPE.JMS) {
			destinationDataList = (List<Object>) localEnv.evaluateXPath(JMS_DESTINATION_DATA_PATH);
			int i = destinationDataList.size();

			if (i == 0) {

				MbElement destinationDataCheck = localEnv
						.getFirstElementByPath("Destination/JMSDestinationList/DestinationData");
				boolean hasDestinationDataCheck = false;
				if (destinationDataCheck != null)
					hasDestinationDataCheck = true;

				MbElement destinationListCheck = localEnv
						.getFirstElementByPath("Destination/JMSDestinationList");
				boolean hasDestinationList = false;
				if (destinationListCheck != null)
					hasDestinationList = true;

				MbElement destinationCheck = localEnv
						.getFirstElementByPath("Destination");
				boolean hasDestination = false;
				if (destinationCheck != null)
					hasDestination = true;

				if (hasDestinationList) {
					System.err.println("if (hasDestinationList): "
							+ hasDestinationList);
					MbElement destinationData = destinationListCheck
							.createElementAsLastChild(MbElement.TYPE_NAME);
					destinationData.setName("DestinationData");
					destinationData.setValue(destino);
				} else if (hasDestination) {
					System.err.println("else if(hasDestination): "
							+ hasDestination);
					MbElement jms = destinationCheck.createElementAsLastChild(
							MbElement.TYPE_NAME, "JMSDestinationList", null);
					MbElement destinationData = jms
							.createElementAsLastChild(MbElement.TYPE_NAME);
					destinationData.setName("DestinationData");
					destinationData.setValue(destino);
				} else if (hasDestinationDataCheck) {
					MbElement destinationData = destinationDataCheck
							.createElementAsLastChild(MbElement.TYPE_NAME);
					destinationData.setName("DestinationData");
					destinationData.setValue(destino);
				} else if (!hasDestination) {
					System.err.println("else if(hasDestination): "
							+ hasDestination);
					MbElement destination = localEnv.createElementAsLastChild(
							MbElement.TYPE_NAME, "Destination", null);
					MbElement jms = destination.createElementAsLastChild(
							MbElement.TYPE_NAME, "JMSDestinationList", null);
					MbElement destinationData = jms
							.createElementAsLastChild(MbElement.TYPE_NAME);
					destinationData.setName("DestinationData");
					destinationData.setValue(destino);
				}
			} else {
				MbElement destinationData = localEnv
						.getFirstElementByPath(JMS_DESTINATION_DATA_PATH);
				MbElement destinationData1 = destinationData
						.createElementAfter(MbElement.TYPE_NAME,
								"DestinationData", null);
				destinationData1.setName("DestinationData");
				destinationData1.setValue(destino);

			}
		}
	}

	/**
	 * Procedimiento que envia el mensaje actual ubicado en inx al ambiente, en
	 * la ubicaciin Environment.Variables.{FIELDNAME}.
	 * 
	 * @param inx
	 * @param env
	 * @return
	 * @throws MbException
	 */
	public static MbElement sendToEnv(MbElement inx, MbElement env)
			throws MbException {
		MbElement variables = env.getFirstElementByPath("Variables");
		if (variables == null) {
			variables = env.createElementAsFirstChild(MbElement.TYPE_NAME);
			variables.setName("Variables");
		}
		String fieldname = inx.getName();
		
		MbElement value = variables.createElementAsLastChild(MbXMLNSC.PARSER_NAME);
		value.setName(fieldname);
		value.copyElementTree(inx);
		
		return value;
	}

	/**
	 * Procedure that creates an XML element with a value given as CDATA. The
	 * new element remains as the last "child" of the parent node
	 * 
	 * @param parent
	 * @param fieldName
	 * @param fieldValue
	 * @throws MbException
	 */
	public static void setCDATAField(MbElement parent, String fieldName,
			String fieldValue) throws MbException {
		parent.createElementAsLastChild(MbXMLNSC.CDATA_FIELD, fieldName, fieldValue);
	}
	
	/**
	 * Copia las cabeceras (Properties, MQMD, etc) de un mensaje de entrada a un
	 * mensaje de salida.
	 * 
	 * @param inMessage
	 *            Representa el MbMessage del mensaje de entrada.
	 * @param outMessage
	 *            Representa el MbMessage del mensaje de salida.
	 * @throws MbException
	 */
	public static void copyMessageHeaders(MbMessage inMessage, MbMessage outMessage) throws MbException {
		MbElement outRoot = outMessage.getRootElement();
		MbElement header = inMessage.getRootElement().getFirstChild();

		while (header != null && header.getNextSibling() != null) {
			outRoot.addAsLastChild(header.copy());
			header = header.getNextSibling();
		}
	}
	
	
	
	/**
	 * Crea un elemento route al final de header.routingStack, que contiene como
	 * route.address la URL lógica que representa la pareja (queue, queueMgr), y
	 * adicionalmente contiene route.correlationId* con el valor de correlId
	 * 
	 * @author dasagude - Danny Agudelo
	 * @param header
	 *            MbElement que contiene el Header en mensajería IL
	 * @param queue
	 *            Cola del endpoint
	 * @param queueMgr
	 *            Manejador de cola del endpoint
	 * @param correlId
	 *            Identificacion de correlacion
	 * @throws MbException
	 */
	public static void pushRTQwithCorrel(MbElement header, String queue, String queueMgr, String correlId)
			throws MbException {

		if (!"".equals(queue)) {

			String uri = MQ_URI_PREFIX + queue + MQ_DELIMITER + queueMgr;

			MbElement routingStack = header.getFirstElementByPath("routingStack");

			if (routingStack == null) {
				
				MbElement messageContext = header.getFirstElementByPath("messageContext");
				
				if (messageContext != null) {
					
					routingStack = messageContext.createElementBefore(MbElement.TYPE_NAME, "routingStack", null);
				} else {
					
					routingStack = header.createElementAsLastChild(MbElement.TYPE_NAME, "routingStack", null);
				}
			}

			MbElement replyTo = routingStack.createElementAsLastChild(MbElement.TYPE_NAME, "route", null)
					.createElementAsLastChild(MbElement.TYPE_NAME, "ReplyTo", null);

			replyTo.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "address", uri);
			replyTo.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "correlationId", correlId);
		}
	}
	
	
	
	/**
	 * Remueve el elemento route al final de header.routingStack, previamente
	 * extrayendo de route.address la pareja (queue, queueMgr) a partir de la
	 * URL logica contenida allí, así mismo extrae la correlId como BLOB de
	 * route.correlationId y los asigna en la cabecera MQMD
	 * 
	 * @param header
	 *            MbElement que contiene el Header en mensajería IL
	 * @param mqmd
	 *            MbElement que contiene la cabecera MQMD
	 * @param deleteRoutingStack
	 *            Valor booleano que determina si se elimina el último elemento
	 *            hijo "route" del routingStack
	 * @throws MbException
	 */
	public static void popRTQWithCorrel(MbElement header, MbElement mqmd, boolean deleteRoutingStack)
			throws MbException {

		String[] reply;
		MbElement routingStack = header.getFirstElementByPath("routingStack");

		if (routingStack != null) {
			MbElement uri = routingStack.getLastChild().getFirstElementByPath("ReplyTo/address");
			MbElement cid = routingStack.getLastChild().getFirstElementByPath("ReplyTo/correlationId");

			if (cid != null) {

				mqmd.getFirstElementByPath("MsgId").setValue(hexStrToBytes(cid.getValueAsString()));
			} else {

				mqmd.getFirstElementByPath("MsgId").setValue(MQCI_NONE);
			}

			if (uri != null) {

				String uriString = uri.getValueAsString();
				reply = uriString.substring(MQIRIPREFIX, uriString.length()).split("@");
				mqmd.getFirstElementByPath("ReplyToQ").setValue(reply[0]);
				mqmd.getFirstElementByPath("ReplyToQMgr").setValue(reply[1]);

				if (deleteRoutingStack) {

					routingStack.getLastChild().delete();
				}
			}
		}
	}

	
	
	/**
	 * Convierte el valor Hexadecimal de un String a un arreglo de bytes
	 * 
	 * @param hexStr
	 *            Representa un Hexadecimal en un String
	 * @return Retorna un arreglo de bytes del Hexadecimal dado
	 */
	public static byte[] hexStrToBytes(String hexStr) {

		byte[] val = new byte[hexStr.length() / NUMBER_TWO];
		for (int i = 0; i < val.length; i++) {
			int index = i * NUMBER_TWO;
			int j = Integer.parseInt(hexStr.substring(index, index + NUMBER_TWO), HEX_BASE);
			val[i] = (byte) j;
		}

		return val;
	}

	
	

}
