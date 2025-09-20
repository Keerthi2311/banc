package core.integrationcontrollers.servicegateway.sg.commons;

public enum CommonConstant {

	SERVICEMEDIATOR("serviceMediator"), CONSUMERMEDIATOR("consumerMediator"), CA_DATAPOWER("channelAdapterDataPower"),
	CA_TS_WS_TLS("CA_WS_TLS"), HASH_VALUE("hashValues"), SECURITY_CONSUMER("SECURITY_CONSUMER"),
	CLEAR_CACHE("clearCache"), SERVICE("service"), ALL("all"), CHANNELADAPTER("ChannelAdapter");

	private String value;

	private CommonConstant(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
