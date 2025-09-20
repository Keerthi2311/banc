package com.bancolombia.integracion.cache.imp.guava;

/**
 * Enumeracion que lista las propiedades con las que se puede crear unanueva
 * instancia de cache
 * 
 * @author hrengifo
 * @see GuavaBasedImp
 */
public enum GuavaBasedCacheProperties {
    MAX_ELEMNTS("MAX_ELEMNTS"), MAX_CONCURRENT_THREADS("MAX_CONCURRENT_THREADS"), EXPIRY_TIME(
	    "EXPIRY_TIME"), EXPIRY_ACCESS("EXPIRY_ACCESS");

    private String propertieValue;

    GuavaBasedCacheProperties(String name) {
	this.propertieValue = name;
    }

    public String getPropertieValue() {
	return propertieValue;
    }
}
