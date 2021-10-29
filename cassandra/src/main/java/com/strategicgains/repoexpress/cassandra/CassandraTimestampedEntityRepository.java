/*
    Copyright 2014, Strategic Gains, Inc.

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
package com.strategicgains.repoexpress.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.strategicgains.repoexpress.domain.TimestampedIdentifiable;
import com.strategicgains.repoexpress.event.DefaultTimestampedIdentifiableRepositoryObserver;

/**
 * A Cassandra repository that manages types of TimestampedIdentifiable, which
 * are identified by a single primary key, that is not a UUID. It uses
 * {@link DefaultTimestampedIdentifiableRepositoryObserver} to set the createAt
 * and updatedAt dates on the object as appropriate.
 * <p/>
 * Extend this repository to persist TimestampedIdentifiable instances that have
 * a single, unique identifier, that is not a UUID and you need createdAt and
 * updatedAt time stamps automatically applied.
 * 
 * @author toddf
 * @since Jan 22, 2014
 */
public abstract class CassandraTimestampedEntityRepository<T extends TimestampedIdentifiable>
extends CassandraEntityRepository<T>
{
	protected CassandraTimestampedEntityRepository(CqlSession session, String tableName, String identifierColumn)
	{
		super(session, tableName, identifierColumn);
		initializeObservers();
	}

	protected void initializeObservers()
	{
		addObserver(new DefaultTimestampedIdentifiableRepositoryObserver<T>());
	}
}
