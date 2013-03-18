package com.strategicgains.repoexpress.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.UUID;

import org.junit.Test;

public class UuidConverterTest
{
	private String longUuid = "00993542-ba2f-4d9f-82bf-0000cd938f95";
	private String shortUuid = "AJk1QrovTZ-CvwAAzZOPlQ";
	private UUID uuid = UUID.fromString(longUuid);

	@Test
	public void shouldShortenUuid()
	{
		String shortened = UuidConverter.format(uuid);
		assertEquals(shortUuid, shortened);
	}

	@Test
	public void shouldExpandShortUuid() throws IOException
	{
		UUID expanded = UuidConverter.parse(shortUuid);
		assertEquals(uuid, expanded);
	}

	@Test
	public void shouldExpandLongUuid() throws IOException
	{
		UUID expanded = UuidConverter.parse(longUuid);
		assertEquals(uuid, expanded);
	}

	@Test
	public void shouldHandleThisUuid1()
	{
		String base64 = "b8tRS7h4TJ2Vt43Dp85v2A";
		String name = "6fcb514b-b878-4c9d-95b7-8dc3a7ce6fd8";
		UUID expect = UUID.fromString(name);

		String shortened = UuidConverter.format(expect);
		assertEquals(base64, shortened);

		UUID expanded = UuidConverter.parse(name);
		assertEquals(expect, expanded);
		
		expanded = UuidConverter.parse(base64);
		assertEquals(expect, expanded);
	}

	@Test
	public void shouldHandlePaddedShortForm()
	{
		String base64 = "b8tRS7h4TJ2Vt43Dp85v2A==";
		UUID expect = UUID.fromString("6fcb514b-b878-4c9d-95b7-8dc3a7ce6fd8");
		
		UUID expanded = UuidConverter.parse(base64);
		assertEquals(expect, expanded);
	}
}
