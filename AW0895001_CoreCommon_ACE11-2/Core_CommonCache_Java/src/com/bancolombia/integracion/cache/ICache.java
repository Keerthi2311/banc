package com.bancolombia.integracion.cache;


/**
 * interface base con la que se debe implementar cualquier cache que se quiera
 * exponer
 * 
 * @author hrengifo
 *
 * @param <K>
 * @param <V>
 */
public interface ICache<K, V> {
    void put(K key, V value);

    V get(K key);

    void remove(K key);

    void removeAll();

    void clear();

    void update(K key, V value);
}
