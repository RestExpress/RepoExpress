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
package com.strategicgains.repoexpress.adapter;

import java.util.UUID;

import com.strategicgains.repoexpress.domain.Identifier;
import com.strategicgains.repoexpress.exception.InvalidObjectIdException;
import com.strategicgains.repoexpress.util.UuidConverter;

/**
 * Adapts (converts) between a UUID Type 3 format (or URL-safe Base64 encoded Type 3 format) and
 * an Identifier instance. Also, does the same for converting UUIDs to String formats.
 * <p/>
 * Can produce UUID string representations that are URL-safe base64 encoded, which are as short as
 * 22 characters.
 * <p/>
 * By default format(UUID) produces the usual Type 3 output format with four segments, as does format(UUID, false).
 * However, format(UUID, true) produces the URL-safe Base64 encoded format.
 * <p/>
 * The default format for this instance of UuidAdapter can be changed by calling useShortUUID(true). After
 * which, all subsequent calls to format(UUID) will produce the short UUID format.
 * 
 * @author toddf
 * @since Mar 11, 2013
 */
public class UuidAdapter
implements IdentifierAdapter
{
	private boolean shouldShorten = false;

	/**
	 * The default format for this instance of UuidAdapter can be changed by calling useShortUUID(true). After
	 * which, all subsequent calls to format(UUID) will produce the short UUID format.
	 * 
	 * @param value
	 */
	public void useShortUUID(boolean value)
	{
		this.shouldShorten = value;
	}

	@Override
    public Identifier parse(String id)
    throws InvalidObjectIdException
    {
		if (id == null || id.isEmpty()) throw new InvalidObjectIdException("Identifier must not be null");

		try
		{
			return new Identifier(UuidConverter.parse(id));
		}
		catch(IllegalArgumentException e)
		{
			throw new InvalidObjectIdException(e);
		}
    }

	/**
	 * Convert the Identifier into a string representation.
	 * 
	 * @param id an Identifier.
	 */
	@Override
    public String format(Identifier id)
    {
		return format(id, shouldShorten);
    }

	/**
	 * Format the Identifier as a string representation of a UUID, optionally shortening it
	 * via URL-safe Base64 encoding.
	 * 
	 * @param id an Identifier containing a UUID.
	 * @param shorten if true, URL-safe Base64 encode the UUID string.
	 * @return a String or null (if the Identifier is null).
	 */
    public String format(Identifier id, boolean shorten)
    {
    	if (id == null) return null;

    	return format((UUID) id.firstComponent(), shorten);
    }

    /**
     * Convert the UUID into a string representation, using the current
     * setting of useShortUUID(boolean) to determine whether to produce
     * a full Type 3 string representation or the short URL-safe Base64
     * encoded format.
     * 
     * @param uuid the UUID to format.
     * @return a String or null (if the UUID is null).
     */
	public String format(UUID uuid)
	{
		return format(uuid, shouldShorten);
	}

	/**
	 * Format the UUID as a string representation of a UUID, optionally shortening it
	 * via URL-safe Base64 encoding.
	 * 
	 * @param uuid a UUID.
	 * @param shorten if true, URL-safe Base64 encode the UUID string.
	 * @return a String or null (if the UUID is null).
	 */
	public String format(UUID uuid, boolean shorten)
    {
    	if (shorten)
		{
    		return (uuid == null ? null : UuidConverter.format(uuid));
		}
    	else
    	{
    		return (uuid == null ? null : uuid.toString());
    	}
    }
}
