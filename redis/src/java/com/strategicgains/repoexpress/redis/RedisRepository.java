/*
    Copyright 2012, Strategic Gains, Inc.

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
package com.strategicgains.repoexpress.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.strategicgains.repoexpress.AbstractObservableRepository;
import com.strategicgains.repoexpress.domain.Identifiable;
import com.strategicgains.repoexpress.domain.Identifier;
import com.strategicgains.repoexpress.event.AbstractRepositoryObserver;
import com.strategicgains.repoexpress.exception.DuplicateItemException;
import com.strategicgains.repoexpress.exception.ItemNotFoundException;
import com.strategicgains.repoexpress.exception.RepositoryException;

/**
 * This Redis repository works on simpler objects than RedisJOhmRepository, in that, the persisted
 * entities must only implement Identifiable and can have String types as their ID.
 * <p/>
 * Note that this repository requires the client to set appropriate ID values.  Otherwise, since
 * this repository is 'observable', a RepositoryObserver may be used to perform that task automatically
 * on a create operation if desired.
 * <p/>
 * Usage of this repository requires de/serialization to occur during the persistence operation.
 * Consequently, sub-classes must implement the marshalFrom(T) and marshalTo(String, T) methods.
 * If using this within a RestExpress service suite, DefaultJsonProcessor or DefaultXmlProcessor
 * can be used.  Or, if a RestExpress Maven archetype was used, SerlializationProvider.JSON_SERIALIZER
 * or SerlializationProvider.XML_SERIALIZER may be leveraged (you might need to make them public or
 * provide a static accessor).
 * 
 * @author toddf, seans
 * @since Jul 19, 2012
 * @see AbstractRepositoryObserver
 * @see RedisJOhmRepository
 */
public abstract class RedisRepository<T extends Identifiable>
extends AbstractObservableRepository<T>
{
	private static final int NEVER_EXPIRE = -1;
	private JedisPool jedisPool;
	private Class<? extends T> entityClass;

	public RedisRepository(JedisPool jedisPool, Class<? extends T> entityClass)
	{
		super();
		this.jedisPool = jedisPool;
		this.entityClass = entityClass;
	}

	protected JedisPool getJedisPool()
	{
		return jedisPool;
	}

	@Override
	public T doCreate(T item, boolean ifUnique)
	{
		return doCreate(item, ifUnique, NEVER_EXPIRE);
	}

	protected T doCreate(T item, boolean ifUnique, int ttlSeconds)
	{
		//Item expires immediately, so no sense in storing it.
		if (ttlSeconds == 0)
		{
			return item;
		}

		if (ifUnique && exists(item.getIdentifier()))
		{
			throw new DuplicateItemException(item.getClass().getSimpleName()
			    + " ID already exists: " + item.getIdentifier());
		}

		Jedis jedis = jedisPool.getResource();
		
		try
		{
			if (!jedis.setex(item.getIdentifier().toString(), ttlSeconds, marshalFrom(item)).equalsIgnoreCase("OK"))
			{
				throw new RepositoryException("Error creating object: " + item.getIdentifier());
			}

			return item;
		}
		finally
		{
			jedis.close();
		}
	}

	@Override
	public void doDelete(T object)
	{
		Jedis jedis = jedisPool.getResource();

		try
		{
			Long reply = jedis.del(object.getIdentifier().toString());

			if (reply < 1)
			{
				throw new ItemNotFoundException("ID not found: " + object.getIdentifier());
			}
		}
		finally
		{
			jedis.close();
		}
	}

	@Override
	public T doRead(Identifier id)
	{
		Jedis jedis = jedisPool.getResource();

		try
		{
			String json = jedis.get(id.toString());

			if (json == null || json.trim().isEmpty())
			{
				throw new ItemNotFoundException("ID not found: " + id);
			}

			return marshalTo(json, entityClass);
		}
		finally
		{
			jedis.close();
		}
	}

	@Override
	public T doUpdate(T item, boolean ifExists)
	{
		return doUpdate(item, ifExists, NEVER_EXPIRE);
	}

	protected T doUpdate(T item, boolean ifExists, int ttlSeconds)
	{
		if (ifExists && !exists(item.getIdentifier()))
		{
			throw new ItemNotFoundException(item.getClass().getSimpleName()
			    + " ID not found: " + item.getIdentifier());
		}

		Jedis jedis = jedisPool.getResource();
		
		try
		{
			if (!jedis.setex(item.getIdentifier().toString(), ttlSeconds, marshalFrom(item)).equalsIgnoreCase("OK"))
			{
				throw new RepositoryException("Error updating object: " + item.getIdentifier());
			}

			return item;
		}
		finally
		{
			jedis.close();
		}
	}

	@Override
	public boolean exists(Identifier id)
	{
		if (id == null) return false;

		Jedis jedis = jedisPool.getResource();
		
		try
		{
			return jedis.exists(id.toString());
		}
		finally
		{
			jedis.close();
		}
	}


	// SECTION: UTILITY
	
	protected abstract T marshalTo(String json, Class<? extends T> entityClass);
	protected abstract String marshalFrom(T instance);
}
