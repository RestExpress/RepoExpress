/*
    Copyright 2013, Strategic Gains, Inc.

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
package com.strategicgains.repoexpress.cassandra;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import java.util.stream.Collectors;

import org.restexpress.common.exception.ConfigurationException;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.DefaultConsistencyLevel;

/**
 * @author toddf
 * @since Dec 20, 2013
 */
public class CassandraConfig
{
	private static final String DEFAULT_PORT = "9042";
	private static final String CONTACT_POINTS_PROPERTY = "cassandra.contactPoints";
	private static final String PORT_PROPERTY = "cassandra.port";
	private static final String KEYSPACE_PROPERTY = "cassandra.keyspace";
	private static final String DATA_CENTER = "cassandra.dataCenter";
	private static final String READ_CONSISTENCY_LEVEL = "cassandra.readConsistencyLevel";
	private static final String WRITE_CONSISTENCY_LEVEL = "cassandra.writeConsistencyLevel";

	private Collection<InetSocketAddress> contactPoints;
	private String keyspace;
	private int port;
	private String dataCenter;
	private ConsistencyLevel readConsistencyLevel;
	private ConsistencyLevel writeConsistencyLevel;

	private CqlSession session;

	public CassandraConfig(Properties p)
	{
		port = Integer.parseInt(p.getProperty(PORT_PROPERTY, DEFAULT_PORT));
		dataCenter = p.getProperty(DATA_CENTER);
		readConsistencyLevel = DefaultConsistencyLevel.valueOf(p.getProperty(READ_CONSISTENCY_LEVEL, "LOCAL_QUORUM"));
		writeConsistencyLevel = DefaultConsistencyLevel.valueOf(p.getProperty(WRITE_CONSISTENCY_LEVEL, "LOCAL_QUORUM"));
		keyspace = p.getProperty(KEYSPACE_PROPERTY);

		if (keyspace == null || keyspace.trim().isEmpty())
		{
			throw new ConfigurationException(
			    "Please define a Cassandra keyspace in property: "
			        + KEYSPACE_PROPERTY);
		}

		String contactPointsCommaDelimited = p.getProperty(CONTACT_POINTS_PROPERTY);

		if (contactPointsCommaDelimited == null || contactPointsCommaDelimited.trim().isEmpty())
		{
			throw new ConfigurationException(
			    "Please define Cassandra contact points (IP addresses) for property: "
			        + CONTACT_POINTS_PROPERTY);
		}

		contactPoints = Arrays.stream(contactPointsCommaDelimited.split(",\\s*")).map(s -> {
			try
			{
				return new InetSocketAddress(InetAddress.getByName(s), port);
			}
			catch (UnknownHostException e)
			{
				throw new ConfigurationException(e);
			}
		}).collect(Collectors.toList());

		initialize(p);
	}

	/**
	 * Sub-classes can override to initialize other properties.
	 * 
	 * @param p Propreties
	 */
	protected void initialize(Properties p)
	{
		// default is to do nothing.
	}

	public String getKeyspace()
	{
		return keyspace;
	}

	public int getPort()
	{
		return port;
	}

	public String getDataCenter()
	{
		return dataCenter;
	}

	public ConsistencyLevel getReadConsistencyLevel()
	{
		return readConsistencyLevel;
	}

	public ConsistencyLevel getWriteConsistencyLevel()
	{
		return writeConsistencyLevel;
	}

	public CqlSession getSession()
	{
		if (session == null)
		{
			session = createSession();
		}

		return session;
	}

	protected CqlSession createSession()
	{
		CqlSessionBuilder cb = CqlSession.builder();
		cb.addContactPoints(contactPoints);
		cb.withKeyspace(getKeyspace());
		
		if (getDataCenter() != null)
		{
			cb.withLocalDatacenter(getDataCenter());
//			cb.withLoadBalancingPolicy(DCAwareRoundRobinPolicy.builder().withLocalDc(getDataCenter()).build());
		}
		
		enrichCluster(cb);
		return cb.build();
	}

	/**
	 * Sub-classes override this method to do specialized cluster configuration.
	 * 
	 * @param clusterBuilder
	 */
	protected void enrichCluster(CqlSessionBuilder clusterBuilder)
    {
		// default is to do nothing.
    }
}
