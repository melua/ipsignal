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
import java.util.logging.Logger;

public class TLVParser {
	
	private static final Logger LOGGER = Logger.getLogger(TLVParser.class.getName());
	private static final Charset CHARSET = StandardCharsets.UTF_8;
	private static final byte EXTENTED_BYTES = 0x00;

	public static String readTLV(byte[] tlv, byte... tags) {

		// Prevent bad input
		if (tlv == null || tlv.length < 1 || tags.length > 2) {
			return null;
		}
		
		// Convert tags byte array to short
		ByteBuffer buffer = ByteBuffer.allocate(2);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (byte b : tags) {
			buffer.put(b);
		}
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
	
	public static byte[] writeTLV(Integer value, byte... tags) {
		return writeTLV(String.valueOf(value), tags);
	}

}
