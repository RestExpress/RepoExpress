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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.strategicgains.repoexpress.domain.Identifiable;
import com.strategicgains.repoexpress.domain.Identifier;
import com.strategicgains.repoexpress.exception.InvalidObjectIdException;
import com.strategicgains.repoexpress.exception.ItemNotFoundException;
import com.strategicgains.repoexpress.exception.RepositoryException;


/**
 * @author toddf
 * @since Oct 13, 2010
 */
public abstract class AbstractRepository<T extends Identifiable>
implements Repository<T>
{
	/**
	 * Simply reads the object associated with the given ID before calling
	 * delete(object).  This ensures existence before calling delete.
	 * 
	 * @throws ItemNotFoundException if the given ID doesn't exist.
	 * @throws InvalidObjectIdException if the give ID is invalid (for the repository).
	 */
	@Override
	public void delete(Identifier id)
	{
		T object = read(id);
		delete(object);
	}

	/**
	 * Read all the items in a given collection of IDs.  IDs in the collection
	 * that are invalid or not found are simply ignored.
	 * <p/>
	 * This default implementation simply calls read(id) for each of the given IDs.
	 * Sub-classes should choose to optimize by overriding this behavior.
	 * 
	 * @return a list of objects associated with the provided IDs. Possibly empty
	 * if none of the IDs are found (or valid). Never null.
	 */
	@Override
	public List<T> readList(Collection<Identifier> ids)
    {
    	List<T> results = new ArrayList<T>(ids.size());
    	
    	for (Identifier id : ids)
    	{
    		try
    		{
    			results.add(read(id));
    		}
    		catch (RepositoryException e)
    		{
    			// ignore it, returning an empty list if necessary.
    		}
    	}
    	
    	return results;
    }

	/**
	 * Returns true if the id already exists in the repository. Otherwise, false.
	 * Also returns false if an ItemNotFoundException occurs.
	 * 
	 * <p/>
	 * This default implementation incurs a read, check for null.  Sub-classes
	 * should optimize this, if applicable, by overriding the behavior.
	 */
	@Override
	public boolean exists(Identifier id)
	{
		try
		{
			return (read(id) != null);
		}
		catch (ItemNotFoundException e)
		{
			return false;
		}
	}


	// SECTION: UTILITY - PROTECTED

	protected boolean hasId(T item)
	{
		return (item.getIdentifier() != null && !item.getIdentifier().isEmpty());
	}
}
