package com.ipsignal.dao.impl;

import java.util.Calendar;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;

import com.ipsignal.dao.WhoisDAO;
import com.ipsignal.entity.impl.WhoisEntity;

@ApplicationScoped
public class WhoisDAOImpl implements WhoisDAO {
	
	private static final int EXPIRES_SOON_DAYS = 180;
	private static final int ACCESS_DELAY_DAYS = 31;
	
	@PersistenceContext(unitName = "ipsignal-unit", type = PersistenceContextType.TRANSACTION)
    EntityManager entityManager;

    @Override
	public void add(final WhoisEntity entity) {
        entityManager.persist(entity);
    }

    @Override
	public void update(final WhoisEntity entity) {
        entityManager.merge(entity);
    }

    @Override
	public void delete(final WhoisEntity entity) {
        entityManager.remove(entity);
    }

    @Override
	public WhoisEntity findById(final String id) {
    	return entityManager.find(WhoisEntity.class, id);
    }

	@Override
	public WhoisEntity findByDomain(String domain) {
		final TypedQuery<WhoisEntity> query = entityManager.createNamedQuery("Whois.findByDomain", WhoisEntity.class);
		query.setParameter("domain", domain);

		final List<WhoisEntity> result = query.getResultList();
		return result.isEmpty() ? null : result.get(0);
	}

	@Override
	public List<WhoisEntity> findWaiting() {
		final TypedQuery<WhoisEntity> query = entityManager.createNamedQuery("Whois.findWaiting", WhoisEntity.class);
		return query.getResultList();
	}

	@Override
	public List<WhoisEntity> findExpired() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, EXPIRES_SOON_DAYS);
		final TypedQuery<WhoisEntity> query = entityManager.createNamedQuery("Whois.findExpired", WhoisEntity.class);
		query.setParameter("expires", cal.getTime());
		cal.add(Calendar.DATE, - (EXPIRES_SOON_DAYS + ACCESS_DELAY_DAYS));
		query.setParameter("access", cal.getTime());
		return query.getResultList();
	}

}

