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

import com.ipsignal.dao.SignalDAO;
import com.ipsignal.entity.impl.SignalEntity;

public class SignalDAOStub implements SignalDAO {
	
	private Map<String, SignalEntity> database = new HashMap<>();
	
	public static final SignalEntity FIRST;
	public static final SignalEntity SECOND;
	public static final SignalEntity THIRD;
	public static final SignalEntity FOURTH;
	public static final SignalEntity FIFTH;
	public static final SignalEntity EXPIRED;

	static {
		FIRST = new SignalEntity(UserDAOStub.STANDARD, null, true, "http://example.com", "FIREFOX_45", 7, 15, "//*", "abc", null, 3600, 3);
		SECOND = new SignalEntity(UserDAOStub.PREMIUM, null, true, "http://example.com", "FIREFOX_45", 7, 15, "//*", "abc", null, 3600, 3);
		THIRD = new SignalEntity(UserDAOStub.STANDARD, null, true, "http://example.com", "FIREFOX_45", 7, 15, "//*", "abc", null, 3600, 3);
		FOURTH = new SignalEntity(UserDAOStub.STANDARD, null, true, "http://example.com", "FIREFOX_45", 7, 15, "//*", "abc", null, 3600, 3);
		FIFTH = new SignalEntity(UserDAOStub.STANDARD, null, true, "http://example.com", "FIREFOX_45", 7, 15, "//*", "abc", null, 3600, 3);
		EXPIRED = new SignalEntity(UserDAOStub.STANDARD, null, true, "http://example.com", "FIREFOX_45", 7, 15, "//*", "abc", null, 3600, 3);
		EXPIRED.setLastaccess(new Date(1514764800));
	}
	
	public SignalDAOStub() {
		database.put(FIRST.getId(), FIRST);
		database.put(SECOND.getId(), SECOND);
		database.put(THIRD.getId(), THIRD);
		database.put(FOURTH.getId(), FOURTH);
		database.put(FIFTH.getId(), FIFTH);
		database.put(EXPIRED.getId(), EXPIRED);
	}

	@Override
	public void add(SignalEntity entity) {
		System.out.println("Added signal " + entity.getId() + " to database");
		database.put(entity.getId(), entity);
	}

	@Override
	public void update(SignalEntity entity) {
		System.out.println("Updated signal " + entity.getId() + " in database");
		database.put(entity.getId(), entity);
	}

	@Override
	public void delete(SignalEntity entity) {
		System.out.println("Deleted signal " + entity.getId() + " from database");
		database.remove(entity.getId());
	}

	@Override
	public SignalEntity findById(String id) {
		System.out.println("Searching signal " + id + " in database");
		return database.get(id);
	}

	@Override
	public List<SignalEntity> findExpired() {
		System.out.println("Searching expired signal in database");
		List<SignalEntity> result = new ArrayList<>();
		result.add(EXPIRED);
		return result;
	}

	@Override
	public List<SignalEntity> findByInterval(Integer interval) {
		System.out.println("Searching signal for interval " + interval + " in database");
		List<SignalEntity> result = new ArrayList<>();
		result.add(new SignalEntity(UserDAOStub.STANDARD, null, true, "http://example.com", "FIREFOX_45", 7, 15, "//*", "abc", null, interval, 3));
		return result;
	}

	@Override
	public SignalEntity findByEmailAndUrlAndPath(String email, String url, String path) {
		System.out.println("Searching signal for " + email +  ", " + url + " and " + path + " in database");
		return null;
	}

	@Override
	public Long findCount() {
		System.out.println("Searching signal count in database");
		return 42L;
	}

}
