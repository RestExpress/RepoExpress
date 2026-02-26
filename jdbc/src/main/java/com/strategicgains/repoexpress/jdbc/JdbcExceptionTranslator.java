/*
    Copyright 2026, Strategic Gains, Inc.

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
package com.strategicgains.repoexpress.jdbc;

import java.sql.SQLException;

import org.jooq.exception.DataAccessException;

import com.strategicgains.repoexpress.exception.DuplicateItemException;
import com.strategicgains.repoexpress.exception.RepositoryException;

final class JdbcExceptionTranslator
{
	private JdbcExceptionTranslator()
	{
		super();
	}

	public static DuplicateItemException toDuplicateItemException(String message, DataAccessException e)
	{
		return new DuplicateItemException(message, e);
	}

	public static RepositoryException toRepositoryException(String message, DataAccessException e)
	{
		return new RepositoryException(message, e);
	}

	public static boolean isDuplicateKey(DataAccessException e)
	{
		SQLException sql = findSqlException(e);

		if (sql == null) return false;

		String state = sql.getSQLState();

		if ("23505".equals(state)) return true;		// PostgreSQL unique_violation
		if ("23000".equals(state) && sql.getErrorCode() == 1062) return true;	// MySQL duplicate key
		return false;
	}

	private static SQLException findSqlException(Throwable t)
	{
		Throwable current = t;

		while (current != null)
		{
			if (current instanceof SQLException)
			{
				return (SQLException) current;
			}

			current = current.getCause();
		}

		return null;
	}
}
