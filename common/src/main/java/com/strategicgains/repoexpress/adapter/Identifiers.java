/*
    Copyright 2014, Strategic Gains, Inc.

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
package com.strategicgains.repoexpress.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.restexpress.common.util.StringUtils;

import com.strategicgains.repoexpress.domain.Identifier;

/**
 * An abstract class with static singleton references to common ID adapters.
 * 
 * @author toddf
 * @since Jan 24, 2014
 */
public interface Identifiers
{
	public static final String SEPARATOR = ":";

	public static final UuidAdapter UUID = new UuidAdapter();
	public static final StringToIntegerIdAdapter INTEGER = new StringToIntegerIdAdapter();
	public static final StringToLongIdAdapter LONG = new StringToLongIdAdapter();

	public static String format(Identifier id)
	{
		return format(id, SEPARATOR);
	}

	public static String format(Identifier id, String separator)
	{
		List<String> components = new ArrayList<>(id.size());

		id.components().stream().forEach(c -> {
			if (c instanceof UUID) components.add(UUID.format((UUID) c));
			else components.add(c.toString());
		});

		return StringUtils.join(separator, components);
	}

	public static void useShortUUID()
	{
		UUID.useShortUUID(true);
	}
}
