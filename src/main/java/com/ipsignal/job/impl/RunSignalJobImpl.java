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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ipsignal.Config;
import com.ipsignal.automate.Automate;
import com.ipsignal.dao.LogDAO;
import com.ipsignal.dao.SignalDAO;
import com.ipsignal.entity.impl.LogEntity;
import com.ipsignal.entity.impl.SignalEntity;
import com.ipsignal.job.RunSignalJob;
import com.ipsignal.mail.MailManager;
import com.ipsignal.tool.IdFactory;

import lombok.extern.java.Log;

@Log
@ApplicationScoped
public class RunSignalJobImpl implements RunSignalJob {
	
	private static final int HEXID_LENGTH = 16;
	
	SignalDAO signals;
	LogDAO logs;
	MailManager mailer;
	Automate automate;
	Config config;
	
	@Inject
	public RunSignalJobImpl(SignalDAO signals, LogDAO logs, MailManager mailer, Automate automate, Config config) {
		this.signals = signals;
		this.logs = logs;
		this.mailer = mailer;
		this.automate = automate;
		this.config = config;
	}

	@Override
	public void execute(Integer interval) {
		final String hexid = IdFactory.generateId(HEXID_LENGTH);

		final List<SignalEntity> entities = signals.findByInterval(interval);
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.log(Level.FINE, "Job [{0}] for interval {1} found {2} signals to process", new Object[] { hexid, interval, entities.size() });
		}

		if (!config.isDisableJobs() && !entities.isEmpty()) {

			long start = Calendar.getInstance().getTimeInMillis();

			// Copy collection before sorting
			List<SignalEntity> copy = new ArrayList<>(entities);

			// Sort premiums first
			Collections.sort(copy, Collections.reverseOrder());

			for (SignalEntity entity : copy) {

				// Execute signal
				LogEntity log = automate.execute(entity, false);

				if (log != null) {
					
					// Notify
					if (entity.getNotify() != null) {

						try {
							// Call GET
							URL url = new URL(entity.getNotify() + (entity.getNotify().endsWith("=") ? "" : "?id=") + log.getSignal().getId() + "/" + log.getId());
							HttpURLConnection conn = (HttpURLConnection) url.openConnection();
							conn.connect();

							if (LOGGER.isLoggable(Level.FINE)) {
								LOGGER.log(Level.FINE, "Job [{0}] GET \"{1}\" {2}", new Object[] { hexid, url, conn.getResponseCode() });
							}
							
							conn.disconnect();

						} catch (MalformedURLException muex) {
							LOGGER.log(Level.WARNING, "Error with stored URL: {0}", muex.getMessage());

						} catch (UnknownHostException uhe) {
							if (LOGGER.isLoggable(Level.FINE)) {
								LOGGER.log(Level.FINE, "Error with stored domain name: {0}", uhe.getMessage());
							}

						} catch (IOException ioex) {
							LOGGER.log(Level.WARNING, "Error with I/O: {0}", ioex.getMessage());

						}
						
					} else {

						// Send email
						mailer.sendSignalNotification(entity, log);

						// Only send a mail each 5 days above 7 days
						if (log.getCertificate() != null && entity.getCertificate() != null && log.getCertificate() <= entity.getCertificate()
								&& (log.getCertificate() == entity.getCertificate() || log.getCertificate() <= 7 || log.getCertificate() % 5 == 0)) {

							mailer.sendCertificateExpirationSoon(entity, log);
						}
					}
					
					// Add log
					entity.getLogs().add(log);

					// Persist in database
					logs.add(log);
					signals.update(entity);
					
					// Purge logs
					if (entity.getLogs().size() > entity.getRetention()) {
						Collections.sort(entity.getLogs());
						Iterator<LogEntity> it = entity.getLogs().iterator();
						while(entity.getLogs().size() > entity.getRetention()) {
							LogEntity obj = it.next();
							logs.delete(obj);
							it.remove();
						}
					}
				}
			}

			long end = Calendar.getInstance().getTimeInMillis();
			long duration = (end - start) / 1000;
			LOGGER.log(Level.INFO, "Job [{0}] for interval {1} processed {2} signals in {3} seconds", new Object[] { hexid, interval, copy.size(), duration });
		}
	}
}
