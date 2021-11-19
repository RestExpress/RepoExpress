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
package com.strategicgains.repoexpress.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.strategicgains.repoexpress.AbstractObservableRepository;
import com.strategicgains.repoexpress.domain.Identifiable;
import com.strategicgains.repoexpress.domain.Identifier;
import com.strategicgains.repoexpress.exception.DuplicateItemException;
import com.strategicgains.repoexpress.exception.InvalidObjectIdException;
import com.strategicgains.repoexpress.exception.ItemNotFoundException;

/**
 * The most-basic Cassandra-based repository, supporting arbitrary single- or compound-identifier based
 * entities. This is the base class for the other Cassandra-based repositories in this package.
 * <p/>
 * Sub-classes must implement the, createEntity(), updateEntity(), readEntityById(), exists()
 * and deleteEntity() abstract methods. Along with any other custom-query-type methods.
 * <p/>
 * The bindIdentifier(BoundStatement, Identifier) method will bind the components in the
 * Identifier instance to a prepared statement, if desired.
 * 
 * @author toddf
 * @since Apr 12, 2013
 */
public abstract class AbstractCassandraRepository<T extends Identifiable>
extends AbstractObservableRepository<T>
{
	private CqlSession session;
	private String table;

	/**
	 * @param session a pre-configured Session instance.
	 * @param tableName the name of the Cassandra table entities are stored in.
	 */
    protected AbstractCassandraRepository(CqlSession session, String tableName)
	{
		super();
		this.session = session;
		this.table = tableName;
	}
    
    protected CqlSession getSession()
    {
    	return session;
    }
    
    protected String getTable()
    {
    	return table;
    }

	@Override
	public T doCreate(T entity, boolean ifUnique)
	{
		if (ifUnique && exists(entity.getIdentifier()))
		{
			throw new DuplicateItemException(entity.getClass().getSimpleName()
			    + " ID already exists: " + entity.getIdentifier().toString());
		}

		return createEntity(entity);
	}

	@Override
	public T doRead(Identifier id)
	{
		T item = readEntityById(id);

		if (item == null)
		{
			throw new ItemNotFoundException("ID not found: " + id.toString());
		}

		return item;
	}

	@Override
	public T doUpdate(T entity, boolean ifExists)
	{
		if (ifExists && !exists(entity.getIdentifier()))
		{
			throw new ItemNotFoundException(entity.getClass().getSimpleName()
			    + " ID not found: " + entity.getIdentifier().toString());
		}

		return updateEntity(entity);
	}

	@Override
	public void doDelete(T entity)
	{
		try
		{
			deleteEntity(entity);
		}
		catch (InvalidObjectIdException e)
		{
			throw new ItemNotFoundException("ID not found: " + entity.getIdentifier().toString());
		}
	}

	protected BoundStatement bindIdentifier(PreparedStatement ps, Identifier identifier)
	{
		return ps.bind(identifier.components().toArray());
	}

	/**
	 * Read a Cassandra table, using the Identifier instance and marshal the return row to
	 * a domain object.
	 * 
	 * @param identifier
	 * @return
	 */
	protected abstract T readEntityById(Identifier identifier);

	/**
	 * Using the data from the entity, persist it to Cassandra, returning the newly-created
	 * model (so it contains newly-assigned identifiers and/or other data).
	 * <p/>
	 * Uniqueness checking has already occurred at this point.
	 * 
	 * @param entity a domain model to persist.
	 * @return the newly-created domain model entity.
	 */
	protected abstract T createEntity(T entity);

	/**
	 * Using the data from the entity, persist it to Cassandra, returning the newly-updated
	 * model (so it contains newly-assigned data, if applicable).
	 * <p/>
	 * A check for existence has already occurred at this point.
	 * 
	 * @param entity a domain model to persist.
	 * @return the newly-updated domain model entity.
	 */
	protected abstract T updateEntity(T entity);

	/**
	 * Using the Identifier from the entity, delete the corresponding row(s) from Cassandra.
	 * <p/>
	 * Existence checking has already occurred at this point.
	 * 
	 * @param entity a domain model to persist.
	 * @return the newly-updated domain model entity.
	 */
	protected abstract void deleteEntity(T entity);
}
