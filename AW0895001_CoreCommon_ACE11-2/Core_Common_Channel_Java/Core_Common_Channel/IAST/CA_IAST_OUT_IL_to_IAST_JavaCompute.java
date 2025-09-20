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
import com.ibm.broker.plugin.MbXPath;

import core.common.util.ILUtil;
import core.common.util2.ConfigurableServiceMultiton;



public class CA_IAST_OUT_IL_to_IAST_JavaCompute extends MbJavaComputeNode {

	private static final String BINARY1 = "Binary1";
	private static final String CA_IAST_MESSAGETYPE = "{http://grupobancolombia.com/intf/DFDL/CA_IAST/V1.0}:IAST";
	private static final String NS_DFDL = "http://grupobancolombia.com/intf/DFDL/CA_IAST/V1.0";
	private static final String CA_IAST_MESSAGELIB = "Core_Common_Channel";															  
	private static final String TR = "TR";
	private static final String SUCCESS = "Success";
	private static final String SYSTEMEXCEPTION = "SystemException";
	private static final String BUSINESSEXCEPTION = "BusinessException";
	private static final String PROVIDEREXCEPTION = "BusinessException";
	private static final String CONFIGURABLESERVICENAME = "UDCS_CAIAST";
	private static final String CSPARAMETER = CONFIGURABLESERVICENAME;
	private static final String MSG = "MSG";
	public final static String POLICY_PROJECT = "DefaultPolicies";
	public final static String POLICY_TYPE = "UserDefined";

	private static final String XML1 = "XML1";
	private static final String SUCCESSCODE = "0000";
	private static final String SUCCESSMAYUS = "SUCCESS";
	private static final String REPETITIOS_STRING = "";
	private static final String WHITE_SPACE = " ";
	private static final String IBEX = "IBEX";
	private static final String DEFAULT_TAG = "NS2";
	private static final String RESPONSE = "RESPONSE";
	private static final String ILSEV2 = "ILSEv2";
	private static final String CODE1203 = "1203";
	private static final String COMMAND = "COMMAND";
	private static final String IAST_ID_SERVIDOR = "ID_SERVIDOR";
	private static final String IAST_COD_TRANSACCION = "COD_TRANSAC";
	private static final String IAST_TIPO_MENSAJE = "TIPO_MENSAJE";
	private static final String IAST_ORIENTACION = "ORIENTACION";
	private static final String IAST_REQUIERE_RESPUESTA = "REQUIERE_RES";
	private static final String IAST_CODIGO_RESP = "COD_RES";
	private static final String IAST_DESCRIPCION_RESP = "DESC_RES";
	private static final String IAST_CARGA_UTIL = "CARGA_UTIL";
	private static final int MAXIMUM_LENGTH_FAULT = 50;
	private static final int MAXIMUM_LENGTH_FAULTCODE = 3;
	private static final int OPTIONS_VALIDATE1 = MbElement.VALIDATE_CONTENT | MbElement.VALIDATE_VALUE
			| MbElement.VALIDATE_LOCAL_ERROR;
	private static final int OPTIONS_VALIDATE2 = MbElement.VALIDATE_CONTENT_AND_VALUE | MbElement.VALIDATE_EXCEPTION;

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {

		MbOutputTerminal out = getOutputTerminal("out");
		MbMessage outMessage = new MbMessage();
		MbMessage env = inAssembly.getGlobalEnvironment();
		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;

		try {
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);

			ILUtil.copyMessageHeaders(inMessage, outMessage);
			MbElement refInHeader = inMessage.getRootElement().getFirstElementByPath("XMLNSC/esbXML/Header");
			MbElement refInBody = inMessage.getRootElement().getFirstElementByPath("XMLNSC/esbXML/Body");

			if (buildIAST(env, inMessage, outMessage, refInHeader, refInBody)) {

				out.propagate(outAssembly);
			}

		} catch (MbException e) {
			// Re-throw to allow Broker handling of MbException
			throw e;
		} catch (RuntimeException e) {
			// Re-throw to allow Broker handling of RuntimeException
			throw e;
		} catch (Exception e) {

			ILUtil.throwILSystemException(RESPONSE, ILSEV2, CODE1203, COMMAND, e.getMessage(), env.getRootElement(),
					inAssembly.getExceptionList().getRootElement());
		}
	}

	/**
	 * M�todo que se encarga de serializar el mensaje XML-IL a IAST, de acuerdo
	 * al encoding y codedCharSetId de las propiedades del mensaje y tomando del
	 * servicio configurable la informaci�n para obtener el formato del cuerpo
	 * del mensaje.
	 * 
	 * @param env
	 *            MbMessage del global enviroment
	 * @param inMessage
	 *            MbMessage del mensaje de entrada
	 * @param outMessage
	 *            MbMessage del mensaje de salida
	 * @param refInHeader
	 *            MbElement del Header del mensaje de entrada XML-IL
	 * @param refInBody
	 *            MbElement del Body del mensaje de entrada XML-IL
	 * @return Retorna falso en caso de que la transacci�n no requiera respuesta
	 * @throws MbException
	 */
	public boolean buildIAST(MbMessage env, MbMessage inMessage, MbMessage outMessage, MbElement refInHeader,
			MbElement refInBody) throws MbException {

		int encoding;
		int codedCharSetId;

		MbElement properties = outMessage.getRootElement().getFirstElementByPath("Properties");
		MbElement mqmd = outMessage.getRootElement().getFirstElementByPath("MQMD");
		MbElement variables = env.getRootElement().createElementAsLastChild(MbElement.TYPE_NAME, "Variables", null);
		MbElement inXML = inMessage.getRootElement().getFirstElementByPath("XMLNSC");

		encoding = Integer.parseInt(properties.getFirstElementByPath("Encoding").getValueAsString());
		codedCharSetId = Integer.parseInt(properties.getFirstElementByPath("CodedCharSetId").getValueAsString());

		byte[] decoded = Base64.decode(refInHeader.getFirstElementByPath("messageContext/property/value")
				.getValueAsString());
		
		MbElement dfdl = variables.createElementAsLastChildFromBitstream(decoded, MbDFDL.PARSER_NAME,
				CA_IAST_MESSAGETYPE, CA_IAST_MESSAGELIB, BINARY1, encoding, codedCharSetId, OPTIONS_VALIDATE1);

		MbElement refHeaderControl = dfdl.getFirstElementByPath("IAST/Header/ENCABEZADO_CONTROL");
		MbElement refHeaderData = dfdl.getFirstElementByPath("IAST/Header/ENCABEZADO_DATOS");

		if ("N".equals(refHeaderData.getFirstElementByPath(IAST_REQUIERE_RESPUESTA).getValueAsString())) {
			// VALIDAR///////////////////////////////////////////
			return false;
		}

		MbElement refOutDFDL = outMessage.getRootElement().createElementAsLastChild(MbDFDL.PARSER_NAME)
				.createElementAsLastChild(MbElement.TYPE_NAME, "IAST", null);

		refOutDFDL.setNamespace(NS_DFDL);

		refHeaderData.getFirstElementByPath(IAST_TIPO_MENSAJE).setValue(TR);
		refHeaderControl.getFirstElementByPath(IAST_ORIENTACION).setValue("2");

		String respStatus = inXML.getFirstElementByPath("esbXML/Header/responseData/status/statusCode")
				.getValueAsString();

		properties.getFirstElementByPath("MessageSet").setValue(CA_IAST_MESSAGELIB);
		properties.getFirstElementByPath("MessageType").setValue(CA_IAST_MESSAGETYPE);
		properties.getFirstElementByPath("MessageFormat").setValue(BINARY1);

		if (respStatus.equals(SUCCESS)) {

			String messageLibKey = refHeaderControl.getFirstElementByPath(IAST_ID_SERVIDOR).getValueAsString()
					+ refHeaderData.getFirstElementByPath(IAST_COD_TRANSACCION).getValueAsString() + MSG;
			

			String messageLib = ConfigurableServiceMultiton.getValue(POLICY_TYPE, POLICY_PROJECT,CSPARAMETER, messageLibKey);
		

			String operation = refInBody.getFirstChild().getName();
			String namespace = refInBody.getFirstChild().getNamespace();
			String massageType = "{" + namespace + "}:" + operation;

			MbElement dfdlBody = variables.createElementAsLastChild(MbElement.TYPE_NAME, "RespMessage", null)
					.createElementAsLastChild(MbDFDL.PARSER_NAME);

			dfdlBody.addAsFirstChild(inXML.getFirstElementByPath("esbXML/Body/" + operation).copy());

			

			byte[] inBitStream = dfdlBody.toBitstream(massageType, "{" + messageLib + "}", XML1, encoding,
					codedCharSetId, OPTIONS_VALIDATE2);

			String cargaUtil = new String(inBitStream);

			refOutDFDL.createElementAsLastChild(MbElement.TYPE_NAME, "Body", null).createElementAsLastChild(
					MbElement.TYPE_NAME_VALUE, IAST_CARGA_UTIL, cargaUtil);

			refHeaderData.getFirstElementByPath(IAST_CODIGO_RESP).setValue(SUCCESSCODE);
			refHeaderData.getFirstElementByPath(IAST_DESCRIPCION_RESP).setValue(SUCCESSMAYUS);
		} else if (respStatus.equals(SYSTEMEXCEPTION)) {

			String faultstring = refInHeader.getFirstElementByPath("responseData/status/systemException/faultstring")
					.getValueAsString();
			String faultCode = refInHeader.getFirstElementByPath("responseData/status/systemException/faultcode")
					.getValueAsString();

			faultstring = faultstring.substring(0, MAXIMUM_LENGTH_FAULTCODE);

			refHeaderData.getFirstElementByPath(IAST_CODIGO_RESP).setValue("I" + faultstring);

			if (CA_IAST_Util.isValidLength(faultCode, MAXIMUM_LENGTH_FAULT)) {
				refHeaderData.getFirstElementByPath(IAST_DESCRIPCION_RESP).setValue(faultCode);
			} else {
				refHeaderData.getFirstElementByPath(IAST_DESCRIPCION_RESP).setValue(
						faultCode.substring(0, MAXIMUM_LENGTH_FAULT));
			}
		} else if (respStatus.equals(BUSINESSEXCEPTION) || respStatus.equals(PROVIDEREXCEPTION)) {

			String descRes = refInBody.getFirstChild().getName();

			refHeaderData.getFirstElementByPath(IAST_CODIGO_RESP).setValue(IBEX);

			if (CA_IAST_Util.isValidLength(descRes, MAXIMUM_LENGTH_FAULT)) {
				refHeaderData.getFirstElementByPath(IAST_DESCRIPCION_RESP).setValue(descRes);
			} else {
				refHeaderData.getFirstElementByPath(IAST_DESCRIPCION_RESP).setValue(
						descRes.substring(0, MAXIMUM_LENGTH_FAULT));
			}

			String statusCode = refInBody.getFirstChild().getFirstElementByPath("genericException/code")
					.getValueAsString();
			String statusDesc = refInBody.getFirstChild().getFirstElementByPath("genericException/description")
					.getValueAsString();

			refOutDFDL.createElementAsLastChild(MbElement.TYPE_NAME, "Body", null).createElementAsLastChild(
					MbElement.TYPE_NAME_VALUE, IAST_CARGA_UTIL, statusCode + "|" + statusDesc);
		}
		
		refOutDFDL.createElementAsFirstChild(MbElement.TYPE_NAME, "Header", null);
		refOutDFDL = refOutDFDL.getFirstElementByPath("Header");
		
		refOutDFDL.addAsFirstChild(refHeaderData.copy());
		refOutDFDL.addAsFirstChild(refHeaderControl.copy());

		popRTQWithCorrel_temp(inXML.getFirstElementByPath("esbXML/Header"), mqmd, false);
		return true;
	}
	
	
	public static byte[] hexStrToBytes_temp(String hexStr) {
		 
        byte[] val = new byte[hexStr.length() / 2];
        for (int i = 0; i < val.length; i++) {
            int index = i * 2;
            int j = Integer.parseInt(hexStr.substring(index, index + 2), 16);
            val[i] = (byte) j;
        }
 
        return val;
    }
	private static final byte[] MQCI_NONE_temp = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	private static final int MQIRIPREFIX_temp = 15;
	public static void popRTQWithCorrel_temp(MbElement header, MbElement mqmd, boolean deleteRoutingStack)
			throws MbException {

		String[] reply;
		MbElement routingStack = header.getFirstElementByPath("routingStack");

		if (routingStack != null) {
			MbElement uri = routingStack.getLastChild().getFirstElementByPath("ReplyTo/address");
			MbElement cid = routingStack.getLastChild().getFirstElementByPath("ReplyTo/correlationId");

			if (cid != null) {

				mqmd.getFirstElementByPath("MsgId").setValue(hexStrToBytes_temp(cid.getValueAsString()));
			} else {

				mqmd.getFirstElementByPath("MsgId").setValue(MQCI_NONE_temp);
			}

			if (uri != null) {

				String uriString = uri.getValueAsString();
				reply = uriString.substring(MQIRIPREFIX_temp, uriString.length()).split("@");
				mqmd.getFirstElementByPath("ReplyToQ").setValue(reply[0]);
				mqmd.getFirstElementByPath("ReplyToQMgr").setValue(reply[1]);

				if (deleteRoutingStack) {

					routingStack.getLastChild().delete();
				}
			}
		}
	}
	
	/**
	 * Evalua si hay elementos de repeticiones v�lidos para agregar en el cuerpo
	 * del mensaje.
	 * 
	 * @param xPathsString
	 *            xPath de los elementos que determinan las repeticiones. Si son
	 *            varios se separan por espacios en blanco.
	 * @param namespace
	 *            Namespace del servicio recuperado del cuerpo del mensaje.
	 * @param dfdlBody
	 *            MbElement del cuerpo de mensaje de salida
	 * @param operation
	 *            Nombre de la operaci�n en la firma del servicio
	 * @throws MbException
	 */
	public void evaluateRepetitions(String xPathsString, String namespace, MbElement dfdlBody) throws MbException {

		if (CA_IAST_Util.isValidString(xPathsString)) {

			String[] xPathsList = xPathsString.split(WHITE_SPACE);

			if (xPathsList.length > 0) {

				for (String xPath : xPathsList) {

					String[] xPathArray = xPath.split("/");
					xPathArray[0] = DEFAULT_TAG + ":" + xPathArray[0];

					MbNamespaceBindings ns = new MbNamespaceBindings();
					ns.addBinding(DEFAULT_TAG, namespace);

					addRepeats(0, xPathArray, dfdlBody, ns);
				}
			}
		}
	}

	/**
	 * 
	 * M�todo recursivo que recorre el arbol del mensaje de acuerdo al xPaths
	 * enviado para agregar un elemento que determina la cantidad de
	 * repeticiones de un arreglo.
	 * 
	 * @param index
	 *            �ndice entero que determina la posici�n del arreglo del xPath
	 * @param xPaths
	 *            Arreglo de String con el xPath del elemento que se repite
	 * @param elementTree
	 *            MbElement del arbol de mensaje actual, iniciando en el body
	 *            del mensaje XML-IL
	 * @param namespace
	 *            Namespace del servicio
	 * @throws MbException
	 */
	public void addRepeats(int index, String[] xPaths, MbElement elementTree, MbNamespaceBindings ns)
			throws MbException {

		if (index < xPaths.length - 1) {

			MbXPath xp = new MbXPath(xPaths[index], ns);
			List<MbElement> element = (List<MbElement>) elementTree.evaluateXPath(xp);

			if (element != null && !element.isEmpty()) {

				for (MbElement eleme : (List<MbElement>) element) {

					addRepeats(index + 1, xPaths, (MbElement) eleme, ns);
				}
			}
		} else {

			String fieldName = xPaths[index].replace(REPETITIOS_STRING, "");

			MbXPath xp = new MbXPath(fieldName, ns);
			List<MbElement> element = (List<MbElement>) elementTree.evaluateXPath(xp);

			if (element != null && !element.isEmpty()) {

				element.get(0).createElementBefore(MbElement.TYPE_NAME_VALUE, xPaths[index], element.size());
			}
		}
	}
}

