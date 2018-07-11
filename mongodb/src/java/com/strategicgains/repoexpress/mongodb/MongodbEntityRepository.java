/*
    Copyright 2012, Strategic Gains, Inc.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package com.strategicgains.repoexpress.mongodb;

import com.mongodb.MongoClient;
import com.strategicgains.repoexpress.domain.TimestampedIdentifiable;
import com.strategicgains.repoexpress.event.DefaultTimestampedIdentifiableRepositoryObserver;

/**
 * A MongoDB repository that manages sub-types of TimestampedIdentifiable. It uses
 * {@link DefaultTimestampedIdentifiableRepositoryObserver} to set the createdAt
 * and updatedAt dates at the appropriate times.
 * <p/>
 * To implement single-table inheritance, simply pass in all the sub-classes that
 * exist in this collection, with the inheritance-root listed first.
 * 
 * @author toddf
 * @since Sept 23, 2012
 */
public class MongodbEntityRepository<T extends TimestampedIdentifiable>
extends MongodbRepository<T>
{
    public MongodbEntityRepository(MongoClient mongo, String databaseName, @SuppressWarnings("unchecked") Class<? extends T>... types)
    {
	    super(mongo, databaseName, types);
		initializeObservers();
    }

	protected void initializeObservers()
    {
	    addObserver(new DefaultTimestampedIdentifiableRepositoryObserver<T>());
    }
}
