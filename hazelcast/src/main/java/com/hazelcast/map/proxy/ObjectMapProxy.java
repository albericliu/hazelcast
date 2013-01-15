/*
 * Copyright (c) 2008-2012, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.map.proxy;

import com.hazelcast.core.EntryListener;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.MapService;
import com.hazelcast.map.ObjectFuture;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.spi.Invocation;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.Response;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.hazelcast.map.MapService.MAP_SERVICE_NAME;

public class ObjectMapProxy<K, V> extends MapProxySupport implements MapProxy<K, V> {

    public ObjectMapProxy(final String name, final MapService mapService, final NodeEngine nodeEngine) {
        super(name, mapService, nodeEngine);
    }

    public V get(Object k) {
        Data key = nodeEngine.toData(k);
        return nodeEngine.toObject(getInternal(key));
    }

    public V put(final K k, final V v) {
        return put(k, v, -1, null);
    }

    public V put(final K k, final V v, final long ttl, final TimeUnit timeunit) {
        final Data key = nodeEngine.toData(k);
        final Data value = nodeEngine.toData(v);
        final Data result = putInternal(key, value, ttl, timeunit);
        return nodeEngine.toObject(result);
    }

    public boolean tryPut(final K k, final V v, final long timeout, final TimeUnit timeunit) {
        final Data key = nodeEngine.toData(k);
        final Data value = nodeEngine.toData(v);
        return tryPutInternal(key, value, timeout, timeunit);
    }

    public V putIfAbsent(final K k, final V v) {
        return putIfAbsent(k, v, -1, null);
    }

    public V putIfAbsent(final K k, final V v, final long ttl, final TimeUnit timeunit) {
        final Data key = nodeEngine.toData(k);
        final Data value = nodeEngine.toData(v);
        final Data result = putIfAbsentInternal(key, value, ttl, timeunit);
        return nodeEngine.toObject(result);
    }

    public void putTransient(final K k, final V v, final long ttl, final TimeUnit timeunit) {
        final Data key = nodeEngine.toData(k);
        final Data value = nodeEngine.toData(v);
        putTransientInternal(key, value, ttl, timeunit);
    }

    public boolean replace(final K k, final V o, final V v) {
        final Data key = nodeEngine.toData(k);
        final Data oldValue = nodeEngine.toData(o);
        final Data value = nodeEngine.toData(v);
        return replaceInternal(key,oldValue,value);
    }

    public V replace(final K k, final V v) {
        final Data key = nodeEngine.toData(k);
        final Data value = nodeEngine.toData(v);
        return nodeEngine.toObject(replaceInternal(key, value));
    }

    public void set(final K k, final V v, final long ttl, final TimeUnit timeunit) {
        final Data key = nodeEngine.toData(k);
        final Data value = nodeEngine.toData(v);
        setInternal(key, value, ttl, timeunit);
    }

    public V remove(Object k) {
        final Data key = nodeEngine.toData(k);
        final Data result = removeInternal(key);
        return nodeEngine.toObject(result);
    }

    public boolean remove(final Object k, final Object v) {
        final Data key = nodeEngine.toData(k);
        final Data value = nodeEngine.toData(v);
        return removeInternal(key, value);
    }

    public boolean containsKey(Object k) {
        Data key = nodeEngine.toData(k);
        return containsKeyInternal(key);
    }

    public boolean containsValue(final Object v) {
        Data value = nodeEngine.toData(v);
        return containsValueInternal(value);
    }

    public void lock(final K key) {
        Data k = nodeEngine.toData(key);
        lockInternal(k);
    }

    public void unlock(final K key) {
        Data k = nodeEngine.toData(key);
        unlockInternal(k);
    }

    public Object tryRemove(final K key, final long timeout, final TimeUnit timeunit) throws TimeoutException {
        Data k = nodeEngine.toData(key);
        return nodeEngine.toObject(tryRemoveInternal(k, timeout, timeunit));
    }

    public Future<V> getAsync(final K k) {
        Data key = nodeEngine.toData(k);
        return new ObjectFuture(getAsyncInternal(key), nodeEngine.getSerializationService());
    }

    public boolean isLocked(final K k) {
        Data key = nodeEngine.toData(k);
        return isLockedInternal(key);
    }

    public Future putAsync(final K key, final V value) {
        Data k = nodeEngine.toData(key);
        Data v = nodeEngine.toData(value);
        return new ObjectFuture(putAsyncInternal(k, v), nodeEngine.getSerializationService());
    }

    public Future removeAsync(final K key) {
        Data k = nodeEngine.toData(key);
        return new ObjectFuture(removeAsyncInternal(k), nodeEngine.getSerializationService());
    }

    public Map<K, V> getAll(final Set<K> keys) {
        Set<Data> ks = new HashSet(keys.size());
        for (K key : keys) {
            Data k = nodeEngine.toData(key);
            ks.add(k);
        }
        return (Map<K, V>) getAllObjectInternal(ks);
    }

    public void putAll(final Map<? extends K, ? extends V> m) {
        putAllObjectInternal(m);
    }

    public boolean tryLock(final K key) {
        return tryLockInternal(nodeEngine.toData(key), 0, null);
    }

    public boolean tryLock(final K key, final long time, final TimeUnit timeunit) {
        return tryLockInternal(nodeEngine.toData(key), time, timeunit);
    }

    public void forceUnlock(final K key) {
        Data k = nodeEngine.toData(key);
        forceUnlockInternal(k);
    }

    public void addLocalEntryListener(final EntryListener<K, V> listener) {
    }

    public void addEntryListener(final EntryListener listener, final boolean includeValue) {
         addEntryListenerInternal(listener, null, includeValue);
    }

    public void addEntryListener(final EntryListener<K, V> listener, final K key, final boolean includeValue) {
        addEntryListenerInternal(listener, nodeEngine.toData(key), includeValue);
    }

    public void removeEntryListener(final EntryListener<K, V> listener) {
        removeEntryListenerInternal(listener);
    }

    public void removeEntryListener(final EntryListener<K, V> listener, final K key) {
        removeEntryListenerInternal(listener, nodeEngine.toData(key));
    }

    public Map.Entry<K, V> getMapEntry(final K key) {
        Map.Entry<Data, Data> entry = getMapEntryInternal(nodeEngine.toData(key));
        return new AbstractMap.SimpleImmutableEntry<K,V>((K)nodeEngine.toObject(entry.getKey()), (V)nodeEngine.toObject(entry.getValue()));
    }

    public boolean evict(final Object key) {
        return evictInternal(nodeEngine.toData(key));
    }

    public void clear() {
        clearInternal();
    }

    public void flush() {
    }

    public Set<K> keySet() {
        Set<Data> dataSet = keySetInternal();
        HashSet<K> keySet = new HashSet<K>();
        for (Data data : dataSet) {
            keySet.add((K) nodeEngine.toObject(data));
        }
        return keySet;
    }

    public Collection<V> values() {
        Collection<Data> dataSet = valuesInternal();
        Collection<V> valueSet = new ArrayList<V>();
        for (Data data : dataSet) {
            valueSet.add((V) nodeEngine.toObject(data));
        }
        return valueSet;
    }

    public Set<Entry<K, V>> entrySet() {
        Set<Entry<Data, Data>> entries = entrySetInternal();
        Set<Entry<K, V>> resultSet = new HashSet<Entry<K, V>>();
        for (Entry<Data, Data> entry : entries) {
            resultSet.add(new AbstractMap.SimpleImmutableEntry((K)nodeEngine.toObject(entry.getKey()), (V)nodeEngine.toObject(entry.getValue()) )) ;

        }
        return resultSet;
    }

    public Set<K> keySet(final Predicate predicate) {
        return null;
    }

    public Set<Entry<K, V>> entrySet(final Predicate predicate) {
        return null;
    }

    public Collection<V> values(final Predicate predicate) {
        return null;
    }

    public Set<K> localKeySet() {
        Set<Data> dataSet = localKeySetInternal();
        HashSet<K> keySet = new HashSet<K>();
        for (Data data : dataSet) {
            keySet.add((K) nodeEngine.toObject(data));
        }
        return keySet;
    }

    public Set<K> localKeySet(final Predicate predicate) {
        return null;
    }

    public Object executeOnKey(K key, EntryProcessor entryProcessor) {
        return nodeEngine.toObject(executeOnKeyInternal(nodeEngine.toData(key), entryProcessor));
    }

    protected Object invoke(Operation operation, int partitionId) throws Throwable {
        Invocation invocation = nodeEngine.getOperationService().createInvocationBuilder(MAP_SERVICE_NAME, operation, partitionId).build();
        Future f = invocation.invoke();
        Object response = f.get();
        Object returnObj;
        if (response instanceof Response) {
            Response r = (Response) response;
            returnObj = r.getResult();
        } else {
            returnObj = nodeEngine.toObject(response);
        }
        if (returnObj instanceof Throwable) {
            throw (Throwable) returnObj;
        }
        return returnObj;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("IMap");
        sb.append("{name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}