package com.ipsignal.mail;

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

import javax.ejb.Local;

import com.ipsignal.entity.impl.LogEntity;
import com.ipsignal.entity.impl.SignalEntity;
import com.ipsignal.entity.impl.UserEntity;

@Local(MailManager.class)
public interface MailManager {

	/**
	 * Send mail to validate creation
	 * @param entity signal
	 * @return true if successful, false otherwise
	 */
	Boolean sendSignalCreation(SignalEntity entity);
	
	/**
	 * Send mail about modification
	 * @param entity signal
	 * @param backup signal
	 * @return true if successful, false otherwise
	 */
	Boolean sendSignalModification(SignalEntity entity, SignalEntity backup);
	
	/**
	 * Send mail about deletion
	 * @param entity signal
	 * @return true if successful, false otherwise
	 */
	Boolean sendSignalDeletion(SignalEntity entity);

	/**
	 * Send mail to notify
	 * @param entity signal
	 * @param log result
	 */
	void sendSignalNotification(SignalEntity entity, LogEntity log);
	
	/**
	 * Send mail about certificate expiration
	 * @param entity signal
	 * @param log result
	 */
	void sendCertificateExpirationSoon(SignalEntity entity, LogEntity log);
	
	/**
	 * Send mail about premium expiration
	 * @param entity user
	 * @param days after expiration
	 */
	void sendPremiumExpiration(UserEntity entity, Integer days);
	
	/**
	 * Send mail about soon premium expiration
	 * @param entity user
	 * @param days before expiration
	 */
	void sendPremiumExpirateSoon(UserEntity entity, Integer days);
	
}