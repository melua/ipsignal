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
import com.ipsignal.UID;
import com.ipsignal.dao.UserDAO;
import com.ipsignal.entity.impl.UserEntity;
import com.ipsignal.job.PurgeUserJob;

@Stateless
public class PurgeUserJobImpl implements PurgeUserJob {
	
	private static final int HEXID_LENGTH = 16;
	private static final Logger LOGGER = Logger.getLogger(PurgeUserJobImpl.class.getName());
	
	@EJB
	private UserDAO users;

	public PurgeUserJobImpl() {
		// For injection
	}

	protected PurgeUserJobImpl(UserDAO users) {
		// For tests
		this.users = users;
	}

	@Override
	public void execute() {
		final String hexid = UID.randomUID(HEXID_LENGTH);

		final List<UserEntity> entities = users.findAlone();
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.log(Level.FINE, "Job [{0}] found {1} users to purge", new Object[] { hexid, entities.size() });
		}

		if (!Config.DISABLE_PURGES && !entities.isEmpty()) {
			long start = Calendar.getInstance().getTimeInMillis();

			for (UserEntity entity : entities) {
				// Delete user
				users.delete(entity);
			}

			long end = Calendar.getInstance().getTimeInMillis();
			long duration = (end - start) / 1000;
			LOGGER.log(Level.INFO, "Job [{0}] purged {1} users in {2} seconds", new Object[] { hexid, entities.size(), duration });
		}
	}
}
