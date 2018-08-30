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

import org.apache.commons.lang3.StringUtils;

public class IdFactory {

	private static final SecureRandom RAND = new SecureRandom();
	public static final String SEPARATOR = "-";

	private IdFactory() {
	}

	/**
	 * Generate an new ID based on the given hex sizes sequence
	 * separated by {@value #SEPARATOR} character
	 * @param blocks array of sizes
	 * @return a generated ID
	 */
	public static String generateId(int[] blocks) {
		String[] hexes = new String[blocks.length];
		
		for (int i = 0; i < blocks.length; i++) {
			hexes[i] = randomHex(blocks[i]);
		}
		
		return StringUtils.join(hexes, SEPARATOR);
	}

	/**
	 * Generate an new ID based on the given hex size
	 * @param block size
	 * @return a generated ID
	 */
	public static String generateId(int block) {
		return randomHex(block);
	}
	
	/**
	 * Generate a random hex block of the given size
	 * @param size of the block
	 * @return random hex block
	 */
	private static String randomHex(int size) {
		StringBuilder sb = new StringBuilder();
        while(sb.length() < size){
            sb.append(Integer.toHexString(RAND.nextInt()));
        }
        return sb.toString().substring(0, size);
	}

}
