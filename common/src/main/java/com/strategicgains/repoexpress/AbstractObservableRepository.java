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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.strategicgains.repoexpress.domain.Identifiable;
import com.strategicgains.repoexpress.domain.Identifier;
import com.strategicgains.repoexpress.event.RepositoryObserver;

/**
 * A base, abstract repository implementation that supports observation.
 * 
 * @author toddf
 * @since Oct 12, 2010
 */
public abstract class AbstractObservableRepository<T extends Identifiable>
extends AbstractRepository<T>
implements ObservableRepository<T>
{
	// SECTION: INSTANCE VARIABLES
	
	private List<RepositoryObserver<T>> observers = new ArrayList<RepositoryObserver<T>>();

	
	// SECTION: CONSTRUCTORS
	
	public AbstractObservableRepository()
	{
		super();
	}


	// SECTION: ACCESSORS/MUTATORS
	
	public void addObserver(RepositoryObserver<T> observer)
	{
		getObserversInternal().add(observer);
	}
	
	/**
	 * Remove all observers from this repository.
	 */
	public void clearObservers()
	{
		getObserversInternal().clear();
	}
	
	private List<RepositoryObserver<T>> getObserversInternal()
	{
		return observers;
	}
	
	/**
	 * Returns the observers for this AbstractRepository as an unmodifiable list.
	 * 
	 * @return the repository's observers.
	 */
	public List<RepositoryObserver<T>> getObservers()
	{
		return Collections.unmodifiableList(getObserversInternal());
	}
	
	public boolean removeObserver(RepositoryObserver<T> observer)
	{
		return getObserversInternal().remove(observer);
	}

	
	// SECTION: REPOSITORY

	@Override
	public final T create(T object)
	{
		return create(object, true);
	}

	@Override
    public final T create(T object, boolean ifUnique)
    {
    	notifyBeforeCreate(object);
    	T created = doCreate(object, ifUnique);
    	notifyAfterCreate(created);
    	return created;
    }

	@Override
    public final void delete(T object)
    {
		notifyBeforeDelete(object);
		doDelete(object);
		notifyAfterDelete(object);
    }

	@Override
    public final T read(Identifier id)
    {
		notifyBeforeRead(id);
		T result = doRead(id);
		notifyAfterRead(result);
	    return result;
    }

	@Override
	public final T update(T object)
	{
		return update(object, true);
	}

	@Override
    public final T update(T object, boolean ifExists)
    {
		notifyBeforeUpdate(object);
		T result = doUpdate(object, ifExists);
		notifyAfterUpdate(object);
		return result;
    }
	
	
	// SECTION: EVENT OBSERVATION
	
	protected void notifyAfterCreate(T object)
	{
		for (RepositoryObserver<T> observer : getObserversInternal())
		{
			observer.afterCreate(object);
		}
	}
	
	protected void notifyAfterDelete(T object)
	{
		for (RepositoryObserver<T> observer : getObserversInternal())
		{
			observer.afterDelete(object);
		}
	}
	
	protected void notifyAfterRead(T object)
	{
		for (RepositoryObserver<T> observer : getObserversInternal())
		{
			observer.afterRead(object);
		}
	}
	
	protected void notifyAfterUpdate(T object)
	{
		for (RepositoryObserver<T> observer : getObserversInternal())
		{
			observer.afterUpdate(object);
		}
	}

	protected void notifyBeforeCreate(T object)
	{
		for (RepositoryObserver<T> observer : getObserversInternal())
		{
			observer.beforeCreate(object);
		}
	}
	
	protected void notifyBeforeDelete(T object)
	{
		for (RepositoryObserver<T> observer : getObserversInternal())
		{
			observer.beforeDelete(object);
		}
	}
	
	protected void notifyBeforeRead(Identifier id)
	{
		for (RepositoryObserver<T> observer : getObserversInternal())
		{
			observer.beforeRead(id);
		}
	}
	
	protected void notifyBeforeUpdate(T object)
	{
		for (RepositoryObserver<T> observer : getObserversInternal())
		{
			observer.beforeUpdate(object);
		}
	}

	
	// SECTION: INNER CLASSES

	/**
	 * An Iterable implementation returning an Iterator that iterates over
	 * a collection of Identifier instances and returns an iterator of only
	 * the primaryKey portion.
	 *  
	 * @author toddf
	 * @since Oct 25, 2012
	 */
	protected class PrimaryIdIterable
	implements Iterable<Object>
	{
		private Iterable<Identifier> iterable;

		public PrimaryIdIterable(Iterable<Identifier> iterable)
		{
			this.iterable = iterable;
		}

        @Override
        public Iterator<Object> iterator()
        {
	        return new PrimaryIdIterator(iterable.iterator());
        }
	}
	
	/**
	 * Takes an Iterator of Identifier instances and iterates the primaryKey
	 * portion.
	 * 
	 * @author toddf
	 * @since Oct 25, 2012
	 */
	protected class PrimaryIdIterator
	implements Iterator<Object>
	{
		private Iterator<Identifier> iterator;

		public PrimaryIdIterator(Iterator<Identifier> iterator)
		{
			this.iterator = iterator;
		}

        @Override
        public boolean hasNext()
        {
	        return iterator.hasNext();
        }

        @Override
        public Object next()
        {
	        return iterator.next().firstComponent();
        }

        @Override
        public void remove()
        {
        	iterator.remove();
        }
	}
}
