package com.ipsignal.dao;

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

import com.ipsignal.entity.Entity;

/**
 * Repository design pattern to manipulate model object
 * @author Kevin Guignard
 *
 * @param <T> entity to manipulate
 */
public interface DAO<T extends Entity> {

	/**
	 * Create entity into persistence
	 * @param entity
	 */
	void add(T entity);

	/**
	 * Merge entity from persistence
	 * @param entity
	 */
	void update(T entity);

	/**
	 * Remove entity from persistence
	 * @param entity
	 */
	void delete(T entity);

	/**
	 * Retrieve an entity by its primary key
	 * @param uuid
	 * @return entity if any, null otherwise
	 */
	T findById(String uuid);

}
