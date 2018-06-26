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
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TLVParser {
	
	private static final Logger LOGGER = Logger.getLogger(TLVParser.class.getName());
	private static final Charset CHARSET = StandardCharsets.UTF_8;

	public static String readTLV(byte[] tlv, byte tag) {

		if (tlv == null || tlv.length < 1) {
			return null;
		}

		try (ByteArrayInputStream stream = new ByteArrayInputStream(tlv)) {

			int type = 0;
			int lenght = 0;
			while ((type = stream.read()) != -1) {
				if ((lenght = stream.read()) != -1) {
					byte[] value = new byte[lenght];
					stream.read(value);
					if (type == tag) {
						return new String(value, CHARSET);
					}
				}
			}

		} catch (IOException ex) {
			LOGGER.log(Level.WARNING, "Error while parsing TLV");
		}

		return null;
	}
	
	public static byte[] writeTLV(String value, byte tag) {
		byte[] bytes = value.getBytes(CHARSET);
		ByteBuffer buffer = ByteBuffer.allocate(2 + bytes.length);
		buffer.put(tag);
		buffer.put((byte) bytes.length);
		buffer.put(bytes);
		return buffer.array();
	}
	
	public static byte[] writeTLV(Integer value, byte tag) {
		return writeTLV(String.valueOf(value), tag);
	}

}
