/**
 * 
 */
package com.strategicgains.repoexpress.cassandra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

/**
 * @author dinusha
 *
 */
public class ItemDaoTest {

	private ItemDao itemDao;
	private String keyspace ="repo_test";
	private String ip = "10.179.3.116";
	
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		itemDao = new ItemDao(keyspace,ip);
	}

	@Test
	public void testCreate() {
		Item newItem = createNew();
		
		Item item = itemDao.create(newItem);
		
		assertNotNull(item);
	}
	@Test
	public void testRetrieveByKey() {
		
		Item item = createNew();
		item = itemDao.create(item);
		
		Item creatItem = itemDao.read(item.getId());
		
		
		assertNotNull(creatItem);
	}
	
	@Test
	public void testUpdate() {
		
		Item item = createNew();
		
		item = itemDao.create(item);
		
		item.setItemQuntity(230);
		Item creatItem = itemDao.update(item);
		
		Item updatedItem = itemDao.read(item.getId());
		
		assertEquals(new Integer(230),updatedItem.getItemQuntity());
	}
	
	
	
	@Test
	public void testRetrieveByIndex() {
		
		Item item = createNew();
		List<Item> creatItems = itemDao.listByIndex("itemCode", item.getItemCode());
		
		
		assertNotNull(creatItems.get(0));
	}
	
	
/*	@Test
	public void testDelete() {
		Item item = createNew();
		
		 itemDao.delete(item);
		
		
		
	}*/
	
	/**
	 * @return
	 */
	private Item createNew() {
		Item item = new Item();
		item.setId(UUID.randomUUID().toString());
		item.setIsAvailable(true);
		item.setItemCode("IC001");
		item.setItemQuntity(233);
		
		return item;
	}

}
