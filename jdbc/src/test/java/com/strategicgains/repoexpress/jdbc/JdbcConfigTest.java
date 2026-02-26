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
import static org.junit.Assert.assertNull;

import java.util.Properties;

import org.jooq.SQLDialect;
import org.junit.Test;
import org.restexpress.common.exception.ConfigurationException;

public class JdbcConfigTest
{
	@Test(expected = ConfigurationException.class)
	public void shouldRequireJdbcUrl()
	{
		new JdbcConfig(new Properties());
	}

	@Test
	public void shouldAllowMissingUsernameAndPassword()
	{
		JdbcConfig config = new JdbcConfig(properties("jdbc.url", "jdbc:test"));

		assertEquals("jdbc:test", config.getUrl());
		assertNull(config.getUsername());
		assertNull(config.getPassword());
	}

	@Test
	public void shouldParseConfiguredDialect()
	{
		JdbcConfig config = new JdbcConfig(properties(
			"jdbc.url", "jdbc:test",
			"jdbc.dialect", "postgres"
		));

		assertEquals(SQLDialect.POSTGRES, config.getSqlDialect());
	}

	@Test(expected = ConfigurationException.class)
	public void shouldRejectUnsupportedDialect()
	{
		new JdbcConfig(properties(
			"jdbc.url", "jdbc:test",
			"jdbc.dialect", "not-a-dialect"
		));
	}

	@Test
	public void shouldLoadConfiguredDriverClass()
	{
		JdbcConfig config = new JdbcConfig(properties(
			"jdbc.url", "jdbc:test",
			"jdbc.driverClass", "java.lang.String"
		));

		assertEquals("java.lang.String", config.getDriverClass());
	}

	@Test(expected = ConfigurationException.class)
	public void shouldFailForMissingDriverClass()
	{
		new JdbcConfig(properties(
			"jdbc.url", "jdbc:test",
			"jdbc.driverClass", "com.example.DoesNotExistDriver"
		));
	}

	private Properties properties(String... keyValues)
	{
		Properties p = new Properties();

		for (int i = 0; i < keyValues.length; i += 2)
		{
			p.setProperty(keyValues[i], keyValues[i + 1]);
		}

		return p;
	}
}
