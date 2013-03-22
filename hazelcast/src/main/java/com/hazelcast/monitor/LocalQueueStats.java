/*
 * Copyright (c) 2008-2013, Hazelcast, Inc. All Rights Reserved.
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

/**
 * Local queue statistics.
 */
public interface LocalQueueStats extends LocalInstanceStats {
    /**
     * Returns the number of owned items in this member.
     *
     * @return number of owned items.
     */
    int getOwnedItemCount();

    /**
     * Returns the number of backup items in this member.
     *
     * @return number of backup items.
     */
    int getBackupItemCount();

    /**
     * Returns the min age of the items in this member.
     *
     * @return min age
     */
    long getMinAge();

    /**
     * Returns the max age of the items in this member.
     *
     * @return max age
     */
    long getMaxAge();

    /**
     * Returns the average age of the items in this member.
     *
     * @return average age
     */
    long getAveAge();

    /**
     * Returns the number of offer/put/add operations.
     * Offers returning false will be included.
     * #getNumberOfRejectedOffers can be used
     * to get the rejected offers.
     *
     * @return number offer/put/add operations
     */
    public long getNumberOfOffers();

    /**
     * Returns the number of rejected offers. Offer
     * can be rejected because of max-size limit
     * on the queue.
     *
     * @return number of rejected offers.
     */
    public long getNumberOfRejectedOffers();

    /**
     * Returns the number of poll/take/remove operations.
     * Polls returning null (empty) will be included.
     * #getNumberOfEmptyPolls can be used to get the
     * number of polls returned null.
     *
     * @return number of poll/take/remove operations.
     */
    public long getNumberOfPolls();

    /**
     * Returns number of null returning poll operations.
     * Poll operation might return null, if the queue is empty.
     *
     * @return number of null returning poll operations.
     */
    public long getNumberOfEmptyPolls();

    /**
     * Returns number of //TODO operations.
     *
     * @return number of null returning poll operations.
     */
    public long getNumberOfOtherOperations();

    /**
     * Returns number of //TODO operations.
     *
     * @return number of null returning poll operations.
     */
    public long getNumberOfEvents();
}
