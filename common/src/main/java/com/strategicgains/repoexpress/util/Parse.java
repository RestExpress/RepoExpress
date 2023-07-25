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

import java.util.UUID;

import com.strategicgains.repoexpress.adapter.Identifiers;
import com.strategicgains.repoexpress.domain.Identifier;
import com.strategicgains.repoexpress.exception.InvalidObjectIdException;

/**
 * Foreign static methods to parse various formats of strings into Identifier instances.
 * 
 * @author toddf
 * @since Sep 24, 2015
 */
public abstract class Parse
{
	/**
	 * Parse a string assuming it's a UUID (Possibly a short UUID: URL-Safe Base64-encoded)
	 * into a UUID instance.
	 * 
	 * @param uuidString a string representation of a UUID.
	 * @return a UUID, if parsing succeeds.
	 * @throws InvalidObjectIdException if parse fails.
	 */
	public static UUID uuid(String uuidString)
	{
		try
		{
			return UuidConverter.parse(uuidString);
		}
		catch(IllegalArgumentException e)
		{
			throw new InvalidObjectIdException(e);
		}
	}

	/**
	 * Parse a string assuming it's a UUID (Possibly a short UUID: URL-Safe Base64-encoded)
	 * into an Identifier instance.
	 * 
	 * @param uuidString a string representation of a UUID.
	 * @return an Identifier instance containing a UUID, if parsing succeeds.
	 * @throws InvalidObjectIdException if parse fails.
	 */
	public static Identifier id(String uuidString)
	{
		return Identifiers.UUID.parse(uuidString);
	}

	/**
	 * Parse a string assuming it's an Integer into an Identifier instance.
	 * 
	 * @param integerId a string representation of an integer.
	 * @return an Identifier instance containing an Integer, if parsing succeeds.
	 * @throws InvalidObjectIdException if parse fails.
	 */	
	public static Identifier intId(String integerId)
	{
		return Identifiers.INTEGER.parse(integerId);
	}

	/**
	 * Parse a string assuming it's a Long into an Identifier instance.
	 * 
	 * @param longId a string representation of a long.
	 * @return an Identifier instance containing a Long, if parsing succeeds.
	 * @throws InvalidObjectIdException if parse fails.
	 */	
	public static Identifier longId(String longId)
	{
		return Identifiers.LONG.parse(longId);
	}
}
