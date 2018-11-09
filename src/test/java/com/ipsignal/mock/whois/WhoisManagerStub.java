package com.ipsignal.mock.whois;

import java.util.Calendar;

import com.ipsignal.entity.impl.WhoisEntity;

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

import com.ipsignal.whois.WhoisManager;

public class WhoisManagerStub implements WhoisManager {

	@Override
	public void execute(WhoisEntity whois) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -10);
		whois.setCreated(cal.getTime());
		cal.add(Calendar.YEAR, 9);
		whois.setUpdated(cal.getTime());
		cal.add(Calendar.YEAR, 2);
		whois.setExpires(cal.getTime());
	}

}
