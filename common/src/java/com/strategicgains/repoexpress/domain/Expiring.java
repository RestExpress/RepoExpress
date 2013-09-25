package com.strategicgains.repoexpress.domain;

/**
 * @since Aug 24, 2013
 */
public interface Expiring
{
	public int getTtlSeconds();
	public void setTtlSeconds(int seconds);
}
