package com.ipsignal.dao;

import java.util.List;

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

import com.ipsignal.entity.impl.WhoisEntity;

/**
 * Repository to manipulate whois entity
 * @author Kevin Guignard
 * @see DAO
 * @see com.ipsignal.entity.impl.WhoisEntity
 */
public interface WhoisDAO extends DAO<WhoisEntity> {
	
	/**
	 * Retrieve a whois by domain
	 * @param domain to search for
	 * @return WhoisEntity
	 */
	WhoisEntity findByDomain(String domain);
	
	/**
	 * Retrieve a list of whois
	 * waiting to be sets
	 * @return list of WhoisEntity
	 */
	List<WhoisEntity> findWaiting();
	
	/**
	 * Retrieve expired whois
	 * @return list of whois
	 */
	List<WhoisEntity> findExpired();

}
