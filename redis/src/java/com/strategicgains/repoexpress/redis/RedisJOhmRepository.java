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

import com.strategicgains.noschema.Identifiable;
import com.strategicgains.noschema.Identifier;
import com.strategicgains.repoexpress.AbstractObservableRepository;
import com.strategicgains.repoexpress.exception.DuplicateItemException;
import com.strategicgains.repoexpress.exception.ItemNotFoundException;

import redis.clients.johm.JOhm;

/**
 * Persist objects (mainly sub-classes of AbstractRedisJOhmEntity) to a Redis datastore using JOhm.
 * The Object must implement Identifiable and the ID must be numeric (e.g. Long, Integer).
 * 
 * @author toddf
 * @since Jun 6, 2012
 * @see AbstractRedisJOhmEntity
 */
public class RedisJOhmRepository<T extends Identifiable>
extends AbstractObservableRepository<T>
{
	private Class<T> entityClass;

	public RedisJOhmRepository(Class<T> entityClass)
	{
		super();
		this.entityClass = entityClass;
	}

	@Override
	public T doCreate(T object, boolean ifUnique)
	{
		if (ifUnique && !JOhm.isNew(object))
		{
			throw new DuplicateItemException(object.getClass().getSimpleName()
				+ " ID already exists: " + object.getIdentifier());
		}
		
		return JOhm.save(object);
	}

	@Override
	public void doDelete(T object)
	{
		JOhm.delete(entityClass, (Long) object.getIdentifier().firstComponent());
	}

	@Override
	public T doRead(Identifier id)
	{
		return JOhm.get(entityClass, (Long) id.firstComponent());
	}

	@Override
	public T doUpdate(T object, boolean ifExists)
	{
		if (ifExists && JOhm.isNew(object))
		{
			throw new ItemNotFoundException(object.getClass().getSimpleName()
			    + " ID not found: " + object.getIdentifier());
		}

		return JOhm.save(object);
	}

    @Override
    public boolean exists(Identifier id)
    {
    	return (JOhm.get(entityClass, (Long) id.firstComponent()) != null);
    }
}
