package com.strategicgains.repoexpress.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.strategicgains.noschema.Identifier;

public class IdentifierTest
{
	@Test
	public void shouldBeEqual()
	{
		assertTrue(new Identifier("key1", "key2", "key3").equals(new Identifier("key1", "key2", "key3")));
		assertTrue(new Identifier("key1").equals(new Identifier("key1")));
	}

	@Test
	public void shouldBeNotEqual()
	{
		assertFalse(new Identifier("key1", "key2", "key2").equals(new Identifier("key1", "key2", "key3")));
		assertFalse(new Identifier("key0").equals(new Identifier("key1")));
	}

	@Test
	public void shouldCompareEqual()
	{
		assertEquals(0, new Identifier("key1", "key2", "key3").compareTo(new Identifier("key1", "key2", "key3")));
		assertEquals(0, new Identifier("key1").compareTo(new Identifier("key1")));
	}

	@Test
	public void shouldCompareLessThan()
	{
		assertEquals(-1, new Identifier("key1", "key2", "key2").compareTo(new Identifier("key1", "key2", "key3")));
		assertEquals(-1, new Identifier("key0").compareTo(new Identifier("key1")));
	}

	@Test
	public void shouldCompareGreaterThan()
	{
		assertEquals(1, new Identifier("key1", "key2", "key4").compareTo(new Identifier("key1", "key2", "key3")));
		assertEquals(1, new Identifier("key2").compareTo(new Identifier("key1")));
	}
}
