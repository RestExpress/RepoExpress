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
package com.strategicgains.repoexpress;

import java.util.List;

import org.restexpress.common.query.QueryFilter;
import org.restexpress.common.query.QueryOrder;
import org.restexpress.common.query.QueryRange;

import com.strategicgains.repoexpress.domain.Identifiable;

/**
 * Defines the interface for a repository implementation that supports dynamic query
 * capabilities.
 * 
 * @author toddf
 * @since Oct 25, 2012
 */
public interface Queryable<T extends Identifiable>
{
	public long count(QueryFilter filter);
	public List<T> readAll(QueryFilter filter, QueryRange range, QueryOrder order);
}
