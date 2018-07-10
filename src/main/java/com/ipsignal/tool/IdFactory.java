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

import java.security.SecureRandom;

public class IdFactory {

	private static final SecureRandom RAND = new SecureRandom();
	private static final String SEPARATOR = "-";

	private IdFactory() {
	}

	public static String generateId(int block, int... blocks) {
		
		StringBuilder builder = new StringBuilder();
		builder.append(randomHex(block));
		
		for (Integer b : blocks) {
			builder.append(SEPARATOR);
			builder.append(randomHex(b));
		}	
		
		return builder.toString();
	}
	
	private static String randomHex(int size) {
		StringBuilder sb = new StringBuilder();
        while(sb.length() < size){
            sb.append(Integer.toHexString(RAND.nextInt()));
        }
        return sb.toString().substring(0, size);
	}

}
