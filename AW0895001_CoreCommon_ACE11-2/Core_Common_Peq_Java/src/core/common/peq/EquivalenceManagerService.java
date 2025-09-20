package core.common.peq;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.net.HttpURLConnection;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.bancolombia.integracion.cache.ESQLCacheWraper;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbUserException;

import core.common.peq.dto.Criteria;
import core.common.peq.dto.PEQDTO;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class EquivalenceManagerService {
	/**
	 * Creaci�n de variables:
	 * UDP_SCHEMA: Nombre de la UDP
	 * RESULT_DELIMETER, DATA_DELIMETER, RESULT_DESCRIPTION: Caracteres
	 * GROUP: Grupo de almacenamiento en cache
	 * request: Trama XML - petici�n PEQ
	 * response: Respuesta XML PEQ 
	 * result: Cadena de caracteres con los valores homologados
	 * UDPPEQ: Objeto a asignar las respuestas
	 * nameSpace: Espacio de nombres del PEQ
	 */
	private static String UDP_SCHEMA = "UDP_ENDPOINT_PEQ",
			RESULT_DELIMETER = "#",
			DATA_DELIMETER = "|",
			RESULT_DESCRIPTION = "-",
			GROUP = "INT_PEQ",

			UDPPEQ = "",
			nameSpace = "http://grupobancolombia.com/intf/componente/tecnico/homologacion/RecuperarParametrizacionEquivalencias/V2.0";
	/**
	 * M�todo principal. Se encarga de validar el cache existente de la
	 * aplicaci�n, en caso de no encontrarlo realiza la petici�n al PEQ y de ser
	 * exitosa crea un nueva llave y un valor para asignar al cache
	 * 
	 * @param inXML
	 *            Objeto con la trama de entrada
	 * @param dtoPEQDTO
	 *            Objeto que contiene los valores originales al cual se le
	 *            asignan las homologaciones
	 * @param context
	 *            Objeto para acceder a las UDP del sistema
	 * @return retorna el objeto con las homologaciones asignadas
	 * @throws MbException
	 *             En caso de falla el servicio entrega una excepci�n que ser�
	 *             manejada por la pol�tica UDCS_ERRORADAPTERV2_EQUIVALENCES
	 */
	public static PEQDTO getEquivalences(MbElement inXML, PEQDTO dtoPEQDTO,
			IExecutionContextPEQ context) throws MbException {

		String keys[] = getKeyString(dtoPEQDTO);
		StringBuffer allResult = new StringBuffer();
		for (int cont = 0; cont < keys.length; cont++) {
			String cache = ESQLCacheWraper.get(GROUP, keys[cont].toString());
			if (cache != null) {
				allResult.append(cache);
			} else {
				UDPPEQ = context.getUDPPEQ(UDP_SCHEMA);
				String result = xmlRequest(inXML, dtoPEQDTO, dtoPEQDTO.getCriterias().get(cont));
				allResult.append(result);
				if (result != "" || !result.equals("")) {ESQLCacheWraper.put(GROUP, keys[cont].toString(), result);}
			}
		}
		return Response(dtoPEQDTO, allResult.toString());
	}

	/**
	 * Extrae las llaves para consultar el cache para los n valores que se van a
	 * a homologar
	 * 
	 * @param dto
	 *            Objeto que contiene los valores originales a homologar
	 * @return
	 */
	private static String[] getKeyString(PEQDTO dto) {
		StringBuffer commonData = new StringBuffer();
		String criteriaKeys[] = new String[dto.getCriterias().size()];
		commonData.append(dto.getOriginApp());
		commonData.append(DATA_DELIMETER);
		commonData.append(dto.getDestinationApp());
		commonData.append(DATA_DELIMETER);
		commonData.append(dto.getOriginSociety());
		commonData.append(DATA_DELIMETER);
		commonData.append(dto.getDestinationSociety());
		commonData.append(DATA_DELIMETER);
		for (int cont = 0; cont < dto.getCriterias().size(); cont++) {
			StringBuffer key = new StringBuffer();
			key.append(commonData);
			key.append(dto.getCriterias().get(cont).getTipology());
			key.append(DATA_DELIMETER);
			key.append(dto.getCriterias().get(cont).getOriginValue());
			criteriaKeys[cont] = key.toString();
		}
		return criteriaKeys;
	}

	/**
	 * Se encarga de crear un documento XML conservando el encabezado del
	 * mensaje original y asignado un nuevo body con los valores que obtiene del
	 * objeto dtoPEQDTO. Al finalizar de crear el documento XML se transforma a
	 * un String y se env�a a connectionRequest() para realizar la conexi�n con
	 * el PEQ
	 * 
	 * @param inXML
	 *            Objeto con la trama de entrada
	 * @param dtoPEQDTO
	 *            Objeto que contiene los valores originales a homologar
	 * @param criteria
	 *            Objeto que contiene la tipolog�a y el valor origen
	 */
	private static String xmlRequest(MbElement inXML, PEQDTO dtoPEQDTO,
			Criteria criteria) {
		String request = "";
		String response = "";
		try {
			String xml = new String(inXML.getParent().toBitstream(null, null,null, 0, 1208, 0));
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(xml));
			Document doc = builder.parse(is);
			if (doc.getElementsByTagName("Body").item(0).getFirstChild() != null) {
				Node deleteBody = doc.getElementsByTagName("Body").item(0).getFirstChild();
				deleteBody.getParentNode().removeChild(deleteBody);
			}
			Node body = doc.getElementsByTagName("Body").item(0);
			body.appendChild(doc.createElementNS(nameSpace,
					"NS2:recuperarParametrizacionEquivalencias"));
			body.getFirstChild().appendChild(
					doc.createElement("requerimientoParametrizacion"));
			body.getFirstChild().getFirstChild()
					.appendChild(doc.createElement("encabezadoHomologacion"));
			Node encabezado = body.getFirstChild().getFirstChild()
					.getFirstChild();
			encabezado.appendChild(doc.createElement("aplicacionOrigen"))
					.setTextContent(dtoPEQDTO.getOriginApp());
			encabezado.appendChild(doc.createElement("aplicacionDestino"))
					.setTextContent(dtoPEQDTO.getDestinationApp());
			encabezado.appendChild(doc.createElement("sociedadOrigen"))
					.setTextContent(dtoPEQDTO.getOriginSociety());
			encabezado.appendChild(doc.createElement("sociedadDestino"))
					.setTextContent(dtoPEQDTO.getDestinationSociety());
			body.getFirstChild().getFirstChild()
					.appendChild(doc.createElement("criterioParametrizacion"));
			Node criterio = body.getFirstChild().getFirstChild()
					.getLastChild();
			criterio.appendChild(doc.createElement("tipologia"))
					.setTextContent(criteria.getTipology());
			criterio.appendChild(doc.createElement("valorOrigen"))
					.setTextContent(criteria.getOriginValue());
			doc.normalize();
			DOMSource domSource = new DOMSource(doc.getLastChild());
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
			request = writer.toString();
			response = connectionRequest(request);
			return response;
		} catch (MbException | ParserConfigurationException | SAXException
				| IOException | TransformerException e) {
			try {
				System.out.println("xmlRequest");
				System.out.println(e.toString());
				System.out.println("Endpoint PEQ: " + UDPPEQ);
				System.out.println("Request PEQ:");
				System.out.println(request);
				System.out.println("Respuesta PEQ:");
				System.out.println(response);
				
				throw new MbUserException(
						"EquivalencesManager_Adapter_PEQLookup",
						"xmlRequest()", "BIPmsgs", "2201",
						"Exception PEQ xmlRequest raised in java node",
						new String[] { e.toString() });
			} catch (MbUserException e1) {
				e1.printStackTrace();
				System.out.println(e1);
				return "";
			}
		}
	}

	/**
	 * Crea la conexi�n http o https con el PEQ designado en la
	 * UDP_ENDPOINT_PEQ. Se realiza el envi� del mensaje request y se reciben
	 * los bytes que contienen el mensaje. Los bytes se env�an a
	 * resultadosParametrizacion() para que sean transformados y asignados al
	 * objeto de respuesta
	 * 
	 * @throws MbUserException
	 * @throws IOException
	 *             En caso de falla el servicio entrega una excepci�n que ser�
	 *             manejada por la pol�tica UDCS_ERRORADAPTERV2_EQUIVALENCES
	 */
	private static String connectionRequest(String request) throws MbUserException, IOException {
		URL obj;
		DataOutputStream wr = null;
		BufferedReader in = null;
		try {
			obj = new URL(UDPPEQ);
			URLConnection con = obj.openConnection();
			con = obj.openConnection();
			if (UDPPEQ.contains("https:")) {
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
			String response = resultadosParametrizacion(request,out.toString().getBytes("UTF8"));
			return response;
		} catch (IOException | MbUserException e) {
			System.out.println(e.toString());
			System.out.println("Endpoint PEQ: " + UDPPEQ);
			throw new MbUserException("EquivalencesManager_Adapter_PEQLookup",
					"connectionRequest()", "BIPmsgs", "2203",
					"Exception PEQ connectionRequest raised in java node",
					new String[] { e.toString() });
		} finally {
			if (wr != null){wr.close();}
			if (in != null){in.close();}
		}
	}

	/**
	 * Transforma los bytes de respuesta en un objeto XML con el fin de
	 * recorrerlo y validar que se contenga la etiqueta statusCode y el mensaje
	 * que contenga sea exitoso. De existir la etiqueta extrae los valores
	 * requeridos y los asigna a la variable result usando los caracteres
	 * delimitantes.
	 * 
	 * @param msg
	 *            Bytes del mensaje de respuesta del PEQ
	 * @throws MbUserException
	 *             En caso de falla el servicio entrega una excepci�n que ser�
	 *             manejada por la pol�tica UDCS_ERRORADAPTERV2_EQUIVALENCES
	 */
	private static String resultadosParametrizacion(String request,byte[] msg)
			throws MbUserException {
		String response = new String(msg, java.nio.charset.StandardCharsets.UTF_8);
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document Rs = docBuilder.parse(new ByteArrayInputStream(msg));
			if (!Rs.getElementsByTagName("statusCode").item(0).getTextContent().equals("Success")) {
				System.out.println("resultadosParametrizacion");
				System.out.println("ResponseCodeManager_Adapter_PEQLookup");
				System.out.println("Exception PEQ resultadosParametrizacion raised in java node");
				System.out.println("Endpoint PEQ: " + UDPPEQ);
				System.out.println("Request PEQ:" + "\n" + request);
				System.out.println("Respuesta PEQ:" + "\n" + response);
				return "";
			} else {
				StringBuffer resultados = new StringBuffer();
				Node ResParam = Rs.getFirstChild().getLastChild().getFirstChild()
						.getFirstChild().getFirstChild().getNextSibling();
				while (ResParam.getNodeName() == "resultadosParametrizacion") {
					Node datosRespuesta = ResParam.getLastChild().getLastChild().getFirstChild();
					Node valorParam = ResParam.getLastChild().getFirstChild();
					ResParam = ResParam.getNextSibling();
					resultados.append(valorParam.getTextContent());
					resultados.append(DATA_DELIMETER);
					resultados.append(valorParam.getNextSibling().getTextContent());
					resultados.append(DATA_DELIMETER);
					resultados.append(datosRespuesta.getTextContent());
					resultados.append(RESULT_DESCRIPTION);
					resultados.append(datosRespuesta.getNextSibling().getTextContent());
					resultados.append(RESULT_DELIMETER);
				}
				return resultados.toString();
			}
		} catch (SAXException | IOException | ParserConfigurationException e) {
			System.out.println(e.toString());
			throw new MbUserException(
					"EquivalencesManager_Adapter_PEQLookup",
					"resultadosParametrizacion()",
					"BIPmsgs",
					"2201",
					"Exception PEQ resultadosParametrizacion raised in java node",
					new String[] { e.toString() });
		}
	}

	/**
	 * Asigna las homologaciones del PEQ al objeto para su manejo en la clase
	 * que realizo la invocaci�n.
	 * 
	 * @param dtoPEQDTO
	 *            Objeto que contiene los valores originales y al cual se le
	 *            asignan las homologaciones
	 * @param Results
	 *            Cadena de caracteres que contiene las homologaciones
	 * @return
	 */
	private static PEQDTO Response(PEQDTO dtoPEQDTO, String Results) {
		String results[] = Results != null ? Results.split(RESULT_DELIMETER): null;
		if (dtoPEQDTO != null && dtoPEQDTO.getCriterias() != null && results != null) {
			for (int cont = 0; cont < results.length&& results[cont].length() > 0; cont++) {
				String st[] = results[cont].toString().split(Pattern.quote(DATA_DELIMETER));
				Criteria criteria = dtoPEQDTO.getCriterias().get(cont);
				criteria.setDestinationValue(st[0]);
				criteria.setDescription(st[1]);
				StringTokenizer description = new StringTokenizer(st[2],RESULT_DESCRIPTION);
				criteria.setResponseCode(description.nextToken().trim());
				criteria.setResponseDesc(description.nextToken().trim());
				st = null;
			}
		}
		return dtoPEQDTO;
	}
}