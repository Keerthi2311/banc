package com.grupobancolombia.integracion.sti.mapping;

import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;

import core.common.peq.IExecutionContextPEQ;
import core.common.per.IExecutionContextPER;

/**
 * Interface that define transformation methods to be implemented by each
 * message implementation
 * 
 * @author Jorge Alberto Tchira Salazar
 * @version 1.0
 * @since 2017-03-02
 */
public interface IMessageMapping {

	/**
	 * Define IL to IAST transformation method.
	 * 
	 * @param mbeIL
	 *            MbElement that reference the esbXML-IL element in the input
	 *            message tree
	 * @param mbeBLOB
	 *            MbElement that reference the BLOB element in the output
	 *            message tree
	 *            
	 * @return MbElement a mbeBLOB
	 */
	public MbElement executeILtoIASTTransformation(MbElement mbeIL,
			MbElement mbeBLO,IExecutionContextPEQ context) throws MbException;

	/**
	 * Define IAST to IL transformation method.
	 * 
	 * @param mbeBLOB
	 *            MbElement that reference the BLOB element in the output
	 *            message tree
	 * @param mbeIL
	 *            MbElement that reference the esbXML-IL element in the input
	 *            message tree
	 */
	public MbElement executeIASTtoILTransformation(MbElement mbeBLOB,
			MbElement mbeIL, IExecutionContextPER contextPER, IExecutionContextPEQ contextPEQ) throws MbException;

}
