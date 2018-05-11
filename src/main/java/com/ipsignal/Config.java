package com.ipsignal;

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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.helpers.IOUtils;

public final class Config
{
	private static final Logger LOGGER = Logger.getLogger(Config.class.getName());
	private static final Properties PROPS = new Properties();
	
	public static final URI SERVICE_URL;
	public static final String READ_ONLY;
	public static final String SMTP_HOST;
	public static final int SMTP_PORT;
	public static final String MEMC_HOST;
	public static final int MEMC_PORT;
	public static final int MEMC_TIME;

	public static final boolean CHECK_ON_UPDATE;
	public static final boolean DISABLE_JOBS;
	public static final boolean DISABLE_PURGES;

	public static final String MAIL_CRUD_CREATE;
	public static final String MAIL_CRUD_UPDATE;
	public static final String MAIL_CRUD_DELETE;
	
	public static final String MAIL_ALERT_NOTIFICATION;
	public static final String MAIL_ALERT_CERTIFICATE;

	public static final String MAIL_PREMIUM_EXPIRED;
	public static final String MAIL_PREMIUM_SOON;

	public static final String HTML_UNSUBSCRIBE_NOTIFICATION;
	public static final String HTML_UNSUBSCRIBE_CERTIFICATE;
	
	static {
		loadProperties("/config.properties");
	
		SERVICE_URL = getURI("serve.url", "http://localhost");
		READ_ONLY = getString("read.uids", "");
		SMTP_HOST = getString("smtp.host", "localhost");
		SMTP_PORT = getInt("smtp.port", 25);
		MEMC_HOST = getString("memc.host", "localhost");
		MEMC_PORT = getInt("memc.port", 11211);
		MEMC_TIME = getInt("memc.time", 0);
		
		CHECK_ON_UPDATE = getBoolean("upd.check", false);
		DISABLE_JOBS = getBoolean("jobs.skip", false);
		DISABLE_PURGES = getBoolean("dele.skip", false);

		MAIL_CRUD_CREATE = getContent("/crud/create.txt");
		MAIL_CRUD_UPDATE = getContent("/crud/update.txt");
		MAIL_CRUD_DELETE = getContent("/crud/delete.txt");
		
		MAIL_ALERT_NOTIFICATION = getContent("/alert/notification.txt");
		MAIL_ALERT_CERTIFICATE = getContent("/alert/certificate.txt");
		
		MAIL_PREMIUM_EXPIRED = getContent("/premium/expired.txt");
		MAIL_PREMIUM_SOON = getContent("/premium/soon.txt");
		
		HTML_UNSUBSCRIBE_NOTIFICATION = getContent("/unsubscribe/notification.html");
		HTML_UNSUBSCRIBE_CERTIFICATE = getContent("/unsubscribe/certificate.html");
		
	}

	private Config() {
	}

	private static void loadProperties(String path) {
		try {
			PROPS.load(Service.class.getResourceAsStream(path));
		} catch (IOException ex) {
			LOGGER.log(Level.SEVERE, "Invalid properties {0}", path);
		}
	}
	
	private static String getValue(String key) {
		String value = PROPS.getProperty(key);
		return value != null ? value.trim() : null;
	}
	
	private static boolean getBoolean(String key, boolean defaultValue) {
		String value = getValue(key);

		if (value == null) {
			LOGGER.log(Level.WARNING, "Missing property for key {0} using default value {1}", new Object[]{key, defaultValue});
			return defaultValue;
		}
		
		if ("true".equalsIgnoreCase(value)) {
			return true;

		} else if ("false".equalsIgnoreCase(value)) {
			return false;

		} else {
			LOGGER.log(Level.WARNING, "Invalid value specified for key {0} specified value {1} should be boolean using default value {2}", new Object[]{key, value, defaultValue});
			return defaultValue;
		}
	}
	
	private static int getInt(String key, int defaultValue) {
		String value = getValue(key);
		if (value == null) {
			LOGGER.log(Level.WARNING, "Missing property for key {0} using default value {1}", new Object[]{key, defaultValue});
			return defaultValue;
		}
		
		try	{
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			LOGGER.log(Level.WARNING, "Invalid value specified for key {0} specified value {1} should be integer using default value {2}", new Object[]{key, value, defaultValue});
			return defaultValue;
		}
	}
	
	private static String getString(String key, String defaultValue) {
		String value = getValue(key);
		if (value == null) {
			LOGGER.log(Level.WARNING, "Missing property for key {0} using default value {1}", new Object[]{key, defaultValue});
			return defaultValue;
		}
		return value;
	}
	
	private static URI getURI(String key, String defaultValue) {
		URI def = null;
		try {
			def = new URI(defaultValue);
		} catch (URISyntaxException ex) {
			LOGGER.log(Level.SEVERE, "Invalid default value for key {0} value {1} should be URI", new Object[]{key, defaultValue});
		}
		
		String value = getValue(key);
		if (value == null) {
			LOGGER.log(Level.WARNING, "Missing property for key {0} using default value {1}", new Object[]{key, defaultValue});
			return def;
		}
		
		try	{
			return new URI(value);
		} catch (URISyntaxException e) {
			LOGGER.log(Level.WARNING, "Invalid value specified for key {0} specified value {1} should be URI using default value {2}", new Object[]{key, value, defaultValue});
			return def;
		}
	}
	
	private static String getContent(String path) {
		String value = null;
		try {		
			value = IOUtils.toString(Config.class.getResourceAsStream(path));
		} catch (IOException ioex) {
			LOGGER.log(Level.SEVERE, "Invalid content {0}", path);
		}
		return value;
	}
	
}