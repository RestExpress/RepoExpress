package com.strategicgains.repoexpress.cassandra;

import java.util.List;

import org.easycassandra.persistence.cassandra.EasyCassandraManager;
import org.easycassandra.persistence.cassandra.Persistence;

import com.strategicgains.repoexpress.AbstractObservableAdaptableRepository;
import com.strategicgains.repoexpress.Queryable;
import com.strategicgains.repoexpress.domain.Identifiable;
import com.strategicgains.repoexpress.exception.DuplicateItemException;
import com.strategicgains.repoexpress.exception.InvalidObjectIdException;
import com.strategicgains.repoexpress.exception.ItemNotFoundException;
import com.strategicgains.repoexpress.exception.RepositoryException;
import com.strategicgains.restexpress.common.query.QueryFilter;
import com.strategicgains.restexpress.common.query.QueryOrder;
import com.strategicgains.restexpress.common.query.QueryRange;

/**
 * @author dinusha
 * @since Aug , 2013
 */
public class CassandraRepository<T extends Identifiable,I> extends AbstractObservableAdaptableRepository<T, I>{

	
	private String keyspace;
	
	private Persistence persistence;

	private Class<T> baseClass;
	
	private static EasyCassandraManager easyCassandraManager;

	/**
	 * @param keyspace name of the keyspace 
	 * @param nodes IP address or node name string array
	 * @param baseClass 
	 */
	public CassandraRepository( String keyspace ,String nodes, Class<T> baseClass) {
		super();
		this.keyspace = keyspace;
		this.easyCassandraManager = new EasyCassandraManager(nodes, keyspace);
		this.persistence =  easyCassandraManager.getPersistence();
		this.baseClass = baseClass;
		easyCassandraManager.addFamilyObject(baseClass, keyspace);
	}
	
	/**
	 * @param item 
	 * @return the created item
	 */
	public T doCreate(T item) {
		if (exists(item.getId()))
		{
			throw new DuplicateItemException(item.getClass().getSimpleName()
			    + " ID already exists: " + item.getId());
		}

		boolean status =persistence.insert(item);
		if(!status)
		{
			throw new RepositoryException(item.getClass().getSimpleName()
				    + " Invalid creat commond : " + item.getId());
		}
		return item;
	}

	/**
	 * @param item 
	 */
	public void doDelete(T item) {
		try
		{
			boolean status = persistence.delete(item);
			if(!status){
				throw new RepositoryException(item.getClass().getSimpleName()
					    + " Invalid delete commond : " + item.getId());
			}
			
		}
		catch (InvalidObjectIdException e)
		{
			throw new ItemNotFoundException("ID not found: " + item.getId());
		}
		
	}

	/**
	 * @param id 
	 * @return item with id
	 */
	public T doRead(String id) {
		T item = persistence.findByKey( adaptId(id),baseClass);

		if (item == null)
		{
			throw new ItemNotFoundException("ID not found: " + id);
		}

		return item;
	}

	/**
	 * @param item 
	 * @return updated item
	 */
	public T doUpdate(T item) {
		if (!exists(item.getId()))
		{
			throw new ItemNotFoundException(item.getClass().getSimpleName()
			    + " ID not found: " + item.getId());
		}

		boolean status = persistence.insert(item);
		if(!status){
			throw new RepositoryException(item.getClass().getSimpleName()
				    + " Invalid update commond : " + item.getId());
		}
		return item;
	}

	/**
	 * @param id 
	 * @return true if given id has item, else false
	 */
	@Override
	public boolean exists(String id) {
		if (id == null) return false;
		return (countByKey(adaptId(id),baseClass) > 0);
	}
	
	
	/**
	 * @param key 
	 * @param entityName name of the entity 
	 * @return count of the item which has give key
	 */
	public long countByKey(Object key,Class<T> entityName) {
		T entityList =persistence.findByKey(key, entityName);
		if (entityList == null) return 0;
		return 1;
	}

	
	
	/**
	 *  
	 * @return return all baseClass item
	 */
	 public List listAll() {
	        return persistence.findAll(baseClass);
	 }
	 
	 
	 /**
		 * @param indexName
		 * @param index 
		 * @return list of all item which has index value
		 */
	 public List<T> listByIndex(String indexName, String index) {
			
			return persistence.findByIndex(indexName,index, baseClass);
		}
	 

}
