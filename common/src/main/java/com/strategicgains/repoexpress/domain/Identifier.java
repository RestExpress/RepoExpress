/*
    Copyright 2013-2018, Strategic Gains, Inc.

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
package com.strategicgains.repoexpress.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.restexpress.common.util.ObjectUtils;

import com.strategicgains.repoexpress.util.Format;

/**
 * Supports the concept of a compound identifier. An Identifier is made up of components, which
 * are Object instances. The components are kept in order of which they are added.
 * 
 * @author toddf
 * @since Aug 29, 2013
 */
public class Identifier
implements Comparable<Identifier>
{
	private static final String SEPARATOR = ", ";

	private List<Object> components = new ArrayList<>();

	/**
	 * Create an empty identifier.
	 */
	public Identifier()
	{
		super();
	}
	
	public Identifier(Identifier that)
	{
		this();
		
		if (that == null || that.isEmpty()) return;
		
		add(that.components.toArray());
	}

	/**
	 * Create an identifier with the given components. Duplicate instances
	 * are not added--only one instance of a component will exist in the identifier.
	 * 
	 * @param components
	 */
	public Identifier(Object... components)
	{
		this();
		add(components);
	}

	/**
	 * Add the given components, in order, to the identifier. Duplicate instances
	 * are not added--only one instance of a component will exist in the identifier.
	 * 
	 * @param components
	 */
	public void add(Object... components)
    {
		if (components == null) return;

		for (Object component : components)
		{
			add(component);
		}
    }

	/**
	 * Add a single component to the identifier. The given component is added to
	 * the end of the identifier.
	 *  
	 * @param component
	 */
	public void add(Object component)
    {
		if (component == null) return;

		components.add(component);
    }

	/**
	 * Get an unmodifiable list of the components that make up this identifier.
	 * 
	 * @return an unmodifiable list of components.
	 */
	public List<Object> components()
	{
		return Collections.unmodifiableList(components);
	}

	/**
	 * Iterate the components of this identifier. Modifications to the underlying components
	 * are not possible via this iterator.
	 * 
	 * @return an iterator over the components of this identifier
	 */
	public Iterator<Object> iterator()
	{
		return components().iterator();
	}

	/**
	 * Indicates the number of components making up this identifier.
	 * 
	 * @return the number of components in this identifier.
	 */
	public int size()
	{
		return components.size();
	}

	/**
	 * Check for equality between identifiers. Returns true if the identifiers
	 * contain equal components. Otherwise, returns false.
	 * 
	 * @return true if the identifiers are equivalent.
	 */
	@Override
	public boolean equals(Object that)
	{
		if (that == null) return false;
		if (this.getClass() != that.getClass()) return false;

		return (compareTo((Identifier) that) == 0);
	}

	/**
	 * Returns a hash code for this identifier.
	 * 
	 * @return an integer hashcode
	 */
	@Override
	public int hashCode()
	{
		return 1 + components.hashCode();
	}

	/**
	 * Compares this identifer to another, returning -1, 0, or 1 depending
	 * on whether this identifier is less-than, equal-to, or greater-than
	 * the other identifier, respectively.
	 * 
	 * @return -1, 0, 1 to indicate less-than, equal-to, or greater-than, respectively.
	 */
    @SuppressWarnings({
        "unchecked", "rawtypes"
    })
    @Override
    public int compareTo(Identifier that)
    {
		if (that == null) return 1;
		if (this.size() < that.size()) return -1;
		if (this.size() > that.size()) return 1;

		int i = 0;
		int result = 0;

		while (result == 0 && i < size())
		{
			Object cThis = this.components.get(i);
			Object cThat = that.components.get(i);

			if (ObjectUtils.areComparable(cThis, cThat))
			{
				result = ((Comparable) cThis).compareTo((cThat));
			}
			else
			{
				result = (cThis.toString().compareTo(cThat.toString()));
			}
			
			++i;
		}

	    return result;
    }

    /**
     * Returns a string representation of this identifier.
     * 
     * @return a string representation of the identifier.
     */
	@Override
	public String toString()
	{
		if (components.isEmpty()) return "";
		
		return (components.size() == 1 ? Format.id(this, SEPARATOR) : "(" + Format.id(this) + ")");
	}

	/**
	 * Returns the first component of the identifier. Return null if the identifier is empty.
	 * Equivalent to components().get(0).
	 * 
	 * @return the first component or null.
	 */
	public Object firstComponent()
	{
		return (isEmpty() ? null : components.get(0));
	}

	/**
	 * Returns the last component of the identifier. Return null if the identifier is empty.
	 * Otherwise, equivalent to components().get(components().size() - 1).
	 * 
	 * @return the first component or null.
	 */
	public Object lastComponent()
	{
		return (isEmpty() ? null : components.get(components.size() - 1));
	}

	/**
	 * Return true if the identifier has no components.
	 * 
	 * @return true if the identifier is empty.
	 */
	public boolean isEmpty()
    {
	    return components.isEmpty();
    }
}
