package com.strategicgains.repoexpress.cassandra;




import com.strategicgains.repoexpress.event.DefaultTimestampedIdentifiableRepositoryObserver;


/**
 * @author dinusha
 * @since Aug , 2013
 * 
 */
public class CassandraEntityRepository<T extends AbstractCassandraEntity>
extends CassandraRepository<T, String>
{
	
	
	
  public CassandraEntityRepository(String keyspace, String nodes,
			Class<T> baseClass) {
		super(keyspace, nodes, baseClass);
		initializeObservers();
		
	}


    protected void initializeObservers()
    {
		addObserver(new DefaultTimestampedIdentifiableRepositoryObserver<T>());
    }
}
