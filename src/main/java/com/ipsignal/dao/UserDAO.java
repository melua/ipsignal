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

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.ipsignal.entity.impl.UserEntity;

/**
 * Repository to manipulate user entity
 * @author Kevin Guignard
 * @see DAO
 * @see com.ipsignal.entity.impl.UserEntity
 */
@Local(UserDAO.class)
public interface UserDAO extends DAO<UserEntity> {
	
	/**
	 * Retrieve user by its email
	 * @param email of the user
	 * @return user entity
	 */
	UserEntity findByEmail(String email);
	
	/**
	 * Retrieve by premium
	 * @param date expires
	 * @return list of users
	 */
	List<UserEntity> findByPremium(Date date);

	/**
	 * Retrieve user without signals
	 * and expired premium
	 * @return list of users
	 */
	List<UserEntity> findAlone();
	
	/**
	 * Retrieve expired premium
	 * @return list of users
	 */
	List<UserEntity> findExpired();
	
}
