package core.integrationcontrollers.servicegateway.sg.cache;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import com.bancolombia.integracion.cache.CacheFactory;
import com.bancolombia.integracion.cache.ICache;

/**
 * @author ypalomeq
 * @author admguzma
 * @author dasagude
 * @since 2018-12-20
 * @version 1.0
 */
public class ServiceGatewayCache {

	private static ICache<String, Object> cache = null;

	private ServiceGatewayCache() {
	}

	public static void init(Properties properties) throws IllegalAccessException, InstantiationException,
			ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException,
			InvocationTargetException {

		cache = CacheFactory.getCacheInstance(properties);
	}

	public static ICache<String, Object> getCacheInstance() {
		return cache;
	}

	public static Object getCache(String key) {
		return cache.get(key);
	}

	public static void create(String key, Object value) {
		cache.put(key, value);
	}

	public static void update(String key, Object value) {
		cache.update(key, value);
	}

	public static void remove(String key) {
		cache.remove(key);
	}

	public static void removeAll() {
		cache.removeAll();
	}

}
