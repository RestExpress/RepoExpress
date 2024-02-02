package com.strategicgains.repoexpress.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.strategicgains.repoexpress.domain.UuidEntity;
import com.strategicgains.repoexpress.event.UuidEntityRepositoryObserver;

/**
 * A Cassandra repository that manages types of UuidIdentifiable, which are identified by a single
 * UUID primary key. It utilizes the {@link UuidEntityRepositoryObserver} to assign a UUID on creation.
 * <p/>
 * Storing a UUID as the ID (as this repository does) requires four (4) bytes for the ID.
 * <p/>
 * Extend this repository to persist entities identified by a UUID but do not implement Timestamped, so do not
 * need the createdAt and updatedAt time stamps applied.
 * 
 * @author toddf
 * @since Jan 28, 2014
 * @see CassandraUuidTimestampedEntityRepository
 */
public abstract class CassandraUuidEntityRepository<T extends UuidEntity>
extends CassandraEntityRepository<T>
{
	public CassandraUuidEntityRepository(CqlSession session, String tableName, String identifierColumn)
    {
	    super(session, tableName, identifierColumn);
	    initializeObservers();
    }

    protected void initializeObservers()
    {
		addObserver(new UuidEntityRepositoryObserver<>());
    }
}
