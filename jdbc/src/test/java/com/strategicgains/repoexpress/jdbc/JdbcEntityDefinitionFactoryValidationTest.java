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

import org.junit.Test;

import com.strategicgains.noschema.AbstractEntity;
import com.strategicgains.noschema.Identifier;
import com.strategicgains.repoexpress.exception.RepositoryException;
import com.strategicgains.repoexpress.jdbc.annotation.Entity;
import com.strategicgains.repoexpress.jdbc.annotation.Id;
import com.strategicgains.repoexpress.jdbc.annotation.Property;

public class JdbcEntityDefinitionFactoryValidationTest
{
	@Test
	public void shouldFailWithoutEntityAnnotation()
	{
		assertFactoryFailure(MissingEntityAnnotation.class, "Missing @Entity");
	}

	@Test
	public void shouldFailWithoutIdField()
	{
		assertFactoryFailure(NoIdField.class, "At least one @Id");
	}

	@Test
	public void shouldFailOnDuplicateIdOrder()
	{
		assertFactoryFailure(DuplicateIdOrder.class, "Duplicate @Id(order=0)");
	}

	@Test
	public void shouldFailOnDuplicateColumnNames()
	{
		assertFactoryFailure(DuplicateColumns.class, "Duplicate mapped column");
	}

	@Test
	public void shouldFailOnDuplicateLogicalQueryNames()
	{
		assertFactoryFailure(DuplicateLogicalNames.class, "Duplicate query field");
	}

	@Test
	public void shouldFailWithoutNoArgConstructor()
	{
		assertFactoryFailure(NoNoArgConstructor.class, "no-arg constructor");
	}

	private void assertFactoryFailure(Class<?> type, String expectedMessagePart)
	{
		try
		{
			JdbcEntityDefinitionFactory.from(type.asSubclass(com.strategicgains.noschema.Identifiable.class));
			fail("Expected RepositoryException");
		}
		catch (RepositoryException e)
		{
			assertTrue(e.getMessage().contains(expectedMessagePart));
		}
	}

	private static abstract class BaseTestEntity<T>
	extends AbstractEntity<T>
	{
		@Override
		public Identifier getIdentifier()
		{
			Object id = getId();
			return (id == null ? null : new Identifier(id));
		}
	}

	private static class MissingEntityAnnotation
	extends BaseTestEntity<Long>
	{
		@Id
		@Property("id")
		private Long id;

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

	@Entity(table = "no_id")
	private static class NoIdField
	extends BaseTestEntity<Long>
	{
		@Property("name")
		private String name;

		@Override
		public Long getId()
		{
			return null;
		}

		@Override
		public void setId(Long id)
		{
			// no-op
		}
	}

	@Entity(table = "dup_id_order")
	private static class DuplicateIdOrder
	extends BaseTestEntity<String>
	{
		@Id(order = 0)
		@Property("tenant_id")
		private String tenantId;

		@Id(order = 0)
		@Property("user_id")
		private String userId;

		@Override
		public String getId()
		{
			return tenantId;
		}

		@Override
		public void setId(String id)
		{
			this.tenantId = id;
		}
	}

	@Entity(table = "dup_columns")
	private static class DuplicateColumns
	extends BaseTestEntity<Long>
	{
		@Id
		@Property("id")
		private Long id;

		@Property("same_column")
		private String a;

		@Property("same_column")
		private String b;

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

	@Entity(table = "dup_query_names")
	private static class DuplicateLogicalNames
	extends BaseTestEntity<Long>
	{
		@Id
		@Property("id")
		private Long id;

		@Property(value = "first_name", queryName = "name")
		private String firstName;

		@Property(value = "last_name", queryName = "name")
		private String lastName;

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

	@Entity(table = "no_noarg")
	private static class NoNoArgConstructor
	extends BaseTestEntity<Long>
	{
		@Id
		@Property("id")
		private Long id;

		private NoNoArgConstructor(String ignored)
		{
			super();
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
