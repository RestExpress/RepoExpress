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

import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.Test;

import com.strategicgains.noschema.Identifier;
import com.strategicgains.noschema.entity.AbstractEntity;
import com.strategicgains.repoexpress.jdbc.annotation.Entity;
import com.strategicgains.repoexpress.jdbc.annotation.Id;
import com.strategicgains.repoexpress.jdbc.annotation.Property;

public class JdbcRepositoryConstructorTest
{
	@Test
	public void shouldConstructFromDslAndEntityClass()
	{
		JdbcRepository<ConstructorEntity> repository = new JdbcRepository<ConstructorEntity>(DSL.using(SQLDialect.DEFAULT), ConstructorEntity.class);
		assertNotNull(repository);
	}

	@Test
	public void shouldConstructFromJdbcConfigAndEntityClass()
	{
		JdbcRepository<ConstructorEntity> repository = new JdbcRepository<ConstructorEntity>(new StubJdbcConfig(), ConstructorEntity.class);
		assertNotNull(repository);
	}

	@Test
	public void shouldConstructFromVarargsSingleEntityClass()
	{
		@SuppressWarnings("unchecked")
		Class<? extends ConstructorEntity>[] types = new Class[] { ConstructorEntity.class };

		JdbcRepository<ConstructorEntity> repository = new JdbcRepository<ConstructorEntity>(DSL.using(SQLDialect.DEFAULT), types);
		assertNotNull(repository);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldRejectMultipleEntityClassesForNow()
	{
		@SuppressWarnings("unchecked")
		Class<? extends ConstructorEntity>[] types = new Class[] { ConstructorEntity.class, ExtendedConstructorEntity.class };

		new JdbcRepository<ConstructorEntity>(DSL.using(SQLDialect.DEFAULT), types);
	}

	private static class StubJdbcConfig
	extends JdbcConfig
	{
		public StubJdbcConfig()
		{
			super(properties());
		}

		@Override
		public DSLContext createDslContext()
		{
			return DSL.using(SQLDialect.DEFAULT);
		}

		private static Properties properties()
		{
			Properties p = new Properties();
			p.setProperty("jdbc.url", "jdbc:test");
			return p;
		}
	}

	@Entity(table = "constructor_entities")
	private static class ConstructorEntity
	extends AbstractEntity<Long>
	{
		@Id
		@Property("id")
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

	private static class ExtendedConstructorEntity
	extends ConstructorEntity
	{
		@Property("child_value")
		private String childValue;
	}
}
