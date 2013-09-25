package com.strategicgains.repoexpress.cassandra;
/*
    Copyright 2013, Strategic Gains, Inc.

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


import com.datastax.driver.core.Session;
import com.strategicgains.repoexpress.event.DefaultTimestampedIdentifiableRepositoryObserver;
import com.strategicgains.repoexpress.event.UuidIdentityRepositoryObserver;

/**
 * A Cassandra repository that manages types of AbstractCassandraEntity, which are
 * identified by UUIDs. It utilizes the {@link UuidIdentityRepositoryObserver} to assign
 * a UUID on creation.  It also uses {@link DefaultTimestampedIdentifiableRepositoryObserver}
 * to set the createAt and updatedAt dates on the object as appropriate.
 * <p/>
 * Storing a UUID as the ID (as this repository does) requires four (4) bytes for the ID.
 * <p/>
 * To implement single-table inheritance, simply pass in all the sub-classes that
 * exist in this collection, with the inheritance-root listed first.
 * 
 * @author toddf
 * @since Apr 12, 2013
 */
public abstract class CassandraEntityRepository<T extends AbstractUuidCassandraEntity>
extends AbstractCassandraRepository<T>
{
    public CassandraEntityRepository(Session session, String databaseName, Class<? extends T>... types)
    {
	    super(session);
	    initializeObservers();
//	    setIdentifierAdapter(new UuidAdapter());
    }

    protected void initializeObservers()
    {
		addObserver(new DefaultTimestampedIdentifiableRepositoryObserver<T>());
		addObserver(new UuidIdentityRepositoryObserver<T>());
    }
}