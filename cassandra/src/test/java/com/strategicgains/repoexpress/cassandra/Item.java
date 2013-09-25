/**
 * 
 */
package com.strategicgains.repoexpress.cassandra;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.easycassandra.Index;

/**
 * @author dinusha
 *
 */
@Entity
public class Item extends AbstractCassandraEntity{

	@Id
	@Column(name="id")
	private String iid;
	
	@Column(name="icode")
	@Index
	private String itemCode;
	
	@Column(name ="quntity")
	private Integer itemQuntity;
	
	@Column(name= "availability")
	private Boolean isAvailable;
	
	@Override
	public String getId() {
		return iid;
	}

	@Override
	public void setId(String id) {
		this.iid =id;
	}


	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public Integer getItemQuntity() {
		return itemQuntity;
	}

	public void setItemQuntity(Integer itemQuntity) {
		this.itemQuntity = itemQuntity;
	}

	public Boolean getIsAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	
}
