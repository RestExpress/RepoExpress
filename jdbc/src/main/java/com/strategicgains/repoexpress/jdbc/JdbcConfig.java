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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.restexpress.common.exception.ConfigurationException;

/**
 * Encapsulates JDBC connection configuration, similar to MongoConfig/CassandraConfig.
 *
 * Supported properties (env vars take precedence):
 * - jdbc.url / JDBC_URL
 * - jdbc.username / JDBC_USERNAME
 * - jdbc.password / JDBC_PASSWORD
 * - jdbc.driverClass / JDBC_DRIVER_CLASS (optional)
 *
 * @author toddf
 * @since Feb 26, 2026
 */
public class JdbcConfig
{
	private static final String URL_PROPERTY = "jdbc.url";
	private static final String URL_ENVIRONMENT_PROPERTY = "JDBC_URL";
	private static final String USERNAME_PROPERTY = "jdbc.username";
	private static final String USERNAME_ENVIRONMENT_PROPERTY = "JDBC_USERNAME";
	private static final String PASSWORD_PROPERTY = "jdbc.password";
	private static final String PASSWORD_ENVIRONMENT_PROPERTY = "JDBC_PASSWORD";
	private static final String DRIVER_CLASS_PROPERTY = "jdbc.driverClass";
	private static final String DRIVER_CLASS_ENVIRONMENT_PROPERTY = "JDBC_DRIVER_CLASS";
	private static final String DIALECT_PROPERTY = "jdbc.dialect";
	private static final String DIALECT_ENVIRONMENT_PROPERTY = "JDBC_DIALECT";

	private String url;
	private String username;
	private String password;
	private String driverClass;
	private SQLDialect sqlDialect;

	public JdbcConfig(Properties p)
	{
		url = p.getProperty(URL_ENVIRONMENT_PROPERTY, p.getProperty(URL_PROPERTY));
		username = p.getProperty(USERNAME_ENVIRONMENT_PROPERTY, p.getProperty(USERNAME_PROPERTY));
		password = p.getProperty(PASSWORD_ENVIRONMENT_PROPERTY, p.getProperty(PASSWORD_PROPERTY));
		driverClass = p.getProperty(DRIVER_CLASS_ENVIRONMENT_PROPERTY, p.getProperty(DRIVER_CLASS_PROPERTY));
		sqlDialect = parseDialect(p.getProperty(DIALECT_ENVIRONMENT_PROPERTY, p.getProperty(DIALECT_PROPERTY)));

		if (url == null || url.trim().isEmpty())
		{
			throw new ConfigurationException(String.format(
				"Please define a JDBC URL for property: %s or %s", URL_PROPERTY, URL_ENVIRONMENT_PROPERTY));
		}

		loadDriverIfConfigured(driverClass);
		initialize(p);
	}

	/**
	 * Sub-classes can override to initialize other properties.
	 *
	 * @param p Properties
	 */
	protected void initialize(Properties p)
	{
		// default is to do nothing.
	}

	public String getUrl()
	{
		return url;
	}

	public String getUsername()
	{
		return username;
	}

	public String getPassword()
	{
		return password;
	}

	public String getDriverClass()
	{
		return driverClass;
	}

	public SQLDialect getSqlDialect()
	{
		return sqlDialect;
	}

	/**
	 * Creates a new JDBC connection using the configured URL and credentials.
	 * Returns a new connection on each invocation.
	 */
	public Connection createConnection()
	{
		try
		{
			if (username == null)
			{
				return DriverManager.getConnection(url);
			}

			return DriverManager.getConnection(url, username, password);
		}
		catch (SQLException e)
		{
			throw new ConfigurationException("Unable to create JDBC connection", e);
		}
	}

	public DSLContext createDslContext()
	{
		return createDslContext(sqlDialect);
	}

	public DSLContext createDslContext(SQLDialect dialect)
	{
		return DSL.using(createConnection(), (dialect == null ? SQLDialect.DEFAULT : dialect));
	}

	private void loadDriverIfConfigured(String configuredDriverClass)
	{
		if (configuredDriverClass == null || configuredDriverClass.trim().isEmpty())
		{
			return;
		}

		try
		{
			Class.forName(configuredDriverClass);
		}
		catch (ClassNotFoundException e)
		{
			throw new ConfigurationException("JDBC driver class not found: " + configuredDriverClass, e);
		}
	}

	private SQLDialect parseDialect(String configuredDialect)
	{
		if (configuredDialect == null || configuredDialect.trim().isEmpty())
		{
			return null;
		}

		try
		{
			return SQLDialect.valueOf(configuredDialect.trim().toUpperCase());
		}
		catch (IllegalArgumentException e)
		{
			throw new ConfigurationException("Unsupported JDBC dialect: " + configuredDialect, e);
		}
	}
}
