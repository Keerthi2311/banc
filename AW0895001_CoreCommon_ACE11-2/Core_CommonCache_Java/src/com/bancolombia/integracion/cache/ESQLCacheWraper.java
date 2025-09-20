package com.bancolombia.integracion.cache;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class ESQLCacheWraper {

	private static Map<String, ICache<String, String>> cacheInstances;
	
	static{
		cacheInstances = new ConcurrentHashMap<String, ICache<String,String>>(10,0.75f);
	}
	
	private static ICache<String, String> getOrCreateCache(String cache){
		ICache<String,String> cacheIns = cacheInstances.get(cache);
		
		if(cacheIns == null){
			cacheIns = CacheFactory.getDefaultLocalCache();
			cacheInstances.put(cache, cacheIns);
		}
		return cacheIns;
	}
	
	public static void put(String cache, String key, String value){
		getOrCreateCache(cache).put(key, value);
	}
	
	public static String get(String cache, String key){
		return getOrCreateCache(cache).get(key);
	}
	
	public static Boolean remove(String cache, String key){
		getOrCreateCache(cache).remove(key);
		return true;
	}
	
	public static Boolean initCache(String properties, String cache){
		if(cache == null || cache.isEmpty()){
			return Boolean.FALSE;
		}
		
		if(properties == null || properties.isEmpty()){
			return Boolean.FALSE;
		}
		
		Properties cacheProperties = parseProperties(properties, "\\|");
		ICache<String, String> cacheInstace = CacheFactory.getCacheInstance(cacheProperties);
		cacheInstances.put(cache, cacheInstace);
		
		return Boolean.TRUE;
	}
	
	public static void clear(String cache){
		getOrCreateCache(cache).clear();
	}
	 
	protected static Properties parseProperties(final String properties, final String separator){
		Properties returnProperties = new Properties();
		String[] propertieEntry = properties.split(separator);
		for(String propertie : propertieEntry)
		{
			String[] propertyeValue =  propertie.split("=");
			returnProperties.put(propertyeValue[0], propertyeValue[1]);
		}
		return returnProperties;
	}
	
	public static void removeAll(String cache){
		getOrCreateCache(cache).removeAll();;
	}
}

