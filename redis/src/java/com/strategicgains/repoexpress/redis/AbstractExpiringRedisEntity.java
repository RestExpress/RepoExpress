package com.strategicgains.repoexpress.redis;

import com.strategicgains.repoexpress.domain.ExpiringIdentifiable;

/**
 * This class implements the Expiring interface by using an int TtlSeconds.
 * 
 * @since Aug 24, 2013
 */
public class AbstractExpiringRedisEntity extends AbstractRedisEntity implements ExpiringIdentifiable
{
	private int ttlSeconds;

	@Override
	public int getTtlSeconds()
	{
		return ttlSeconds;
	}

	@Override
	public void setTtlSeconds(int seconds)
	{
		this.ttlSeconds = seconds;
	}
}
