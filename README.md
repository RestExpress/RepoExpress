**Build Status** [![Build Status](https://buildhive.cloudbees.com/job/RestExpress/job/RepoExpress/badge/icon)](https://buildhive.cloudbees.com/job/RestExpress/job/RepoExpress/)

**Waffle.io** [![Stories in Ready](https://badge.waffle.io/RestExpress/RepoExpress.png?label=ready)](https://waffle.io/RestExpress/RepoExpress)

RepoExpress is a set of thin wrappers to provide a simple and easy way to create
domain storage (CRUD persistence) options using the Repository pattern against NoSQL
databases. There are various repositories supporting MongoDB, Redis, and Cassandra.

Designed to be utilized in conjunction with RestExpress, but stands alone and does not
require RestExpress (except for RestExpress-Common) to be used.

MongoDB Usage
=============
Stable:
```xml
		<dependency>
			<groupId>com.strategicgains.repoexpress</groupId>
			<artifactId>repoexpress-mongodb</artifactId>
			<version>0.4.8</version>
		</dependency>
```
Development:
```xml
		<dependency>
			<groupId>com.strategicgains.repoexpress</groupId>
			<artifactId>repoexpress-mongodb</artifactId>
			<version>1.0.0-SNAPSHOT</version>
		</dependency>
```
Or download the 'stable' jar directly from: 
http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22RepoExpress%22

Cassandra Usage
===============
Stable:
```xml
		<dependency>
			<groupId>com.strategicgains.repoexpress</groupId>
			<artifactId>repoexpress-cassandra</artifactId>
			<version>0.4.8</version>
		</dependency>
```
Development:
```xml
		<dependency>
			<groupId>com.strategicgains.repoexpress</groupId>
			<artifactId>repoexpress-cassandra</artifactId>
			<version>1.0.0-SNAPSHOT</version>
		</dependency>
```

Redis Usage
===========
Stable:
```xml
		<dependency>
			<groupId>com.strategicgains.repoexpress</groupId>
			<artifactId>repoexpress-redis</artifactId>
			<version>0.4.8</version>
		</dependency>
```
Development:
```xml
		<dependency>
			<groupId>com.strategicgains.repoexpress</groupId>
			<artifactId>repoexpress-redis</artifactId>
			<version>1.0.0-SNAPSHOT</version>
		</dependency>
```
Or download the 'stable' jar directly from: 
http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22RepoExpress%22

About Maven Snapshots
=====================
Note that to use the SNAPSHOT Maven versions mentioned above, you must enable snapshots and a repository in your pom file as follows:
```xml
  <profiles>
    <profile>
       <id>allow-snapshots</id>
          <activation><activeByDefault>true</activeByDefault></activation>
       <repositories>
         <repository>
           <id>snapshots-repo</id>
           <url>https://oss.sonatype.org/content/repositories/snapshots</url>
           <releases><enabled>false</enabled></releases>
           <snapshots><enabled>true</enabled></snapshots>
         </repository>
       </repositories>
     </profile>
  </profiles>
```

Change History:
===============

Release 1.0.0-SNAPSHOT
----------------------
* Yuge refactoring for V1. Changed semantics of Identifiable to return a fully-qualified identifier. Entity, which Identifiable, has object ID properties: getId() and setId(T).
* Introduced Entity<T> interface and AbstractEntity<T> implementation.
* Introduced CreatedAt and UpdatedAt interfaces. Changed Timestamped to extend them.
* Renamed UuidIdentifiable to UuidEntity and TimestampedUuidIdentifiable to TimestampedUuidEntity.
* Introduced AbstractTimestampedUuidEntity.
* Renamed Identifier.primaryKey() to firstComponent(). Added lastComponent(). Removed Identifier.setId(), renamed setId(Identifier) to setIdentifier(Identifier).
* Renamed AbstractTimestampedIdentifiable to AbstractTimestampedEntity. Changed inheritance hierarchy.
* InMemoryRepository no longer assigns IDs and, instead, expects the ID to be set on the incoming object. Throws exception if not.
* Upgraded to Morphia 1.3.2
* Upgraded to Jedis 2.9.0
* Upgraded to DataStax driver 3.6.0

Release 0.4.8 - 10 Feb 2016
---------------------------
* Added Identifiers.useShortUUID() method to support usage of shorter, URL-safe Base64-encoded UUIDs.
* Introduced Parse and Format utility classes containing static foreign methods to enable formatting of various Identifier types.
* Updated Cassandra, MongoDB and Redis drivers.

Release 0.4.7 - 28 Jul 2015
---------------------------
* Set Java output to 1.7 target and source.

Release 0.4.6 - 24 Jul 2015
---------------------------
* Fixed issue #13 - Added support for :in: filtering on the query string, with support in MongoDB.
* Updated to Java target and source 1.8.
* Added test for issue #15. No code changes other than test.
* Updated database drivers: Cassandra 2.1.7.1, MongoDB 3.0.2, Morphia 1.0.1, Redis 2.7.3

Release 0.4.5 - 13 Mar 2015
---------------------------
* Upgraded to Morphia 0.110 (MongoDB driver 2.13.0)
* Upgraded to Jedis 2.6.2

Release 0.4.4 - 02 Dec 2014
---------------------------
* Upgraded to Cassandra driver 2.1.3
* Upgraded to Jedis 2.6.1

Release 0.4.3 - 5 Sep 2014
--------------------------
* Upgraded to RestExpress-Common 0.10.4, cassandra-driver-core to 2.1.0, Jedis 2.5.2, Morphia 0.108, MongoDB repositories now require MongoClient (vs. Mongo) as a constructor parameter.

Release 0.4.2 - 27 May 2014
---------------------------
* Added ability to change default UUID string format via UuidAdapter.useShortUUID(boolean).
* Upgraded to RestExpress-Common 0.10.3

Release 0.4.1 - 3 Apr 2014
--------------------------
* Upgraded Datastax Cassandra driver to 2.0.1
* Introduced CassandraUuidEntityRepository and CassandraUuidTimestampedEntityRepository.
* Changed inheritance requirements of CassandraEntityRepository & CassandraTimestampedEntityRepository—to Identifiable (from UuidIdentifiable) and removed UuidIdentifiable, respectively.
* Exposed deleteStmt to subclasses (protected) in CassandraEntityRepository.java
* Fixed pattern in string split for cassandra contact points.
* Added format() methods for Identifier in UuidAdapter class.
* Changed Identifier.add() to allow duplicate components.
* Upgrade to RestExpress-Common 0.10.2

Release 0.4.0 - 24 Jan 2014
* **Breaking Change** Introduced Identifier class that now supports compound identifiers.
  This change ripples through the Identifiable interface so that getId() now returns
  an Identifier instead of a String. Also, setId() now takes an Identifier as an argument.
* **Voldemort Support Removed** due to lack of usage and desire to maintain it.
* **Breaking Change** Removed AbsractObservableRepository and all identifier adapters since, due to Identifier,
  adapting IDs is no longer needed (or desired) at the persistence layer. Also changed inheritence
  hierarchy for MongodbRepository and MongodbEntityRepository which now extend AbstractObservableRepository.
* **Breaking Change** refactored IdentifierAdapter methods and type hierarchy. Introduced
  Identifiers class with static singleton adapters.
* Upgraded to latest official MongoDB Morphia release (0.105).
* Introduced TimestampedUuidIdentifiable and Introduced CassandraTimestampedEntityRepository.

Release 0.3.4 - SNAPSHOT (never actually released)
* Voldemort support is deprecated.
* Introduced initial Cassandra repository support.

Release 0.3.3 - 17 Jul 2013
* Fixed issue with MongoDB repository query filter implementation where it converted the
  value to lower-case erroneously.
* Added MongodbRepository.find(QueryFilter) method.
* Changed message in InvalidObjectIdException thrown within StringToByteArrayAdapter.convert().
* Changed generic type on AbstractRepositoryObserver from T extends TimestampedIdentifiable to T extends Identifiable.
* Changed generic type on types parameter of MongodbEntityRepository constructor from Class<T> to Class<? extends T> to allow subclasses of T to be passed in.
* Fixed issued in configureQueryFilter(), removing call to toLowerCase() on the value portion of the contains query.
* Introduced MongodbUuidEntityRepository and related support classes, including UuidConverter
  to produce/parse URL-safe UUID instances.
* Changed MongodbRepository.configureQueryFilter() to leverage FilterOperator settings in FilterComponent
  and support Object values in FilterComponent.

Release 0.3.2
* Updated to latest RestExpress-Common, refactoring for QueryFilter addition of operator.

Release 0.3.1
* Introduced Maven build.
* Switched to com.github.jmkgreen.morphia version of Morphia (v 1.2.2)

Release 0.3.0
* Refactored to create separate jars for common functionality and DB-dependent functionality.
  Now creates 2 jars: RepoExpress...jar and RepoExpress...-mongodb.jar
* Introduced AbstractTimestampedIdentifiable which has default behavior for TimestampedIdentifiable.
* Introduced AbstractMongodbPersistable which extends AbstractTimestampedIdentifiable and includes
  id that is a MongoDB ObjectId.  get/set-Id() methods adapt the string to/from an ObjectId.
* DefaultTimestampedIdentifiableRepositoryObserver now also sets updatedAt on creation.
* Introduced Voldemort repository.
* Added Redis repository, using JOhm.

Release 0.2.2
* Exposed Mongo driver instance in case you need it for something tricky (that Morphia doesn't
  support) in sub-classes.
* Fixed issue in MongodbRepository.exists() where it wasn't calling convertId() and using new
  ObjectId() directly.
* Added try/catch in MongodbRepository.doDelete(String) to catch InvalidObjectIdException and
  translate it to NotFoundException.
* Introduced InvalidObjectIdException to enable implementations of IdentifierAdapter to throw
  it when it cannot convert the string to an internal identifier.  This enables upstream callers
  to handle it and identify the issue.

Release 0.2
* Update MongodbRepository to allow use of Replication Sets.

Release 0.1
* Initial import. Combined concepts from RestExpress and JigForJava full-stack framework.
