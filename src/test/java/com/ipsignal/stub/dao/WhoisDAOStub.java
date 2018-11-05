package com.ipsignal.stub.dao;

import java.util.HashMap;
import java.util.Map;

import com.ipsignal.dao.WhoisDAO;
import com.ipsignal.entity.impl.WhoisEntity;

public class WhoisDAOStub implements WhoisDAO {
	
	private Map<String, WhoisEntity> database = new HashMap<>();
	
	public static final WhoisEntity FIRST;
	public static final WhoisEntity SECOND;
	public static final WhoisEntity THIRD;

	static {
		//TODO
		FIRST = new WhoisEntity();
		SECOND = new WhoisEntity();
		THIRD = new WhoisEntity();
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
		// TODO Auto-generated method stub
		return null;
	}

}
