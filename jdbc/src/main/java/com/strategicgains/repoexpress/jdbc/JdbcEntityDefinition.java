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

import java.util.List;
import java.util.Map;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;

import com.strategicgains.noschema.Identifiable;

/**
 * A thin RepoExpress-to-jOOQ adapter describing how an entity maps to a relational table.
 * Implementations should whitelist queryable/sortable fields in {@link #field(String)}.
 *
 * @author toddf
 * @since Feb 26, 2026
 */
public interface JdbcEntityDefinition<T extends Identifiable>
{
	public Table<?> table();

	public List<Field<?>> idFields();

	public Map<Field<?>, Object> insertValues(T entity);

	public Map<Field<?>, Object> updateValues(T entity);

	public T fromRecord(Record record);

	public Field<?> field(String logicalFieldName);
}
