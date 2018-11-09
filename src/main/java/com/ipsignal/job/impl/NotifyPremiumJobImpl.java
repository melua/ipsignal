package com.ipsignal.job.impl;

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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.ipsignal.Config;
import com.ipsignal.dao.UserDAO;
import com.ipsignal.entity.impl.UserEntity;
import com.ipsignal.job.NotifyPremiumJob;
import com.ipsignal.mail.MailManager;
import com.ipsignal.tool.IdFactory;

@Stateless
public class NotifyPremiumJobImpl implements NotifyPremiumJob {

	private static final int HEXID_LENGTH = 16;
	private static final Logger LOGGER = Logger.getLogger(NotifyPremiumJobImpl.class.getName());

	@EJB
	private UserDAO users;
	@EJB
	private MailManager mailer;

	public NotifyPremiumJobImpl() {
		// For injection
	}

	@Override
	public void execute(Integer days) {
		final String hexid = IdFactory.generateId(HEXID_LENGTH);

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -days);

		final List<UserEntity> entities = users.findByPremium(cal.getTime());
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.log(Level.FINE, "Job [{0}] found {1} premiums to notify about expiration", new Object[] { hexid, entities.size() });
		}

		if (!Config.DISABLE_JOBS && !entities.isEmpty()) {
			long start = Calendar.getInstance().getTimeInMillis();

			for (UserEntity user : entities) {

				if (days > 0) {
					mailer.sendPremiumExpirateSoon(user, days);
				} else {
					mailer.sendPremiumExpiration(user, days);
				}

			}

			long end = Calendar.getInstance().getTimeInMillis();
			long duration = (end - start) / 1000;
			LOGGER.log(Level.INFO, "Job [{0}] notified {1} premiums in {2} seconds", new Object[] { hexid, entities.size(), duration });
		}
	}
}
