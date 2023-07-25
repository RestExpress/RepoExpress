/*
    Copyright 2011, Strategic Gains, Inc.

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
 * Thrown from an IdentifierAdapter.convert() when the ID cannot be converted into an identifier.
 *
 * @author toddf
 * @since Mar 24, 2011
 */
public class InvalidObjectIdException
extends RepositoryException
{
    private static final long serialVersionUID = -8427649738145349078L;

	public InvalidObjectIdException()
	{
	}

	/**
	 * @param message
	 */
	public InvalidObjectIdException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public InvalidObjectIdException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidObjectIdException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
