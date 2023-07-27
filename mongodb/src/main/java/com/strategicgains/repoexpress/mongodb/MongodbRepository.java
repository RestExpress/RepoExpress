/*
    Copyright 2010-2012, Strategic Gains, Inc.

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
package com.strategicgains.repoexpress.mongodb;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.restexpress.common.query.QueryFilter;
import org.restexpress.common.query.QueryOrder;
import org.restexpress.common.query.QueryRange;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import com.strategicgains.repoexpress.AbstractObservableRepository;
import com.strategicgains.repoexpress.Queryable;
import com.strategicgains.repoexpress.domain.Identifiable;
import com.strategicgains.repoexpress.domain.Identifier;
import com.strategicgains.repoexpress.exception.DuplicateItemException;
import com.strategicgains.repoexpress.exception.InvalidObjectIdException;
import com.strategicgains.repoexpress.exception.ItemNotFoundException;

import dev.morphia.Datastore;

/**
 * Uses MongoDB as its back-end store to persist Identifiable implementations.
 * This repository can handle "single-table inheritance" by passing all the
 * supported types into the constructor, with the inheritance root listed first.
 * 
 * @author toddf
 * @since Aug 24, 2010
 */
public class MongodbRepository<T extends Identifiable>
extends AbstractObservableRepository<T>
implements Queryable<T>
{
	private static final String ID_PROPERTY = "_id";

	private MongoClient mongo;
	MongoCollection<T> collection;
	private Class<T> inheritanceRoot;

	/**
	 * 
	 * @param mongo a pre-configured Mongo instance.
	 * @param dbName the name of the database (in MongoDB).
	 * @param entityClasses Class(es) managed by this repository. Inheritance root first.
	 */
	@SuppressWarnings("unchecked")
	public MongodbRepository(MongoClient mongo, String dbName, Class<? extends T>... entityClasses)
	{
		super();
		this.mongo = mongo;
		initialize(dbName, entityClasses);
	}

	@SuppressWarnings("unchecked")
	private void initialize(String name, Class<? extends T>... entityClasses)
	{
		MongoDatabase db = mongo.getDatabase(name);
		inheritanceRoot = (Class<T>) entityClasses[0];
		this.collection = db.getCollection(inheritanceRoot.getSimpleName(), inheritanceRoot);

		//TODO: create indexes
//		collection.createIndexes(null);
	}

	@Override
	public T doCreate(T item, boolean ifUnique)
	{
		if (ifUnique && exists(item.getIdentifier()))
		{
			throw new DuplicateItemException(item.getClass().getSimpleName()
			    + " ID already exists: " + item.getIdentifier());
		}

		collection.insertOne(item);
		return item;
	}

	@Override
	public T doRead(Identifier id)
	{
		T item = collection.find(eq(ID_PROPERTY, id.lastComponent())).first();

		if (item == null)
		{
			throw new ItemNotFoundException("ID not found: " + id);
		}

		return item;
	}

	@Override
	public T doUpdate(T item, boolean ifExists)
	{
		Document filterById = new Document("_id", item.getIdentifier());
        FindOneAndReplaceOptions returnDocAfterReplace = new FindOneAndReplaceOptions().returnDocument(ReturnDocument.AFTER);
        T updated = collection.findOneAndReplace(filterById, item, returnDocAfterReplace);

		if (ifExists && !exists(item.getIdentifier()))
		{
			throw new ItemNotFoundException(item.getClass().getSimpleName()
			    + " ID not found: " + item.getIdentifier());
		}

		collection..save(item);
		return item;
	}

	@Override
	public void doDelete(T object)
	{
		try
		{
			datastore.delete(object);
		}
		catch (InvalidObjectIdException e)
		{
			throw new ItemNotFoundException("ID not found: " + object.getIdentifier());
		}
	}

	/**
	 * A general-purpose 'finder' method, useful for implementing alternate-key queries. Since
	 * it does not support ordering and range sub-sets, it's best for creating queries that
	 * return a list size of 1.
	 * <p/>
	 * Essentially, just calls readAll() with null range and order.  So if you need ordering,
	 * call readAll(filter, null, order).
	 * 
	 * @param filter query criteria.
	 * @return
	 */
	public List<T> find(QueryFilter filter)
	{
		return readAll(filter, null, null);
	}

	/**
	 * Implements a 'default' readAll' method that queries for all instances of the inheritance
	 * root class matching the given criteria.
	 * 
	 * This method does not invoke an observer method, so is not observable by default.  Override,
	 * calling super() to get that functionality, or call notifyBeforeXXX() and/or notifyAfterXXX()
	 * methods, if desired.
	 * 
	 * @param filter
	 * @param range
	 * @param order
	 * @return a list of results. Never null.
	 */
	@Override
	public List<T> readAll(QueryFilter filter, QueryRange range, QueryOrder order)
	{
		return query(inheritanceRoot, filter, range, order);
	}

	/**
	 * Read each of the instances corresponding to the given Collection of IDs, returning the 
	 * results as a list.  If an ID in the provided Collection does not exist, it is simply
	 * not included in the returned results.
	 * 
	 * @param ids a Collection of IDs to read.
	 */
	@Override
	public List<T> readList(Collection<Identifier> ids)
	{
		return getDataStore().find(inheritanceRoot).filter(Filters.in(ID_PROPERTY, new PrimaryIdIterable(ids))).iterator().toList();
	}

	/**
	 * Count the instances of the inheritance root (class) that match the given filter criteria.
	 * 
	 * @param filter
	 */
	@Override
	public long count(QueryFilter filter)
	{
		return count(inheritanceRoot, filter);
	}

	/**
	 * Count the instances of the given type matching the given filter criteria.
	 * 
	 * @param type
	 * @param filter
	 */
	public long count(Class<T> type, QueryFilter filter)
	{
		return getBaseQuery(type, filter).count();
	}

	/**
	 * Returns true if the given id exists in the repository.
	 * 
	 * @param id the identifier of the object.
	 */
	@Override
	public boolean exists(Identifier id)
	{
		if (id == null) return false;

		return (datastore.find(inheritanceRoot).filter(Filters.eq(ID_PROPERTY, id.lastComponent())).count() > 0);
	}


	// SECTION: UTILITY

	/**
	 * Get the underlying Morphia Datastore object with which to construct queries against.
	 * 
	 * @return the underlying Morphia Datastore.
	 */
	protected Datastore getDataStore()
	{
		return datastore;
	}

	/**
	 * Return the underlying Mongo instance.
	 * 
	 * @return the underlying Mongo instance.
	 */
	protected MongoClient getMongo()
	{
		return mongo;
	}

	/**
	 * Execute a query against the repository, using QueryFilter, QueryRange and QueryOrder
	 * as criteria against the type.  Returns the results as a List.
	 * 
	 * @param type
	 * @param range
	 * @param filter
	 * @param order
	 */
	protected List<T> query(Class<T> type, QueryFilter filter, QueryRange range, QueryOrder order)
	{
		Query<T> q = getBaseQuery(type, filter);
		FindOptions fo = createFindOptions(range, order);
		return (fo != null ? q.iterator(fo).toList() : q.iterator().toList());
	}

	/**
	 * Create and configure a basic query utilizing just QueryFilter as criteria.
	 * 
	 * @param type
	 * @param filter
	 * @return a Morphia Query instance configured for the QueryFilter criteria.
	 */
	private Query<T> getBaseQuery(Class<T> type, QueryFilter filter)
	{
		Query<T> q = getDataStore().find(type);
		configureQueryFilter(q, filter);
		return q;
	}

	/**
	 * @param range a QueryRange instance.
	 * @return a configured FindOptions if the range is initialized. Or null, if not.
	 */
	protected FindOptions createFindOptions(QueryRange range, QueryOrder order)
	{
		if (range == null && order == null) return null;

		FindOptions fo = new FindOptions();
		configureRange(fo, range);
		configureOrdering(fo, order);
		return fo;
	}

	private void configureRange(FindOptions findOptions, QueryRange range)
	{
		if (range == null) return;

		if (range.isInitialized())
		{
			findOptions
				.skip((int) range.getStart())
				.limit(range.getLimit());
		}
	}

	private void configureOrdering(FindOptions findOptions, QueryOrder order)
	{
		if (order == null) return;

		if (order.isSorted())
		{
			final ArrayList<Sort> sorts = new ArrayList<>();

			order.iterate(component -> {
				if (component.isDescending())
				{
					sorts.add(Sort.descending(component.getFieldName()));
				}
				else
				{
					sorts.add(Sort.ascending(component.getFieldName()));
				}				
			});
			
			findOptions.sort(sorts.toArray(new Sort[0]));
		}
	}

	private void configureQueryFilter(final Query<T> q, QueryFilter filter)
	{
		if (filter == null) return;

		filter.iterate(c -> {
			switch(c.getOperator())
			{
				case CONTAINS:		// String-related
					q.filter(Filters.regex(c.getField(), Pattern.compile((String) c.getValue())));
					break;
				case STARTS_WITH:	// String-related
					q.filter(Filters.regex(c.getField(), Pattern.compile("^" + (String) c.getValue())));
					break;
				case GREATER_THAN:
					q.filter(Filters.gt(c.getField(), c.getValue()));
					break;
				case GREATER_THAN_OR_EQUAL_TO:
					q.filter(Filters.gte(c.getField(), c.getValue()));
					break;
				case LESS_THAN:
					q.filter(Filters.lt(c.getField(), c.getValue()));
					break;
				case LESS_THAN_OR_EQUAL_TO:
					q.filter(Filters.lte(c.getField(), c.getValue()));
					break;
				case NOT_EQUALS:
					q.filter(Filters.ne(c.getField(), c.getValue()));
					break;
				case IN:
					q.filter(Filters.in(c.getField(), (Iterable<?>) c.getValue()));
					break;
				case EQUALS:
				default:
					q.filter(Filters.eq(c.getField(), c.getValue()));
					break;
			}
		});
	}
}
