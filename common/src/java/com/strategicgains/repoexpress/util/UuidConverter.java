/*
    Copyright 2013, Strategic Gains, Inc.

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
package com.strategicgains.repoexpress.util;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Utility class to convert between a UUID and a short (22-character) string
 * representation of it (and back). Implements a very efficient URL-safe Base64
 * encoding/decoding algorithm to format/parse the UUID.
 * 
 * NOTE: There is NO WAY for this algorithm to detect an invalid short-form 
 *       UUID if it is 22 characters in length and composed of alpha-numeric
 *       characters! So be careful to not use the parse() method to check
 *       the short-form UUID string for validity.
 * 
 * @author toddf
 * @since Mar 13, 2013
 */
public abstract class UuidConverter
{
	// Varies from standard Base64 by the last two characters in this string ("-" and "_").
	// The standard characters are "+" and "/" respectively, but are not URL safe.
	private static final char[] C64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".toCharArray();
	private static final int[] I256 = new int[256];
	static
	{
		for (int i = 0; i < C64.length; i++)
		{
			I256[C64[i]] = i;
		}
	}
	private static final UUID ZERO_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
	private static final String ZERO_SHORT_ID = "0000000000000000000000";

	/**
	 * Given a UUID instance, return a short (22-character) string
	 * representation of it.
	 * 
	 * @param uuid a UUID instance.
	 * @return a short string representation of the UUID.
	 * @throws NullPointerException if the UUID instance is null.
	 * @throws IllegalArgumentException if the underlying UUID implementation is not 16 bytes.
	 */
	public static String format(UUID uuid)
	{
		if (uuid == null) throw new NullPointerException("Null UUID");

		if (ZERO_UUID.equals(uuid)) return ZERO_SHORT_ID;

		byte[] bytes = toByteArray(uuid);
		return encodeBase64(bytes);
	}

	/**
	 * Given a UUID representation (either a short or long form) string, return a
	 * UUID instance from it.
	 * <p/>
	 * If the uuidString is longer than our short, 22-character form (or 24 with padding),
	 * it is assumed to be a full-length 36-character UUID string.
	 * 
	 * @param uuidString a string representation of a UUID.
	 * @return a UUID instance
	 * @throws IllegalArgumentException if the uuidString is not a valid UUID representation.
	 * @throws NullPointerException if the uuidString is null.
	 */
	public static UUID parse(String uuidString)
	{
		if (uuidString == null) throw new NullPointerException("Null UUID string");

		if (uuidString.length() > 24)
		{
			return UUID.fromString(uuidString);
		}
		
		if (uuidString.length() < 22)
		{
			throw new IllegalArgumentException("Short UUID must be 22 characters: " + uuidString);
		}

		if (uuidString.length() > 22 && !"==".equals(uuidString.substring(22)))
		{
			throw new IllegalArgumentException("Invalid short UUID: " + uuidString);
		}

		if (ZERO_SHORT_ID.equals(uuidString)) return ZERO_UUID;

		byte[] bytes = decodeBase64(uuidString);
		ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
		bb.put(bytes, 0, 16);
		bb.clear();
		UUID result = new UUID(bb.getLong(), bb.getLong());

		if (ZERO_UUID.equals(result)) throw new IllegalArgumentException("Invalid short UUID: " + uuidString);

		return result;
	}

	/**
	 * Extracts the bytes from a UUID instance in MSB, LSB order.
	 * 
	 * @param uuid a UUID instance.
	 * @return the bytes from the UUID instance.
	 */
	private static byte[] toByteArray(UUID uuid)
	{
		ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
		bb.putLong(uuid.getMostSignificantBits());
		bb.putLong(uuid.getLeastSignificantBits());
		return bb.array();
	}

	/**
	 * Accepts a UUID byte array (of exactly 16 bytes) and base64 encodes it, using a URL-safe
	 * encoding scheme.  The resulting string will be 22 characters in length with no extra
	 * padding on the end (e.g. no "==" on the end).
	 * <p/>
	 * Base64 encoding essentially takes each three bytes from the array and converts them into
	 * four characters.  This implementation, not using padding, converts the last byte into two
	 * characters.
	 * 
	 * @param bytes a UUID byte array.
	 * @return a URL-safe base64-encoded string.
	 */
	private static String encodeBase64(byte[] bytes)
	{
		if (bytes == null) throw new NullPointerException("Null UUID byte array");
		if (bytes.length != 16) throw new IllegalArgumentException("UUID must be 16 bytes");
		
		// Output is always 22 characters.
		char[] chars = new char[22];
		
		int i = 0;
		int j = 0;
		
		while(i < 15)
		{
			// Get the next three bytes.
			int d = (bytes[i++] & 0xff) << 16 | (bytes[i++] & 0xff) << 8 | (bytes[i++] & 0xff);
			
			// Put them in these four characters
			chars[j++] = C64[(d >>> 18) & 0x3f];
			chars[j++] = C64[(d >>> 12) & 0x3f];
			chars[j++] = C64[(d >>> 6) & 0x3f];
			chars[j++] = C64[d & 0x3f];
		}
		
		// The last byte of the input gets put into two characters at the end of the string.
		int d = (bytes[i] & 0xff) << 10;
		chars[j++] = C64[d >> 12];
		chars[j++] = C64[(d >>> 6) & 0x3f];
		return new String(chars);
	}

	/**
	 * Base64 decodes a short, 22-character UUID string (or 24-characters with padding)
	 * into a byte array. The resulting byte array contains 16 bytes.
	 * <p/>
	 * Base64 decoding essentially takes each four characters from the string and converts
	 * them into three bytes. This implementation, not using padding, converts the final
	 * two characters into one byte.
	 * 
	 * @param s
	 * @return
	 */
	private static byte[] decodeBase64(String s)
	{
		if (s == null) throw new NullPointerException("Cannot decode null string");
		if (s.isEmpty() || (s.length() > 24)) throw new IllegalArgumentException("Invalid short UUID");

		// Output is always 16 bytes (UUID).
		byte[] bytes = new byte[16];
		int i = 0;
		int j = 0;

		while (i < 15)
		{
			// Get the next four characters.
			int d = I256[s.charAt(j++)] << 18 | I256[s.charAt(j++)] << 12 | I256[s.charAt(j++)] << 6 | I256[s.charAt(j++)];

			// Put them in these three bytes.
			bytes[i++] = (byte) (d >> 16);
			bytes[i++] = (byte) (d >> 8);
			bytes[i++] = (byte) d;
		}

		// Add the last two characters from the string into the last byte.
		bytes[i] = (byte) ((I256[s.charAt(j++)] << 18 | I256[s.charAt(j++)] << 12) >> 16);
		return bytes;
	}
}
