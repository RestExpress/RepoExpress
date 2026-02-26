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

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;

import com.strategicgains.noschema.Identifiable;
import com.strategicgains.repoexpress.exception.RepositoryException;
import com.strategicgains.repoexpress.jdbc.annotation.Entity;
import com.strategicgains.repoexpress.jdbc.annotation.Id;
import com.strategicgains.repoexpress.jdbc.annotation.Property;

/**
 * Builds {@link JdbcEntityDefinition} instances from RepoExpress JDBC annotations.
 *
 * @author toddf
 * @since Feb 26, 2026
 */
public final class JdbcEntityDefinitionFactory
{
	private static final ConcurrentHashMap<Class<?>, JdbcEntityDefinition<?>> CACHE = new ConcurrentHashMap<Class<?>, JdbcEntityDefinition<?>>();

	private JdbcEntityDefinitionFactory()
	{
		super();
	}

	public static <T extends Identifiable> JdbcEntityDefinition<T> create(Class<T> entityClass)
	{
		return from(entityClass);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Identifiable> JdbcEntityDefinition<T> from(Class<T> entityClass)
	{
		if (entityClass == null) throw new IllegalArgumentException("entityClass is required");

		JdbcEntityDefinition<?> existing = CACHE.get(entityClass);

		if (existing != null)
		{
			return (JdbcEntityDefinition<T>) existing;
		}

		JdbcEntityDefinition<T> created = new ReflectionJdbcEntityDefinition<T>(entityClass);
		JdbcEntityDefinition<?> previous = CACHE.putIfAbsent(entityClass, created);
		return (JdbcEntityDefinition<T>) (previous == null ? created : previous);
	}

	private static final class ReflectionJdbcEntityDefinition<T extends Identifiable>
	implements JdbcEntityDefinition<T>
	{
		private final Class<T> entityClass;
		private final Constructor<T> ctor;
		private final Table<?> table;
		private final List<PropertyBinding> bindings;
		private final List<Field<?>> idFields;
		private final Map<String, Field<?>> queryFields;

		private ReflectionJdbcEntityDefinition(Class<T> entityClass)
		{
			this.entityClass = entityClass;
			this.ctor = noArgConstructor(entityClass);

			Entity entity = entityClass.getAnnotation(Entity.class);

			if (entity == null)
			{
				throw new RepositoryException("Missing @" + Entity.class.getSimpleName() + " on " + entityClass.getName());
			}

			String tableName = tableName(entity, entityClass);
			this.table = createTable(entity.schema(), tableName);

			List<PropertyBinding> discovered = discoverBindings(entityClass, tableName);

			if (discovered.isEmpty())
			{
				throw new RepositoryException("No mapped fields found on " + entityClass.getName());
			}

			validate(discovered, entityClass);
			Collections.sort(discovered, PropertyBindingComparator.INSTANCE);
			this.bindings = Collections.unmodifiableList(discovered);
			this.idFields = Collections.unmodifiableList(toIdFields(discovered));
			this.queryFields = Collections.unmodifiableMap(toQueryFields(discovered));
		}

		@Override
		public Table<?> table()
		{
			return table;
		}

		@Override
		public List<Field<?>> idFields()
		{
			return idFields;
		}

		@Override
		public Map<Field<?>, Object> insertValues(T entity)
		{
			Map<Field<?>, Object> values = new LinkedHashMap<Field<?>, Object>();

			for (PropertyBinding binding : bindings)
			{
				if (!binding.insertable) continue;
				values.put(binding.jooqField, binding.get(entity));
			}

			return values;
		}

		@Override
		public Map<Field<?>, Object> updateValues(T entity)
		{
			Map<Field<?>, Object> values = new LinkedHashMap<Field<?>, Object>();

			for (PropertyBinding binding : bindings)
			{
				if (binding.id || !binding.updatable) continue;
				values.put(binding.jooqField, binding.get(entity));
			}

			return values;
		}

		@Override
		public T fromRecord(Record record)
		{
			T instance = newInstance();

			for (PropertyBinding binding : bindings)
			{
				Object value = readValue(record, binding);
				binding.set(instance, value);
			}

			return instance;
		}

		@Override
		public Field<?> field(String logicalFieldName)
		{
			return queryFields.get(logicalFieldName);
		}

		private T newInstance()
		{
			try
			{
				return ctor.newInstance();
			}
			catch (Exception e)
			{
				throw new RepositoryException("Unable to instantiate " + entityClass.getName(), e);
			}
		}

		private Object readValue(Record record, PropertyBinding binding)
		{
			try
			{
				return record.get(binding.jooqField);
			}
			catch (ClassCastException e)
			{
				throw new RepositoryException("Unable to read column '" + binding.columnName + "' as "
					+ binding.javaField.getType().getName(), e);
			}
		}

		private static Table<?> createTable(String schema, String table)
		{
			if (schema == null || schema.trim().isEmpty())
			{
				return DSL.table(DSL.name(table));
			}

			return DSL.table(DSL.name(schema, table));
		}

		private static String tableName(Entity entity, Class<?> entityClass)
		{
			String table = trimToNull(entity.table());

			if (table == null)
			{
				table = trimToNull(entity.value());
			}

			if (table == null)
			{
				throw new RepositoryException("@" + Entity.class.getSimpleName() + " must define table/value on " + entityClass.getName());
			}

			return table;
		}

		private static List<PropertyBinding> discoverBindings(Class<?> type, String tableName)
		{
			List<PropertyBinding> results = new ArrayList<PropertyBinding>();
			Class<?> current = type;

			while (current != null && current != Object.class)
			{
					for (java.lang.reflect.Field field : current.getDeclaredFields())
					{
						if (Modifier.isStatic(field.getModifiers())) continue;
						if (Modifier.isTransient(field.getModifiers())) continue;
						if (field.isAnnotationPresent(com.strategicgains.repoexpress.jdbc.annotation.Transient.class)) continue;

					Property property = field.getAnnotation(Property.class);
					Id id = field.getAnnotation(Id.class);

					if (property == null && id == null) continue;

					results.add(PropertyBinding.create(field, property, id, tableName));
				}

				current = current.getSuperclass();
			}

			return results;
		}

		private static void validate(List<PropertyBinding> bindings, Class<?> entityClass)
		{
			int idCount = 0;
			Set<Integer> idOrders = new LinkedHashSet<Integer>();
			Set<String> logicalNames = new LinkedHashSet<String>();
			Set<String> columnNames = new LinkedHashSet<String>();

			for (PropertyBinding binding : bindings)
			{
				if (!columnNames.add(binding.columnName))
				{
					throw new RepositoryException("Duplicate mapped column '" + binding.columnName + "' on " + entityClass.getName());
				}

				if ((binding.queryable || binding.sortable) && !logicalNames.add(binding.logicalName))
				{
					throw new RepositoryException("Duplicate query field '" + binding.logicalName + "' on " + entityClass.getName());
				}

				if (binding.id)
				{
					++idCount;

					if (!idOrders.add(Integer.valueOf(binding.idOrder)))
					{
						throw new RepositoryException("Duplicate @" + Id.class.getSimpleName() + "(order=" + binding.idOrder
							+ ") on " + entityClass.getName());
					}
				}
			}

			if (idCount < 1)
			{
				throw new RepositoryException("At least one @" + Id.class.getSimpleName() + " field is required on " + entityClass.getName());
			}
		}

		private static List<Field<?>> toIdFields(Collection<PropertyBinding> bindings)
		{
			List<PropertyBinding> ids = new ArrayList<PropertyBinding>();

			for (PropertyBinding binding : bindings)
			{
				if (binding.id)
				{
					ids.add(binding);
				}
			}

			Collections.sort(ids, IdOrderComparator.INSTANCE);

			List<Field<?>> fields = new ArrayList<Field<?>>(ids.size());

			for (PropertyBinding binding : ids)
			{
				fields.add(binding.jooqField);
			}

			return fields;
		}

		private static Map<String, Field<?>> toQueryFields(Collection<PropertyBinding> bindings)
		{
			Map<String, Field<?>> fields = new LinkedHashMap<String, Field<?>>();

			for (PropertyBinding binding : bindings)
			{
				if (binding.queryable || binding.sortable)
				{
					fields.put(binding.logicalName, binding.jooqField);
				}
			}

			return fields;
		}

		private static <T> Constructor<T> noArgConstructor(Class<T> type)
		{
			try
			{
				Constructor<T> ctor = type.getDeclaredConstructor();
				ctor.setAccessible(true);
				return ctor;
			}
			catch (Exception e)
			{
				throw new RepositoryException("A no-arg constructor is required for " + type.getName(), e);
			}
		}

		private static String trimToNull(String value)
		{
			if (value == null) return null;
			String trimmed = value.trim();
			return (trimmed.isEmpty() ? null : trimmed);
		}
	}

	private static final class PropertyBinding
	{
		private final java.lang.reflect.Field javaField;
		private final Field<?> jooqField;
		private final String logicalName;
		private final String columnName;
		private final boolean queryable;
		private final boolean sortable;
		private final boolean insertable;
		private final boolean updatable;
		private final boolean id;
		private final int idOrder;

		private PropertyBinding(java.lang.reflect.Field javaField, Field<?> jooqField, String logicalName, String columnName,
			boolean queryable, boolean sortable, boolean insertable, boolean updatable, boolean id, int idOrder)
		{
			this.javaField = javaField;
			this.jooqField = jooqField;
			this.logicalName = logicalName;
			this.columnName = columnName;
			this.queryable = queryable;
			this.sortable = sortable;
			this.insertable = insertable;
			this.updatable = updatable;
			this.id = id;
			this.idOrder = idOrder;
		}

		private Object get(Object target)
		{
			try
			{
				return javaField.get(target);
			}
			catch (Exception e)
			{
				throw new RepositoryException("Unable to read field '" + javaField.getName() + "'", e);
			}
		}

		private void set(Object target, Object value)
		{
			try
			{
				if (value == null && javaField.getType().isPrimitive()) return;
				javaField.set(target, value);
			}
			catch (Exception e)
			{
				throw new RepositoryException("Unable to write field '" + javaField.getName() + "'", e);
			}
		}

		private static PropertyBinding create(java.lang.reflect.Field javaField, Property property, Id id, String tableName)
		{
			javaField.setAccessible(true);

			String columnName = columnName(property, javaField);
			String logicalName = logicalName(property, javaField);
			boolean isId = (id != null);
			boolean queryable = (property == null ? true : property.queryable());
			boolean sortable = (property == null ? true : property.sortable());
			boolean insertable = (property == null ? true : property.insertable());
			boolean updatable = (property == null ? !isId : property.updatable());

			if (isId)
			{
				updatable = false;
			}

			Field<?> jooqField = createField(tableName, columnName, javaField.getType());

			return new PropertyBinding(
				javaField,
				jooqField,
				logicalName,
				columnName,
				queryable,
				sortable,
				insertable,
				updatable,
				isId,
				(isId ? id.order() : Integer.MAX_VALUE)
			);
		}

		private static Field<?> createField(String tableName, String columnName, Class<?> type)
		{
			return DSL.field(DSL.name(tableName, columnName), type);
		}

		private static String columnName(Property property, java.lang.reflect.Field javaField)
		{
			if (property != null && property.value() != null && !property.value().trim().isEmpty())
			{
				return property.value().trim();
			}

			return javaField.getName();
		}

		private static String logicalName(Property property, java.lang.reflect.Field javaField)
		{
			if (property != null && property.queryName() != null && !property.queryName().trim().isEmpty())
			{
				return property.queryName().trim();
			}

			return javaField.getName();
		}
	}

	private static final class PropertyBindingComparator
	implements Comparator<PropertyBinding>
	{
		private static final PropertyBindingComparator INSTANCE = new PropertyBindingComparator();

		@Override
		public int compare(PropertyBinding a, PropertyBinding b)
		{
			return a.columnName.compareTo(b.columnName);
		}
	}

	private static final class IdOrderComparator
	implements Comparator<PropertyBinding>
	{
		private static final IdOrderComparator INSTANCE = new IdOrderComparator();

		@Override
		public int compare(PropertyBinding a, PropertyBinding b)
		{
			int result = Integer.compare(a.idOrder, b.idOrder);
			return (result != 0 ? result : a.columnName.compareTo(b.columnName));
		}
	}
}
