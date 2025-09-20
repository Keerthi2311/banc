package com.grupobancolombia.integracion.sti.mapping.operation;

import java.util.List;

import com.grupobancolombia.integracion.sti.mapping.ListElement;
import com.grupobancolombia.integracion.sti.rule.BooleanFormat;

/**
 * Represent a instruction set to perform a transformation esbXMLIL - IAST or
 * IAST - esbXMLIL
 * 
 * @author Jorge Alberto Tchira Salazar
 * @version 1.0
 * @since 2017-03-24
 */
public class TransformationElement {

	/**
	 * The rule to be applied
	 */
	private RuleOption ruleOption;

	/**
	 * The resulted message format. If the transformation is esbXMLIL - IAST,
	 * the resulted message format is IAST If the transformation is IAST -
	 * esbXMLIL, the resulted message format is esbXMLIL
	 */
	private MessageFormat messageFormat;

	/**
	 * A constant value to be transformed. If the value o a specific message
	 * path is not required and is needed transform a constant value
	 */
	private String value;

	/**
	 * A boolean format to the desired boolean output. LETTER represent S/N,
	 * BOOL represent true/false, NUMBER represent 1/0
	 */
	private BooleanFormat booleanFormat;

	/**
	 * A data to define the value length in a esbXMLIL - IAST transformation
	 */
	private Integer length;

	/**
	 * A path to esbXMLIL element. In the esbXMLIL - IAST transformation
	 * represents a path where the values to transform In the IAST - esbXMLIL
	 * transformation represents the output message structure
	 */
	private String path;

	/**
	 * In the IAST - esbXMLIL transformation represents the initial position to
	 * extract the value in the receive string
	 */
	private Integer iastInitialPosition;

	/**
	 * In the IAST - esbXMLIL transformation represents the final position to
	 * extract the value in the receive string
	 */
	private Integer iastFinalPosition;
	
	/**
	 *
	 */
	private boolean homologatePEQ;
	
	/**
	 *
	 */
	private String typology;
	
	/**
	 *
	 */
	private String characterRefill;
	
	/**
	 * The full parameters constructor
	 */
	
	private boolean listRequest;
	/**
	 *
	 */
	private List<ListElement> listFiedRequest;
	
	public TransformationElement(RuleOption ruleOption, String value,
			BooleanFormat booleanFormat, Integer length, String path,
			Integer iastInitialPosition, Integer iastFinalPosition) {
		super();
		this.ruleOption = ruleOption;
		this.value = value;
		this.booleanFormat = booleanFormat;
		this.length = length;
		this.path = path;
		this.iastInitialPosition = iastInitialPosition;
		this.iastFinalPosition = iastFinalPosition;
		this.homologatePEQ = false;
		this.typology = "";
	}
	
	public TransformationElement(RuleOption ruleOption, String value,
			BooleanFormat booleanFormat, Integer length, String path,
			Integer iastInitialPosition, Integer iastFinalPosition, String characterRefill) {
		super();
		this.ruleOption = ruleOption;
		this.value = value;
		this.booleanFormat = booleanFormat;
		this.length = length;
		this.path = path;
		this.iastInitialPosition = iastInitialPosition;
		this.iastFinalPosition = iastFinalPosition;
		this.homologatePEQ = false;
		this.typology = "";
		this.characterRefill = characterRefill;
		
	}
	
	public TransformationElement(RuleOption ruleOption, String value,
			BooleanFormat booleanFormat, Integer length, String path,
			Integer iastInitialPosition, Integer iastFinalPosition, boolean homologatePEQ, 
			String typology) {
		super();
		this.ruleOption = ruleOption;
		this.value = value;
		this.booleanFormat = booleanFormat;
		this.length = length;
		this.path = path;
		this.iastInitialPosition = iastInitialPosition;
		this.iastFinalPosition = iastFinalPosition;
		this.homologatePEQ = homologatePEQ;
		this.typology = typology;
	}
	
	public TransformationElement(RuleOption ruleOption, String value,
			BooleanFormat booleanFormat, Integer length, String path,
			Integer iastInitialPosition, Integer iastFinalPosition, boolean listRequest, List<ListElement> listFieldRequest) {
		super();
		this.ruleOption = ruleOption;
		this.value = value;
		this.booleanFormat = booleanFormat;
		this.length = length;
		this.path = path;
		this.iastInitialPosition = iastInitialPosition;
		this.iastFinalPosition = iastFinalPosition;
		this.listRequest = listRequest;
		this.listFiedRequest = listFieldRequest;
	}


	public TransformationElement(){}
	
	public boolean isHomologatePEQ() {
		return homologatePEQ;
	}

	public void setHomologatePEQ(boolean homologatePEQ) {
		this.homologatePEQ = homologatePEQ;
	}

	public String getTypology() {
		return typology;
	}

	public void setTypology(String typology) {
		this.typology = typology;
	}

	/**
	 * @return the ruleOption
	 */
	public RuleOption getRuleOption() {
		return ruleOption;
	}

	/**
	 * @param ruleOption
	 *            the ruleOption to set
	 */
	public void setRuleOption(RuleOption ruleOption) {
		this.ruleOption = ruleOption;
	}

	/**
	 * @return the messageFormat
	 */
	public MessageFormat getMessageFormat() {
		return messageFormat;
	}

	/**
	 * @param messageFormat
	 *            the messageFormat to set
	 */
	public void setMessageFormat(MessageFormat messageFormat) {
		this.messageFormat = messageFormat;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the booleanFormat
	 */
	public BooleanFormat getBooleanFormat() {
		return booleanFormat;
	}

	/**
	 * @param booleanFormat
	 *            the booleanFormat to set
	 */
	public void setBooleanFormat(BooleanFormat booleanFormat) {
		this.booleanFormat = booleanFormat;
	}

	/**
	 * @return the length
	 */
	public Integer getLength() {
		return length;
	}

	/**
	 * @param length
	 *            the length to set
	 */
	public void setLength(Integer length) {
		this.length = length;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the iastInitialPosition
	 */
	public Integer getIastInitialPosition() {
		return iastInitialPosition;
	}

	/**
	 * @param iastInitialPosition
	 *            the iastInitialPosition to set
	 */
	public void setIastInitialPosition(Integer iastInitialPosition) {
		this.iastInitialPosition = iastInitialPosition;
	}

	/**
	 * @return the iastFinalPosition
	 */
	public Integer getIastFinalPosition() {
		return iastFinalPosition;
	}

	/**
	 * @param iastFinalPosition
	 *            the finalPosition to set
	 */
	public void setIastFinalPosition(Integer iastFinalPosition) {
		this.iastFinalPosition = iastFinalPosition;
	}

	public String getCharacterRefill() {
		return characterRefill;
	}

	public void setCharacterRefill(String characterRefill) {
		this.characterRefill = characterRefill;
	}

	public boolean isListRequest() {
		return listRequest;
	}

	public void setListRequest(boolean listRequest) {
		this.listRequest = listRequest;
	}

	public List<ListElement> getListFiedRequest() {
		return listFiedRequest;
	}

	public void setListFiedRequest(List<ListElement> listFiedRequest) {
		this.listFiedRequest = listFiedRequest;
	}
	
}
