# RepoExpress JDBC (jOOQ)

`repoexpress-jdbc` is a jOOQ-backed JDBC repository module for RepoExpress that supports
PostgreSQL/MySQL-style relational databases.

It provides:
- `JdbcRepository<T>`: a `Repository<T>` + `Queryable<T>` implementation
- `JdbcConfig`: JDBC connection configuration helper (URL, username, password, optional driver class)
- `JdbcEntityDefinition<T>`: the mapping contract used by the repository
- `JdbcEntityDefinitionFactory`: reflection-based mapping generation from RepoExpress JDBC annotations

## Status

Initial iteration:
- CRUD support via `JdbcRepository`
- `QueryFilter`, `QueryRange`, `QueryOrder` support
- duplicate-key translation for PostgreSQL/MySQL
- annotation-driven `JdbcEntityDefinition` generation

Current expectations:
- entities have IDs assigned before `create()`
- entities have a no-arg constructor for `fromRecord()` mapping

## Dependency

```xml
<dependency>
	<groupId>com.strategicgains.repoexpress</groupId>
	<artifactId>repoexpress-jdbc</artifactId>
	<version>${repoexpress.version}</version>
</dependency>
```

You must also configure your JDBC driver and create a jOOQ `DSLContext`.

## `JdbcConfig`

`JdbcConfig` mirrors the style of `MongoConfig` / `CassandraConfig` and reads connection
configuration from `Properties` (environment-style keys first, then property keys).

Supported keys:
- `JDBC_URL` or `jdbc.url` (required)
- `JDBC_USERNAME` or `jdbc.username` (optional)
- `JDBC_PASSWORD` or `jdbc.password` (optional)
- `JDBC_DRIVER_CLASS` or `jdbc.driverClass` (optional, loaded via `Class.forName`)
- `JDBC_DIALECT` or `jdbc.dialect` (optional, jOOQ `SQLDialect` name such as `POSTGRES`, `MYSQL`)

Example:

```java
Properties p = new Properties();
JdbcConfig config = new JdbcConfig(p);
Connection connection = config.createConnection();
DSLContext dsl = config.createDslContext(); // uses JDBC_DIALECT/jdbc.dialect if provided
```

## Annotations (RepoExpress JDBC)

These are RepoExpress-owned annotations (not Morphia, not JPA):
- `com.strategicgains.repoexpress.jdbc.annotation.Entity`
- `com.strategicgains.repoexpress.jdbc.annotation.Id`
- `com.strategicgains.repoexpress.jdbc.annotation.Property`
- `com.strategicgains.repoexpress.jdbc.annotation.Transient`

### Annotation Reference

#### `@Entity`

Declares the table-level mapping for a POJO.

Attributes:
- `table`:
  - Physical table name.
  - Preferred attribute for clarity in relational code.
  - Default: empty string.
- `value`:
  - Alias for `table` (included for Morphia-style familiarity).
  - Used only when `table` is empty.
  - Default: empty string.
- `schema`:
  - Optional schema qualifier.
  - When set, generated table metadata is schema-qualified.
  - Default: empty string.

Validation:
- Exactly one class-level `@Entity` is expected for annotation-driven mapping.
- At least one of `table` or `value` must be set.

#### `@Id`

Marks a field as part of the primary identifier.

Attributes:
- `order`:
  - Component order for composite identifiers.
  - Default: `0`.

Behavior:
- One `@Id` field: simple key.
- Multiple `@Id` fields: composite key ordered by ascending `order`.
- Duplicate `order` values are invalid.
- `@Id` fields are excluded from updates (`updateValues`) by the factory.

#### `@Property`

Maps a field to a physical column and controls query/insert/update participation.

Attributes:
- `value`:
  - Physical column name.
  - If empty, Java field name is used.
  - Default: empty string.
- `queryName`:
  - Logical field name used in repository queries (`field("...")`) and dynamic queries.
  - Supports dotted names (for example `account.id`) so Mongo-style logical paths can be preserved.
  - If empty, Java field name is used.
  - Default: empty string.
- `queryable`:
  - Exposes field for filtering lookup.
  - Default: `true`.
- `sortable`:
  - Exposes field for ordering lookup.
  - Default: `true`.
- `insertable`:
  - Includes field in generated insert values.
  - Set `false` for DB-generated values.
  - Default: `true`.
- `updatable`:
  - Includes field in generated update values.
  - Default: `true`.
  - Note: `@Id` fields are never updated even if set `true`.

Validation:
- Duplicate physical column names are invalid.
- Duplicate logical names (`queryName` / derived logical name) among queryable/sortable fields are invalid.

#### `@Transient`

Excludes a field from JDBC mapping metadata.

Behavior:
- Field is omitted from:
  - insert values
  - update values
  - record hydration
  - query/sort lookup exposure
- Java `transient` modifier has the same effect.

### Mapping Defaults and Factory Rules

`JdbcEntityDefinitionFactory` applies these defaults when generating definitions:
- If `@Property.value` is empty, column name defaults to Java field name.
- If `@Property.queryName` is empty, logical name defaults to Java field name.
- If a field has `@Id` only (no `@Property`), it is still mapped.
- Static fields are ignored.
- Java `transient` and `@Transient` fields are ignored.
- A no-arg constructor is required for entity hydration from jOOQ `Record`.

### Queryability and Logical Paths

Queryable/sortable behavior is based on logical names, not Java object graph traversal.

Example:

```java
@Property(value = "account_id", queryName = "account.id", queryable = true, sortable = false)
private UUID accountId;
```

Then in repository code:

```java
field("account.id", UUID.class).eq(accountId)
```

This preserves a Mongo-style logical API while still mapping to flat SQL columns.

## Example Entity

```java
import com.strategicgains.noschema.AbstractEntity;
import com.strategicgains.noschema.Identifier;
import com.strategicgains.repoexpress.jdbc.annotation.Entity;
import com.strategicgains.repoexpress.jdbc.annotation.Id;
import com.strategicgains.repoexpress.jdbc.annotation.Property;

@Entity(table = "users")
public class User extends AbstractEntity<Long>
{
	@Id(order = 0)
	@Property(value = "id", queryable = true, sortable = true)
	private Long id;

	@Property(value = "email_address", queryName = "email", queryable = true, sortable = true)
	private String email;

	@Property(value = "display_name", queryable = false, sortable = false)
	private String displayName;

	private transient String cachedDisplayLabel; // ignored by the factory

	@Override
	public Identifier getIdentifier()
	{
		return (id == null ? null : new Identifier(id));
	}

	@Override
	public Long getId()
	{
		return id;
	}

	@Override
	public void setId(Long id)
	{
		this.id = id;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}
}
```

## Create a `JdbcEntityDefinition`

```java
JdbcEntityDefinition<User> definition = JdbcEntityDefinitionFactory.from(User.class);
```

The factory caches definitions per entity class.

## Create a Repository

```java
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

JdbcConfig config = new JdbcConfig(properties);
DSLContext dsl = DSL.using(config.createConnection(), SQLDialect.POSTGRES);

JdbcRepository<User> repository = new JdbcRepository<User>(dsl, definition);
```

## Convenience Constructors

`JdbcRepository` also supports annotation-driven definition creation directly from entity classes:

```java
JdbcRepository<User> repository = new JdbcRepository<User>(dsl, User.class);
JdbcRepository<User> repository2 = new JdbcRepository<User>(jdbcConfig, SQLDialect.POSTGRES, User.class);
JdbcRepository<User> repository3 = new JdbcRepository<User>(jdbcConfig, User.class); // uses configured dialect or DEFAULT
```

This is intended to support custom repositories with concise constructors:

```java
public class UserRepository
extends JdbcRepository<User>
{
	public UserRepository(JdbcConfig config)
	{
		super(config, SQLDialect.POSTGRES, User.class);
	}
}
```

Alternate-key query methods in subclasses can use `field(...)`, `table()`, and `readOneBy...(...)`:

```java
public class LinkRepository extends JdbcRepository<Link>
{
	public LinkRepository(JdbcConfig config)
	{
		super(config, SQLDialect.POSTGRES, Link.class);
	}

	public Link readByAlias(UUID accountId, String alias)
	{
		return readOneBy(
			field("accountId", UUID.class).eq(accountId)
				.and(field("alias", String.class).eq(alias)),
			"Link alias not found for accountId=" + accountId + ", alias=" + alias
		);
	}
}
```

### About Multiple Entity Classes

`JdbcRepository` includes varargs constructors (similar shape to the Mongo API), but this initial
JDBC iteration does **not** yet support polymorphic/discriminator mapping. Passing multiple classes
currently throws `UnsupportedOperationException` instead of silently returning incorrect types.

Because `JdbcRepository` extends `AbstractObservableRepository`, existing RepoExpress observers
(for timestamps, UUID assignment, etc.) can still be added to the repository.

## Query Field Whitelisting

`JdbcEntityDefinitionFactory` only exposes fields declared queryable/sortable via `@Property` for
`QueryFilter` and `QueryOrder` lookups. This prevents arbitrary field-name injection from being
passed into SQL construction.

## Composite Identifier Example

Use multiple `@Id` fields and define explicit ordering:

```java
@Id(order = 0)
@Property("tenant_id")
private String tenantId;

@Id(order = 1)
@Property("user_id")
private Long userId;
```

Your `getIdentifier()` should return `new Identifier(tenantId, userId)` in the same order.

## Limitations / Next Steps

- Generated-key strategies (identity/serial) are not implemented yet
- Per-operation distinction between queryable vs sortable enforcement can be tightened further
- Integration tests against PostgreSQL/MySQL are still needed
