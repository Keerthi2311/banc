package com.grupobancolombia.integracion.sti.mapping;


import com.grupobancolombia.integracion.sti.mapping.IMessageMapping;
import com.grupobancolombia.integracion.sti.mapping.MessageMappingFactory;
import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.broker.plugin.MbXMLNSC;

import core.common.peq.IExecutionContextPEQ;
import core.common.per.IExecutionContextPER;

/**
 * Service that receives a IAST to IL request transformation from the message
 * broker flow
 * 
 * @author Jorge Alberto Tchira Salazar
 * @version 1.0
 * @since 2017-03-02
 */
public class IASTtoILService extends MbJavaComputeNode implements IExecutionContextPER, IExecutionContextPEQ {
	
	public String SUCCESS = "Success";
	public String BUSINESS = "BusinessException";
	public String RESPONSE = "Response";
	public String N_SPACE_IL = "http://grupobancolombia.com/intf/IL/esbXML/V3.0";
	/**
	 * Method that process a IAST to IL request
	 * 
	 * @param assembly
	 *            input message root
	 * @throws MbException
	 */
	public void evaluate(MbMessageAssembly assembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");

		MbElement headerIL = assembly.getGlobalEnvironment().getRootElement()
				.getFirstElementByPath("Variables/PutMessageMementoQ/XMLNSC/esbXML/Header");

		// Get service name, operation and version
		String service 	 = headerIL.getFirstElementByPath("requestData/destination/name").getValueAsString();
		String operation = headerIL.getFirstElementByPath("requestData/destination/operation").getValueAsString();
		String namespace = headerIL.getFirstElementByPath("requestData/destination/namespace").getValueAsString();
		String version	 = namespace.substring(namespace.length() - 4);
		
		StringBuffer sb = new StringBuffer();
		sb.append(service);
		sb.append(Character.toUpperCase(operation.charAt(0)));
		sb.append(operation.substring(1, operation.length()));
		sb.append(version.replace(".", ""));
		// Create message out
		MbMessageAssembly assemblyOut = createMessageOut(assembly, headerIL);
		MbElement response = assemblyOut.getMessage().getRootElement();
		
		// Transform IL to IAST message
		try {
			IMessageMapping messageMapping = MessageMappingFactory
			.getMessageMapping(sb.toString());
			
			//El codigo fue exitoso
			MbMessage message 				= new MbMessage(assembly.getMessage());
			MbMessageAssembly RequestIAST 	= new MbMessageAssembly(assembly, message);
			MbElement blobReferenceIAST 	= RequestIAST.getMessage().getRootElement().getLastChild();		
			messageMapping.executeIASTtoILTransformation(blobReferenceIAST,	response, this, this);
			
		} catch (IllegalArgumentException e) {
			throw new MbUserException(this, "evaluate()", "", "", e.toString(),
					null);
		} catch (IllegalAccessException e) {
			throw new MbUserException(this, "evaluate()", "", "", e.toString(),
					null);
		} catch (InstantiationException e) {
			throw new MbUserException(this, "evaluate()", "", "", e.toString(),
					null);
		} catch (ClassNotFoundException e) {
			throw new MbUserException(this, "evaluate()", "", "", e.toString(),
					null);
		}
		out.propagate(assemblyOut);
	}

	/**
	 * Create a message out IL structure
	 * 
	 * @param assembly
	 *            input message root
	 * @param headerIL
	 *            a MbElement with a headerIL reference
	 * @return MbMessageAssembly with the message out
	 * @throws MbException
	 */
	public MbMessageAssembly createMessageOut(MbMessageAssembly assembly,
			MbElement headerIL) throws MbException {

		// message in references
		MbMessage messageIn = assembly.getMessage();
		MbMessage localEnvirotmentIn = assembly.getLocalEnvironment();
		MbMessage localEnvirotmentOut = new MbMessage(localEnvirotmentIn);

		// create message out
		MbMessage messageOut = new MbMessage();
		MbMessageAssembly assemblyOut = new MbMessageAssembly(assembly,
				localEnvirotmentOut, assembly.getExceptionList(), messageOut);

		// copia las cabeceras de entrada
		copyMessageHeaders(messageIn, messageOut);
		
		//crea el mensaje de salida en formato XMLNSC crea la estructura IL de salida
		addXMLNSC(messageOut, headerIL);
		
		return assemblyOut;
	}
	
	/**
	 * Create a XMLNSC domain parser to a output message
	 * 
	 * @param message
	 *            output
	 * @throws MbException
	 */
	public void addXMLNSC(MbMessage message, MbElement headerIL) throws MbException {
		MbElement root = message.getRootElement();
		// create a top level 'parser' element with parser class name
		MbElement xmlnsc = root.createElementAsLastChild(MbXMLNSC.PARSER_NAME);
		try {
			//Crea la estructura de salida
			xmlnsc = xmlnsc.createElementAsLastChild(MbElement.TYPE_NAME,"esbXML", null);
			xmlnsc.setNamespace(N_SPACE_IL);
			
			//Copia el Header de Request a la salida
			xmlnsc.addAsLastChild(headerIL.copy());
			
			//Crea el elmento Body como ultimo hujo
			xmlnsc.createElementAsLastChild(MbElement.TYPE_NAME, "Body", null); 
	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Copy message input headers to message output headers
	 * 
	 * @param inMessage
	 *            input message
	 * @param outMessage
	 *            output message
	 * @throws MbException
	 */
	public void copyMessageHeaders(MbMessage inMessage, MbMessage outMessage)
			throws MbException {
		MbElement outRoot = outMessage.getRootElement();
		MbElement header = inMessage.getRootElement().getFirstChild();

		while (header != null && header.getNextSibling() != null) {
			outRoot.addAsLastChild(header.copy());
			header = header.getNextSibling();
		}
	}
	public String getUDPPER(String udp) {   
		String udpValue = "";
		Object udpObject = null;
		udpObject = getUserDefinedAttribute(udp);
		if (udpObject != null) {
			udpValue = udpObject.toString();
		}
		return udpValue;
	}

	public String getUDPPEQ(String udpName) {
		// TODO Auto-generated method stub
		String udpValue = "";
        Object udpObject = null;
        udpObject = getUserDefinedAttribute(udpName);
        if (udpObject != null) {
                        udpValue = udpObject.toString();
        }
        return udpValue;
	}
}
