/*
    Copyright 2017-2018, Strategic Gains, Inc.

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

/**
 * An entity (has an Identifer) as well as a single Object ID (id property).
 * It also has createdAt and updatedAt properties.
 * 
 * @author toddf
 * @since Oct 6, 2017
 */
public abstract class AbstractEntity<T>
implements Entity<T>
{
	/**
	 * The object ID for this Entity.
	 */
	private T id;

	@Override
	public Identifier getIdentifier()
	{
		return (id == null ? null : new Identifier(id));
	}

	/**
	 * Answer the object ID for this Entity.
	 * 
	 * @return the entity's object ID.
	 */
    public T getId()
    {
	    return id;
    }

    /**
     * Set the object ID for this Entity.
     * 
     * @param objectId the new object ID.
     */
    public void setId(T objectId)
    {
		this.id = objectId;
    }
}
