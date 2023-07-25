/*
    Copyright 2015, Strategic Gains, Inc.

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
package com.strategicgains.repoexpress.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

import com.strategicgains.repoexpress.adapter.Identifiers;
import com.strategicgains.repoexpress.domain.Identifier;

/**
 * @author toddf
 * @since Sep 24, 2015
 */
public abstract class Format
{
	private Format()
	{
		// Prevents instantiation
	}

	/**
	 * Format a UUID as a string. Possibly a short UUID (URL-Safe Base64-encoded).
	 * 
	 * @param uuid a UUID
	 * @return a string representation of the UUID.
	 */
	public static String uuid(UUID uuid)
	{
		return UuidConverter.format(uuid);
	}

	/**
	 * Format an Identifier instance as a string, with it's components separated by the default delimiter.
	 * If the Identifier contains UUIDs, it will be shortened based on the UUID shortening settings (e.g. URL-Safe Base64-encoded).
	 * 
	 * @param id an Identifier instance.
	 * @return a string representation of the Identifier components.
	 */
	public static String id(Identifier id)
	{
		return Identifiers.format(id);
	}

	/**
	 * Format an Identifier instance as a string, with it's components separated by the given delimiter.
	 * If the Identifier contains UUIDs, it will be shortened based on the UUID shortening settings (e.g. URL-Safe Base64-encoded).
	 * 
	 * @param id an Identifier instance.
	 * @param separator a String to use in delimiting the Identifier components.
	 * @return a string representation of the Identifier components.
	 */
	public static String id(Identifier id, String separator)
	{
		return Identifiers.format(id, separator);
	}

	/**
	 * Format an Identifier instance containing an Integer as a String.
	 * 
	 * @param id an Identifier instance containing an Integer.
	 * @return a string representation of the Integer.
	 */
	public static String intId(Identifier id)
	{
		return Identifiers.INTEGER.format(id);
	}

	/**
	 * Format an Identifier instance containing a Long as a String.
	 * 
	 * @param id an Identifier instance containing a Long.
	 * @return a string representation of the Long.
	 */
	public static String longId(Identifier id)
	{
		return Identifiers.LONG.format(id);
	}

	/**
	 * URL encode the string, returning the encoded value in UTF-8 encoding.
	 * 
	 * @param value a String to be URL encoded.
	 * @return the URL-encoded value in UTF-8 encoding.
	 */
	public static String urlEncode(String value)
	{
		try
        {
	        return URLEncoder.encode(value, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
        	return value;
        }
	}

	/**
	 * Support shortened (URL-Safe Base64-encoded) UUIDs when formatting for output.
	 * This is the same as calling {@link Identifiers.useShortUUID()}.
	 */
	public static void useShortUUID()
	{
		Identifiers.useShortUUID();
	}
}
