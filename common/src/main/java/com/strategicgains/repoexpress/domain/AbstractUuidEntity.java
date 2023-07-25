/*
    Copyright 2013-2018, Strategic Gains, Inc.

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
package com.strategicgains.repoexpress.domain;

import java.util.UUID;

/**
 * An entity that is identified by a UUID as its primary identifier.
 * 
 * @author toddf
 * @since Mar 18, 2013
 */
public abstract class AbstractUuidEntity
extends AbstractEntity<UUID>
{
	private UUID id;

	@Override
	public Identifier getIdentifier()
	{
		return (id == null ? null : new Identifier(id));
	}

	@Override
    public UUID getId()
    {
	    return id;
    }

	@Override
    public void setId(UUID uuid)
    {
		this.id = uuid;
    }
}
