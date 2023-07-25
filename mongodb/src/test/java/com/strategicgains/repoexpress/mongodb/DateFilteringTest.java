/*
    Copyright 2015, Strategic Gains, Inc.

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.restexpress.common.query.FilterOperator;
import org.restexpress.common.query.QueryFilter;

/**
 * @author tfredrich
 * @since Jul 16, 2015
 */
@Ignore
public class DateFilteringTest
{
	private static final int CREATE_COUNT = 5;
	private static MongodbRepository<TestEntity> REPOSITORY;
	private static List<TestEntity> CREATED = new ArrayList<TestEntity>();

	@BeforeClass
	public static void beforeClass()
	{
		Properties p = new Properties();
		p.setProperty("mongodb.uri", "mongodb://localhost:27017/repoexpress_mongodb_test");
		MongoConfig config = new MongoConfig(p);
		REPOSITORY = new MongodbUuidEntityRepository<TestEntity>(config.getClient(), "test_entities", TestEntity.class);
		createEntities(CREATE_COUNT);
	}

	private static void createEntities(int createCount)
    {
		for (int i = 0; i < createCount; i++)
		{
			CREATED.add(REPOSITORY.create(new TestEntity()));
		}
    }

	@AfterClass
	public static void afterClass()
	{
		for (TestEntity entity : CREATED)
		{
			REPOSITORY.delete(entity.getIdentifier());
		}
	}

	@Test
	public void testFixedDate()
	throws ParseException
	{
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'");
		dateFormatter.setTimeZone(tz);

		String start = "2015-02-03T03:20:59.000Z";
		Date date = dateFormatter.parse(start);
		QueryFilter filter = new QueryFilter()
			.addCriteria("createdAt", FilterOperator.GREATER_THAN_OR_EQUAL_TO, date);

		List<TestEntity> entities = REPOSITORY.readAll(filter, null, null);
		assertNotNull(entities);
		assertEquals(CREATE_COUNT, entities.size());
		assertEquals((long) CREATE_COUNT, REPOSITORY.count(filter));
	}

	@Test
	public void testComputedIntermediateDate()
	throws ParseException
	{
		Date date = new Date(CREATED.get(2).getCreatedAt().getTime() + 1);
		QueryFilter filter = new QueryFilter()
			.addCriteria("createdAt", FilterOperator.GREATER_THAN_OR_EQUAL_TO, date);

		List<TestEntity> entities = REPOSITORY.readAll(filter, null, null);
		assertNotNull(entities);
		assertEquals(CREATE_COUNT - 3, entities.size());
		assertEquals((long) CREATE_COUNT - 3, REPOSITORY.count(filter));
	}

}
