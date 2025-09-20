package Core_Common_Channel.IAST;

import java.util.List;

import com.ibm.broker.config.common.Base64;
import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbDFDL;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbNamespaceBindings;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbPolicy;
import com.ibm.broker.plugin.MbXMLNSC;
import com.ibm.broker.plugin.MbXPath;

import core.common.util.ILUtil;
import core.common.util2.ConfigurableServiceMultiton;


public class CA_IAST_IN_IAST_to_IL_JavaCompute extends MbJavaComputeNode {

	private static final String NS_IL = "http://grupobancolombia.com/intf/IL/esbXML/V3.0";
	private static final String CONFIGURABLESERVICENAME = "UDCS_CAIAST";
	private static final String USR = "USR";
	private static final String SRV = "SRV";
	private static final String OPE = "OPE";
	private static final String CBK = "CBK";
	private static final String XPHREQ = "XPHREQ";
	private static final String MSG = "MSG";
	private static final String IDENTITYNOTFOUND = "Error, identidad del servicio no encontrada";
	private static final String USERNOTFOUND = "Error, usuario del servicio no encontrado";
	private static final String DESTINATIONNOTFOUND = "Error, destino del servicio no encontrado";
	private static final String CANNOTRESOLVECBKQ = "Error, no se puede determinar la cola de respuesta";
	private static final String REQUEST = "REQUEST";
	private static final String ILSEV2 = "ILSEV2";
	private static final String CODE1103 = "1103";
	private static final String COMMAND = "COMMAND";
	private static final String DATETIMEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
	private static final String WHITE_SPACE = " ";
	private static final String BINARY1 = "Binary1";
	private static final String CSPARAMETER = CONFIGURABLESERVICENAME;
	private static final String DEFAULT_TAG = "NS2";
	private static final String IAST_ID_SERVIDOR = "ID_SERVIDOR";
	private static final String IAST_ID_APLICATIVO = "ID_APLICATIVO";
	private static final String IAST_COD_TRANSACCION = "COD_TRANSAC";
	private static final String IAST_SEC_TRANSACCION = "SEC_TRANSAC";
	public final static String POLICY_PROJECT = "DefaultPolicies";
	public final static String POLICY_TYPE = "UserDefined";
	private static final int OPTIONS_VALIDATE1 = MbElement.VALIDATE_CONTENT | MbElement.VALIDATE_VALUE
			| MbElement.VALIDATE_LOCAL_ERROR;
	private static final int OPTIONS_VALIDATE2 = MbElement.VALIDATE_CONTENT_AND_VALUE | MbElement.VALIDATE_EXCEPTION;

	private String cnPrefix = (String) getUserDefinedAttribute("CN_PREFIX");

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {

		MbMessageAssembly outAssembly;
		MbMessage inMessage;
		MbMessage outMessage;
		MbMessage env;

		MbOutputTerminal out = getOutputTerminal("out");
		
		env = inAssembly.getGlobalEnvironment();
		inMessage = inAssembly.getMessage();
		outMessage = new MbMessage();
		outAssembly = new MbMessageAssembly(inAssembly, outMessage);

		try {

			MbElement refDFDLHeaderControl = inMessage.getRootElement().getFirstElementByPath("DFDL/IAST/Header/ENCABEZADO_CONTROL");
			MbElement refDFDLHeaderData = inMessage.getRootElement().getFirstElementByPath("DFDL/IAST/Header/ENCABEZADO_DATOS");

            copyMessageHeadersTemp(inMessage, outMessage);
            

			MbElement outXML = outMessage.getRootElement().createElementAsLastChild(MbXMLNSC.PARSER_NAME);
			MbElement esbXML = outXML.createElementAsLastChild(MbElement.TYPE_NAME, "esbXML", null);
			esbXML.setNamespace(NS_IL);
			MbElement refHeaderOut = esbXML.createElementAsLastChild(MbElement.TYPE_NAME, "Header", null);
			MbElement refBodyOut = esbXML.createElementAsLastChild(MbElement.TYPE_NAME, "Body", null);
			
			buildIL(inAssembly, outAssembly, refDFDLHeaderControl, refDFDLHeaderData, refHeaderOut, refBodyOut);

			out.propagate(outAssembly);

		} catch (MbException e) {
			
			ILUtil.throwILSystemException(REQUEST, ILSEV2, CODE1103, COMMAND, e.getMessage(), env.getRootElement(),
					inAssembly.getExceptionList().getRootElement());
		} catch (RuntimeException e) {
			
			ILUtil.throwILSystemException(REQUEST, ILSEV2, CODE1103, COMMAND, e.getMessage(), env.getRootElement(),
					inAssembly.getExceptionList().getRootElement());
		} catch (Exception e) {

			ILUtil.throwILSystemException(REQUEST, ILSEV2, CODE1103, COMMAND, e.getMessage(), env.getRootElement(),
					inAssembly.getExceptionList().getRootElement());
		}
	}
	public static void copyMessageHeadersTemp(MbMessage inMessage, MbMessage outMessage) throws MbException {
		MbElement outRoot = outMessage.getRootElement();
		MbElement header = inMessage.getRootElement().getFirstChild();

		while (header != null && header.getNextSibling() != null) {
			outRoot.addAsLastChild(header.copy());
			header = header.getNextSibling();
		}
	}
	/**
	 * M�todo que se encarga de parsear el mensaje IAST a formato can�nico
	 * XML-IL, de acuerdo al encoding y codedCharSetId de las propiedades del
	 * mensaje y tomando del servicio configurable la informaci�n para obtener
	 * el formato del cuerpo del mensaje.
	 * 
	 * @param inAssembly
	 *            Representa el MbMessageAssembly de entrada
	 * @param outAssembly
	 *            Representa el MbMessageAssembly de salida
	 * @param refDFDLHeader
	 *            Es un MbElement del Header del IAST
	 * @param refHeaderOut
	 *            Es un MbElement del Header del XML-IL
	 * @param refBodyOut
	 *            Es un MbElement del Body del XML-IL
	 * @throws MbException
	 */
	public void buildIL(MbMessageAssembly inAssembly, MbMessageAssembly outAssembly, MbElement refDFDLHeaderControl, 
			MbElement refDFDLHeaderData, MbElement refHeaderOut, MbElement refBodyOut) throws MbException {

		String elementUserName;
		String elementDestinationName;
		String elementOperation;
		String systemId;
		String userName;
		String operationName;
		String namespaceName;
		String operation;
		String elementIdAplication;
		int encoding;
		int codedCharSetId;
		
		MbElement mqmd = inAssembly.getMessage().getRootElement().getFirstElementByPath("MQMD");
		MbElement properties = inAssembly.getMessage().getRootElement().getFirstElementByPath("Properties");
		MbElement variables = inAssembly.getGlobalEnvironment().getRootElement()
				.createElementAsLastChild(MbElement.TYPE_NAME, "Variables", null);

		encoding = Integer.parseInt(properties.getFirstElementByPath("Encoding").getValueAsString());
		codedCharSetId = Integer.parseInt(properties.getFirstElementByPath("CodedCharSetId").getValueAsString());

	    elementIdAplication = refDFDLHeaderControl.getFirstElementByPath(IAST_ID_APLICATIVO).getValueAsString(); // APP
		elementUserName = refDFDLHeaderControl.getFirstElementByPath(IAST_ID_SERVIDOR).getValueAsString()
				+ refDFDLHeaderControl.getFirstElementByPath(IAST_ID_APLICATIVO).getValueAsString()
				+ refDFDLHeaderData.getFirstElementByPath(IAST_COD_TRANSACCION).getValueAsString() + USR; // SA2APP0640USR
		elementDestinationName = refDFDLHeaderControl.getFirstElementByPath(IAST_ID_SERVIDOR).getValueAsString()
				+ refDFDLHeaderData.getFirstElementByPath(IAST_COD_TRANSACCION).getValueAsString() + SRV;  //SA20640SRV
		elementOperation = refDFDLHeaderControl.getFirstElementByPath(IAST_ID_SERVIDOR).getValueAsString()
				+ refDFDLHeaderData.getFirstElementByPath(IAST_COD_TRANSACCION).getValueAsString() + OPE; // SA20640OPE
		
		systemId = ConfigurableServiceMultiton.getValue(POLICY_TYPE, POLICY_PROJECT,CSPARAMETER, elementIdAplication); 
		userName = ConfigurableServiceMultiton.getValue(POLICY_TYPE, POLICY_PROJECT,CSPARAMETER, elementUserName);
		operationName = ConfigurableServiceMultiton.getValue(POLICY_TYPE, POLICY_PROJECT,CSPARAMETER, elementDestinationName);
		namespaceName = ConfigurableServiceMultiton.getValue(POLICY_TYPE, POLICY_PROJECT,CSPARAMETER,
				refDFDLHeaderControl.getFirstElementByPath(IAST_ID_SERVIDOR).getValueAsString());
		operation = ConfigurableServiceMultiton.getValue(POLICY_TYPE, POLICY_PROJECT,CSPARAMETER, elementOperation);

		validateDestination(systemId, userName, operationName, operation, namespaceName, inAssembly);

		String originMsgId = mqmd.getFirstElementByPath("MsgId").getValueAsString();
		variables.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "originMsgId", originMsgId);

		refHeaderOut.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "systemId", systemId);
		refHeaderOut.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "messageId", refDFDLHeaderData
				.getFirstElementByPath(IAST_SEC_TRANSACCION).getValueAsString());
		refHeaderOut.createElementAsLastChild(MbElement.TYPE_NAME, "interactionData", null).createElementAsLastChild(
				MbElement.TYPE_NAME_VALUE, "timestamp", CA_IAST_Util.currentDate(DATETIMEFORMAT));

		MbElement requestData = refHeaderOut.createElementAsLastChild(MbElement.TYPE_NAME, "requestData", null);

		requestData.createElementAsLastChild(MbElement.TYPE_NAME, "userId", null).createElementAsLastChild(
				MbElement.TYPE_NAME_VALUE, "userName", userName);

		MbElement destination = requestData.createElementAsLastChild(MbElement.TYPE_NAME, "destination", null);

		destination.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "name", operationName);
		destination.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "namespace", namespaceName);
		destination.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "operation", operation);

		requestData.createElementAsLastChild(MbElement.TYPE_NAME, "classifications", null).createElementAsLastChild(
				MbElement.TYPE_NAME_VALUE, "classification", cnPrefix + systemId);

		String replyToQ = mqmd.getFirstElementByPath("ReplyToQ").getValueAsString();

		pushRTQ(replyToQ, refDFDLHeaderControl, refHeaderOut, mqmd, inAssembly);

		byte[] rispostaBitStream = inAssembly
				.getMessage()
				.getRootElement()
				.getFirstElementByPath("DFDL")
				.toBitstream(properties.getFirstElementByPath("MessageType").getValueAsString(),
						properties.getFirstElementByPath("MessageSet").getValueAsString(),
						properties.getFirstElementByPath("MessageFormat").getValueAsString(), encoding, codedCharSetId,
						OPTIONS_VALIDATE1);

		MbElement property = refHeaderOut.createElementAsLastChild(MbElement.TYPE_NAME, "messageContext", null)
				.createElementAsLastChild(MbElement.TYPE_NAME, "property", null);

		property.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "key", "CAIAST");
		property.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "value", Base64.encode(rispostaBitStream));
		
		String xPathRepeatKey = refDFDLHeaderControl.getFirstElementByPath(IAST_ID_SERVIDOR).getValueAsString()
				+ refDFDLHeaderData.getFirstElementByPath(IAST_COD_TRANSACCION).getValueAsString() + XPHREQ;

		String messageLibKey = refDFDLHeaderControl.getFirstElementByPath(IAST_ID_SERVIDOR).getValueAsString()
				+ refDFDLHeaderData.getFirstElementByPath(IAST_COD_TRANSACCION).getValueAsString() + MSG;
				
        String xPathRepeat = ConfigurableServiceMultiton.getValue(POLICY_TYPE, POLICY_PROJECT,CSPARAMETER, xPathRepeatKey);
		String messageLib = ConfigurableServiceMultiton.getValue(POLICY_TYPE, POLICY_PROJECT,CSPARAMETER, messageLibKey);

		String inputCarga = inAssembly.getMessage().getRootElement().getFirstElementByPath("DFDL/IAST/Body/CARGA_UTIL")
				.getValueAsString();
		String messageType = "{" + namespaceName + "}:" + operation;

		outAssembly.getMessage().getRootElement().getFirstElementByPath("Properties/MessageType").setValue(messageType);
		outAssembly.getMessage().getRootElement().getFirstElementByPath("Properties/MessageFormat").setValue(BINARY1);
		outAssembly.getMessage().getRootElement().getFirstElementByPath("Properties/MessageSet")
				.setValue("{" + messageLib + "}");

		outAssembly.getMessage().getRootElement().getFirstElementByPath("MQMD").getFirstElementByPath("ReplyToQMgr")
				.setValue("");

		MbElement dfdlBody = variables.createElementAsLastChildFromBitstream(inputCarga.getBytes(), MbDFDL.PARSER_NAME,
				messageType, "{" + messageLib + "}", BINARY1, encoding, codedCharSetId, OPTIONS_VALIDATE2);

		refBodyOut.addAsLastChild(dfdlBody.getFirstElementByPath(operation).copy());

		if (CA_IAST_Util.isValidString(xPathRepeat)) {

			deleteRepeats(xPathRepeat, refBodyOut, namespaceName);
		}
		 
		 
		 validateValuesNullByOperation(operation, refBodyOut,namespaceName);	 
	}

	/**
	 * Elimina del body del mensaje IL los elementos "_repeticiones..." que
	 * representan las repeticiones de un elemento en la mensajer�a IAST.
	 * 
	 * @param xPathRepeat
	 *            xPath de los elementos que determinan las repeticiones. Si son
	 *            varios se separan por espacios en blanco.
	 * @param refBodyOut
	 *            Es un MbElement del Body del XML-IL
	 * @param namespace
	 *            Namespace del servicio recuperado del servicio configurable
	 * @throws MbException
	 */
	public void deleteRepeats(String xPathRepeat, MbElement refBodyOut, String namespace) throws MbException {

		MbNamespaceBindings ns = new MbNamespaceBindings();
		ns.addBinding(DEFAULT_TAG, namespace);

		String[] xPathRepeatArray = xPathRepeat.split(WHITE_SPACE);

		for (String repeat : xPathRepeatArray) {

			MbXPath xp = new MbXPath(DEFAULT_TAG + ":" + repeat, ns);

			List<MbElement> nodeset = (List<MbElement>) refBodyOut.evaluateXPath(xp);

			if (nodeset != null) {

				for (MbElement mbElement : nodeset) {
					mbElement.delete();
				}
			}
		}
	}

	/**
	 * 
	 * Valida que se hayan recuperado exitosamente la informaci�n de destino del
	 * servicio, en caso contrario desencadena una excepci�n de sistema
	 * personalizada.
	 * 
	 * @param systemId
	 *            Representa la identificaci�n del sistema origen.
	 * @param userName
	 *            Representa el usuario del sistema origen.
	 * @param operationName
	 *            Representa el nombre del servicio destino.
	 * @param operation
	 *            Representa el nombre de la operaci�n del servicio destino.
	 * @param namespaceName
	 *            Representa el namespace del servicio destino.
	 * @param inAssembly
	 *            Representa el MbMessageAssembly de entrada
	 * @throws MbException
	 */
	public void validateDestination(String systemId, String userName, String operationName, String operation,
			String namespaceName, MbMessageAssembly inAssembly) throws MbException {

		if (!CA_IAST_Util.isValidString(systemId)) {

			ILUtil.throwILSystemException(REQUEST, ILSEV2, CODE1103, COMMAND, IDENTITYNOTFOUND, inAssembly
					.getGlobalEnvironment().getRootElement(), inAssembly.getExceptionList().getRootElement());
		} else if (!CA_IAST_Util.isValidString(userName)) {

			ILUtil.throwILSystemException(REQUEST, ILSEV2, CODE1103, COMMAND, USERNOTFOUND, inAssembly
					.getGlobalEnvironment().getRootElement(), inAssembly.getExceptionList().getRootElement());
		} else if (!CA_IAST_Util.isValidString(operationName) || !CA_IAST_Util.isValidString(namespaceName)
				|| !CA_IAST_Util.isValidString(operation)) {

			ILUtil.throwILSystemException(REQUEST, ILSEV2, CODE1103, COMMAND, DESTINATIONNOTFOUND, inAssembly
					.getGlobalEnvironment().getRootElement(), inAssembly.getExceptionList().getRootElement());
		}
	}

	/**
	 * Registra un push reply to queue en el header del mensaje de acuerdo al
	 * replyToQ enviado, en caso de que este sea un valor no v�lido, la cola de
	 * replicaci�n se toma del servicio configurable.
	 * 
	 * @param replyToQ
	 *            Nombre de la cola MQ de replicaci�n
	 * @param refDFDLHeader
	 *            Es un MbElement del Header del IAST
	 * @param refHeaderOut
	 *            Es un MbElement del Header del XML-IL
	 * @param mqmd
	 *            Es un MbElement de la cabecera MQMD
	 * @param inAssembly
	 *            Representa el MbMessageAssembly de entrada
	 * @throws MbException
	 */
	public void pushRTQ(String replyToQ, MbElement refDFDLHeader, MbElement refHeaderOut, MbElement mqmd,
			MbMessageAssembly inAssembly) throws MbException {

		String callbackQueue;

		if (CA_IAST_Util.isValidString(replyToQ)) {

			callbackQueue = replyToQ.trim();
		} else {

			String callbackQueueKey = refDFDLHeader.getFirstElementByPath(IAST_ID_SERVIDOR).getValueAsString()
					+ refDFDLHeader.getFirstElementByPath(IAST_ID_APLICATIVO).getValueAsString()
					+ refDFDLHeader.getFirstElementByPath(IAST_COD_TRANSACCION).getValueAsString() + CBK;
			callbackQueue = ConfigurableServiceMultiton.getValue(POLICY_TYPE, POLICY_PROJECT,CSPARAMETER, callbackQueueKey);
		}

		if (CA_IAST_Util.isValidString(callbackQueue)) {

			String replyToQMgr = mqmd.getFirstElementByPath("ReplyToQMgr").getValueAsString();

			if (replyToQMgr != null) {
				replyToQMgr = replyToQMgr.trim();
			}

			ILUtil.pushRTQwithCorrel(refHeaderOut, callbackQueue, replyToQMgr, mqmd.getFirstElementByPath("MsgId")
					.getValueAsString());
		} else {

			ILUtil.throwILSystemException(REQUEST, ILSEV2, CODE1103, COMMAND, CANNOTRESOLVECBKQ, inAssembly
					.getGlobalEnvironment().getRootElement(), inAssembly.getExceptionList().getRootElement());
		}
	}
	
	/**Editado **/
	public void validateValuesNullByOperation(String operation, MbElement refBodyOut, String namespace) throws MbException {
		
		String xElement ="";
		
		 if(operation.equals("evaluarTransferenciaDispositivo")==true){
			xElement = "evaluarTransferenciaDispositivo/indicadorUtc";
			deleteValuesNull(xElement, refBodyOut, namespace);
	         
	    }
	}
	
	
	public void deleteValuesNull(String xElemet, MbElement refBodyOut, String namespaceName) throws MbException {

		namespaceName = refBodyOut.getFirstChild().getNamespace();
        MbNamespaceBindings ns = new MbNamespaceBindings();
        ns.addBinding(DEFAULT_TAG, namespaceName);
        MbXPath xp = new MbXPath(DEFAULT_TAG + ":" + xElemet, ns);
        List<MbElement> nodesets = (List<MbElement>) refBodyOut.evaluateXPath(xp);
        if (nodesets != null) {
            for (MbElement mbElement : nodesets) {
                String xNodeset = mbElement.getValueAsString();
                if(xNodeset == null || xNodeset.isEmpty()){
                    mbElement.delete();
                }
            }
        }    
    }	
}