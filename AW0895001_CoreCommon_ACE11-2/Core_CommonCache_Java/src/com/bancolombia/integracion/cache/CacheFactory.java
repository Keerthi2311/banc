package com.bancolombia.integracion.cache;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import com.bancolombia.integracion.cache.imp.globalcache.GlobalCacheImp;
import com.bancolombia.integracion.cache.imp.guava.GuavaBasedImp;

/**
 * Cache factory
 * 
 * @author hrengifo
 *
 */
public class CacheFactory {
    /**
     * obtiene una instancia de cache
     * 
     * @param <K>
     *            tipo de la llave
     * @param <V>
     *            tipo del valor
     * @param initProperties
     *            propiedades las cuales se entregaran al constructor de la
     *            implementacion
     * @return instancia de cache
     */
    public static <K, V> ICache<K, V> getCacheInstance(Properties initProperties) {
	ICache<K, V> cache = GuavaBasedImp.getInstance(initProperties);
	return cache;
    }

    /**
     * obtiene una instancia de cache local por defecto
     * 
     * @param <K>
     *            tipo de la llave
     * @param <V>
     *            tipo del valor
     * @return instancia de cache
     */
    public static <K, V> ICache<K, V> getDefaultLocalCache() {
	ICache<K, V> cache = GuavaBasedImp.getDefaultInstance();
	return cache;
    }

    /**
     * Crea una cache basado en la clase enviada como parametro
     * 
     * @param <K>
     *            tipo de la llave de la cache
     * @param <V>
     *            tipo de los valores de la cache
     * @param clazz
     *            nombre de la clase que implmenta la interface ICache
     *            {@link ICache}
     * @return instancia ICache
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     */
    @SuppressWarnings("unchecked")
    public static <K, V> ICache<K, V> getCache(String clazz)
	    throws IllegalAccessException, InstantiationException,
	    ClassNotFoundException, SecurityException, NoSuchMethodException,
	    IllegalArgumentException, InvocationTargetException {
	Class<?> iCacheImp = Class.forName(clazz).asSubclass(ICache.class);
	ICache<K, V> iCacheImpInstance = (ICache<K, V>) iCacheImp.newInstance();
	Method getInstanceMethod = iCacheImp.getMethod("getDefaultInstance");
	ICache<K, V> ret = (ICache<K, V>) getInstanceMethod.invoke(iCacheImpInstance);
	return ret;
    }

    /**
     * Crea una cache basado en la clase enviada como parametro
     * 
     * @param <K>
     *            tipo de la llave de la cache
     * @param <V>
     *            tipo de los valores de la cache
     * @param clazz
     *            nombre de la clase que implmenta la interface ICache
     *            {@link ICache}
     * @return instancia ICache
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     */
    @SuppressWarnings("unchecked")
    public static <K, V> ICache<K, V> getCache(String clazz,
	    Properties cacheProperties) throws IllegalAccessException,
	    InstantiationException, ClassNotFoundException, SecurityException,
	    NoSuchMethodException, IllegalArgumentException,
	    InvocationTargetException {
	Class<?> iCacheImp = Class.forName(clazz).asSubclass(ICache.class);
	ICache<K, V> iCacheImpInstance = (ICache<K, V>) iCacheImp.newInstance();
	Method getInstanceMethod = iCacheImp.getMethod("getInstance",
		Properties.class);
	ICache<K, V> ret = (ICache<K, V>) getInstanceMethod.invoke(iCacheImpInstance,
		cacheProperties);
	return ret;
    }

    /**
     * obtiene una instancia de cache local por defecto cache global del broker
     * 
     * @param <K>
     *            tipo de la llave
     * @param <V>
     *            tipo del valor
     * @return instancia de cache
     */
    public static <K, V> ICache<K, V> getDefaultGlobalCache() {
	ICache<K, V> cache = GlobalCacheImp.getDefaultInstance();
	return cache;
    }

    /**
     * obtiene una instancia de cache global del broker
     * 
     * @param <K>
     *            tipo de la llave
     * @param <V>
     *            tipo del valor
     * @param initProperties
     *            propiedades las cuales se entregaran al constructor de la
     *            implementacion
     * @return instancia de cache
     */
    public static <K, V> ICache<K, V> getGlobalCacheInstance(
	    Properties initProperties) {
	ICache<K, V> cache = GlobalCacheImp.getInstance(initProperties);
	return cache;
    }
}
