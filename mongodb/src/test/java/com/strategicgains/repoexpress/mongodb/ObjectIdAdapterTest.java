/*
    Copyright 2011, Strategic Gains, Inc.

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
package com.strategicgains.repoexpress.mongodb;

import static org.junit.Assert.*;

import org.bson.types.ObjectId;
import org.junit.Test;

import com.strategicgains.repoexpress.domain.Identifier;
import com.strategicgains.repoexpress.exception.InvalidObjectIdException;

/**
 * @author toddf
 * @since Mar 24, 2011
 */
public class ObjectIdAdapterTest
{
	@Test(expected=InvalidObjectIdException.class)
	public void shouldThrowExceptionOnInvalidId()
	{
		Identifiers.MONGOID.parse("invalid");
	}

	@Test(expected=InvalidObjectIdException.class)
	public void shouldThrowExceptionOnNullId()
	{
		Identifiers.MONGOID.parse(null);
	}

	@Test
	public void shouldParseAndFormatObjectId()
	{
		Identifier original = new Identifier(new ObjectId());
		String stringValue = original.toString();
		Identifier identifier = Identifiers.MONGOID.parse(stringValue);
		assertEquals(original, identifier);
		assertEquals(stringValue, Identifiers.MONGOID.format(identifier));
	}
}
