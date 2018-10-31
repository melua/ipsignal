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

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Order implements Comparator<String> {

	private static final List<String> attributes = new LinkedList<>();

	/**
	 * Set the order of the attributes
	 * for the marshaler
	 */
	static {
		attributes.add("code");
		attributes.add("email");
		attributes.add("premium");
		attributes.add("notify");
		attributes.add("access");
		attributes.add("detail");
		attributes.add("browser");
		attributes.add("url");
		attributes.add("path");
		attributes.add("expected");
		attributes.add("latency");
		attributes.add("certificate");
		attributes.add("interval");
		attributes.add("retention");
		attributes.add("whois");
		attributes.add("created");
		attributes.add("updated");
		attributes.add("expires");
		attributes.add("http");
		attributes.add("obtained");
		attributes.add("source");
		attributes.add("logs");
		attributes.add("log");
		attributes.add("beacon");
	}

	@Override
	public int compare(String o1, String o2) {
		return attributes.indexOf(o1) - attributes.indexOf(o2);
	}

}
