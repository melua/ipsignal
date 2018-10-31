package com.ipsignal.dao.impl;

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

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import com.ipsignal.dao.LogDAO;
import com.ipsignal.entity.impl.LogEntity;

@Stateless
public class LogDAOImpl implements LogDAO {
	
    @PersistenceContext(unitName = "ipsignal-unit", type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;

    @Override
	public void add(final LogEntity entity) {
        entityManager.persist(entity);
    }

    @Override
	public void update(final LogEntity entity) {
        entityManager.merge(entity);
    }

    @Override
	public void delete(final LogEntity entity) {
        entityManager.remove(entity);
    }

    @Override
	public LogEntity findById(final String id) {
    	return entityManager.find(LogEntity.class, id);
    }

}
