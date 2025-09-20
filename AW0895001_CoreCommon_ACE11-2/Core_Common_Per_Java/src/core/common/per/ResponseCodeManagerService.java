package core.common.per;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import com.bancolombia.integracion.cache.ESQLCacheWraper;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbUserException;

import core.common.per.dto.RequestSPRDTO;
import core.common.per.dto.ResponseSPRDTO;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ResponseCodeManagerService {
	/**
	 * Creación de variables 
	 * DTOResponse: Objeto a asignar las respuestas
	 * UDP_ENDPOINT_SPR : Nombre de la UDP 
	 * DATA_DELIMETER: Se implementa por problemas de codificacion 
	 * GROUP: Grupo de almacenamiento en cache
	 * request: Trama XML - petición SPR 
	 * response: Respuesta XML SPR 
	 * UDPPER: Asigna el valor de la UDP_ENDPOINT_SPR 
	 * nameSpace: Espacio de nombres del SPR
	 */
	private static ResponseSPRDTO DTOResponse = new ResponseSPRDTO();
	private static String UDP_ENDPOINT_SPR = "UDP_ENDPOINT_SPR",
			DATA_DELIMETER = "|",
			GROUP = "INT_SPR",
			request = "",
			response = "",
			result = "",
			UDPPER = "",
			nameSpace = "http://grupobancolombia.com/intf/componente/tecnico/Homologacion/ParametrizadorRespuesta/V1.0";

	/**
	 * Método principal. Se encarga de validar el cache existente de la
	 * aplicación, en caso de no encontrarlo realiza la petición al SPR y de ser
	 * exitosa crea un nueva llave y un valor para asignar al cache
	 * 
	 * @param inXML
	 *            Objeto con la trama de entrada
	 * @param rcmDTO
	 *            Objeto request SPR
	 * 
	 * @return retorna el objeto con las homologaciones asignadas
	 * @throws MbException
	 *             En caso de falla el servicio entrega una excepción que será
	 *             manejada por la política UDCS_ERRORADAPTERV2_EQUIVALENCES
	 */
	public static ResponseSPRDTO Run(MbElement inXML, RequestSPRDTO rcmDTO,
			IExecutionContextPER executionContex) throws MbException {
		request = "";response = "";result = "";
		String key = getKeyString(rcmDTO);
		String cache = ESQLCacheWraper.get(GROUP, key);
		if (cache != null) {
			response(cache);
		} else {
			UDPPER = executionContex.getUDPPER(UDP_ENDPOINT_SPR);
			xmlRequest(inXML, rcmDTO);
			if (result != "") {ESQLCacheWraper.put(GROUP, key, result);} 
			else {addNullCache(key, rcmDTO);}
		}
		return DTOResponse;
	}
/**
 * Metodo que imprime la homologacion nula y añade al cache la llave,
 * el grupo y un delimitador para indicar que es una homologacion vacia
 * @param key llave con la homologacion nula
 * @param rcmDTO Objeto request SPR
 */
	private static void addNullCache(String key,RequestSPRDTO rcmDTO){
		System.out.println("SPR Homologacion no encontrada, recuerde despues de crearla reiniciar este pod:");
		System.out.println("codigoIdioma:" + rcmDTO.getCodigoIdioma());
		System.out.println("codigoProveedorServicio:" + rcmDTO.getCodigoProveedorServicio());
		System.out.println("codigoRespuestaProveedor:" + rcmDTO.getCodigoRespuestaProveedor());
		System.out.println("estadoRespuesta:" + rcmDTO.getEstadoRespuesta());
		ESQLCacheWraper.put(GROUP, key, DATA_DELIMETER);
	}
	/**
	 * Extrae las llaves para consultar el cache para los n valores que se van a
	 * a homologar
	 * 
	 * @param dto
	 *            Objeto que contiene los valores originales a homologar
	 * @return
	 */
	private static String getKeyString(RequestSPRDTO rcmDTO) {
		StringBuffer criteriaKeys = new StringBuffer();
		criteriaKeys.append(rcmDTO.getCodigoIdioma());
		criteriaKeys.append(DATA_DELIMETER);
		criteriaKeys.append(rcmDTO.getCodigoProveedorServicio());
		criteriaKeys.append(DATA_DELIMETER);
		criteriaKeys.append(rcmDTO.getCodigoRespuestaProveedor());
		criteriaKeys.append(DATA_DELIMETER);
		criteriaKeys.append(rcmDTO.getEstadoRespuesta());
		return criteriaKeys.toString();
	}

	/**
	 * Se encarga de crear un documento XML conservando el encabezado del
	 * mensaje original y asignado un nuevo body con los valores que obtiene del
	 * objeto rcmDTO. Al finalizar de crear el documento XML se transforma a un
	 * String y se envía a connectionRequest() para realizar la conexión con el
	 * PEQ
	 * 
	 * @param inXML
	 *            Objeto con la trama de entrada
	 * @param rcmDTO
	 *            Objeto que contiene los valores originales a homologar
	 */
	private static void xmlRequest(MbElement inXML, RequestSPRDTO rcmDTO) {
		try {
			String xml = new String(inXML.getParent().toBitstream(null, null,null, 0, 1208, 0));
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(xml));
			Document doc = builder.parse(is);
			if (doc.getElementsByTagName("Body").item(0).getFirstChild() != null) {
				Node deleteBody = doc.getElementsByTagName("Body").item(0).getFirstChild();
				deleteBody.getParentNode().removeChild(deleteBody);
			}
			Node body = doc.getElementsByTagName("Body").item(0);
			body.appendChild(doc.createElementNS(nameSpace,"NS2:parametrizadorRespuesta"));
			body.getFirstChild().appendChild(doc.createElement("codigoIdioma"));
			body.getFirstChild().getFirstChild()
					.appendChild(doc.createElement("codigo-ISO-639-1"))
					.setTextContent(rcmDTO.getCodigoIdioma());
			body.getFirstChild()
					.appendChild(doc.createElement("codigoProveedorServicio"))
					.setTextContent(rcmDTO.getCodigoProveedorServicio());
			body.getFirstChild()
					.appendChild(doc.createElement("codigoRespuestaProveedor"))
					.setTextContent(rcmDTO.getCodigoRespuestaProveedor());
			body.getFirstChild()
					.appendChild(doc.createElement("estadoRespuesta"))
					.setTextContent(rcmDTO.getEstadoRespuesta());
			doc.normalize();
			DOMSource domSource = new DOMSource(doc.getLastChild());
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
			request = writer.toString();
			connectionRequest();
		} catch (ParserConfigurationException | SAXException | IOException
				| TransformerException | MbException e) {
			try {
				System.out.println(e.toString());
				System.out.println("Endpoint SPR: " + UDPPER);
				System.out.println("Request SPR:");
				System.out.println(request);
				System.out.println("Respuesta SPR:");
				System.out.println(response);
				throw new MbUserException(
						"ResponseCodeManager_Adapter_PERLookup",
						"xmlRequest()", "BIPmsgs", "2201",
						"Exception PER xmlRequest raised in java node",
						new String[] { e.toString() });
			} catch (MbUserException e1) {
				e1.printStackTrace();
				System.out.println(e1);
			}
		}
	}

	/**
	 * Crea la conexión http o https con el SPR designado en la
	 * UDP_ENDPOINT_SPR. Se realiza el envió del mensaje request y se reciben
	 * los bytes que contienen el mensaje. Los bytes se envían a
	 * resultadosParametrizacion() para que sean transformados y asignados al
	 * objeto de respuesta
	 * 
	 * @throws MbUserException
	 * @throws IOException
	 *             En caso de falla el servicio entrega una excepción que será
	 *             manejada por la política UDCS_ERRORADAPTERV2_EQUIVALENCES
	 */
	private static void connectionRequest() throws MbUserException, IOException {
		URL obj;
		DataOutputStream wr = null;
		BufferedReader in = null;
		try {
			obj = new URL(UDPPER);
			URLConnection con = obj.openConnection();
			con = obj.openConnection();
			if (UDPPER.contains("https:")) {
				((HttpsURLConnection) con).setRequestMethod("POST");
			} else {
				((HttpURLConnection) con).setRequestMethod("POST");
			}
			con.setRequestProperty("Content-Type","application/soap+xml; charset=utf-8");
			con.setDoOutput(true);
			con.setConnectTimeout(2000);
			con.setReadTimeout(3000);
			wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(request);
			wr.flush();
			wr.close();
			in = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
			int bufferSize = 1024;
			char[] buffer = new char[bufferSize];
			StringBuilder out = new StringBuilder();
			for (int numRead; (numRead = in.read(buffer, 0, buffer.length)) > 0;) {
				out.append(buffer, 0, numRead);
			}
			in.close();
			resultadosParametrizacion(out.toString().getBytes("UTF8"));
		} catch (IOException | MbUserException e) {
			System.out.println(e.toString());
			System.out.println("Endpoint SPR: " + UDPPER);
			throw new MbUserException("ResponseCodeManager_Adapter_PERLookup",
					"xmlRequest()", "BIPmsgs", "2201",
					"Exception PER connectionRequest raised in java node",
					new String[] { e.toString() });
		} finally {
			if (wr != null){wr.close();}
			if (in != null){in.close();}
		}
	}

	/**
	 * Transforma los bytes de respuesta en un objeto XML con el fin de
	 * recorrerlo y validar que se contenga la etiqueta body. De existir la
	 * etiqueta extrae los valores requeridos y los asigna al objeto DTOResponse
	 * 
	 * @param msg
	 *            Bytes del mensaje de respuesta del SPR
	 * @throws MbUserException
	 *             En caso de falla el servicio entrega una excepción que será
	 *             manejada por la política UDCS_ERRORADAPTERV2_EQUIVALENCES
	 */
	private static void resultadosParametrizacion(byte[] msg)
			throws MbUserException {
		response = new String(msg, java.nio.charset.StandardCharsets.UTF_8);
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document Rs = docBuilder.parse(new ByteArrayInputStream(msg));
			if (Rs.getFirstChild().getLastChild().getFirstChild() == null) {
				System.out.println("ResponseCodeManager_Adapter_PERLookup");
				System.out.println("Exception SPR resultadosParametrizacion raised in java node");
				System.out.println("Endpoint SPR: " + UDPPER);
				System.out.println("Request SPR:" + "\n" + request);
				System.out.println("Respuesta SPR:" + "\n" + response);
			} else {
				// Evalua el resultado de la homologacion
				StringBuffer resultado = new StringBuffer();
				resultado.append(Rs.getElementsByTagName("codigoRespuestaCanal")
						.item(0).getTextContent());
				resultado.append(DATA_DELIMETER);
				resultado.append(Rs.getElementsByTagName("descripcionNegocio")
						.item(0).getTextContent());
				resultado.append(DATA_DELIMETER);
				resultado.append(Rs.getElementsByTagName("codigoCanonico")
						.item(0).getTextContent());
				response(resultado.toString());
			}
		} catch (SAXException | IOException | ParserConfigurationException e) {
			System.out.println(e.toString());
			throw new MbUserException(
					"ResponseCodeManager_Adapter_PERLookup",
					"resultadosParametrizacion()",
					"BIPmsgs",
					"2201",
					"Exception PER resultadosParametrizacion raised in java node",
					new String[] { e.toString() });
		}
	}

	/**
	 * Asigna las homologaciones del SPR al objeto para su manejo en la clase
	 * que realizo la invocación.
	 * 
	 * @param inputString
	 *            Cadena de caracteres que contiene las homologaciones
	 */
	private static void response(String inputString) {
		if (inputString == DATA_DELIMETER){
			DTOResponse.setCodigoRespuestaCanal(null);
			DTOResponse.setDescripcionNegocio(null);
			DTOResponse.setCodigoCanonico(null);
		}else{
			String[] cache = inputString.split("\\|");
			DTOResponse.setCodigoRespuestaCanal(cache[0]);
			DTOResponse.setDescripcionNegocio(cache[1]);
			DTOResponse.setCodigoCanonico(cache[2]);
		}
		result = inputString;
	}
}