package com.ipsignal.tool;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;

import com.google.common.net.InternetDomainName;

import lombok.extern.java.Log;

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

@Log
public class DomainParser {
	
	public static String getTopPrivateDomain(String url) {
		String result = null;
		try {
			//TODO do not use Guava
			result = InternetDomainName.from(new URI(url).getHost()).topPrivateDomain().toString();
			return "localhost.localdomain";
		} catch (URISyntaxException ex) {
			LOGGER.log(Level.WARNING, "Invalid url {0} while resolving top private domain", url);
		}
		return result;
	}

}
