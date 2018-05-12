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
import com.ipsignal.entity.impl.SignalEntity;
import com.ipsignal.entity.impl.UserEntity;
import com.ipsignal.job.PurgePremiumJob;
import com.ipsignal.mail.MailManager;
import com.ipsignal.tool.UID;

@Stateless
public class PurgePremiumJobImpl implements PurgePremiumJob {

	private static final int HEXID_LENGTH = 16;
	private static final Logger LOGGER = Logger.getLogger(PurgePremiumJobImpl.class.getName());

	@EJB
	private UserDAO users;
	@EJB
	private MailManager mailer;

	public PurgePremiumJobImpl() {
		// For injection
	}

	protected PurgePremiumJobImpl(UserDAO users, MailManager mailer) {
		// For tests
		this.users = users;
		this.mailer = mailer;
	}

	@Override
	public void execute() {
		final String hexid = UID.randomUID(HEXID_LENGTH);

		final List<UserEntity> entities = users.findExpired();
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.log(Level.FINE, "Job [{0}] found {1} premiums to downgrade", new Object[] { hexid, entities.size() });
		}

		if (!Config.DISABLE_PURGES && !entities.isEmpty()) {
			long start = Calendar.getInstance().getTimeInMillis();

			for (UserEntity entity : entities) {

				entity.setPremium(null);

				for (SignalEntity signal : entity.getSignals()) {
					if (signal.getCertificate() != null && signal.getCertificate() > 7) {
						signal.setCertificate(7);
					}
					if (signal.getRetention() > 3) {
						signal.setRetention(3);
					}
					if (signal.getInterval() < 60) {
						signal.setInterval(60);
					}
				}

				users.update(entity);
			}

			long end = Calendar.getInstance().getTimeInMillis();
			long duration = (end - start) / 1000;
			LOGGER.log(Level.INFO, "Job [{0}] downgraded {1} premiums in {2} seconds", new Object[] { hexid, entities.size(), duration });
		}
	}
}
