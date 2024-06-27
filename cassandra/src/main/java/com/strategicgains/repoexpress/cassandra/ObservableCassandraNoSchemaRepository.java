package com.strategicgains.repoexpress.cassandra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.datastax.oss.driver.api.core.CqlSession;
import com.strategicgains.noschema.Identifiable;
import com.strategicgains.noschema.Identifier;
import com.strategicgains.noschema.cassandra.CassandraRepository;
import com.strategicgains.noschema.cassandra.PrimaryTable;
import com.strategicgains.noschema.document.ObjectCodec;
import com.strategicgains.repoexpress.event.Observable;
import com.strategicgains.repoexpress.event.RepositoryObserver;
import com.strategicgains.repoexpress.exception.DuplicateItemException;
import com.strategicgains.repoexpress.exception.InvalidObjectIdException;
import com.strategicgains.repoexpress.exception.ItemNotFoundException;
import com.strategicgains.repoexpress.exception.RepositoryException;

public abstract class ObservableCassandraNoSchemaRepository<T extends Identifiable>
extends CassandraRepository<T>
implements Observable<T>
{
	private List<RepositoryObserver<T>> observers = new ArrayList<>();

	protected ObservableCassandraNoSchemaRepository(CqlSession session, PrimaryTable table, ObjectCodec<T> codec)
	{
		super(session, table, codec);
	}

	public Observable<T> addObserver(RepositoryObserver<T> observer)
	{
		observers.add(observer);
		return this;
	}
	
	/**
	 * Remove all observers from this repository.
	 */
	public void clearObservers()
	{
		observers.clear();
	}
	
	/**
	 * Returns the observers for this AbstractRepository as an unmodifiable list.
	 * 
	 * @return the repository's observers.
	 */
	public List<RepositoryObserver<T>> getObservers()
	{
		return Collections.unmodifiableList(observers);
	}
	
	public boolean removeObserver(RepositoryObserver<T> observer)
	{
		return observers.remove(observer);
	}

	@Override
	public T create(T entity)
	{
		try
		{
			observers.stream().forEach(o -> o.beforeCreate(entity));
			T created = super.create(entity);
			observers.stream().forEach(o -> o.afterCreate(entity));
			return created;
		}
		catch (Exception e)
		{
			mapException(e);
		}

		return null; // won't get here.
	}

	@Override
	public void delete(Identifier id)
	{
		try
		{
			T entity = read(id);
			observers.stream().forEach(o -> o.beforeDelete(entity));
			super.delete(id);
			observers.stream().forEach(o -> o.afterDelete(entity));
		}
		catch (Exception e)
		{
			mapException(e);
		}
	}

	@Override
	public T update(T entity, T original)
	{
		try
		{
			observers.stream().forEach(o -> o.beforeUpdate(original));
			T updated = super.update(entity, original);
			observers.stream().forEach(o -> o.afterUpdate(updated));
			return updated;
		}
		catch (Exception e)
		{
			mapException(e);
		}

		return null; // won't get here.
	}

	@Override
	public T upsert(T entity)
	{
		try
		{
			observers.stream().forEach(o -> o.beforeUpdate(entity));
			T updated = super.upsert(entity);
			observers.stream().forEach(o -> o.afterUpdate(updated));
			return updated;
		}
		catch (Exception e)
		{
			mapException(e);
		}

		return null; // won't get here.
	}

	private void mapException(Exception e)
    {
		if (e instanceof com.strategicgains.noschema.exception.DuplicateItemException)
		{
			throw new DuplicateItemException(e.getMessage());
		}
		else if (e instanceof com.strategicgains.noschema.exception.ItemNotFoundException)
		{
			throw new ItemNotFoundException(e.getMessage());
		}
		else if (e instanceof com.strategicgains.noschema.exception.InvalidObjectIdException)
		{
			throw new InvalidObjectIdException(e.getMessage());
		}
		else if (e instanceof com.strategicgains.noschema.exception.StorageException)
		{
			throw new RepositoryException(e.getMessage());
		}
    }
}
