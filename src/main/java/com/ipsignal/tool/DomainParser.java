package com.ipsignal.tool;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.net.InternetDomainName;

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

public class DomainParser {
	
	private static final Logger LOGGER = Logger.getLogger(DomainParser.class.getName());
	
	public static String getTopPrivateDomain(String url) {
		String result = null;
		try {
			result = InternetDomainName.from(new URI(url).getHost()).topPrivateDomain().toString();
		} catch (URISyntaxException ex) {
			LOGGER.log(Level.WARNING, "Invalid url {0} while resolving top private domain", url);
		}
		return result;
	}

}
