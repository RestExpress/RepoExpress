/*
    Copyright 2018, Strategic Gains, Inc.

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
 * Represents an object that has an object ID.
 * 
 * @author toddf
 * @since Mar 2, 2018
 * @param <T> the type of the 'id' property.
 */
public interface Entity<T>
extends Identifiable
{
	public T getId();
	public boolean hasId();
	public void setId(T oid);
}
