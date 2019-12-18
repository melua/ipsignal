package com.ipsignal.mail.impl;

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

import java.util.logging.Level;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ipsignal.Config;
import com.ipsignal.entity.impl.LogEntity;
import com.ipsignal.entity.impl.SignalEntity;
import com.ipsignal.entity.impl.UserEntity;
import com.ipsignal.mail.MailManager;
import com.ipsignal.tool.TemplateBuilder;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.ReactiveMailer;
import lombok.extern.java.Log;

@Log
@ApplicationScoped
public class MailManagerImpl implements MailManager {

	public static final String NOTIFY_UNSUBSCRIBE_PATH = "/unsub/notif/";
	public static final String CERTIFICATE_UNSUBSCRIBE_PATH = "/unsub/certif/";
	
	ReactiveMailer mailer;
	Config config;
	
	@Inject
	public MailManagerImpl(ReactiveMailer mailer, Config config) {
		this.mailer = mailer;
		this.config = config;
	}

	@Override
	public Boolean sendSignalCreation(final SignalEntity entity) {
		TemplateBuilder body = new TemplateBuilder(config.getCrudCreateContent(), config);
		body.formatSignal(entity);
		body.formatLink(entity.getId());
		return send("Confirm your email address.", body.toString(), config.getNoReplyMail(), entity.getUser().getEmail(), null);
	}
	
	@Override
	public Boolean sendSignalDeletion(SignalEntity entity) {
		TemplateBuilder body = new TemplateBuilder(config.getCrudeDeleteContent(), config);
		body.formatSignal(entity);
		body.formatLink(entity.getId());
		return send("Beacon deleted.", body.toString(), config.getNoReplyMail(), entity.getUser().getEmail(), null);
	}

	@Override
	public Boolean sendSignalModification(final SignalEntity entity, final SignalEntity backup) {
		TemplateBuilder body = new TemplateBuilder(config.getCrudUpdateContent(), config);
		body.formatSignal(entity);
		body.formatLink(backup.getId());
		return send("Beacon modified.", body.toString(), config.getNoReplyMail(), entity.getUser().getEmail(), null);
	}

	@Override
	public void sendSignalNotification(final SignalEntity entity, final LogEntity log) {
		String unsubscribe = config.getServiceUrl() + NOTIFY_UNSUBSCRIBE_PATH + entity.getId();
		TemplateBuilder body = new TemplateBuilder(config.getAlertNotificationContent(), config);
		body.formatLog(log);
		if (log.getSource() != null) {
			body.formatLink(entity.getId(), log.getId());
		} else {
			body.formatLink(entity.getId());
		}
		body.formatUnsubscribe(unsubscribe);
		send("Beacon alert: " + log.getDetail(), body.toString(), config.getAlertMail(), entity.getUser().getEmail(), unsubscribe);
	}
	
	@Override
	public void sendCertificateExpirationSoon(final SignalEntity entity, final LogEntity log) {
		String unsubscribe = config.getServiceUrl() + CERTIFICATE_UNSUBSCRIBE_PATH + entity.getId();
		TemplateBuilder body = new TemplateBuilder(config.getAlertCertificateContent(), config);
		body.formatDomain(entity.getUrl());
		body.formatDays(log.getCertificate());
		body.formatLink(entity.getId());
		body.formatUnsubscribe(unsubscribe);
		send("Your certificate expires in " + log.getCertificate() + " days.", body.toString(), config.getAlertMail(), entity.getUser().getEmail(), unsubscribe);
	}

	@Override
	public void sendPremiumExpiration(final UserEntity entity, final Integer days) {
		TemplateBuilder body = new TemplateBuilder(config.getPremiumExpiredContent(), config);
		body.formatDays(days);
		send("Your premium expirated.", body.toString(), config.getPremiumMail(), entity.getEmail(), null);
	}

	@Override
	public void sendPremiumExpirateSoon(final UserEntity entity, final Integer days) {
		TemplateBuilder body = new TemplateBuilder(config.getPremiumSoonContent(), config);
		body.formatDays(days);
		send("Your premium expires soon.", body.toString(), config.getPremiumMail(), entity.getEmail(), null);
	}

	/**
	 * Prepare mail for SMTP and call {@link #send(MimeMessage)}
	 * @param title
	 * @param body
	 * @param sender mail address
	 * @param recipient mail address
	 * @param unsubscribe link
	 * @return true if successful, false otherwise
	 */
	private Boolean send(final String title, final String body, final String sender, final String recipient, final String unsubscribe) {
		
		try {
			Mail mail = Mail.withText(recipient, title, body);
			mail.setFrom(config.getBrandName() + "<" +  sender + ">");
			if (unsubscribe != null) {
				mail.addHeader("List-Unsubscribe", "<" + unsubscribe + ">");
			}
			send(mail);
			LOGGER.log(Level.INFO, "{0} has sent a mail to {1}", new String[]{sender, recipient});
			return true;
		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, "Error with message or encoding: {0}", ex.getMessage());
			return false;
		}
	}

	/**
	 * Send the message to the mail server
	 * @param mail
	 */
	protected void send(final Mail mail) {
		mailer.send(mail);
	}

}
