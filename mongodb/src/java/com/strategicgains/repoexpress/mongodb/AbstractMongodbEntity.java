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

import org.bson.types.ObjectId;
import dev.morphia.annotations.Id;

import com.strategicgains.repoexpress.domain.AbstractTimestampedEntity;
import com.strategicgains.repoexpress.domain.Identifier;

/**
 * An Entity object that is identified by a MongoDB ObjectId. It also has
 * createdAt and updatedAt properties.
 * <p/>
 * Note that, while this ObjectId only occupies three (3) bytes of storage and a
 * UUID occupies four (4), it is arguably more readable and universally usable
 * to have a base64-encoded, 22-character UUID in a URL, than a proprietary
 * MongoDB ID exposed on a URL.
 * 
 * @author toddf
 * @since Oct 27, 2011
 * @see MongodbUuidEntityRepository
 */
public abstract class AbstractMongodbEntity
extends AbstractTimestampedEntity<ObjectId>
{
	@Id
	private ObjectId id;

	@Override
	public Identifier getIdentifier()
	{
		return (id == null ? null : new Identifier(id));
	}

	@Override
	public void setId(ObjectId id)
	{
		this.id = (id == null ? null : id);
	}

	@Override
	public boolean hasId()
	{
		return (id != null);
	}

	@Override
	public ObjectId getId()
	{
		return id;
	}
}
