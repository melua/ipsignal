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
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;

import com.ipsignal.dao.SignalDAO;
import com.ipsignal.entity.impl.SignalEntity;

@Stateless
public class SignalDAOImpl implements SignalDAO {

	private static final int EXPIRATION_DAYS = 15;

    @PersistenceContext(unitName = "ipsignal-unit", type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;

    @Override
	public void add(final SignalEntity entity) {
        entityManager.persist(entity);
    }

    @Override
	public void update(final SignalEntity entity) {
        entityManager.merge(entity);
    }

    @Override
	public void delete(final SignalEntity entity) {
        entityManager.remove(entity);
    }

    @Override
	public SignalEntity findById(final String uuid) {
    	return entityManager.find(SignalEntity.class, uuid);
    }

	@Override
	public List<SignalEntity> findExpired() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -EXPIRATION_DAYS);

		final TypedQuery<SignalEntity> query = entityManager.createNamedQuery("Signal.findExpired", SignalEntity.class);
		query.setParameter("min", cal.getTime());

		return query.getResultList();
	}
	
	@Override
	public List<SignalEntity> findByInterval(Integer interval) {
		final TypedQuery<SignalEntity> query = entityManager.createNamedQuery("Signal.findByInterval", SignalEntity.class);
		query.setParameter("interval", interval);

		return query.getResultList();
	}

    @Override
	public SignalEntity findByEmailAndUrlAndPath(final String email, final String url, final String path) {
		final TypedQuery<SignalEntity> query = entityManager.createNamedQuery("Signal.findByEmailAndUrlAndPath", SignalEntity.class);
        query.setParameter("email", email);
        query.setParameter("url", url);
		query.setParameter("path", path);
        
		final List<SignalEntity> result = query.getResultList();
		return result.isEmpty() ? null : result.get(0);
    }

	@Override
	public Long findCount() {
		final TypedQuery<Long> query = entityManager.createNamedQuery("Signal.findCount", Long.class);
		return query.getSingleResult();
	}

}

