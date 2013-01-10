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

package com.hazelcast.collection;

import com.hazelcast.collection.processor.BackupAwareEntryProcessor;
import com.hazelcast.collection.processor.Entry;
import com.hazelcast.collection.processor.EntryProcessor;
import com.hazelcast.core.EntryEventType;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.*;
import com.hazelcast.spi.impl.AbstractNamedKeyBasedOperation;

import java.io.IOException;
import java.util.Collection;

/**
 * @ali 1/1/13
 */
public class CollectionOperation extends AbstractNamedKeyBasedOperation implements BackupAwareOperation, IdentifiedDataSerializable {

    EntryProcessor processor;

    transient Object response;
    transient Entry entry;

    CollectionOperation() {
    }

    CollectionOperation(String name, Data dataKey, EntryProcessor processor, int partitionId) {
        super(name, dataKey);
        this.processor = processor;
        setPartitionId(partitionId);
    }

    public void run() throws Exception {
        CollectionService service = getService();
        CollectionContainer collectionContainer = service.getOrCreateCollectionContainer(getPartitionId(), name);
        entry = new Entry(collectionContainer, dataKey);
        response = processor.execute(entry);
    }

    public void afterRun() throws Exception {
        if(entry != null && entry.getEventType() != null){
            Object eventValue = entry.getEventValue();
            if (eventValue instanceof Collection){
                for (Object obj: (Collection)eventValue){
                    publishEvent(entry.getEventType(), obj);
                }
            }
            else {
                publishEvent(entry.getEventType(), eventValue);
            }
        }
    }

    public Object getResponse() {
        return response;
    }

    public boolean shouldBackup() {
        return processor instanceof BackupAwareEntryProcessor && response != null;
    }

    public int getSyncBackupCount() {
        //TODO config
        return 1;
    }

    public int getAsyncBackupCount() {
        //TODO config
        return 0;
    }

    public Operation getBackupOperation() {
        return new CollectionBackupOperation(name, dataKey, processor);
    }

    public void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(processor);
    }

    public void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        processor = in.readObject();
    }

    public int getId() {
        return DataSerializerCollectionHook.COLLECTION_OPERATION;
    }

    public void publishEvent(EntryEventType eventType, Object value){
        NodeEngine engine = getNodeEngine();
        EventService eventService = engine.getEventService();
        Collection<EventRegistration> registrations = eventService.getRegistrations(CollectionService.COLLECTION_SERVICE_NAME, name);
        for (EventRegistration registration: registrations){
            CollectionEventFilter filter = (CollectionEventFilter)registration.getFilter();
            if (filter.getKey() == null || filter.getKey().equals(dataKey)){
                Data dataValue = filter.isIncludeValue() ? engine.toData(value) : null;
                CollectionEvent event = new CollectionEvent(name,  dataKey, dataValue, eventType, engine.getThisAddress());
                eventService.publishEvent(CollectionService.COLLECTION_SERVICE_NAME, registration, event);
            }
        }
    }
}