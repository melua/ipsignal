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

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.ipsignal.Config;
import com.ipsignal.entity.impl.LogEntity;
import com.ipsignal.entity.impl.SignalEntity;
import com.ipsignal.entity.impl.UserEntity;
import com.ipsignal.mail.MailManager;
import com.ipsignal.tool.TemplateBuilder;

@Stateless
public class MailManagerImpl implements MailManager {

	private static final String BRAND_NAME = "IP Signal";
	private static final String DONOTREPLY = "noreply@ip-signal.com";
	private static final String ALERT = "alert@ip-signal.com";
	private static final String PREMIUM = "premium@ip-signal.com";
	public static final String NOTIFY_UNSUBSCRIBE_PATH = "/unsub/notif/";
	public static final String CERTIFICATE_UNSUBSCRIBE_PATH = "/unsub/certif/";

	private static final Logger LOGGER = Logger.getLogger(MailManagerImpl.class.getName());
	
	@Override
	public Boolean sendSignalCreation(final SignalEntity entity) {
		TemplateBuilder body = new TemplateBuilder(Config.MAIL_CRUD_CREATE);
		body.formatSignal(entity);
		body.formatLink(entity.getUuid());
		return send("Confirm your email address", body.toString(), DONOTREPLY, entity.getUser().getEmail(), null);
	}
	
	@Override
	public Boolean sendSignalDeletion(SignalEntity entity) {
		TemplateBuilder body = new TemplateBuilder(Config.MAIL_CRUD_DELETE);
		body.formatSignal(entity);
		body.formatLink(entity.getUuid());
		return send("Beacon deleted", body.toString(), DONOTREPLY, entity.getUser().getEmail(), null);
	}

	@Override
	public Boolean sendSignalModification(final SignalEntity entity, final SignalEntity backup) {
		TemplateBuilder body = new TemplateBuilder(Config.MAIL_CRUD_UPDATE);
		body.formatSignal(entity);
		body.formatLink(backup.getUuid());
		return send("Beacon modified", body.toString(), DONOTREPLY, entity.getUser().getEmail(), null);
	}

	@Override
	public void sendSignalNotification(final SignalEntity entity, final LogEntity log) {
		String unsubscribe = Config.SERVICE_URL + NOTIFY_UNSUBSCRIBE_PATH + entity.getUuid();
		TemplateBuilder body = new TemplateBuilder(Config.MAIL_ALERT_NOTIFICATION);
		body.formatLog(log);
		body.formatLink(entity.getUuid(), log.getUuid());
		body.formatUnsubscribe(unsubscribe);
		send("Beacon Alert", body.toString(), ALERT, entity.getUser().getEmail(), unsubscribe);
	}
	
	@Override
	public void sendCertificateExpirationSoon(final SignalEntity entity, final LogEntity log) {
		String unsubscribe = Config.SERVICE_URL + CERTIFICATE_UNSUBSCRIBE_PATH + entity.getUuid();
		TemplateBuilder body = new TemplateBuilder(Config.MAIL_ALERT_CERTIFICATE);
		body.formatSignal(entity);
		body.formatLink(entity.getUuid());
		body.formatUnsubscribe(unsubscribe);
		send("Your certificate expires soon", body.toString(), ALERT, entity.getUser().getEmail(), unsubscribe);
	}

	@Override
	public void sendPremiumExpiration(final UserEntity entity, final Integer days) {
		TemplateBuilder body = new TemplateBuilder(Config.MAIL_PREMIUM_EXPIRED);
		body.formatDays(days);
		send("Your premium expirated", body.toString(), PREMIUM, entity.getEmail(), null);
	}

	@Override
	public void sendPremiumExpirateSoon(final UserEntity entity, final Integer days) {
		TemplateBuilder body = new TemplateBuilder(Config.MAIL_PREMIUM_SOON);
		body.formatDays(days);
		send("Your premium expires soon", body.toString(), PREMIUM, entity.getEmail(), null);
	}

	protected Boolean send(final String title, final String body, final String sender, final String recipient, final String unsubscribe) {
		final Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", Config.SMTP_HOST);
		properties.setProperty("mail.smtp.port", String.valueOf(Config.SMTP_PORT));

		final Session session = Session.getDefaultInstance(properties);
		final MimeMessage message = new MimeMessage(session);

		try {
			message.setFrom(new InternetAddress(sender, BRAND_NAME, "UTF-8"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
			if (unsubscribe != null) {
				message.setHeader("List-Unsubscribe", "<" + unsubscribe + ">");
			}
			message.setSubject(title, "UTF-8");
			message.setContent(body, "text/plain; charset=utf-8");
			message.setSentDate(new Date());
			Transport.send(message);
			LOGGER.log(Level.INFO, "{0} has sent a mail to {1}", new String[]{sender, recipient});
			return true;
		} catch (MessagingException | UnsupportedEncodingException ex) {
			LOGGER.log(Level.SEVERE, "Error with message or encoding: {0}", ex.getMessage());
			return false;
		}
	}

}
