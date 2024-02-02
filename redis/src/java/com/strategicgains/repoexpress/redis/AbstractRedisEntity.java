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
package com.strategicgains.repoexpress.redis;

import com.strategicgains.noschema.AbstractEntity;
import com.strategicgains.noschema.Identifiable;
import com.strategicgains.noschema.Identifier;

/**
 * This super-class implements the Identifiable interface by using a String ID.
 * 
 * @author toddf
 * @since Jul 19, 2012
 */
public class AbstractRedisEntity
extends AbstractEntity<String>
implements Identifiable
{
	private String id;

	@Override
	public Identifier getIdentifier()
	{
		return (id == null ? new Identifier() : new Identifier(id));
	}
}
