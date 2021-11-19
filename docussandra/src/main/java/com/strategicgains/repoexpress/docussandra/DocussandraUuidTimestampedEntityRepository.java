package com.strategicgains.repoexpress.docussandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.strategicgains.repoexpress.domain.TimestampedIdentifiable;
import com.strategicgains.repoexpress.domain.UuidEntity;
import com.strategicgains.repoexpress.event.DefaultTimestampedIdentifiableRepositoryObserver;
import com.strategicgains.repoexpress.event.UuidEntityRepositoryObserver;

/**
 * A Cassandra repository that manages types of Timestamped UuidIdentifiable instances, which are
 * identified by a single UUID primary key. It utilizes the {@link UuidEntityRepositoryObserver}
 * to assign a UUID on creation.  It also uses {@link DefaultTimestampedIdentifiableRepositoryObserver}
 * to set the createAt and updatedAt dates on the object as appropriate.
 *
 * <p/>
 * Extend this repository to persist entities identified by a UUID and implement Timestamped, so need the
 * createdAt and updatedAt time stamps automatically applied.
 * 
 * @author toddf
 * @since Jan 28, 2014
 */
public abstract class DocussandraUuidTimestampedEntityRepository<T extends UuidEntity & TimestampedIdentifiable>
extends DocussandraUuidEntityRepository<T>
{
	protected DocussandraUuidTimestampedEntityRepository(CqlSession session, String tableName, String identifierColumn, Class<?> resultClass)
    {
	    super(session, tableName, identifierColumn, resultClass);
    }

	@Override
    protected void initializeObservers()
    {
		super.initializeObservers();
		addObserver(new DefaultTimestampedIdentifiableRepositoryObserver<>());
    }
}
