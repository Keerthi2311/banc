package core.common.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;

public class IASTMessage {

	private final static String FALSE = "0";
	private final static String DATETIME_FORMAT = "MM/dd/yyyy hh:mm:ss";
	private final static String DATE_FORMAT = "MM/dd/yyyy";
	private final static int idServerLength = 3;
	private final static int customerIdApplicationLength = 3;
	private final static int messageIdLength = 16;
	private final static int responseMqQueueLength = 10;
	private final static int libraryDtaQResponseLength = 10;
	private final static int orientationOfTheMessageLength = 1;
	private final static int timerLength = 2;
	private final static int sendMessageToServerTimeLength = 8;
	private final static int receiptTimeServerResponseLength = 8;
	private final static int arrivalToTheServerTimeLength = 8;
	private final static int serverResponseTimeLength = 8;
	private final static int messageUsefulLoadLengthLength = 5;
	private final static int totalSegmentsOfTheMessageLength = 2;
	private final static int posWithinTheSegmentLength = 5;
	private final static int freeLength = 1;
	private final static int typeMessageLength = 2;
	private final static int transCodeLength = 4;
	private final static int seqTransactionLength = 20;
	private final static int requiresResponseLength = 1;
	private final static int responseCodeLength = 4;
	private final static int responseDescriptionLength = 50;
	private final static int freeHeaderLength = 9;
	
	private static MbMessage mbMessage = null;
	private static String idServer = "";
	private static String customerIdApplication = "";
	private static String messageId = "";
	private static String responseMqQueue = "";
	private static String libraryDtaQResponse = "";
	private static String orientationOfTheMessage = "";
	private static String timer = "";
	private static String sendMessageToServerTime = "";
	private static String receiptTimeServerResponse = "";
	private static String arrivalToTheServerTime = "";
	private static String serverResponseTime = "";
	private static String messageUsefulLoadLength = "";
	private static String totalSegmentsOfTheMessage = "";
	private static String posWithinTheSegment = "";
	private static String free = "";
	private static String typeMessage = "";
	private static String transCode = "";
	private static String seqTransaction = "";
	private static String requiresResponse = "";
	private static String responseCode = "";
	private static String responseDescription = "";
	private static String freeHeader = "";
	private static String cargaUtil = "";
	
	public IASTMessage (){}
 
	public IASTMessage (MbMessage root){
		mbMessage = root;
	}

    /**
     * Procedimiento que serializa un numero dado como un decimal en formato de ultima.
     *
     * @param num long to be converted
     * @return String value of parameter num
     * 
     */
	public static String toIASTDecimal (long num){        
		String result = "";
		String pattern = "";
		
		if (num < 0) {
			pattern = "00000000000000000";
			DecimalFormat decimalFormat = new DecimalFormat(pattern);
			result = decimalFormat.format(num);
			result = result.replace('-', 'p');
		} else {
			pattern = "000000000000000000";
			DecimalFormat decimalFormat = new DecimalFormat(pattern);
			result = decimalFormat.format(num);
		}
		
		return result;
	}
	
    /**
     * Procedimiento que serializa una fecha determinada como la fecha en formato de ultima.	
     * 
     * @param date Calendar to be converted
     * @return String value of parameter date
     */
	public static String toIASTDate (Calendar date){
		String iastDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		iastDate = sdf.format(date.getTime()); 
		
		return iastDate;
	}
	
    /**
     * Procedimiento que serializa una determinada fecha y hora como la fecha y hora en formato de ultima.	
     *
     * @param dateTime Calendar to be converted
     * @return String value of parameter dateTime
     */  
	public static String toIASTDateTime (Calendar dateTime){
		String iastDateTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT);
		iastDateTime = sdf.format(dateTime.getTime()); 
		
		return iastDateTime;
	}

    /**
     * Procedimiento que serializa un numero dado como un entero en formato de ultima.	
     *
     * @param num int to be converted
     * @return String value of parameter num
     */
	public static String toIASTInteger (int num){
		String result = "";
		String pattern = "";
		
		if (num < 0) {
			pattern = "000000000000000";
			DecimalFormat decimalFormat = new DecimalFormat(pattern);
			result = decimalFormat.format(num);
			result = result.replace('-', 'p');
		} else {
			pattern = "0000000000000000";
			DecimalFormat decimalFormat = new DecimalFormat(pattern);
			result = decimalFormat.format(num);
		}
		
		return result;
	}

	 /**
     * Procedimiento que serializa un numero dado como un entero en formato de ultima.	
     *
     * @param valor boolean value to be converted
     * @return String value of parameter num
     */
	public static String toIASTBoolean (boolean valor){
		String iasBool = "0";
		
		if(valor){
			iasBool = "1";
		}
		
		return iasBool;
	}
	
	 /**
     * Procedimiento que deserializa un numero dado en formato pasado como un decimal	
     *
     * @param num String to be converted
     * @return long value of parameter num
     */ 
	public static long fromIASTDecimal (String num){
		if (num.startsWith("p")) {
			num = num.replace('p', '-');
		}
		
		long iastDecimal = Long.parseLong(num);
		
		return iastDecimal;
	}

	 /**
     * Procedimiento que deserializa una fecha en formato de la ultima fecha	
     *
     * @param date String value of date to be converted
     * @return Calendar value of parameter date
     */ 
	public static Calendar fromIASTDate (String date) throws ParseException{
		Calendar iastDate = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		Date d = sdf.parse(date);
		iastDate.setTime(d);
		
		return iastDate;
	}
	
	 /**
     * Procedimiento que deserializa una determinada fecha y hora en formato de ultima como fecha y hora.	
     *
     * @param dateTime String value to be converted
     * @return Calendar value of parameter dateTime
     */ 
	public static Calendar fromIASTDateTime (String dateTime) throws ParseException{
		Calendar iastDateTime = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT);
		Date date = sdf.parse(dateTime);
		iastDateTime.setTime(date);
		
		return iastDateTime;
	}
	
	 /**
     * Procedimiento que se deserializa un numero dado en formato de ultima como entero.	
     *
     * @param num String value to be converted
     * @return int value of parameter num
     */
	public static int fromIASTInteger (String num){
		if (num.startsWith("p")) {
			num = num.replace('p', '-');
		}
		
		int iastInteger = Integer.parseInt(num);
		return iastInteger;
	}
	
	 /**
     * Procedimiento que deserializa un booleano en formato de ultima.	
     *
     * @param valor String value to be converted
     * @return boolean value of parameter valor
     */
	public static boolean fromIASTBoolean (String valor){
		boolean iastBool = true;

		if(FALSE.equals(valor)){
			iastBool = false;
		}
		
		return iastBool;
	}

	 /**
     * Procedimiento para convertir IASTMessage a Cadena.	
     *
     * @return String value of IASTMessage
     */
	public String toString(){
		
		StringBuilder iastMessageString = new StringBuilder();
		int diff = 0;
		try{
			idServer = mbMessage.getRootElement().getFirstElementByPath("ID_SERVER").getValueAsString();
			diff = lengthDifference(idServer, idServerLength);
			if(diff == 0){
				iastMessageString.append(idServer);
			}else{
				iastMessageString.append(padString(idServer, diff, " "));
			}
			
			customerIdApplication = mbMessage.getRootElement().getFirstElementByPath("CUSTOMER_ID_APPLICATION").getValueAsString();
			diff = lengthDifference(customerIdApplication, customerIdApplicationLength);
			if(diff == 0){
				iastMessageString.append(customerIdApplication);
			}else{
				iastMessageString.append(padString(customerIdApplication, diff, " "));
			}
			
			messageId = mbMessage.getRootElement().getFirstElementByPath("MESSAGE-ID").getValueAsString();
			diff = lengthDifference(messageId, messageIdLength);
			if(diff == 0){
				iastMessageString.append(messageId);
			}else{
				iastMessageString.append(padString(messageId, diff, " "));
			}
			
			responseMqQueue = mbMessage.getRootElement().getFirstElementByPath("RESPONSE_MQ_QUEUE").getValueAsString();
			diff = lengthDifference(responseMqQueue, responseMqQueueLength);
			if(diff == 0){
				iastMessageString.append(responseMqQueue);
			}else{
				iastMessageString.append(padString(responseMqQueue, diff, " "));
			}
			
			libraryDtaQResponse = mbMessage.getRootElement().getFirstElementByPath("LIBRARY_DTAQ_RESPONSE(NA)").getValueAsString();
			diff = lengthDifference(libraryDtaQResponse, libraryDtaQResponseLength);
			if(diff == 0){
				iastMessageString.append(libraryDtaQResponse);
			}else{
				iastMessageString.append(padString(libraryDtaQResponse, diff, " "));
			}
			
			orientationOfTheMessage = mbMessage.getRootElement().getFirstElementByPath("ORIENTATION_OF_THE_MESSAGE").getValueAsString();
			diff = lengthDifference(orientationOfTheMessage , orientationOfTheMessageLength);
			if(diff == 0){
				iastMessageString.append(orientationOfTheMessage);
			}else{
				iastMessageString.append(padString(orientationOfTheMessage, diff, "0"));
			}
			
			timer = mbMessage.getRootElement().getFirstElementByPath("TIMER").getValueAsString();
			diff = lengthDifference(timer , timerLength);
			if(diff == 0){
				iastMessageString.append(timer);
			}else{
				iastMessageString.append(padString(timer, diff, "0"));
			}
			
			sendMessageToServerTime = mbMessage.getRootElement().getFirstElementByPath("SEND_MESSAGE_TO_SERVER_TIME").getValueAsString();
			diff = lengthDifference(sendMessageToServerTime , sendMessageToServerTimeLength);
			if(diff == 0){
				iastMessageString.append(sendMessageToServerTime);
			}else{
				iastMessageString.append(padString(sendMessageToServerTime, diff, "0"));
			}
			
			receiptTimeServerResponse = mbMessage.getRootElement().getFirstElementByPath("RECEIPT_TIME_SERVER_RESPONSE").getValueAsString();
			diff = lengthDifference(receiptTimeServerResponse, receiptTimeServerResponseLength);
			if(diff == 0){
				iastMessageString.append(receiptTimeServerResponse);
			}else{
				iastMessageString.append(padString(receiptTimeServerResponse, diff, "0"));
			}
			
			arrivalToTheServerTime = mbMessage.getRootElement().getFirstElementByPath("ARRIVAL_TO_THE_SERVER_TIME").getValueAsString();
			diff = lengthDifference(arrivalToTheServerTime, arrivalToTheServerTimeLength);
			if(diff == 0){
				iastMessageString.append(arrivalToTheServerTime);
			}else{
				iastMessageString.append(padString(arrivalToTheServerTime, diff, "0"));
			}
			
			serverResponseTime = mbMessage.getRootElement().getFirstElementByPath("SERVER_RESPONSE_TIME").getValueAsString();
			diff = lengthDifference(serverResponseTime, serverResponseTimeLength);
			if(diff == 0){
				iastMessageString.append(serverResponseTime);
			}else{
				iastMessageString.append(padString(serverResponseTime, diff, "0"));
			}
			
			messageUsefulLoadLength = mbMessage.getRootElement().getFirstElementByPath("THE_MESSAGE_USEFUL_LOAD_LENGTH").getValueAsString();
			diff = lengthDifference(messageUsefulLoadLength, messageUsefulLoadLengthLength);
			if(diff == 0){
				iastMessageString.append(messageUsefulLoadLength);
			}else{
				iastMessageString.append(padString(messageUsefulLoadLength, diff, "0"));
			}
			
			totalSegmentsOfTheMessage = mbMessage.getRootElement().getFirstElementByPath("TOTAL_SEGMENTS_OF_THE_MESSAGE").getValueAsString();
			diff = lengthDifference(totalSegmentsOfTheMessage, totalSegmentsOfTheMessageLength);
			if(diff == 0){
				iastMessageString.append(totalSegmentsOfTheMessage);
			}else{
				iastMessageString.append(padString(totalSegmentsOfTheMessage, diff, "0"));
			}
			
			posWithinTheSegment = mbMessage.getRootElement().getFirstElementByPath("POSITION_WITHIN_THE_SEGMENT").getValueAsString();
			diff = lengthDifference(posWithinTheSegment , posWithinTheSegmentLength);
			if(diff == 0){
				iastMessageString.append(posWithinTheSegment);
			}else{
				iastMessageString.append(padString(posWithinTheSegment, diff, "0"));
			}
			
			free = mbMessage.getRootElement().getFirstElementByPath("FREE").getValueAsString();
			diff = lengthDifference(free, freeLength);
			if(diff == 0){
				iastMessageString.append(free);
			}else{
				iastMessageString.append(padString(free, diff, "0"));
			}
			
			typeMessage = mbMessage.getRootElement().getFirstElementByPath("TYPE_MESSAGE").getValueAsString();
			diff = lengthDifference(typeMessage, typeMessageLength);
			if(diff == 0){
				iastMessageString.append(typeMessage);
			}else{
				iastMessageString.append(padString(typeMessage, diff, " "));
			}
			
			transCode = mbMessage.getRootElement().getFirstElementByPath("TRANSACTION_CODE").getValueAsString();
			diff = lengthDifference(transCode.toString() , transCodeLength);
			if(diff == 0){
				iastMessageString.append(transCode);
			}else{
				iastMessageString.append(padString(transCode.toString(), diff, " "));
			}
			
			seqTransaction = mbMessage.getRootElement().getFirstElementByPath("SEQUENCE_TRANSACTION").getValueAsString();
			diff = lengthDifference(seqTransaction, seqTransactionLength);
			if(diff == 0){
				iastMessageString.append(seqTransaction);
			}else{
				iastMessageString.append(padString(seqTransaction, diff, " "));
			}

			requiresResponse = mbMessage.getRootElement().getFirstElementByPath("REQUIRES_RESPONSE").getValueAsString();
			diff = lengthDifference(requiresResponse, requiresResponseLength);
			if(diff == 0){
				iastMessageString.append(requiresResponse);
			}else{
				iastMessageString.append(padString(requiresResponse, diff, " "));
			}
			
			responseCode = mbMessage.getRootElement().getFirstElementByPath("RESPONSE_CODE").getValueAsString();
			diff = lengthDifference(responseCode, responseCodeLength);
			if(diff == 0){
				iastMessageString.append(responseCode);
			}else{
				iastMessageString.append(padString(responseCode, diff, " "));
			}
			
			responseDescription = mbMessage.getRootElement().getFirstElementByPath("RESPONSE_DESCRIPTION").getValueAsString();
			diff = lengthDifference(responseDescription, responseDescriptionLength);
			if(diff == 0){
				iastMessageString.append(responseDescription);
			}else{
				iastMessageString.append(padString(responseDescription, diff, " "));
			}
			
			freeHeader = mbMessage.getRootElement().getFirstElementByPath("FREE").getValueAsString();
			diff = lengthDifference(freeHeader, freeHeaderLength);
			if(diff == 0){
				iastMessageString.append(freeHeader);
			}else{
				iastMessageString.append(padString(freeHeader, diff, " "));
			}
			
			cargaUtil = mbMessage.getRootElement().getFirstElementByPath("CargaUtil").getValueAsString();
			iastMessageString.append(cargaUtil);
			System.out.println("cargaUtil:\t|" +iastMessageString+"|"+iastMessageString.length());
		}catch(MbException mbe){
			
		}
		
		return iastMessageString.toString();
		
	}
	
	
	/* Mutodos de Utilidad */
	
	/**
	 * Procedimiento para obtener la diferencia de longitud de la cadena
	 * 
	 * @param field length of the field
	 * @param requiredLength the required length for the given field
	 * @return int the difference between the field length and the required length for that field
	 * */
	private int lengthDifference(String field, int requiredLength){
		return requiredLength-field.length();
	}
	
	/**
	 * Procedimiento para obtener la diferencia de longitud de la cadena
	 * 
	 * @param origString the original string
	 * @param noOfChars number of characters that is needed to be padded
	 * @param character the character that will be used for padding
	 * @return String the resulting padded String
	 * */
	private String padString(String origString, int noOfChars, String character){
		
		StringBuffer returnString = new StringBuffer();
		returnString.append(origString);
		for(int i=0; i<noOfChars; i++){
			returnString.append(character);
		}
		
		return returnString.toString();
	}

}

