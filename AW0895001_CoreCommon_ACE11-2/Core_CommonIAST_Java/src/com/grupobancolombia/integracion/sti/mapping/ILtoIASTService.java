package com.grupobancolombia.integracion.sti.mapping;

import com.grupobancolombia.integracion.sti.mapping.IMessageMapping;
import com.grupobancolombia.integracion.sti.mapping.MessageMappingFactory;
import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbBLOB;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;

import core.common.peq.IExecutionContextPEQ;

/**
 * Service that receives a IL to IAST request transformation from the message
 * broker flow
 * 
 * @author Jorge Alberto Tchira Salazar
 * @version 1.0
 * @since 2017-03-02
 */
public class ILtoIASTService extends MbJavaComputeNode implements IExecutionContextPEQ{

	/**
	 * Method that process a IL to IAST request
	 * 
	 * @param assembly
	 *            input message root
	 * @throws MbException
	 */
	public void evaluate(MbMessageAssembly assembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbMessageAssembly assemblyOut = null;
		
		MbElement headerIL = assembly.getMessage().getRootElement()
				.getFirstElementByPath("XMLNSC/esbXML/Header");
		
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

		assemblyOut = createMessageOut(assembly);
		
		MbElement response = assemblyOut.getMessage().getRootElement();
		try {
			// Transform IL to IAST message
			MbMessage message 				= new MbMessage(assembly.getMessage());
			MbMessageAssembly RequestIAST 	= new MbMessageAssembly(assembly, message);
			MbElement blobReferenceIAST 	= RequestIAST.getMessage().getRootElement().getLastChild().getLastChild();	
			IMessageMapping messageMapping 	= MessageMappingFactory.getMessageMapping(sb.toString());
			messageMapping.executeILtoIASTTransformation(blobReferenceIAST, response, this);
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
	 * Creates a MbMessageAssembly for output
	 * 
	 * @param assembly
	 *            a input MbMessageAssembly
	 * @return MbMessageAssembly out
	 * @throws MbException
	 */
	public MbMessageAssembly createMessageOut(MbMessageAssembly assembly)
			throws MbException {

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
		addBLOB(messageOut);
		return assemblyOut;
	}

	/**
	 * Creates a MbMessageAssembly for output
	 * 
	 * @param inMessage
	 *            a input MbMessage
	 * @return MbMessage out
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

	/**
	 * Adds a parser BLOB to the output message
	 * 
	 * @param message
	 *            a input MbMessage
	 * @throws MbException
	 */
	public void addBLOB(MbMessage message) throws MbException {
		MbElement root = message.getRootElement();
		// create a top level 'parser' element with parser class name
		root.createElementAsLastChild(MbBLOB.PARSER_NAME);
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
