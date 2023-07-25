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
package com.strategicgains.repoexpress;

import com.strategicgains.repoexpress.domain.Identifiable;
import com.strategicgains.repoexpress.domain.Identifier;
import com.strategicgains.repoexpress.event.Observable;

/**
 * @author toddf
 * @since Oct 13, 2009
 */
public interface ObservableRepository<T extends Identifiable>
extends Repository<T>, Observable<T>
{
	public T doCreate(T object, boolean ifUnique);
	public void doDelete(T object);
	public T doRead(Identifier id);
	public T doUpdate(T object, boolean ifExists);
}
