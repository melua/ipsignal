package com.ipsignal.stub.dao;

import java.util.HashMap;
import java.util.Map;

import com.ipsignal.dao.LogDAO;
import com.ipsignal.entity.impl.LogEntity;

public class LogDAOStub implements LogDAO {
	
	private Map<String, LogEntity> database = new HashMap<>();
	
	public static final LogEntity FIRST;
	public static final LogEntity SECOND;
	public static final LogEntity THIRD;

	static {
		FIRST = LogEntity.EQUALS.getInstance(SignalDAOStub.THIRD, 15, 6, "FIREFOX_45", 200, "xyz", "<html>xyz</html>", "abc");
		SECOND = LogEntity.EQUALS.getInstance(SignalDAOStub.THIRD, 15, 6, "FIREFOX_45", 200, "xyz", "<html>xyz</html>", "abc");
		THIRD = LogEntity.EQUALS.getInstance(SignalDAOStub.THIRD, 15, 6, "FIREFOX_45", 200, "xyz", "<html>xyz</html>", "abc");
	}
	
	public LogDAOStub() {
		database.put(FIRST.getId(), FIRST);
		database.put(SECOND.getId(), SECOND);
		database.put(THIRD.getId(), THIRD);
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

}
