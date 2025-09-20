package com.bancolombia.integracion.cache.imp.globalcache;

public enum GlobalCacheProperties {

    CREATE_CACHE("create"), UPDATE_CACHE("update"), GET_CACHE("getcache"), REMOVE_CACHE(
	    "remove"), OPTIONS("options"), TIME_TO_LIVE("timeToLive"), NAME_CACHE_KEY(
	    "nameCache"), NAME_CACHE_DEFAULT("SYSTEM.SERVICEGATEWAY.CACHE"), MESSAGE_ERROR(
	    "");

    private String propertiesValue;

    private GlobalCacheProperties(String propertiesValue) {
	this.propertiesValue = propertiesValue;
    }

    public String getPropertiesValue() {
	return propertiesValue;
    }

}
