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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.jooq.Field;
import org.junit.Test;

import com.strategicgains.repoexpress.jdbc.example.AnnotatedUser;

public class JdbcEntityDefinitionFactoryTest
{
	@Test
	public void shouldCacheDefinitionsByEntityClass()
	{
		JdbcEntityDefinition<AnnotatedUser> one = JdbcEntityDefinitionFactory.from(AnnotatedUser.class);
		JdbcEntityDefinition<AnnotatedUser> two = JdbcEntityDefinitionFactory.from(AnnotatedUser.class);

		assertSame(one, two);
	}

	@Test
	public void shouldBuildDefinitionFromAnnotations()
	{
		JdbcEntityDefinition<AnnotatedUser> definition = JdbcEntityDefinitionFactory.from(AnnotatedUser.class);
		AnnotatedUser user = new AnnotatedUser();
		user.setId(42L);
		user.setEmail("user@example.com");
		user.setDisplayName("Example User");
		user.setIgnored("ignore me");

		assertEquals("users", definition.table().getName());
		assertEquals(1, definition.idFields().size());
		assertEquals("id", definition.idFields().get(0).getName());

		assertNotNull(definition.field("id"));
		assertNotNull(definition.field("email"));
		assertNull(definition.field("displayName"));
		assertNull(definition.field("ignored"));

		Map<Field<?>, Object> insertValues = definition.insertValues(user);
		Map<Field<?>, Object> updateValues = definition.updateValues(user);

		assertEquals(3, insertValues.size());
		assertEquals(2, updateValues.size());
		assertTrue(insertValues.values().contains("user@example.com"));
		assertTrue(updateValues.values().contains("Example User"));
		assertTrue(updateValues.values().contains("user@example.com"));
	}
}
