/*
    Copyright 2011-2012, Strategic Gains, Inc.

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

import com.strategicgains.repoexpress.domain.Identifier;
import com.strategicgains.repoexpress.exception.InvalidObjectIdException;

/**
 * Converts between a String ID and an Integer Identifier.
 * 
 * @author toddf
 * @since Feb 16, 2011
 */
public class StringToIntegerIdAdapter
implements IdentifierAdapter
{
	/**
	 * throws InvalidObjectIdException if the ID is not a valid integer.
	 */
	@Override
	public Identifier parse(String id)
	{
		if (id == null || id.isEmpty()) throw new InvalidObjectIdException("null ID");

		try
		{
			return new Identifier(Integer.valueOf(id));
		}
		catch (NumberFormatException e)
		{
			throw new InvalidObjectIdException(id, e);
		}
	}

	@Override
    public String format(Identifier id)
    {
		return (id == null ? null : id.firstComponent().toString());
    }
}
