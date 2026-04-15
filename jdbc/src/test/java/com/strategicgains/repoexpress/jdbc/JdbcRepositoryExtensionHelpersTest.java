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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.jooq.Condition;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.Test;

import com.strategicgains.noschema.Identifier;
import com.strategicgains.noschema.entity.AbstractEntity;
import com.strategicgains.repoexpress.jdbc.annotation.Entity;
import com.strategicgains.repoexpress.jdbc.annotation.Id;
import com.strategicgains.repoexpress.jdbc.annotation.Property;

public class JdbcRepositoryExtensionHelpersTest
{
	@Test
	public void shouldSupportSubclassAlternateKeyConditionConstruction()
	{
		LinkRepository repository = new LinkRepository();
		UUID accountId = UUID.randomUUID();
		String alias = "my-link";

		Condition condition = repository.aliasCondition(accountId, alias);

		assertEquals("links", repository.mappedTableName());
		assertTrue(condition.toString().contains("account_id"));
		assertTrue(condition.toString().contains("alias"));
	}

	private static class LinkRepository
	extends JdbcRepository<Link>
	{
		private LinkRepository()
		{
			super(DSL.using(SQLDialect.DEFAULT), Link.class);
		}

		public Condition aliasCondition(UUID accountId, String alias)
		{
			return field("accountId", UUID.class).eq(accountId)
				.and(field("alias", String.class).eq(alias));
		}

		public String mappedTableName()
		{
			return table().getName();
		}
	}

	@Entity(table = "links")
	private static class Link
	extends AbstractEntity<UUID>
	{
		@Id
		@Property(value = "id", queryName = "id")
		private UUID id;

		@Property(value = "account_id", queryName = "accountId")
		private UUID accountId;

		@Property(value = "alias", queryName = "alias")
		private String alias;

		@Override
		public Identifier getIdentifier()
		{
			return (id == null ? null : new Identifier(id));
		}

		@Override
		public UUID getId()
		{
			return id;
		}

		@Override
		public void setId(UUID id)
		{
			this.id = id;
		}
	}
}
