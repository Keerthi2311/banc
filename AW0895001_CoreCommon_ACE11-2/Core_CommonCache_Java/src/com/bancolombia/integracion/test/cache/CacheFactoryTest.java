package com.bancolombia.integracion.test.cache;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.junit.Assert;

import org.junit.Test;

import com.bancolombia.integracion.cache.CacheFactory;
import com.bancolombia.integracion.cache.ICache;
import com.bancolombia.integracion.cache.imp.guava.GuavaBasedCacheProperties;

public class CacheFactoryTest {
    @Test
    public void testCreateCache() throws IllegalAccessException,
	    InstantiationException, ClassNotFoundException, SecurityException,
	    IllegalArgumentException, NoSuchMethodException,
	    InvocationTargetException {
	String cacheName = "com.bancolombia.integracion.cache.imp.guava.GuavaBasedImp";
	ICache<String, String> cacheImp = CacheFactory.getCache(cacheName);
	Assert.assertNotNull(cacheImp);
    }

    @Test
    public void testCreateCacheWithProperties() throws IllegalAccessException,
	    InstantiationException, ClassNotFoundException, SecurityException,
	    IllegalArgumentException, NoSuchMethodException,
	    InvocationTargetException {
	String cacheName = "com.bancolombia.integracion.cache.imp.guava.GuavaBasedImp";
	long maxElements = 100000;
	int maxConcurrentThreads = 5;
	long expiryTime = 60;
	long expiryAccess = 120;

	Properties properties = new Properties();
	properties.put(GuavaBasedCacheProperties.MAX_ELEMNTS, maxElements);
	properties.put(GuavaBasedCacheProperties.MAX_CONCURRENT_THREADS,
		maxConcurrentThreads);
	properties.put(GuavaBasedCacheProperties.EXPIRY_TIME, expiryTime);
	properties.put(GuavaBasedCacheProperties.EXPIRY_ACCESS, expiryAccess);

	ICache<String, String> cacheImp = CacheFactory.getCache(cacheName,
		properties);
	Assert.assertNotNull(cacheImp);
    }

}
