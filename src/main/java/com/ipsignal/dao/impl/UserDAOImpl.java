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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;

import com.ipsignal.dao.UserDAO;
import com.ipsignal.entity.impl.UserEntity;

@ApplicationScoped
public class UserDAOImpl implements UserDAO {
	
	private static final int GRACETIME_DAYS = 14;

    @PersistenceContext(unitName = "ipsignal-unit", type = PersistenceContextType.TRANSACTION)
    EntityManager entityManager;

    @Override
	public void add(final UserEntity entity) {
        entityManager.persist(entity);
    }

    @Override
	public void update(final UserEntity entity) {
        entityManager.merge(entity);
    }

    @Override
	public void delete(final UserEntity entity) {
        entityManager.remove(entity);
    }

    @Override
	public UserEntity findById(final String id) {
    	return entityManager.find(UserEntity.class, id);
    }

	@Override
	public UserEntity findByEmail(String email) {
		final TypedQuery<UserEntity> query = entityManager.createNamedQuery("User.findByEmail", UserEntity.class);
		query.setParameter("email", email);

		final List<UserEntity> result = query.getResultList();
		return result.isEmpty() ? null : result.get(0);
	}

	@Override
	public List<UserEntity> findByPremium(Date date) {
		final TypedQuery<UserEntity> query = entityManager.createNamedQuery("User.findByPremium", UserEntity.class);
		query.setParameter("date", date);
		
		return query.getResultList();
	}
	
	@Override
	public List<UserEntity> findAlone() {
		final TypedQuery<UserEntity> query = entityManager.createNamedQuery("User.findAlone", UserEntity.class);
		return query.getResultList();
	}

	@Override
	public List<UserEntity> findExpired() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -GRACETIME_DAYS);
		
		final TypedQuery<UserEntity> query = entityManager.createNamedQuery("User.findExpired", UserEntity.class);
		query.setParameter("min", cal.getTime());
		
		return query.getResultList();
	}

}

