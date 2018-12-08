package com.ipsignal.tool;

/*
 * Copyright (C) 2017 Kevin Guignard
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

import lombok.extern.java.Log;

@Log
public class TLVParser {
	
	private static final Charset CHARSET = StandardCharsets.UTF_8;
	public static final byte EXTENTED_BYTES = 0x00;

	/**
	 * Read the byte and extract value for the given 1-byte or 2-bytes tag.
	 * From 0x01 (1) to 0xff (255) the tag is represented as one byte.
	 * From 0x0100 (256) to 0xffff (65535) the tag is represented as two bytes
	 * and must be given in {@link ByteOrder#BIG_ENDIAN} order.
	 * 
	 * @param tlv byte to read
	 * @param tags to search for
	 * @return value for the given tag
	 */
	public static String readTLV(byte[] tlv, byte... tags) {

		// Prevent bad input
		if (tlv == null || tlv.length < 1 || tags.length > 2) {
			return null;
		}
		
		// Convert tags byte array to short
		ByteBuffer buffer = ByteBuffer.allocate(2);
		if (tags.length < 2) {
			buffer.put((byte) 0x00);
		}
		buffer.put(tags);
		short tag = buffer.getShort(0);

		try (DataInputStream stream = new DataInputStream(new ByteArrayInputStream(tlv))) {

			while(stream.available() > 0) {
				
				// Read first byte or next 2 bytes if extended
				int type = stream.readUnsignedByte();
				if (type == EXTENTED_BYTES && stream.available() > 1) {
					type = stream.readUnsignedShort();
				}
				
				// Read first byte or next 2 bytes if extended
				int length = stream.readUnsignedByte();
				if (length == EXTENTED_BYTES && stream.available() > 1) {
					length = stream.readUnsignedShort();
				}
				
				// Read or skip value
				if (stream.available() >= length) {
					if (type == tag) {
						byte[] value = new byte[length];
						stream.readFully(value);
						return new String(value, CHARSET);
					} else {
						stream.skip(length);
					}
				}
			}
			
		} catch (IOException ioex) {
			LOGGER.log(Level.WARNING, "Error while parsing TLV: {0}", ioex.getMessage());
		}

		return null;
	}
	
	/**
	 * Write a Tag-Length-Value for the given tag and value,
	 * and store them as 1-byte or 2-bytes.
	 * From 0x01 (1) to 0xff (255) tag and length are represented as one byte.
	 * From 0x0100 (256) to 0xffff (65535) tag and length are represented as two bytes
	 * and must be given in {@link ByteOrder#BIG_ENDIAN} order. An extra
	 * {@link #EXTENTED_BYTES} byte is automatically added for 2-bytes tag and length.
	 * 
	 * @param value for the given tag
	 * @param tags to write
	 * @return byte in Tag-Length-Value representation
	 */
	public static byte[] writeTLV(String value, byte... tags) {
		byte[] bytes = value.getBytes(CHARSET);
		
		// Prepare tag and add extended mark if necessary
		ByteBuffer tbuffer = ByteBuffer.allocate(3);
		if (tags.length > 1) {
			tbuffer.put(EXTENTED_BYTES);
		}
		tbuffer.put(tags);
		tbuffer.flip();
		byte[] tag = new byte[tbuffer.limit()];
		tbuffer.get(tag, 0, tbuffer.limit());
		
		// Prepare length and add extended mark if necessary
		ByteBuffer lbuffer = ByteBuffer.allocate(3);
		if (bytes.length > 255) {
			lbuffer.put(EXTENTED_BYTES);
			lbuffer.putShort((short) bytes.length);
		} else {
			lbuffer.put((byte) bytes.length);
		}
		lbuffer.flip();
		byte[] length = new byte[lbuffer.limit()];
		lbuffer.get(length, 0, lbuffer.limit());
		
		// Create Tag-Length-Value with calculated size
		ByteBuffer buffer = ByteBuffer.allocate(tag.length + length.length + bytes.length);
		buffer.put(tag);
		buffer.put(length);
		buffer.put(bytes);
		return buffer.array();
	}
	
	/**
	 * Write a Tag-Length-Value for the given tag and integer value
	 * @see #writeTLV(String, byte...)
	 * 
	 * @param value for the given tag
	 * @param tags to write
	 * @return byte in Tag-Length-Value representation
	 */
	public static byte[] writeTLV(Integer value, byte... tags) {
		return writeTLV(String.valueOf(value), tags);
	}

}
