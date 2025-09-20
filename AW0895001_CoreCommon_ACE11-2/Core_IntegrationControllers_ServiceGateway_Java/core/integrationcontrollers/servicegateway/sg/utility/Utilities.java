package core.integrationcontrollers.servicegateway.sg.utility;

import java.util.List;

import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbNamespaceBindings;

import core.integrationcontrollers.servicegateway.sg.dto.DataParameterWSRR;

public class Utilities {

	private Utilities() { }

	public static String getMessageContext(MbElement header, String key) throws MbException {
		String value = "";

		String path = "messageContext/property";
		String nameSpace = "http://grupobancolombia.com/intf/IL/esbXML/V3.0";
		MbNamespaceBindings ns = new MbNamespaceBindings();
		ns.addBinding("tns", nameSpace);

		@SuppressWarnings("unchecked")
		List<MbElement> property = (List<MbElement>) header.evaluateXPath(path, ns);

		for (int i = 0; i < property.size(); i++) {
			if (property.get(i).getFirstElementByPath("key").getValueAsString().equals(key)) {
				value = property.get(i).getFirstElementByPath("value").getValueAsString();
				i = property.size() + 1;
			}
		}

		return value;
	}
	
	public static boolean isAuthorizedTLS(DataParameterWSRR response) {

		return !response.isHasTLSPolicy() || (response.isHasTLSPolicy() && response.isAutorizationConsumerTLS());
	}

	/**
	 * mapea los valores que a retornar en el Environment
	 * 
	 * @author ypalomeq
	 * @author dasagude
	 * @version 1.0 ypalomeq@bancolombia.com.co
	 **/
	public static void mappingValues(DataParameterWSRR response, MbMessage env) throws MbException {
		if (response.isConsumerAuthorized() && response.isOperationAuthorized()) {
			String destination = "DestinationParameter";
			env.getRootElement().createElementAsFirstChild(MbElement.TYPE_NAME, destination, "");
			MbElement destElem = env.getRootElement().getFirstElementByPath(destination);

			destElem.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "QueueManagerRequest", response.getQmanager());
			env.getRootElement().getFirstElementByPath(destination)
					.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "QueueRequest", response.getQueueRequest());
			env.getRootElement().getFirstElementByPath(destination)
					.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "QueueResponse", response.getQmanager());
			env.getRootElement().getFirstElementByPath(destination)
					.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "dataDomainOrigin", response.getDataDomainOrigin());
			env.getRootElement().getFirstElementByPath(destination)
					.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "systemaOrigin", response.getSystemOrigin());
			env.getRootElement().getFirstElementByPath(destination)
					.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "queueDestinationCMSTI", response.getQueueCMSTI());
		}
	}
}
