/**
 * 
 */
package com.strategicgains.repoexpress.cassandra;

/**
 * @author dinusha
 *
 */
public class ItemDao extends CassandraEntityRepository<Item>{

	/**
	 * @param keyspace
	 * @param nodes
	 * @param baseClass
	 */
	public ItemDao(String keyspace, String nodes) {
		super(keyspace, nodes, Item.class);
	}

	
	
}
