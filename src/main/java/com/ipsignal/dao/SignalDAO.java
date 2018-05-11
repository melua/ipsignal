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

import java.util.List;

import javax.ejb.Local;

import com.ipsignal.entity.impl.SignalEntity;

/**
 * Repository to manipulate signal entity
 * @author Kevin Guignard
 * @see DAO
 * @see com.ipsignal.entity.impl.SignalEntity
 */
@Local(SignalDAO.class)
public interface SignalDAO extends DAO<SignalEntity> {

	/**
	 * Retrieve a list of signals to be cleaned
	 * @return list of signals
	 */
	List<SignalEntity> findExpired();
	
	/**
	 * Retrieve a list of signals to be cleaned
	 * @param interval of the signal
	 * @return list of signals
	 */
	List<SignalEntity> findByInterval(Integer interval);

	/**
	 * Retrieve signal by its email
	 * @param email of the signal
	 * @param url of the signal
	 * @param path of the signal
	 * @return signal entity
	 */
	SignalEntity findByEmailAndUrlAndPath(String email, String url, String path);
	
	/**
	 * Retrieve the count of signal
	 * @return signal count
	 */
	Long findCount();

}
