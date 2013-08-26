package com.strategicgains.repoexpress.cassandra;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.strategicgains.repoexpress.domain.AbstractTimestampedIdentifiable;
import com.strategicgains.repoexpress.event.DefaultTimestampedIdentifiableRepositoryObserver;
import com.strategicgains.repoexpress.event.UuidIdentityRepositoryObserver;

/**
 * A Cassandra repository that manages types of AbstractUuidEntity. It utilizes the
 * {@link UuidIdentityRepositoryObserver} to assign a UUID on creation.  It also uses
 * {@link DefaultTimestampedIdentifiableRepositoryObserver} to set the createAt and 
 * updatedAt dates on the object as appropriate.
 * <p/>
 * Storing a UUID using Easy-Cassandra (as this repository does) requires four (4) bytes for the ID,
 * whereas, using a Cassandra ObjectId only requires three (3).  However, a UUID, shortened to
 * 22 characters (using Base64 encoding) is arguably more readable and universally applicable
 * on a URL.
 * <p/>
 * To implement single-table inheritance, simply pass in all the sub-classes that
 * exist in this collection, with the inheritance-root listed first.
 * @author dinusha
 * @since Aug , 2013
 */
@MappedSuperclass
public abstract class AbstractCassandraEntity extends AbstractTimestampedIdentifiable
{
	@Column
	private Date createdAt;
	@Column
	private Date updatedAt;

	@Override
	public Date getCreatedAt()
	{
		return (createdAt == null ? null : new Date(createdAt.getTime()));
	}

	
	@Override
	public Date getUpdatedAt()
	{
		return (updatedAt == null ? null : new Date(updatedAt.getTime()));
	}

	/**
	 * @param  date
	 * @uml.property  name="createdAt"
	 */
	@Override
	public void setCreatedAt(Date date)
	{
		this.createdAt = (date == null ? new Date() : new Date(date.getTime()));
	}

	/**
	 * @param  date
	 * @uml.property  name="updatedAt"
	 */
	@Override
	public void setUpdatedAt(Date date)
	{
		this.updatedAt = (date == null ? new Date() : new Date(date.getTime()));
	}

}
