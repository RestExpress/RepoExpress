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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.junit.Test;

import com.strategicgains.noschema.AbstractEntity;
import com.strategicgains.noschema.Identifier;
import com.strategicgains.repoexpress.exception.InvalidObjectIdException;

public class JdbcRepositoryIdentifierValidationTest
{
	@Test
	public void shouldThrowWhenIdentifierComponentCountDoesNotMatchPrimaryKeyFields()
	{
		JdbcRepository<SimpleEntity> repository = new JdbcRepository<SimpleEntity>(DSL.using(org.jooq.SQLDialect.DEFAULT), new CompositeIdDefinition());

		try
		{
			repository.exists(new Identifier("tenant-only"));
			fail("Expected InvalidObjectIdException");
		}
		catch (InvalidObjectIdException e)
		{
			assertTrue(e.getMessage().contains("component count"));
		}
	}

	private static class CompositeIdDefinition
	implements JdbcEntityDefinition<SimpleEntity>
	{
		@Override
		public Table<?> table()
		{
			return DSL.table(DSL.name("simple_entities"));
		}

		@Override
		public List<Field<?>> idFields()
		{
			return java.util.Arrays.<Field<?>>asList(
				DSL.field(DSL.name("tenant_id"), String.class),
				DSL.field(DSL.name("user_id"), Long.class)
			);
		}

		@Override
		public Map<Field<?>, Object> insertValues(SimpleEntity entity)
		{
			return Collections.emptyMap();
		}

		@Override
		public Map<Field<?>, Object> updateValues(SimpleEntity entity)
		{
			return Collections.emptyMap();
		}

		@Override
		public SimpleEntity fromRecord(Record record)
		{
			return new SimpleEntity();
		}

		@Override
		public Field<?> field(String logicalFieldName)
		{
			return null;
		}
	}

	private static class SimpleEntity
	extends AbstractEntity<Long>
	{
		private Long id;

		@Override
		public Identifier getIdentifier()
		{
			return (id == null ? null : new Identifier(id));
		}

		@Override
		public Long getId()
		{
			return id;
		}

		@Override
		public void setId(Long id)
		{
			this.id = id;
		}
	}
}
