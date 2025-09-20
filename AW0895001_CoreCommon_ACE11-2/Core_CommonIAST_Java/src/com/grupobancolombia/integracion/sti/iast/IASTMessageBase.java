package com.grupobancolombia.integracion.sti.iast;

import com.grupobancolombia.integracion.sti.rule.ILtoIASTTranformationRules;
import com.grupobancolombia.integracion.sti.rule.ILtoIASTTranformationRules.AlignOption;
import com.grupobancolombia.integracion.sti.rule.ILtoIASTTranformationRules.RefillOption;

public class IASTMessageBase {

	/**
	 * Tamaño del campo ID. Servidor
	 */
	public final int SERVER_ID_LENGTH = 3;

	/**
	 * Tamaño del campo ID. Aplicativo Cliente
	 */
	public final int CUSTOMER_APP_ID_LENGTH = 3;

	/**
	 * Tamaño del campo Identificador de Mensaje
	 */
	public final int MESSAGE_ID_LENGTH = 16;

	/**
	 * Tamaño del campo Nombre DataQ de Respuestas
	 */
	public final int DATAQ_RESP_NAME_LENGTH = 10;

	/**
	 * Tamaño del campo Librería DataQ de Respuestas
	 */
	public final int DATAQ_RESP_LIBRARY_LENGTH = 10;

	/**
	 * Tamaño del campo Orientacion del Mensaje
	 */
	public final int MESSAGE_DIRECTION_LENGTH = 1;

	/**
	 * Tamaño del campo Temporizador del Mensaje
	 */
	public final int MESSAGE_TIMER_LENGTH = 2;

	/**
	 * Tamaño del campo Hora Envío del Mensaje
	 */
	public final int MESSAGE_REQUEST_HOUR_LENGTH = 8;

	/**
	 * Tamaño del campo Hora Recibo Respuesta Servidor
	 */
	public final int MESSAGE_RESPONSE_HOUR_LENGTH = 8;

	/**
	 * Tamaño del campo Hora Llegada al Servidor
	 */
	public final int SERVER_REQUEST_ARRIVE_HOUR_LENGTH = 8;

	/**
	 * Tamaño del campo Hora Respuesta Servidor
	 */
	public final int SERVER_RESPONSE_HOUR_LENGTH = 8;

	/**
	 * Tamaño del campo Longitud Carga Útil Msg
	 */
	public final int MESSAGE_PAYLOAD_LENGTH = 5;
	/**
	 * Tamaño del campo Total Segmentos Msg
	 */
	public final int MESSAGE_TOTAL_SEGMENTS_LENGTH = 2;

	/**
	 * Tamaño del campo Posición Segmento Msg
	 */
	public final int MESSAGE_SEGMENT_POSITION_LENGTH = 5;

	/**
	 * Tamaño del campo Filler. Para uso futuro
	 */
	public final int FILLER_DATA_CONTROL_LENGTH = 1;

	/**
	 * Tamaño del campo Tipo de Mensaje
	 */
	public final int MESSAGE_TYPE_LENGTH = 2;

	/**
	 * Tamaño del campo ID. Transacción
	 */
	public final int TRANSACTION_ID_LENGTH = 4;

	/**
	 * Tamaño del campo Secuencia de Transacción
	 */
	public final int TRANSACTION_SEQUENCE_LENGTH = 20;

	/**
	 * Tamaño del campo Indicador Requiere Respuesta
	 */
	public final int RESPONSE_REQUIRED_LENGTH = 1;

	/**
	 * Tamaño del campo Código Respuesta
	 */
	public final int RESPONSE_CODE_LENGTH = 4;

	/**
	 * Tamaño del campo Descripción Respuesta
	 */
	public final int RESPONSE_DESCRIPTION_LENGTH = 50;

	/**
	 * Tamaño del campo Filler. Para uso futuro
	 */
	public final int FILLER_DATA_HEADER_LENGTH = 9;

	/**
	 * ID. Servidor
	 */
	private String serverId;

	/**
	 * ID. Aplicativo Cliente
	 */
	private String customerAppId;

	/**
	 * Identificador de Mensaje
	 */
	private String messageId;

	/**
	 * Nombre DataQ de Respuestas
	 */
	private String dataQRespName;

	/**
	 * Librería DataQ de Respuestas
	 */
	private String dataQRespLibrary;

	/**
	 * Orientacion del Mensaje
	 */
	private String messageDirection = "1";

	/**
	 * Temporizador del Mensaje
	 */
	private String messageTimer;

	/**
	 * Hora Envío del Mensaje
	 */
	private String messageRequestHour;

	/**
	 * Hora Recibo Respuesta Servidor
	 */
	private String messageResponseHour;

	/**
	 * Hora Llegada al Servidor
	 */
	private String serverRequestArriveHour;

	/**
	 * Hora Respuesta Servidor
	 */
	private String serverResponseHour;

	/**
	 * Longitud Carga Útil Msg
	 */
	private String messagePayLoadLength;

	/**
	 * Total Segmentos Msg
	 */
	private String messageTotalSegments = "01";

	/**
	 * Posición Segmento Msg
	 */
	private String messageSegmentPosition = "00001";

	/**
	 * Filler. Para uso futuro
	 */
	private String fillerDataControl = " ";

	/**
	 * Tipo de Mensaje
	 */
	private String messageType = "TS";

	/**
	 * ID. Transacción
	 */
	private String transactionId;

	/**
	 * Secuencia de Transacción
	 */
	private String transactionSequence;

	/**
	 * Indicador Requiere Respuesta
	 */
	private String responseRequire;

	/**
	 * Código Respuesta
	 */
	private String responseCode = "0000";

	/**
	 * Descripción Respuesta
	 */
	private String responseDescription;

	/**
	 * Filler. Para uso futuro
	 */
	private String fillerDataHeader;

	/**
	 * @return the serverId
	 */
	public String getServerId() {
		return serverId;
	}

	/**
	 * @param serverId
	 *            the serverId to set
	 */
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	/**
	 * @return the customerAppId
	 */
	public String getCustomerAppId() {
		return customerAppId;
	}

	/**
	 * @param customerAppId
	 *            the customerAppId to set
	 */
	public void setCustomerAppId(String customerAppId) {
		this.customerAppId = customerAppId;
	}

	/**
	 * @return the messageId
	 */
	public String getMessageId() {
		return messageId;
	}

	/**
	 * @param messageId
	 *            the messageId to set
	 */
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	/**
	 * @return the dataQRespName
	 */
	public String getDataQRespName() {
		return dataQRespName;
	}

	/**
	 * @param dataQRespName
	 *            the dataQRespName to set
	 */
	public void setDataQRespName(String dataQRespName) {
		this.dataQRespName = dataQRespName;
	}

	/**
	 * @return the dataQRespLibrary
	 */
	public String getDataQRespLibrary() {
		return dataQRespLibrary;
	}

	/**
	 * @param dataQRespLibrary
	 *            the dataQRespLibrary to set
	 */
	public void setDataQRespLibrary(String dataQRespLibrary) {
		this.dataQRespLibrary = dataQRespLibrary;
	}

	/**
	 * @return the messageDirection
	 */
	public String getMessageDirection() {
		return messageDirection;
	}

	/**
	 * @param messageDirection
	 *            the messageDirection to set
	 */
	public void setMessageDirection(String messageDirection) {
		this.messageDirection = messageDirection;
	}

	/**
	 * @return the messageTimer
	 */
	public String getMessageTimer() {
		return messageTimer;
	}

	/**
	 * @param messageTimer
	 *            the messageTimer to set
	 */
	public void setMessageTimer(String messageTimer) {
		this.messageTimer = messageTimer;
	}

	/**
	 * @return the messageRequestHour
	 */
	public String getMessageRequestHour() {
		return messageRequestHour;
	}

	/**
	 * @param messageRequestHour
	 *            the messageRequestHour to set
	 */
	public void setMessageRequestHour(String messageRequestHour) {
		this.messageRequestHour = messageRequestHour;
	}

	/**
	 * @return the messageResponseHour
	 */
	public String getMessageResponseHour() {
		return messageResponseHour;
	}

	/**
	 * @param messageResponseHour
	 *            the messageResponseHour to set
	 */
	public void setMessageResponseHour(String messageResponseHour) {
		this.messageResponseHour = messageResponseHour;
	}

	/**
	 * @return the serverRequestArriveHour
	 */
	public String getServerRequestArriveHour() {
		return serverRequestArriveHour;
	}

	/**
	 * @param serverRequestArriveHour
	 *            the serverRequestArriveHour to set
	 */
	public void setServerRequestArriveHour(String serverRequestArriveHour) {
		this.serverRequestArriveHour = serverRequestArriveHour;
	}

	/**
	 * @return the serverResponseHour
	 */
	public String getServerResponseHour() {
		return serverResponseHour;
	}

	/**
	 * @param serverResponseHour
	 *            the serverResponseHour to set
	 */
	public void setServerResponseHour(String serverResponseHour) {
		this.serverResponseHour = serverResponseHour;
	}

	/**
	 * @return the messagePayLoadLength
	 */
	public String getMessagePayLoadLength() {
		return messagePayLoadLength;
	}

	/**
	 * @param messagePayLoadLength
	 *            the messagePayLoadLength to set
	 */
	public void setMessagePayLoadLength(String messagePayLoadLength) {
		this.messagePayLoadLength = messagePayLoadLength;
	}

	/**
	 * @return the messageTotalSegments
	 */
	public String getMessageTotalSegments() {
		return messageTotalSegments;
	}

	/**
	 * @param messageTotalSegments
	 *            the messageTotalSegments to set
	 */
	public void setMessageTotalSegments(String messageTotalSegments) {
		this.messageTotalSegments = messageTotalSegments;
	}

	/**
	 * @return the messageSegmentPosition
	 */
	public String getMessageSegmentPosition() {
		return messageSegmentPosition;
	}

	/**
	 * @param messageSegmentPosition
	 *            the messageSegmentPosition to set
	 */
	public void setMessageSegmentPosition(String messageSegmentPosition) {
		this.messageSegmentPosition = messageSegmentPosition;
	}

	/**
	 * @return the fillerDataControl
	 */
	public String getFillerDataControl() {
		return fillerDataControl;
	}

	/**
	 * @param fillerDataControl
	 *            the fillerDataControl to set
	 */
	public void setFillerDataControl(String fillerDataControl) {
		this.fillerDataControl = fillerDataControl;
	}

	/**
	 * @return the messageType
	 */
	public String getMessageType() {
		return messageType;
	}

	/**
	 * @param messageType
	 *            the messageType to set
	 */
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	/**
	 * @return the transactionId
	 */
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * @param transactionId
	 *            the transactionId to set
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	/**
	 * @return the transactionSequence
	 */
	public String getTransactionSequence() {
		return transactionSequence;
	}

	/**
	 * @param transactionSequence
	 *            the transactionSequence to set
	 */
	public void setTransactionSequence(String transactionSequence) {
		this.transactionSequence = transactionSequence;
	}

	/**
	 * @return the responseRequire
	 */
	public String getResponseRequire() {
		return responseRequire;
	}

	/**
	 * @param responseRequire
	 *            the responseRequire to set
	 */
	public void setResponseRequire(String responseRequire) {
		this.responseRequire = responseRequire;
	}

	/**
	 * @return the responseCode
	 */
	public String getResponseCode() {
		return responseCode;
	}

	/**
	 * @param responseCode
	 *            the responseCode to set
	 */
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	/**
	 * @return the responseDescription
	 */
	public String getResponseDescription() {
		return responseDescription;
	}

	/**
	 * @param responseDescription
	 *            the responseDescription to set
	 */
	public void setResponseDescription(String responseDescription) {
		this.responseDescription = responseDescription;
	}

	/**
	 * @return the fillerDataHeader
	 */
	public String getFillerDataHeader() {
		return fillerDataHeader;
	}

	/**
	 * @param fillerDataHeader
	 *            the fillerDataHeader to set
	 */
	public void setFillerDataHeader(String fillerDataHeader) {
		this.fillerDataHeader = fillerDataHeader;
	}

	/**
	 * Concatenate all headers attributes to create a header string
	 * 
	 * @return String that represents header
	 */
	public String getHeader() {
		StringBuilder sb = new StringBuilder();

		// Control headers
		sb.append(ILtoIASTTranformationRules.refillValue(getServerId(),
				RefillOption.BLANK_SPACE, AlignOption.LEFT, SERVER_ID_LENGTH, null));
		sb.append(ILtoIASTTranformationRules.refillValue(getCustomerAppId(),
				RefillOption.BLANK_SPACE, AlignOption.LEFT,
				CUSTOMER_APP_ID_LENGTH, null));
		sb.append(ILtoIASTTranformationRules.refillValue(getMessageId(),
				RefillOption.BLANK_SPACE, AlignOption.LEFT, MESSAGE_ID_LENGTH, null));
		sb.append(ILtoIASTTranformationRules.refillValue(getDataQRespName(),
				RefillOption.BLANK_SPACE, AlignOption.LEFT,
				DATAQ_RESP_NAME_LENGTH, null));
		sb.append(ILtoIASTTranformationRules.refillValue(getDataQRespLibrary(),
				RefillOption.BLANK_SPACE, AlignOption.LEFT,
				DATAQ_RESP_LIBRARY_LENGTH, null));
		sb.append(ILtoIASTTranformationRules.refillValue(
						getMessageDirection(), RefillOption.ZERO,
						AlignOption.RIGHT, MESSAGE_DIRECTION_LENGTH, null));
		sb.append(ILtoIASTTranformationRules.refillValue(getMessageTimer(),
				RefillOption.ZERO, AlignOption.RIGHT, MESSAGE_TIMER_LENGTH, null));
		sb.append(ILtoIASTTranformationRules.refillValue(
				getMessageRequestHour(), RefillOption.ZERO, AlignOption.RIGHT,
				MESSAGE_REQUEST_HOUR_LENGTH, null));
		sb.append(ILtoIASTTranformationRules.refillValue(
				getMessageResponseHour(), RefillOption.ZERO, AlignOption.RIGHT,
				MESSAGE_RESPONSE_HOUR_LENGTH, null));
		sb.append(ILtoIASTTranformationRules.refillValue(
				getServerRequestArriveHour(), RefillOption.ZERO,
				AlignOption.RIGHT, SERVER_REQUEST_ARRIVE_HOUR_LENGTH, null));
		sb.append(ILtoIASTTranformationRules.refillValue(
				getServerResponseHour(), RefillOption.ZERO, AlignOption.RIGHT,
				SERVER_RESPONSE_HOUR_LENGTH, null));
		sb.append(ILtoIASTTranformationRules.refillValue(
				getMessagePayLoadLength(), RefillOption.ZERO,
				AlignOption.RIGHT, MESSAGE_PAYLOAD_LENGTH, null));
		sb.append(ILtoIASTTranformationRules.refillValue(
				getMessageTotalSegments(), RefillOption.ZERO,
				AlignOption.RIGHT, MESSAGE_TOTAL_SEGMENTS_LENGTH, null));
		sb.append(ILtoIASTTranformationRules.refillValue(
				getMessageSegmentPosition(), RefillOption.ZERO,
				AlignOption.RIGHT, MESSAGE_SEGMENT_POSITION_LENGTH, null));
		sb.append(ILtoIASTTranformationRules.refillValue(
				getFillerDataControl(), RefillOption.BLANK_SPACE,
				AlignOption.LEFT, FILLER_DATA_CONTROL_LENGTH, null));

		// Data headers
		sb.append(ILtoIASTTranformationRules
				.refillValue(getMessageType(), RefillOption.BLANK_SPACE,
						AlignOption.LEFT, MESSAGE_TYPE_LENGTH, null));
		sb.append(ILtoIASTTranformationRules.refillValue(getTransactionId(),
				RefillOption.BLANK_SPACE, AlignOption.LEFT,
				TRANSACTION_ID_LENGTH, null));
		sb.append(ILtoIASTTranformationRules.refillValue(
				getTransactionSequence(), RefillOption.BLANK_SPACE,
				AlignOption.LEFT, TRANSACTION_SEQUENCE_LENGTH, null));
		sb.append(ILtoIASTTranformationRules.refillValue(getResponseRequire(),
				RefillOption.BLANK_SPACE, AlignOption.LEFT,
				RESPONSE_REQUIRED_LENGTH, null));
		sb.append(ILtoIASTTranformationRules.refillValue(getResponseCode(),
				RefillOption.BLANK_SPACE, AlignOption.LEFT,
				RESPONSE_CODE_LENGTH, null));
		sb.append(ILtoIASTTranformationRules.refillValue(
				getResponseDescription(), RefillOption.BLANK_SPACE,
				AlignOption.LEFT, RESPONSE_DESCRIPTION_LENGTH, null));
		sb.append(ILtoIASTTranformationRules.refillValue(getFillerDataHeader(),
				RefillOption.BLANK_SPACE, AlignOption.LEFT,
				FILLER_DATA_HEADER_LENGTH, null));

		return sb.toString();
	}
}
