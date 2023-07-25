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
package com.strategicgains.repoexpress.exception;

/**
 * @author toddf
 * @since Oct 13, 2010
 */
public class RepositoryException
extends RuntimeException
{
    private static final long serialVersionUID = 3017766856659675935L;

	public RepositoryException()
	{
		super();
	}

	/**
	 * @param message
	 */
	public RepositoryException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public RepositoryException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RepositoryException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
