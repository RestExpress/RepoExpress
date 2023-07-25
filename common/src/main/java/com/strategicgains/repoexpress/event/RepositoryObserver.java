/*
    Copyright 2010, Strategic Gains, Inc.

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
package com.strategicgains.repoexpress.event;

import com.strategicgains.repoexpress.domain.Identifiable;
import com.strategicgains.repoexpress.domain.Identifier;

/**
 * @author toddf
 * @since Oct 13, 2009
 */
public interface RepositoryObserver<T extends Identifiable>
{
	public void afterCreate(T object);
	public void afterDelete(T object);
	public void afterRead(T object);
	public void afterUpdate(T object);

	public void beforeCreate(T object);
	public void beforeDelete(T object);
	public void beforeRead(Identifier id);
	public void beforeUpdate(T object);
}
