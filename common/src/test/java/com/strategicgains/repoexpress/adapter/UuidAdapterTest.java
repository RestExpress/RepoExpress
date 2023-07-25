package com.strategicgains.repoexpress.adapter;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Test;

import com.strategicgains.repoexpress.domain.Identifier;
import com.strategicgains.repoexpress.exception.InvalidObjectIdException;

public class UuidAdapterTest
{
	private String uuidString = "00993542-ba2f-4d9f-82bf-0000cd938f95";
	private String uuidEncoded = "AJk1QrovTZ-CvwAAzZOPlQ";
	private UUID uuid = UUID.fromString(uuidString);
	private Identifier uuidId = new Identifier(uuid);

	@Test
	public void shouldParseStringToUuid()
	{
		assertEquals(uuidId, Identifiers.UUID.parse(uuidString));
	}

	@Test(expected=InvalidObjectIdException.class)
	public void shouldThrowInvalidObjectIdException()
	{
		assertEquals(uuidId, Identifiers.UUID.parse("abcde"));
	}

	@Test
	public void shouldParseShortStringToUuid()
	{
		assertEquals(uuidId, Identifiers.UUID.parse(uuidEncoded));
	}

	@Test
	public void shouldFormatLongUuidFromIdentifier()
	{
		assertEquals(uuidString, Identifiers.UUID.format(uuidId));
	}

	@Test
	public void shouldFormatLongUuid()
	{
		assertEquals(uuidString, Identifiers.UUID.format(uuid));
	}

	@Test
	public void shouldFormatShortUuidFromIdentifier()
	{
		assertEquals(uuidEncoded, Identifiers.UUID.format(uuidId, true));
	}

	@Test
	public void shouldFormatShortUuid()
	{
		assertEquals(uuidEncoded, Identifiers.UUID.format(uuid, true));
	}

	@Test
	public void shouldDefaultToShortUuidFromIdentifier()
	{
		Identifiers.UUID.useShortUUID(true);
		assertEquals(uuidEncoded, Identifiers.UUID.format(uuidId));
		Identifiers.UUID.useShortUUID(false);
	}

	@Test
	public void shouldDefaultToShortUuid()
	{
		Identifiers.UUID.useShortUUID(true);
		assertEquals(uuidEncoded, Identifiers.UUID.format(uuid));
		Identifiers.UUID.useShortUUID(false);
	}
}
