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
package com.strategicgains.repoexpress.jdbc.example;

import com.strategicgains.noschema.Identifier;
import com.strategicgains.noschema.entity.AbstractEntity;
import com.strategicgains.repoexpress.jdbc.annotation.Entity;
import com.strategicgains.repoexpress.jdbc.annotation.Id;
import com.strategicgains.repoexpress.jdbc.annotation.Property;
import com.strategicgains.repoexpress.jdbc.annotation.Transient;

@Entity(table = "users")
public class AnnotatedUser
extends AbstractEntity<Long>
{
	@Id(order = 0)
	@Property(value = "id", queryable = true, sortable = true)
	private Long id;

	@Property(value = "email_address", queryName = "email", queryable = true, sortable = true)
	private String email;

	@Property(value = "display_name", queryable = false, sortable = false)
	private String displayName;

	@Transient
	private String ignored;

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

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public void setIgnored(String ignored)
	{
		this.ignored = ignored;
	}

	public String getIgnored()
	{
		return ignored;
	}
}
