package com.bancolombia.integracion.cache.imp.globalcache;

import java.util.Properties;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.bancolombia.integracion.cache.ICache;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbGlobalMap;
import com.ibm.broker.plugin.MbGlobalMapSessionPolicy;

public class GlobalCacheImp<K, V> implements ICache<K, V> {

    private MbGlobalMap cacheGlobal;

    public GlobalCacheImp() { }

    public GlobalCacheImp(String nameCache, int timeToLive) throws MbException {
	MbGlobalMapSessionPolicy policy = null;
	if (timeToLive == 0) {
	    policy = new MbGlobalMapSessionPolicy(300);
	} else {
	    policy = new MbGlobalMapSessionPolicy(timeToLive);
	}

	if (nameCache == null || nameCache.isEmpty()) {
	    cacheGlobal = MbGlobalMap.getGlobalMap(
		    "SYSTEM.SERVICEGATEWAY.CACHE", policy);
	} else {
	    cacheGlobal = MbGlobalMap.getGlobalMap(nameCache, policy);
	}
    }

    @Override
    public void clear() {
	throw new NotImplementedException();
    }

    public static <K, V>  ICache<K, V> getDefaultInstance() {
	GlobalCacheImp<K, V> globalCacheImp = null;
	try {
	    globalCacheImp = new GlobalCacheImp<>(null, 0);
	} catch (MbException e) {
	    e.printStackTrace();
	}
	return globalCacheImp;
    }
    
    public static <K, V> ICache<K, V> getInstance(Properties properties) {
	String nameCache = GlobalCacheProperties.NAME_CACHE_KEY
		.getPropertiesValue().toString();
	int timeToLife = Integer.parseInt(GlobalCacheProperties.TIME_TO_LIVE
		.getPropertiesValue().toString());
	GlobalCacheImp<K, V> cacheGlobal = null;
	if (nameCache != null && !nameCache.isEmpty() && timeToLife > 0) {
	    try {
		cacheGlobal = new GlobalCacheImp<>(nameCache, timeToLife);
	    } catch (MbException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	} else {
	    cacheGlobal = new GlobalCacheImp<>();
	}
	return cacheGlobal;

    }

    @Override
    public void put(K key, V value) {
	if (key != null && value != null) {
	    try {
		cacheGlobal.put(key.toString(), value);
	    } catch (MbException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get(K key) {
	Object returnValue = null;
	try {
	    returnValue = cacheGlobal.get(key.toString());
	} catch (MbException e) {
	    e.printStackTrace();
	}
	return (V) returnValue;
    }

    @Override
    public void remove(K key) {
	if (key != null) {
	    try {
		cacheGlobal.remove(key.toString());
	    } catch (MbException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    }

    @Override
    public void update(K key, V value) {
	if (key != null && value != null) {
	    try {
		cacheGlobal.update(key.toString(), value);
	    } catch (MbException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    }

    @Override
    public void removeAll() {
	// TODO Auto-generated method stub
	new Throwable("Method not support");
    }

}
