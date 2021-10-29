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
import com.datastax.oss.driver.api.core.cql.Row;
import com.strategicgains.repoexpress.domain.Identifiable;
import com.strategicgains.repoexpress.domain.Identifier;

/**
 * A Cassandra repository that manages types of Identifiable instances, which are
 * identified by a single primary key.
 * <p/>
 * Extend this repository to persist Identifiable instances that have a single, unique
 * identifier, that is not a UUID and you don't need the createdAt and updatedAt
 * time stamps (of TimestampedIdentifiable) automatically applied.
 * 
 * @author toddf
 * @since Apr 12, 2013
 */
public abstract class CassandraEntityRepository<T extends Identifiable>
extends AbstractCassandraRepository<T>
{
	private static final String EXISTENCE_CQL = "select count(*) from %s where %s = ?";
	private static final String READ_CQL = "select * from %s where %s = ?";
	private static final String DELETE_CQL = "delete from %s where %s = ?";

	private String identifierColumn;
	private PreparedStatement existStmt;
	private PreparedStatement readStmt;
	protected PreparedStatement deleteStmt;

	/**
	 * @param session a pre-configured Session instance.
	 * @param databaseName the name of a database this repository works against.
	 * @param identifierColumn the column name that holds the row key or unique identifier.
	 */
    public CassandraEntityRepository(CqlSession session, String tableName, String identifierColumn)
	{
		super(session, tableName);
		this.identifierColumn = identifierColumn;
		initialize();
	}

    protected void initialize()
    {
		existStmt = getSession().prepare(String.format(EXISTENCE_CQL, getTable(), identifierColumn));
		readStmt = getSession().prepare(String.format(READ_CQL, getTable(), identifierColumn));
		deleteStmt = getSession().prepare(String.format(DELETE_CQL, getTable(), identifierColumn));
    }

	public String getIdentifierColumn()
	{
		return identifierColumn;
	}

	@Override
	public boolean exists(Identifier identifier)
	{
		if (identifier == null || identifier.isEmpty()) return false;

		BoundStatement bs = bindIdentifier(existStmt, identifier);
		return (getSession().execute(bs).one().getLong(0) > 0);
	}

	@Override
	protected T readEntityById(Identifier identifier)
	{
		if (identifier == null || identifier.isEmpty()) return null;
		
		BoundStatement bs = bindIdentifier(readStmt, identifier);
		return marshalRow(getSession().execute(bs).one());
	}

	@Override
	protected void deleteEntity(T entity)
	{
		if (entity == null) return;
		
		BoundStatement bs = bindIdentifier(deleteStmt, entity.getIdentifier());
		getSession().execute(bs);
	}

	protected abstract T marshalRow(Row row);
}
