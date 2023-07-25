/*
 * Copyright 2010, Strategic Gains, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.strategicgains.repoexpress;

import java.util.Collection;
import java.util.List;

import com.strategicgains.repoexpress.domain.Identifiable;
import com.strategicgains.repoexpress.domain.Identifier;

/**
 * @author toddf
 * @since Jul 12, 2010
 */
public interface Repository<T extends Identifiable>
{
	/**
	 * Stores the object if it doesn't already exist. Calls exists() before writing.
	 * Thus, this method incurs a read before write expense.
	 * 
	 * @param object the entity to store.
	 * @return the new entity with its ID set.
	 * @see {@link exists()}
	 */
	public T create(T object);

	/**
	 * Stores the object, optionally checking uniqueness via exists() before writing.
	 * If ifUnique is false then the object is simply written without checking for existence.
	 * 
	 * @param object the entity to store.
	 * @param ifUnique if true, exists() is called before writing the entity. Otherwise, the entity is simply written.
	 * @return the new entity with its ID set.
	 */
	public T create(T object, boolean ifUnique);

	/**
	 * Removes an entity by its identifier.
	 * 
	 * @param id the identifier to use when removing the entity.
	 */
	public void delete(Identifier id);

	/**
	 * Removes an entity.
	 * 
	 * @param object
	 */
	public void delete(T object);
	public boolean exists(Identifier id);
	public T read(Identifier id);
	public List<T> readList(Collection<Identifier> ids);
	public T update(T object);
	public T update(T object, boolean ifExists);
}
