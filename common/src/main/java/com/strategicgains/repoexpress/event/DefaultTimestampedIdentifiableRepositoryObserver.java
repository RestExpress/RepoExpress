/*
    Copyright 2010-2018, Strategic Gains, Inc.

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

import java.util.Date;

import com.strategicgains.repoexpress.domain.TimestampedIdentifiable;

/**
 * Sets the createdAt and updatedAt properties on a TimestampedIdentifiable instance before
 * creation and updatedAt on update.
 * 
 * @author toddf
 * @since Oct 13, 2010
 */
public class DefaultTimestampedIdentifiableRepositoryObserver<T extends TimestampedIdentifiable>
extends AbstractRepositoryObserver<T>
{
	@Override
    public void beforeCreate(T object)
    {
	    super.beforeCreate(object);
	    Date now = new Date(System.currentTimeMillis());
	    object.setCreatedAt(now);
	    object.setUpdatedAt(now);
    }

	@Override
    public void beforeUpdate(T object)
    {
	    super.beforeUpdate(object);
	    object.setUpdatedAt(new Date(System.currentTimeMillis()));
    }
}
