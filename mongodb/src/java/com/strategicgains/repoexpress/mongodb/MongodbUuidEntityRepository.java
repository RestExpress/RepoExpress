/*
    Copyright 2013-2018, Strategic Gains, Inc.

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
import com.strategicgains.repoexpress.domain.UuidEntity;
import com.strategicgains.repoexpress.event.DefaultTimestampedIdentifiableRepositoryObserver;
import com.strategicgains.repoexpress.event.UuidEntityRepositoryObserver;

/**
 * A MongoDB repository that manages types of TimestampedUuidIdentifiable. It utilizes the
 * {@link UuidEntityRepositoryObserver} to assign a UUID on creation.  It also uses
 * {@link DefaultTimestampedIdentifiableRepositoryObserver} to set the createAt and 
 * updatedAt dates on the object as appropriate.
 * <p/>
 * Storing a UUID using Morphia (as this repository does) requires four (4) bytes for the ID,
 * whereas, using a MongoDB ObjectId only requires three (3).  However, a UUID, shortened to
 * 22 characters (using Base64 encoding) is arguably more readable and universally applicable
 * on a URL.
 * <p/>
 * To implement single-table inheritance, simply pass in all the sub-classes that
 * exist in this collection, with the inheritance-root listed first.
 * 
 * @author toddf
 * @since Mar 18, 2013
 */
public class MongodbUuidEntityRepository<T extends TimestampedIdentifiable & UuidEntity>
extends MongodbRepository<T>
{
    public MongodbUuidEntityRepository(MongoClient mongo, String databaseName, @SuppressWarnings("unchecked") Class<? extends T>... types)
    {
	    super(mongo, databaseName, types);
		initializeObservers();
    }

	protected void initializeObservers()
    {
	    addObserver(new DefaultTimestampedIdentifiableRepositoryObserver<T>());
		addObserver(new UuidEntityRepositoryObserver<T>());
    }
}
