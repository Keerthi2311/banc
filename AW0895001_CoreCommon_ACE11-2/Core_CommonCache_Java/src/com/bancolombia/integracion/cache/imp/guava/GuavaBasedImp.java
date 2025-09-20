package com.bancolombia.integracion.cache.imp.guava;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.bancolombia.integracion.cache.ICache;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Cache basada en google Guava Cache
 * 
 * @author hrengifo
 *
 */
public class GuavaBasedImp<K, V> implements ICache<K, V> {
    Cache<K, V> currentCache = null;

    public static <K, V> ICache<K, V> getDefaultInstance() {
	return new GuavaBasedImp<K, V>(10000000, 10, -1, -1);
    }

    public static <K, V> ICache<K, V> getInstance(long maxElements, int maxConcurrentThreads,
	    long expiryTime, long expiryAccess) {
	return new GuavaBasedImp<K, V>(maxElements, maxConcurrentThreads,
		expiryTime, expiryAccess);
    }

    public static <K, V> ICache<K, V> getInstance(Properties initProperties) {
	long maxElements = -1, expiryTime = -1, expiryAccess = -1;
	int maxConcurrentThreads = -1;

	String maxElementsValue, expiryTimeValue, expiryAccessValue, maxConcurrentThreadsValue;

	maxElementsValue = initProperties
		.getProperty(GuavaBasedCacheProperties.MAX_ELEMNTS
			.getPropertieValue());
	expiryTimeValue = initProperties
		.getProperty(GuavaBasedCacheProperties.EXPIRY_TIME
			.getPropertieValue());
	expiryAccessValue = initProperties
		.getProperty(GuavaBasedCacheProperties.EXPIRY_ACCESS
			.getPropertieValue());
	maxConcurrentThreadsValue = initProperties
		.getProperty(GuavaBasedCacheProperties.MAX_CONCURRENT_THREADS
			.getPropertieValue());

	if (maxElementsValue != null && !maxElementsValue.isEmpty()) {
	    maxElements = Long.parseLong(maxElementsValue);
	}

	if (expiryTimeValue != null && !expiryTimeValue.isEmpty()) {
	    expiryTime = Long.parseLong(expiryTimeValue);
	}

	if (expiryAccessValue != null && !expiryAccessValue.isEmpty()) {
	    expiryAccess = Long.parseLong(expiryAccessValue);
	}

	if (maxConcurrentThreadsValue != null
		&& !maxConcurrentThreadsValue.isEmpty()) {
	    maxConcurrentThreads = Integer.parseInt(maxConcurrentThreadsValue);
	}

	new GuavaBasedImp<K, V>(maxElements, maxConcurrentThreads, expiryTime,
		expiryAccess);
	return new GuavaBasedImp<K, V>(maxElements, maxConcurrentThreads,
		expiryTime, expiryAccess);
    }

    @SuppressWarnings("unchecked")
    private GuavaBasedImp(long maxElements, int maxConcurrentThreads,
	    long expiryTime, long expiryAccess) {
	@SuppressWarnings("rawtypes")
	CacheBuilder builder = CacheBuilder.newBuilder();
	if (maxElements > 0) {
	    builder.maximumSize(maxElements);
	}
	if (maxConcurrentThreads > 0) {
	    builder.concurrencyLevel(maxConcurrentThreads);
	}
	if (expiryTime > 0) {
	    builder.expireAfterWrite(expiryTime, TimeUnit.SECONDS);
	}
	if (expiryAccess > 0) {
	    builder.expireAfterAccess(expiryAccess, TimeUnit.SECONDS);
	}
	currentCache = builder.build();
    }

    private GuavaBasedImp() { }

    @Override
    public void clear() {
	this.currentCache.cleanUp();
    }

    @Override
    public V get(K key) {
	return this.currentCache.getIfPresent(key);
    }

    @Override
    public void put(K key, V value) {
	this.currentCache.put(key, value);
    }

    @Override
    public void remove(K key) {
	this.currentCache.invalidate(key);
    }

    public Cache<K, V> getInternal() {
	return currentCache;
    }

    @Override
    public void update(K key, V value) {
	remove(key);
	put(key, value);

    }

    @Override
    public void removeAll() {
	// TODO Auto-generated method stub
	this.currentCache.invalidateAll();
    }

}
