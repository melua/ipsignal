package com.ipsignal.automate.impl;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.cxf.jaxrs.client.WebClient;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ipsignal.Config;
import com.ipsignal.automate.Automate;
import com.ipsignal.automate.Browser;
import com.ipsignal.dao.LogDAO;
import com.ipsignal.dao.SignalDAO;
import com.ipsignal.dto.Restrictive;
import com.ipsignal.entity.impl.LogEntity;
import com.ipsignal.entity.impl.SignalEntity;
import com.ipsignal.mail.MailManager;
import com.ipsignal.mem.Memcached;

import lombok.Cleanup;
import lombok.extern.java.Log;

@Log
@Stateless
public class AutomateImpl implements Automate {

	public static final int TIMEOUT = 2000;
	public static final int MAX_BYTES = 2097152;
	private static final int BUFFER_BYTES = 4096;
	private static final int MAX_CHARS = 200;
	private static final int DAY = 86400000;
	private static final int LEAF = 0;

	@EJB
	private SignalDAO signals;
	@EJB
	private LogDAO logs;
	@EJB
	private MailManager mailer;
	@EJB
	private Memcached mem;

	public AutomateImpl() {
		// For injection
	}

	protected AutomateImpl(SignalDAO signals, LogDAO logs, MailManager mailer, Memcached mem) {
		// For tests
		this.signals = signals;
		this.logs = logs;
		this.mailer = mailer;
		this.mem = mem;
	}

	@Override
	public LogEntity execute(SignalEntity signal, boolean feedback) {

		// Default values
		Integer ping = -1;
		Integer ssl = null;

		// Host and URI
		URL url = null;
		InetAddress inetAddress = null;

		// Browser
		Browser browser = null;

		// Connections
		@Cleanup("disconnect")
		HttpsURLConnection conn = null;
		@Cleanup
		WebClient client = null;

		try {

			@Cleanup
			Socket soc = null;

			if (signal.getBrowser() != null) {
				browser = Browser.valueOf(signal.getBrowser());
			} else {
				browser = Browser.random();
			}

			url = new URL(signal.getUrl());
			inetAddress = InetAddress.getByName(url.getHost());
			boolean isHttps = "https".equals(url.getProtocol()) ? true : false;

			// First we ping
			soc = new Socket();
			long start = Calendar.getInstance().getTimeInMillis();
			this.doConnect(soc, new InetSocketAddress(inetAddress, isHttps ? 443 : 80), TIMEOUT);
			long end = Calendar.getInstance().getTimeInMillis();
			ping = (int) (end - start);
			
			// Then we get SSL/TLS certificate
			if (isHttps && signal.getCertificate() != null) {
		        long expiration = this.getExpirationTimestamp(conn, url);
		        long now = Calendar.getInstance().getTimeInMillis();
		        ssl = (int) ((expiration - now) / DAY);
			}
	        
	        // after we get the page
			client = WebClient.create(signal.getUrl())
					.replaceHeader(HttpHeaders.USER_AGENT, browser.getUserAgent())
					.accept(MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML_XML, MediaType.APPLICATION_XML);
			
			Response response = this.doGet(client);
			int status = response.getStatus();

			/*
			 *  check the status
			 */
			if (status < 200 || status > 399) {
				if (LOGGER.isLoggable(Level.FINE)) {
					LOGGER.log(Level.FINE, "Error with HTTP status: {0}", status);
				}
				return LogEntity.HTTP.getInstance(signal, ping, ssl, browser.toString(), status, null, null);
			}
			
	        // prepare I/O		
			InputStream input = (InputStream) response.getEntity();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			
			// read the data
			int bytes = 0;
			int length = 0;
			byte[] buffer = new byte[BUFFER_BYTES];
			while ((length = input.read(buffer)) != -1) {
				output.write(buffer, 0, length);
				bytes += length;
				/*
				 * prevent page too big
				 * or gzip bomb
				 */
				if (bytes > MAX_BYTES) {
					return LogEntity.PAGESIZE.getInstance(signal, ping, ssl, browser.toString(), status, null, null, MAX_BYTES);
				}
			}
			
			// retrieve sources
			String source = output.toString(Charset.defaultCharset().toString());

			// Prepare parser
			Document doc = this.doParse(source);

			XPath xpath = XPathFactory.newInstance().newXPath();
			String obtained = strip(xpath.evaluate(signal.getPath(), doc));

			/*
			 * check the regular expression if any
			 */
			if (signal.getExpected().length() > 2
					&& signal.getExpected().startsWith(Restrictive.REGEX_SEPARATOR) && signal.getExpected().endsWith(Restrictive.REGEX_SEPARATOR)) {

				String regex = signal.getExpected().substring(1, signal.getExpected().length()-1);

				if (!Pattern.matches(regex, obtained)) {
					return LogEntity.MATCHES.getInstance(signal, ping, ssl, browser.toString(), status, shorten(obtained), source, regex);
				}

			/*
			 * check text
			 */
			} else if (!signal.getExpected().equals(obtained)) {
				return LogEntity.EQUALS.getInstance(signal, ping, ssl, browser.toString(), status, shorten(obtained), source, signal.getExpected());
			}

			/*
			 * check certificate
			 */
			if (ssl != null && signal.getCertificate() != null && ssl <= signal.getCertificate()) {
				return LogEntity.SSL.getInstance(signal, ping, ssl, browser.toString(), status, shorten(obtained), null, signal.getCertificate());
			}

			/*
			 * check latency
			 */
			if (signal.getLatency() != null && ping > signal.getLatency()) {
				return LogEntity.LATENCY.getInstance(signal, ping, ssl, browser.toString(), status, shorten(obtained), null, signal.getLatency());
			}

			/*
			 * forced feedback
			 */
			if (feedback) {
				return LogEntity.SUCCESS.getInstance(signal, ping, ssl, browser.toString(), status, shorten(obtained), source);
			}

		} catch (MalformedURLException muex) {
			LOGGER.log(Level.WARNING, "Error with stored URL: {0}", muex.getMessage());
			return LogEntity.URL.getInstance(signal, ping, ssl, browser.toString(), null, null, null);
			
		} catch (UnknownHostException uhex) {
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.log(Level.FINE, "Error with host: {0}", uhex.getMessage());
			}
			return LogEntity.UNKNOWNHOST.getInstance(signal, ping, ssl, browser.toString(), null, null, null, url.getHost());

		} catch (SAXException sex) {
			LOGGER.log(Level.WARNING, "Error with parser: {0}", sex.getMessage());
			return LogEntity.PARSER.getInstance(signal, ping, ssl, browser.toString(), null, null, null);

		} catch (XPathExpressionException xpex) {
			LOGGER.log(Level.WARNING, "Error with stored xpath: {0}", xpex.getMessage());
			return LogEntity.XPATH.getInstance(signal, ping, ssl, browser.toString(), null, null, null);

		} catch (PatternSyntaxException psex) {
			LOGGER.log(Level.WARNING, "Error with stored regex: {0}", psex.getMessage());
			return LogEntity.REGEX.getInstance(signal, ping, ssl, browser.toString(), null, null, null);

		} catch (IllegalArgumentException iaex) {
			LOGGER.log(Level.WARNING, "Error with stored browser: {0}", iaex.getMessage());
			return LogEntity.BROWSER.getInstance(signal, ping, ssl, null, null, null, null);

		} catch (SocketTimeoutException stoex) {
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.log(Level.FINE, "Error with timeout: {0}", stoex.getMessage());
			}
			return LogEntity.TIMEOUT.getInstance(signal, ping, ssl, browser.toString(), null, null, null, TIMEOUT);

		} catch (IOException ioex) {
			LOGGER.log(Level.WARNING, "Error with I/O: {0}", ioex.getMessage());
			return LogEntity.REACHABILITY.getInstance(signal, ping, ssl, browser.toString(), null, null, null, inetAddress.getHostAddress());

		}

		// All is fine
		return null;
	}

	@Override
	public void executeAsync(SignalEntity signal) {
		// Execute signal
		LogEntity log = this.execute(signal, true);

		// Notify
		if (signal.getNotify() != null) {

			try {
				// Call GET
				URL url = new URL(signal.getNotify() + (signal.getNotify().endsWith("=") ? "" : "?id=") + log.getSignal().getId() + "/" + log.getId());
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.connect();

				if (LOGGER.isLoggable(Level.FINE)) {
					LOGGER.log(Level.FINE, "GET \"{1}\" {2}", new Object[] { url, conn.getResponseCode() });
				}

				conn.disconnect();

			} catch (MalformedURLException muex) {
				LOGGER.log(Level.WARNING, "Error with new URL: {0}", muex.getMessage());

			} catch (UnknownHostException uhe) {
				if (LOGGER.isLoggable(Level.FINE)) {
					LOGGER.log(Level.FINE, "Error with new domain name: {0}", uhe.getMessage());
				}

			} catch (IOException ioex) {
				LOGGER.log(Level.WARNING, "Error with I/O: {0}", ioex.getMessage());

			}

		} else {

			// Send email
			mailer.sendSignalNotification(signal, log);
		}

		// Add log
		signal.getLogs().add(log);

		// Persist in database
		logs.add(log);
		signals.update(signal);

		if (Config.MEMC_TIME != 0) {
			// Remove from cache
			mem.remove(signal.getId());
		}
	}

	private static String shorten(String value) {
		return value.length() > MAX_CHARS ? value.substring(0, MAX_CHARS-1).concat(" [...]") : value;
	}

	private static String strip(String value) {
		return value.replaceAll("[\t\r\n]+", " ").trim();
	}

	protected void doConnect(Socket socket, SocketAddress endpoint, int timeout) throws IOException {
		/* do not requires root privileges */
		socket.connect(endpoint, timeout);
	}

	protected Response doGet(WebClient client) throws IOException {
		return client.get();
	}

	protected Document doParse(String source) throws SAXException, IOException {
		DOMParser htmlParser = new DOMParser();
		htmlParser.parse(new InputSource(new StringReader(source)));
		return htmlParser.getDocument();
	}

	protected long getExpirationTimestamp(HttpsURLConnection conn, URL url) throws IOException {
		conn = (HttpsURLConnection) url.openConnection();
		conn.connect();
        Certificate[] chain = conn.getServerCertificates();
        X509Certificate cert = (X509Certificate) chain[LEAF];
        return cert.getNotAfter().getTime();
	}

}
