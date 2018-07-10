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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ipsignal.dao.LogDAO;
import com.ipsignal.entity.impl.LogEntity;

public class LogDAOStub implements LogDAO {
	
	private Map<String, LogEntity> database = new HashMap<>();
	
	public static final LogEntity FIRST;
	public static final LogEntity SECOND;
	public static final LogEntity THIRD;
	public static final LogEntity EXPIRED;

	static {
		FIRST = LogEntity.EQUALS.getInstance(SignalDAOStub.THIRD, 15, 6, "FIREFOX_45", 200, "xyz", "<html>xyz</html>", "abc");
		SECOND = LogEntity.EQUALS.getInstance(SignalDAOStub.THIRD, 15, 6, "FIREFOX_45", 200, "xyz", "<html>xyz</html>", "abc");
		THIRD = LogEntity.EQUALS.getInstance(SignalDAOStub.THIRD, 15, 6, "FIREFOX_45", 200, "xyz", "<html>xyz</html>", "abc");
		EXPIRED = LogEntity.EQUALS.getInstance(SignalDAOStub.THIRD, 15, 6, "FIREFOX_45", 200, "xyz", "<html>xyz</html>", "abc");
		EXPIRED.setAccess(new Date(1514764800));
	}
	
	public LogDAOStub() {
		database.put(FIRST.getId(), FIRST);
		database.put(SECOND.getId(), SECOND);
		database.put(THIRD.getId(), THIRD);
		database.put(EXPIRED.getId(), EXPIRED);
	}

	@Override
	public void add(LogEntity entity) {
		System.out.println("Added log " + entity.getId() + " to database");
		database.put(entity.getId(), entity);
	}

	@Override
	public void update(LogEntity entity) {
		System.out.println("Updated log " + entity.getId() + " in database");
		database.put(entity.getId(), entity);
	}

	@Override
	public void delete(LogEntity entity) {
		System.out.println("Deleted log " + entity.getId() + " from database");
		database.remove(entity.getId());
	}

	@Override
	public LogEntity findById(String id) {
		System.out.println("Searching log " + id + " in database");
		return database.get(id);
	}

	@Override
	public List<LogEntity> findExpired() {
		System.out.println("Searching expired log in database");
		List<LogEntity> result = new ArrayList<>();
		result.add(EXPIRED);
		return result;
	}

}
