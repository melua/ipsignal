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

import com.ipsignal.dao.UserDAO;
import com.ipsignal.entity.impl.UserEntity;

public class UserDAOStub implements UserDAO {
	
	private Map<String, UserEntity> database = new HashMap<>();
	
	public static final UserEntity STANDARD;
	public static final UserEntity PREMIUM;
	public static final UserEntity ALONE;
	public static final UserEntity EXPIRED;

	static {
		STANDARD = new UserEntity("user@example.com", null);
		PREMIUM = new UserEntity("user@example.com", new Date());
		ALONE = new UserEntity("alone@example.com", new Date(1514764800));
		EXPIRED = new UserEntity("expired@example.com", new Date(1514764800));
	}
	
	public UserDAOStub() {
		database.put(STANDARD.getId(), STANDARD);
		database.put(PREMIUM.getId(), PREMIUM);
		database.put(ALONE.getId(), ALONE);
		database.put(EXPIRED.getId(), EXPIRED);
	}

	@Override
	public void add(UserEntity entity) {
		System.out.println("Added user " + entity.getId() + " to database");
		database.put(entity.getId(), entity);
	}

	@Override
	public void update(UserEntity entity) {
		System.out.println("Updated user " + entity.getId() + " in database");
		database.put(entity.getId(), entity);	
	}

	@Override
	public void delete(UserEntity entity) {
		System.out.println("Deleted user " + entity.getId() + " from database");
		database.remove(entity.getId());	
	}

	@Override
	public UserEntity findById(String id) {
		System.out.println("Searching user " + id + " in database");
		return database.get(id);
	}

	@Override
	public UserEntity findByEmail(String email) {
		System.out.println("Searching user for " + email + " in database");
		return new UserEntity(email, null);
	}

	@Override
	public List<UserEntity> findByPremium(Date date) {
		System.out.println("Searching premium " + date);
		List<UserEntity> result = new ArrayList<>();
		result.add(PREMIUM);
		return result;
	}

	@Override
	public List<UserEntity> findAlone() {
		System.out.println("Searching alone premium");
		List<UserEntity> result = new ArrayList<>();
		result.add(ALONE);
		return result;
	}

	@Override
	public List<UserEntity> findExpired() {
		System.out.println("Searching expired premium");
		List<UserEntity> result = new ArrayList<>();
		result.add(EXPIRED);
		return result;
	}

}
