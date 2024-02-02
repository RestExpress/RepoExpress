package com.strategicgains.repoexpress.cassandra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.datastax.oss.driver.api.core.CqlSession;
import com.strategicgains.noschema.Identifiable;
import com.strategicgains.noschema.Identifier;
import com.strategicgains.noschema.cassandra.CassandraNoSchemaRepository;
import com.strategicgains.noschema.cassandra.PrimaryTable;
import com.strategicgains.repoexpress.event.Observable;
import com.strategicgains.repoexpress.event.RepositoryObserver;

public abstract class ObservableCassandraNoSchemaRepository<T extends Identifiable>
extends CassandraNoSchemaRepository<T>
implements Observable<T>
{
	private List<RepositoryObserver<T>> observers = new ArrayList<RepositoryObserver<T>>();

	public ObservableCassandraNoSchemaRepository(CqlSession session, PrimaryTable table)
	{
		super(session, table);
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
		observers.stream().forEach(o -> o.beforeCreate(entity));
		T created = super.create(entity);
		observers.stream().forEach(o -> o.afterCreate(entity));
		return created;
	}

	@Override
	public void delete(Identifier id)
	{
		T entity = read(id);
		observers.stream().forEach(o -> o.beforeDelete(entity));
		super.delete(id);
		observers.stream().forEach(o -> o.afterDelete(entity));
	}

	@Override
	public T update(T entity, T original)
	{
		observers.stream().forEach(o -> o.beforeUpdate(original));
		T updated = super.update(entity, original);
		observers.stream().forEach(o -> o.afterUpdate(updated));
		return updated;
	}

	@Override
	public T upsert(T entity)
	{
		observers.stream().forEach(o -> o.beforeUpdate(entity));
		T updated = super.upsert(entity);
		observers.stream().forEach(o -> o.afterUpdate(updated));
		return updated;
	}
}
