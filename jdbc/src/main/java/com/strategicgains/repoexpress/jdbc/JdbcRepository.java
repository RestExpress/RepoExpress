/*
    Copyright 2026, Strategic Gains, Inc.

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
package com.strategicgains.repoexpress.jdbc;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.SelectQuery;
import org.jooq.SortField;
import org.jooq.UpdateQuery;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.restexpress.common.query.OrderCallback;
import org.restexpress.common.query.OrderComponent;
import org.restexpress.common.query.QueryFilter;
import org.restexpress.common.query.QueryOrder;
import org.restexpress.common.query.QueryRange;

import com.strategicgains.noschema.Identifiable;
import com.strategicgains.noschema.Identifier;
import com.strategicgains.repoexpress.AbstractObservableRepository;
import com.strategicgains.repoexpress.Queryable;
import com.strategicgains.repoexpress.exception.DuplicateItemException;
import com.strategicgains.repoexpress.exception.InvalidObjectIdException;
import com.strategicgains.repoexpress.exception.ItemNotFoundException;
import com.strategicgains.repoexpress.exception.RepositoryException;

/**
 * A jOOQ-backed JDBC repository implementation for PostgreSQL/MySQL-style relational databases.
 * This initial iteration expects the entity identifier to be assigned before create().
 *
 * @author toddf
 * @since Feb 26, 2026
 */
public class JdbcRepository<T extends Identifiable>
extends AbstractObservableRepository<T>
implements Queryable<T>
{
	private final DSLContext dsl;
	private final JdbcEntityDefinition<T> definition;

	public JdbcRepository(DSLContext dsl, JdbcEntityDefinition<T> definition)
	{
		super();

		if (dsl == null) throw new IllegalArgumentException("DSLContext is required");
		if (definition == null) throw new IllegalArgumentException("JdbcEntityDefinition is required");

		this.dsl = dsl;
		this.definition = definition;
	}

	public JdbcRepository(DSLContext dsl, Class<T> entityClass)
	{
		this(dsl, JdbcEntityDefinitionFactory.from(entityClass));
	}

	public JdbcRepository(JdbcConfig config, Class<T> entityClass)
	{
		this(config.createDslContext(), entityClass);
	}

	public JdbcRepository(JdbcConfig config, SQLDialect dialect, Class<T> entityClass)
	{
		this(config.createDslContext(dialect), entityClass);
	}

	@SafeVarargs
	public JdbcRepository(DSLContext dsl, Class<? extends T>... entityClasses)
	{
		this(dsl, resolveDefinition(entityClasses));
		validateSupportedEntityClasses(entityClasses);
	}

	@SafeVarargs
	public JdbcRepository(JdbcConfig config, Class<? extends T>... entityClasses)
	{
		this(config.createDslContext(), entityClasses);
	}

	@SafeVarargs
	public JdbcRepository(JdbcConfig config, SQLDialect dialect, Class<? extends T>... entityClasses)
	{
		this(config.createDslContext(dialect), entityClasses);
	}

	@Override
	public T doCreate(T entity, boolean ifUnique)
	{
		requireIdentifier(entity);

		if (ifUnique && exists(entity.getIdentifier()))
		{
			throw duplicate(entity);
		}

		try
		{
			insertEntity(entity);
			return entity;
		}
		catch (DataAccessException e)
		{
			if (JdbcExceptionTranslator.isDuplicateKey(e))
			{
				throw JdbcExceptionTranslator.toDuplicateItemException(duplicate(entity).getMessage(), e);
			}

			throw JdbcExceptionTranslator.toRepositoryException("Unable to create " + entity.getClass().getSimpleName(), e);
		}
	}

	@Override
	public T doRead(Identifier id)
	{
		try
		{
			Record found = selectOneById(id);

			if (found == null)
			{
				throw new ItemNotFoundException("ID not found: " + id.toString());
			}

			return definition.fromRecord(found);
		}
		catch (DataAccessException e)
		{
			throw JdbcExceptionTranslator.toRepositoryException("Unable to read item by id", e);
		}
	}

	@Override
	public T doUpdate(T entity, boolean ifExists)
	{
		requireIdentifier(entity);

		try
		{
			int rows = updateEntity(entity);

			if (rows == 0)
			{
				if (ifExists)
				{
					throw new ItemNotFoundException(entity.getClass().getSimpleName()
						+ " ID not found: " + entity.getIdentifier().toString());
				}

				insertEntity(entity);
				return entity;
			}

			return entity;
		}
		catch (DataAccessException e)
		{
			if (JdbcExceptionTranslator.isDuplicateKey(e))
			{
				throw JdbcExceptionTranslator.toDuplicateItemException("Duplicate key on update: " + entity.getIdentifier(), e);
			}

			throw JdbcExceptionTranslator.toRepositoryException("Unable to update " + entity.getClass().getSimpleName(), e);
		}
	}

	@Override
	public void doDelete(T entity)
	{
		requireIdentifier(entity);

		try
		{
			int rows = dsl.deleteFrom(definition.table())
				.where(idCondition(entity.getIdentifier()))
				.execute();

			if (rows == 0)
			{
				throw new ItemNotFoundException("ID not found: " + entity.getIdentifier().toString());
			}
		}
		catch (DataAccessException e)
		{
			throw JdbcExceptionTranslator.toRepositoryException("Unable to delete " + entity.getClass().getSimpleName(), e);
		}
	}

	@Override
	public boolean exists(Identifier id)
	{
		if (id == null) return false;

		try
		{
			return dsl.fetchExists(
				dsl.selectOne()
					.from(definition.table())
					.where(idCondition(id))
			);
		}
		catch (DataAccessException e)
		{
			throw JdbcExceptionTranslator.toRepositoryException("Unable to determine if item exists", e);
		}
	}

	@Override
	public long count(QueryFilter filter)
	{
		try
		{
			SelectQuery<Record> q = dsl.selectQuery();
			q.addFrom(definition.table());
			q.addConditions(filterCondition(filter));
			return dsl.fetchCount(q);
		}
		catch (DataAccessException e)
		{
			throw JdbcExceptionTranslator.toRepositoryException("Unable to count items", e);
		}
	}

	@Override
	public List<T> readAll(QueryFilter filter, QueryRange range, QueryOrder order)
	{
		try
		{
			SelectQuery<Record> q = dsl.selectQuery();
			q.addFrom(definition.table());
			q.addConditions(filterCondition(filter));
			applyOrder(q, order);
			applyRange(q, range);

			List<T> results = new ArrayList<>();

			for (Record record : q.fetch())
			{
				results.add(definition.fromRecord(record));
			}

			return results;
		}
		catch (DataAccessException e)
		{
			throw JdbcExceptionTranslator.toRepositoryException("Unable to query items", e);
		}
	}

	protected DSLContext getDsl()
	{
		return dsl;
	}

	protected JdbcEntityDefinition<T> getDefinition()
	{
		return definition;
	}

	@SuppressWarnings("unchecked")
	private static <T extends Identifiable> JdbcEntityDefinition<T> resolveDefinition(Class<? extends T>[] entityClasses)
	{
		Class<? extends T> root = requireRootEntityClass(entityClasses);
		return (JdbcEntityDefinition<T>) JdbcEntityDefinitionFactory.from((Class<T>) root);
	}

	private static <T extends Identifiable> Class<? extends T> requireRootEntityClass(Class<? extends T>[] entityClasses)
	{
		if (entityClasses == null || entityClasses.length < 1 || entityClasses[0] == null)
		{
			throw new IllegalArgumentException("At least one entity class is required");
		}

		return entityClasses[0];
	}

	private static <T extends Identifiable> void validateSupportedEntityClasses(Class<? extends T>[] entityClasses)
	{
		if (entityClasses == null || entityClasses.length <= 1) return;

		// Placeholder for future polymorphic/discriminator support. For now, avoid silently
		// accepting subtypes and returning the wrong concrete type from fromRecord().
		throw new UnsupportedOperationException("Multiple entity classes are not yet supported by JdbcRepository");
	}

	private void insertEntity(T entity)
	{
		Map<Field<?>, Object> values = definition.insertValues(entity);

		if (values == null || values.isEmpty())
		{
			throw new RepositoryException("insertValues() must return at least one column value");
		}

		org.jooq.InsertQuery<?> query = dsl.insertQuery(definition.table());

		for (Map.Entry<Field<?>, Object> entry : values.entrySet())
		{
			addInsertValue(query, entry.getKey(), entry.getValue());
		}

		query.execute();
	}

	private int updateEntity(T entity)
	{
		Map<Field<?>, Object> values = definition.updateValues(entity);

		if (values == null || values.isEmpty())
		{
			throw new RepositoryException("updateValues() must return at least one column value");
		}

		UpdateQuery<?> query = dsl.updateQuery(definition.table());

		for (Map.Entry<Field<?>, Object> entry : values.entrySet())
		{
			addUpdateValue(query, entry.getKey(), entry.getValue());
		}

		query.addConditions(idCondition(entity.getIdentifier()));
		return query.execute();
	}

	private Record selectOneById(Identifier id)
	{
		SelectQuery<Record> q = dsl.selectQuery();
		q.addFrom(definition.table());
		q.addConditions(idCondition(id));
		q.addLimit(1);
		return q.fetchOne();
	}

	private DuplicateItemException duplicate(T entity)
	{
		return new DuplicateItemException(entity.getClass().getSimpleName()
			+ " ID already exists: " + entity.getIdentifier().toString());
	}

	private void requireIdentifier(T entity)
	{
		if (entity == null)
		{
			throw new IllegalArgumentException("entity is required");
		}

		if (!hasId(entity))
		{
			throw new InvalidObjectIdException("Identifier required for " + entity.getClass().getSimpleName());
		}
	}

	private Condition idCondition(Identifier id)
	{
		if (id == null || id.isEmpty())
		{
			throw new InvalidObjectIdException("Identifier is required");
		}

		List<Field<?>> idFields = definition.idFields();

		if (idFields == null || idFields.isEmpty())
		{
			throw new RepositoryException("idFields() must define at least one primary key field");
		}

		if (id.size() != idFields.size())
		{
			throw new InvalidObjectIdException("Identifier component count does not match primary key field count");
		}

		Condition condition = DSL.trueCondition();
		Iterator<?> components = id.components().iterator();

		for (Field<?> field : idFields)
		{
			condition = condition.and(eq(field, components.next()));
		}

		return condition;
	}

	private Condition filterCondition(QueryFilter filter)
	{
		if (filter == null) return DSL.trueCondition();

		final List<Condition> conditions = new ArrayList<>();

		filter.iterate(c -> {
			Field<?> field = requireField(c.getField());
			Object value = c.getValue();

			switch (c.getOperator())
			{
				case CONTAINS:
					conditions.add(likeContains(field, value));
					break;
				case STARTS_WITH:
					conditions.add(likeStartsWith(field, value));
					break;
				case GREATER_THAN:
					conditions.add(gt(field, value));
					break;
				case GREATER_THAN_OR_EQUAL_TO:
					conditions.add(ge(field, value));
					break;
				case LESS_THAN:
					conditions.add(lt(field, value));
					break;
				case LESS_THAN_OR_EQUAL_TO:
					conditions.add(le(field, value));
					break;
				case NOT_EQUALS:
					conditions.add(ne(field, value));
					break;
				case IN:
					conditions.add(in(field, value));
					break;
				case EQUALS:
				default:
					conditions.add(eq(field, value));
					break;
			}
		});

		return (conditions.isEmpty() ? DSL.trueCondition() : DSL.and(conditions));
	}

	private void applyOrder(SelectQuery<Record> q, QueryOrder order)
	{
		if (order == null || !order.isSorted()) return;

		final List<SortField<?>> sorts = new ArrayList<SortField<?>>();

		order.iterate(new OrderCallback()
		{
			@Override
			public void orderBy(OrderComponent component)
			{
				Field<?> field = requireField(component.getFieldName());
				sorts.add(component.isDescending() ? field.desc() : field.asc());
			}
		});

		if (!sorts.isEmpty())
		{
			q.addOrderBy(sorts);
		}
	}

	private void applyRange(SelectQuery<Record> q, QueryRange range)
	{
		if (range == null || !range.isInitialized()) return;

		q.addLimit((int) range.getStart(), range.getLimit());
	}

	private Field<?> requireField(String logicalFieldName)
	{
		Field<?> field = definition.field(logicalFieldName);

		if (field == null)
		{
			throw new RepositoryException("Unsupported query field: " + logicalFieldName);
		}

		return field;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addInsertValue(org.jooq.InsertQuery query, Field<?> field, Object value)
	{
		query.addValue(field, value);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addUpdateValue(UpdateQuery query, Field<?> field, Object value)
	{
		query.addValue(field, value);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Condition eq(Field<?> field, Object value)
	{
		return ((Field) field).eq(value);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Condition ne(Field<?> field, Object value)
	{
		return ((Field) field).ne(value);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Condition gt(Field<?> field, Object value)
	{
		return ((Field) field).gt(value);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Condition ge(Field<?> field, Object value)
	{
		return ((Field) field).ge(value);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Condition lt(Field<?> field, Object value)
	{
		return ((Field) field).lt(value);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Condition le(Field<?> field, Object value)
	{
		return ((Field) field).le(value);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Condition in(Field<?> field, Object value)
	{
		Collection<?> values = toCollection(value);

		if (values.isEmpty())
		{
			return DSL.falseCondition();
		}

		return ((Field) field).in(values);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Condition likeContains(Field<?> field, Object value)
	{
		return DSL.lower(((Field) field).cast(String.class)).like("%" + stringValue(value).toLowerCase() + "%");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Condition likeStartsWith(Field<?> field, Object value)
	{
		return DSL.lower(((Field) field).cast(String.class)).like(stringValue(value).toLowerCase() + "%");
	}

	private String stringValue(Object value)
	{
		return (value == null ? "" : value.toString());
	}

	private Collection<?> toCollection(Object value)
	{
		if (value == null)
		{
			return Collections.emptyList();
		}

		if (value instanceof Collection<?>)
		{
			return (Collection<?>) value;
		}

		if (value instanceof Iterable<?>)
		{
			List<Object> values = new ArrayList<>();
			((Iterable<?>) value).forEach(values::add);

			return values;
		}

		if (value.getClass().isArray())
		{
			int length = Array.getLength(value);
			List<Object> values = new ArrayList<>(length);

			for (int i = 0; i < length; ++i)
			{
				values.add(Array.get(value, i));
			}

			return values;
		}

		List<Object> singleValue = new ArrayList<>(1);
		singleValue.add(value);
		return singleValue;
	}
}
