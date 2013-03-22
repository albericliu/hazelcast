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

package com.hazelcast.monitor;

public interface LocalExecutorStats extends LocalInstanceStats {

    /**
     * Returns the number of pending operations of the executor service
     *
     * @return the number of pending operations
     */
    long getPending();

    /**
     * Returns the number of started operations of the executor service
     *
     * @return the number of started operations
     */
    long getStarted();

    /**
     * Returns the number of completed operations of the executor service
     *
     * @return the number of completed operations
     */
    long getCompleted();

    /**
     * Returns the total start latency of operations started
     *
     * @return the total start latency
     */
    long getTotalStartLatency();

    /**
     * Returns the total execution time of operations finished
     *
     * @return the total execution time
     */
    long getTotalExecutionTime();

//    /**
//     * Returns the min execution time of operations finished
//     *
//     * @return the min execution time
//     */
//    long getMinExecutionTime();
//
//    /**
//     * Returns the max execution time of operations finished
//     *
//     * @return the max execution time
//     */
//    long getMaxExecutionTime();

}
