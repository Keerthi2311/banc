package core.common.proxy;

import java.util.List;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbNamespaceBindings;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.broker.plugin.MbXPath;

public class EquivalencesProxy_JavaCompute extends MbJavaComputeNode {
	private static final String USEREX_GETLISTMBELEMENT = "get_List_MbElement()";
	private static final String SYSEX_IE = "Server.ESB.InfrastructureError";
	private static final String SYSEX_IM = "Client.InvalidMessage";
	private static final String SYSEX_ISRE = "Server.ESB.InternalServerResponseError";
	private static final String SYSEX_IPRM = "Server.ESB.InvalidProviderResponseMessage";
	private static final String USEREX_MSGSOURCE = "";
	private static final String USEREX_MSGKEY = "";
	private static final String USEREX_EVALUATE = "evaluate()";
	private static final String PATH_PARAMETRIZACION = "/peq_ns:parametrizadorEquivalenciasResponse/parametrizacion";
	private static final String INF_ERROR = "Error de infraestructura en el llamado al Parametrizador de Equivalencias.";
	private static final String INF_ERROR_CLIENT = "Mensaje de solicitud no v�lido. Equivalencia no encontrada en el Parametrizador de Equivalencias.";
	private static final String INF_ERROR_PROVIDER = "Mensaje de respuesta no v�lido. Equivalencia no encontrada en el Parametrizador de Equivalencias.";
	private static final String NS_IL = "http://grupobancolombia.com/intf/IL/esbXML/V3.0";
	private static final String NS_PEQ = "http://grupobancolombia.com/intf/componente/tecnico/homologacion/ParametrizadorEquivalencias/V1.0";

	public void evaluate(MbMessageAssembly assembly) throws MbException {

		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");
		String pathOriginalMessage = get_UDP_Broker("UDP_pathOriginalMessage");
		MbMessage message = assembly.getMessage();

		MbMessage environmentMessage = assembly.getGlobalEnvironment();

		List<MbElement> parametrizacion = get_List_MbElement(
				PATH_PARAMETRIZACION, NS_PEQ, "peq_ns", message);

		MbElement resultadosParametrizacion;
		String desRespuestaGen = null;
		try {
			resultadosParametrizacion = (MbElement) parametrizacion.get(0)
					.getFirstElementByPath("resultadosParametrizacion");
			desRespuestaGen = (String) parametrizacion.get(0)
					.getFirstElementByPath("datosRespuesta")
					.getFirstElementByPath("codigoRespuesta")
					.getValueAsString();
		} catch (NullPointerException e) {
			throw new MbUserException(
					EquivalencesProxy_JavaCompute.class.getName(),
					USEREX_EVALUATE,
					USEREX_MSGSOURCE,
					USEREX_MSGSOURCE,
					"Elemento de respuesta "
							+ PATH_PARAMETRIZACION
							+ "/resultadosParametrizacion/datosRespuesta/codigoRespuesta no encontrado.",
					null);
		}

		if (desRespuestaGen.equals("00")) {
			MbMessage outMessage = new MbMessage(message);
			MbMessageAssembly outAssembly = new MbMessageAssembly(assembly,
					outMessage);
			out.propagate(outAssembly);
		} else {
			MbMessage outMessage = new MbMessage();
			copyMessageHeaders(message, outMessage);
			MbElement outRoot = outMessage.getRootElement();
			String codigoRespuesta = "";
			MbElement environment = environmentMessage.getRootElement()
					.getFirstElementByPath("Variables");

			List<MbElement> originalMessage = get_List_MbElement(
					pathOriginalMessage + "/XMLNSC", NS_IL, "tns",
					environmentMessage);
			outRoot.addAsLastChild((MbElement) originalMessage.get(0).copy());
			List<MbElement> esbXMLref = get_List_MbElement("/tns:esbXML",
					NS_IL, "tns", outMessage);
			
			MbElement headerOut = (MbElement) esbXMLref.get(0)
					.getFirstElementByPath("Header");
			if (resultadosParametrizacion!=null){
				// BCSWF00080973 Start
				//environment.addAsLastChild(resultadosParametrizacion.copy());
				while(resultadosParametrizacion!=null && 
						resultadosParametrizacion.getName().equals("resultadosParametrizacion")){
					environment.addAsLastChild(resultadosParametrizacion.copy());
					resultadosParametrizacion = resultadosParametrizacion.getNextSibling();
				}
				// BCSWF00080973 End
			}
			if (desRespuestaGen.equals("05")) {
				// BCSWF00080973 Start
				resultadosParametrizacion = (MbElement) parametrizacion.get(0)
				.getFirstElementByPath("resultadosParametrizacion");
				// BCSWF00080973 End
				
				while (resultadosParametrizacion != null) {
					codigoRespuesta = (String) resultadosParametrizacion
							.getFirstElementByPath("valorParametrizado")
							.getFirstElementByPath("datosRespuesta")
							.getFirstElementByPath("codigoRespuesta")
							.getValue();
					MbElement criterioParametrizacion=resultadosParametrizacion
					.getFirstElementByPath("criterioParametrizacion");
					
					String tipologia=(String)criterioParametrizacion
					.getFirstElementByPath("tipologia").getValue();						
					
					String valorOrigen=(String)criterioParametrizacion
					.getFirstElementByPath("valorOrigen").getValue();

					if (!codigoRespuesta.equals("00")) {
						if (headerOut.getFirstElementByPath("responseData") != null) {
							MbElement responseDataHeader = headerOut
									.getFirstElementByPath("responseData");
							responseDataHeader.detach();
							if (codigoRespuesta.equals("02")) {
								Set_Error_Header(headerOut, SYSEX_IPRM,
										INF_ERROR_PROVIDER + " TIPOLOGIA: " + tipologia + " VALOR_ORIGEN: " + valorOrigen);
							} else {
								Set_Error_Header(headerOut, SYSEX_ISRE,
										INF_ERROR);
							}
							break;
						} else {
							if (codigoRespuesta.equals("02")) {
								Set_Error_Header(headerOut, SYSEX_IM,
										INF_ERROR_CLIENT + " TIPOLOGIA: " + tipologia + " VALOR_ORIGEN: " + valorOrigen);
							} else {
								Set_Error_Header(headerOut, SYSEX_IE, INF_ERROR);
							}
							break;
						}
					}
					resultadosParametrizacion = resultadosParametrizacion
							.getNextSibling();
				}
			} else {
				if (headerOut.getFirstElementByPath("responseData") != null) {
					MbElement responseDataHeader = headerOut
							.getFirstElementByPath("responseData");
					responseDataHeader.detach();
					Set_Error_Header(headerOut, SYSEX_ISRE, INF_ERROR);
				} else {
					Set_Error_Header(headerOut, SYSEX_IE, INF_ERROR);
				}
			}
			MbMessageAssembly outAssembly = new MbMessageAssembly(assembly,
					outMessage);
			alt.propagate(outAssembly);
		}

	}

	public String get_UDP_Broker(String s_udp) throws MbException {
		String ls_udp_valor = USEREX_MSGSOURCE;
		Object obj_udp = null;
		obj_udp = getUserDefinedAttribute(s_udp);
		if (obj_udp != null) {
			ls_udp_valor = obj_udp.toString();
		}
		return ls_udp_valor;
	}


	@SuppressWarnings({ "unchecked" })//Fernando Mondrag�n Amaya 06/02/2017
	public static List<MbElement> get_List_MbElement(String Xpath, String nameSpace, String defnameSpace, MbMessage message) throws MbException {
		MbXPath refHeder = new MbXPath(Xpath);
		MbNamespaceBindings nspace = new MbNamespaceBindings();
		//refHeder.addNamespacePrefix(defnameSpace, nameSpace);
		nspace.addBinding(defnameSpace, nameSpace);
		refHeder.setNamespaceBindings(nspace);
		List<MbElement> result = null;
		try {
			result = (List<MbElement>) message.evaluateXPath(refHeder);
		} catch (Exception e) {
			throw new MbUserException(EquivalencesProxy_JavaCompute.class
					.getName(), USEREX_GETLISTMBELEMENT, USEREX_MSGSOURCE,
					USEREX_MSGKEY, "No pudo ser resuelto el elemento XPath "
							+ Xpath + " como Lista.", null);
		}
		return result;

	}

	public void Set_Error_Header(MbElement HeaderOut, String faultcode,
			String detail) throws MbException {
		MbElement responseData = HeaderOut.getFirstElementByPath("requestData")
				.createElementAfter(0, "responseData", null);
		MbElement status = responseData.createElementAsLastChild(
				MbElement.TYPE_NAME, "status", null);
		status.createElementAsFirstChild(MbElement.TYPE_VALUE, "statusCode",
				"SystemException");
		MbElement SystemException = status.createElementAsLastChild(
				MbElement.TYPE_NAME, "systemException", null);
		SystemException.createElementAsLastChild(MbElement.TYPE_VALUE,
				"faultcode", faultcode);
		SystemException.createElementAsLastChild(MbElement.TYPE_VALUE,
				"faultstring", "System Exception.");
		SystemException.createElementAsLastChild(MbElement.TYPE_VALUE,
				"detail", detail);
	}

	public void copyMessageHeaders(MbMessage inMessage, MbMessage outMessage)
			throws MbException {
		MbElement outRoot = outMessage.getRootElement();
		MbElement header = inMessage.getRootElement().getFirstChild();
		while (header != null && header.getNextSibling() != null) {
			outRoot.addAsLastChild(header.copy());
			header = header.getNextSibling();
		}
	}

}
