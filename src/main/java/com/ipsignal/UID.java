package com.ipsignal;

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
import java.util.Random;

public class UID {

	private static final String SEPARATOR = "-";

	private UID() {
	}

	public static String randomUID(int block, int... blocks) {
		
		SecureRandom rand = new SecureRandom();		
		StringBuilder uid = new StringBuilder();
		uid.append(randomHex(block, rand));
		
		for (Integer b : blocks) {
			uid.append(SEPARATOR);
			uid.append(randomHex(b, rand));
		}	
		
		return uid.toString();
	}
	
	private static String randomHex(int size, Random rnd) {
		StringBuilder sb = new StringBuilder();
        while(sb.length() < size){
            sb.append(Integer.toHexString(rnd.nextInt()));
        }
        return sb.toString().substring(0, size);
	}

}
