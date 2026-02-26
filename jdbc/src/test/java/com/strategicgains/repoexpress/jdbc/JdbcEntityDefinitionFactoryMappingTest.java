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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.jooq.Field;
import org.junit.Test;

import com.strategicgains.noschema.AbstractEntity;
import com.strategicgains.noschema.Identifier;
import com.strategicgains.repoexpress.jdbc.annotation.Entity;
import com.strategicgains.repoexpress.jdbc.annotation.Id;
import com.strategicgains.repoexpress.jdbc.annotation.Property;

public class JdbcEntityDefinitionFactoryMappingTest
{
	@Test
	public void shouldRespectTransientAndPropertyFlags()
	{
		JdbcEntityDefinition<MappedEntity> definition = JdbcEntityDefinitionFactory.from(MappedEntity.class);
		MappedEntity entity = new MappedEntity();
		entity.setId(7L);
		entity.email = "user@example.com";
		entity.displayName = "Display";
		entity.serverGenerated = "db-only";
		entity.javaTransientField = "ignore-me";
		entity.annotatedTransientField = "ignore-me-too";

		assertNotNull(definition.field("id"));
		assertNotNull(definition.field("email"));
		assertNull(definition.field("javaTransientField"));
		assertNull(definition.field("annotatedTransientField"));
		assertNull(definition.field("displayName"));

		Map<Field<?>, Object> insertValues = definition.insertValues(entity);
		Map<Field<?>, Object> updateValues = definition.updateValues(entity);

		assertTrue(containsField(insertValues, "id"));
		assertTrue(containsField(insertValues, "email_address"));
		assertTrue(containsField(insertValues, "display_name"));
		assertFalse(containsField(insertValues, "server_generated"));
		assertFalse(containsField(insertValues, "java_transient"));
		assertFalse(containsField(insertValues, "annotated_transient"));

		assertFalse(containsField(updateValues, "id"));					// IDs are never updated
		assertTrue(containsField(updateValues, "email_address"));
		assertFalse(containsField(updateValues, "display_name"));		// updatable = false
		assertFalse(containsField(updateValues, "server_generated"));	// insertable/updatable = false
	}

	@Test
	public void shouldOrderCompositeIdFieldsByIdOrder()
	{
		JdbcEntityDefinition<CompositeMappedEntity> definition = JdbcEntityDefinitionFactory.from(CompositeMappedEntity.class);

		assertEquals(2, definition.idFields().size());
		assertEquals("tenant_id", definition.idFields().get(0).getName());
		assertEquals("user_id", definition.idFields().get(1).getName());
	}

	private boolean containsField(Map<Field<?>, Object> values, String fieldName)
	{
		for (Field<?> field : values.keySet())
		{
			if (fieldName.equals(field.getName())) return true;
		}

		return false;
	}

	@Entity(table = "mapped_entities")
	private static class MappedEntity
	extends AbstractEntity<Long>
	{
		@Id
		@Property("id")
		private Long id;

		@Property(value = "email_address", queryName = "email")
		private String email;

		@Property(value = "display_name", queryable = false, sortable = false, updatable = false)
		private String displayName;

		@Property(value = "server_generated", insertable = false, updatable = false)
		private String serverGenerated;

		@Property("java_transient")
		private transient String javaTransientField;

		@Property("annotated_transient")
		@com.strategicgains.repoexpress.jdbc.annotation.Transient
		private String annotatedTransientField;

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

	@Entity(table = "composite_entities")
	private static class CompositeMappedEntity
	extends AbstractEntity<String>
	{
		@Id(order = 1)
		@Property("user_id")
		private Long userId;

		@Id(order = 0)
		@Property("tenant_id")
		private String tenantId;

		@Override
		public Identifier getIdentifier()
		{
			return new Identifier(tenantId, userId);
		}

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
}
