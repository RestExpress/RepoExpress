package com.strategicgains.repoexpress.redis;

import com.strategicgains.repoexpress.domain.ExpiringIdentifiable;
import com.strategicgains.repoexpress.event.AbstractRepositoryObserver;

import redis.clients.jedis.JedisPool;

/**
 * This Redis repository extends RedisRepository and allows the persisted entities to set an
 * expiration time. A time of -1 is used to specify that the entity should never expire.
 * 
 * @since Aug 24, 2013
 * @see AbstractRepositoryObserver
 * @see RedisRepository
 */
public abstract class ExpiringRedisRepository<T extends ExpiringIdentifiable>
extends RedisRepository<T>
{
	public ExpiringRedisRepository(JedisPool jedisPool, Class<? extends T> entityClass)
	{
		super(jedisPool, entityClass);
	}

	@Override
	public T doCreate(T item, boolean ifUnique)
	{
		return super.doCreate(item, ifUnique, item.getTtlSeconds());
	}

	@Override
	public T doUpdate(T item, boolean ifExists)
	{
		return super.doUpdate(item, ifExists, item.getTtlSeconds());
	}
}
