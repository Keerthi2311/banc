package com.grupobancolombia.integracion.sti.mapping;

/**
 * 
 * Responsible for instantiating a Class that implements the transformation
 * methods
 * 
 * @author Jorge Alberto Tchira Salazar
 * @version 1.0
 * @since 2017-03-02
 */
public class MessageMappingFactory {						
	public static final String MESSAGE_MAPPING_CLASSPATH = "com.grupobancolombia.integracion.sti.mapping.operation";

	public static IMessageMapping getMessageMapping(String messageMapping)
			throws IllegalArgumentException, IllegalAccessException, InstantiationException, ClassNotFoundException {
		IMessageMapping messageMappingInstanced;
		if (messageMapping != null && !messageMapping.isEmpty()) {
			StringBuffer sb = new StringBuffer();
			sb.append(MESSAGE_MAPPING_CLASSPATH);
			sb.append(".");
			sb.append(messageMapping);

			messageMappingInstanced = (IMessageMapping) Class.forName(
					sb.toString()).newInstance();
			sb = null;
		} else {
			throw new IllegalArgumentException("Invalid message mapping class name");
		}
		return messageMappingInstanced;
	}

}
