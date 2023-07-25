/*
    Copyright 2010, Strategic Gains, Inc.

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
package com.strategicgains.repoexpress.event;

import java.util.List;

import com.strategicgains.repoexpress.domain.Identifiable;


/**
 * @author toddf
 * @since Oct 13, 2009
 */
public interface Observable<T extends Identifiable>
{
	/**
	 * Add an observer to the observable.
	 * 
	 * @param observer
	 */
	public void addObserver(RepositoryObserver<T> observer);

	/**
     * Remove all observers from this observable.
     */
    public void clearObservers();

	/**
     * Returns a list of the observers.
     * 
     * @return the observers.
     */
    public List<RepositoryObserver<T>> getObservers();

    /**
     * Remove given observer from the list of observers.
     * 
     * @param observer
     * @return true if the observer was removed.
     */
	public boolean removeObserver(RepositoryObserver<T> observer);

}
