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
package com.strategicgains.repoexpress.util;

import java.util.Iterator;

import com.strategicgains.repoexpress.domain.Identifiable;
import com.strategicgains.repoexpress.domain.Identifier;

/**
 * An Iterator implementation iterating over the identifiers of an Iterable (collection) of
 * Identifiable instances.  Thus, it iterates the IDs of the underlying instances--instead
 * of the instances themselves.
 * <p/>
 * This means, that if you have a Collection of Identifiable instances, you can iterate over
 * the IDs of those Identifiable guys.  This is useful when creating queries and such using,
 * say the "in" operator.
 * <p/>
 * For example, this may be used as follows (psuedo-code):<br/>
 * List<BlogEntry> blogEntries = ... // list of blog entries we're deleting.<br/>
 * blogCommentsRepository.delete().where("blogEntryId").isIn(new IdentifierIterator(blogEntries));
 * 
 * @author toddf
 * @since Oct 25, 2012
 */
public class IdentifierIterator
implements Iterator<Identifier>
{
	private Iterator<? extends Identifiable> iterator;

	public IdentifierIterator(Iterable<? extends Identifiable> iterable)
	{
		iterator = iterable.iterator();
	}

	@Override
	public boolean hasNext()
	{
		return iterator.hasNext();
	}

	@Override
	public Identifier next()
	{
		return iterator.next().getIdentifier();
	}

	@Override
	public void remove()
	{
		iterator.remove();
	}
}