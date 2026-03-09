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
package com.strategicgains.repoexpress.jdbc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the relational table mapping for a RepoExpress JDBC entity.
 * <p/>
 * Use on the entity class once. The table name is required and may be supplied
 * via either {@link #table()} or {@link #value()}.
 * <p/>
 * Resolution order:
 * <ol>
 * <li>{@code table()}</li>
 * <li>{@code value()}</li>
 * </ol>
 * If neither is set, {@code JdbcEntityDefinitionFactory} throws a
 * {@code RepositoryException}.
 *
 * @author toddf
 * @since Feb 26, 2026
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Entity
{
	/**
	 * Alias for {@link #table()} for Morphia-style familiarity.
	 * Prefer {@link #table()} for clarity in relational code.
	 *
	 * @return mapped table name alias.
	 */
	public String value() default "";

	/**
	 * Physical database table name.
	 *
	 * @return mapped table name.
	 */
	public String table() default "";

	/**
	 * Optional schema/catalog qualifier used in generated table metadata.
	 * If empty, the table is unqualified.
	 *
	 * @return optional schema name.
	 */
	public String schema() default "";
}
