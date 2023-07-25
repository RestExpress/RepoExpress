/*
    Copyright 2012, Strategic Gains, Inc.

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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.strategicgains.repoexpress.domain.Identifier;
import com.strategicgains.repoexpress.exception.InvalidObjectIdException;

/**
 * @author toddf
 * @since Oct 25, 2011
 */
public class StringToIntegerIdAdapterTest
{
	@Test(expected=InvalidObjectIdException.class)
	public void shouldThrowExceptionOnInvalidId()
	{
		Identifiers.INTEGER.parse("invalid");
	}

	@Test(expected=InvalidObjectIdException.class)
	public void shouldHandleNull()
	{
		Identifiers.INTEGER.parse(null);
	}

	@Test
	public void shouldParseIdentifier()
	{
		String stringValue = "655";
		Identifier objectId = Identifiers.INTEGER.parse(stringValue);
		Identifier expected = new Identifier(Integer.valueOf(stringValue));
		assertEquals(expected, objectId);
		assertEquals(stringValue, Identifiers.INTEGER.format(objectId));
	}
}
