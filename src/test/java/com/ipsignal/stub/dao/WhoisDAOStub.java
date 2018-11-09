package com.ipsignal.stub.dao;

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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.FastDateFormat;

import com.ipsignal.dao.WhoisDAO;
import com.ipsignal.entity.impl.WhoisEntity;

public class WhoisDAOStub implements WhoisDAO {
	
	private static final FastDateFormat FORMATTER = FastDateFormat.getInstance("yyyy-MM-dd");
	private Map<String, WhoisEntity> database = new HashMap<>();
	
	public static WhoisEntity FIRST;
	public static WhoisEntity SECOND;
	public static WhoisEntity THIRD;

	static {
		try {
			FIRST = new WhoisEntity("domain1.com", FORMATTER.parse("2009-10-03"), FORMATTER.parse("2017-10-03"), FORMATTER.parse("2019-10-03"));
			FIRST.setAccess(FORMATTER.parse("2018-11-08"));
			SECOND = new WhoisEntity("domain2.com", FORMATTER.parse("2012-08-12"), FORMATTER.parse("2018-08-12"), FORMATTER.parse("2019-08-12"));
			SECOND.setAccess(FORMATTER.parse("2018-10-29"));
			THIRD = new WhoisEntity("domain3.com", FORMATTER.parse("2016-03-15"), FORMATTER.parse("2018-03-15"), FORMATTER.parse("2020-03-15"));
			THIRD.setAccess(FORMATTER.parse("2018-11-12"));
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
	}
	
	public WhoisDAOStub() {
		database.put(FIRST.getId(), FIRST);
		database.put(SECOND.getId(), SECOND);
		database.put(THIRD.getId(), THIRD);
	}

	@Override
	public void add(WhoisEntity entity) {
		System.out.println("Added log " + entity.getId() + " to database");
		database.put(entity.getId(), entity);
	}

	@Override
	public void update(WhoisEntity entity) {
		System.out.println("Updated log " + entity.getId() + " in database");
		database.put(entity.getId(), entity);
	}

	@Override
	public void delete(WhoisEntity entity) {
		System.out.println("Deleted log " + entity.getId() + " from database");
		database.remove(entity.getId());
	}

	@Override
	public WhoisEntity findById(String id) {
		System.out.println("Searching log " + id + " in database");
		return database.get(id);
	}

	@Override
	public WhoisEntity findByDomain(String domain) {
		try {
			return new WhoisEntity("domain3.com", FORMATTER.parse("2014-03-15"), FORMATTER.parse("2018-09-15"), FORMATTER.parse("2018-10-29"));
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public List<WhoisEntity> findWaiting() {
		List<WhoisEntity> result = new LinkedList<>();
		result.add(new WhoisEntity("waitingdomain.com"));
		result.add(new WhoisEntity("anotherwaitingdomain.com"));
		return result;
	}

	@Override
	public List<WhoisEntity> findExpired() {
		return new ArrayList<WhoisEntity>(database.values());
	}

}
