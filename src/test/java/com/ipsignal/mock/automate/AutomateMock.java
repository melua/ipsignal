package com.ipsignal.mock.automate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.cxf.jaxrs.client.WebClient;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

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

import com.ipsignal.automate.impl.AutomateImpl;
import com.ipsignal.mock.mail.MailManagerMock;
import com.ipsignal.stub.dao.LogDAOStub;
import com.ipsignal.stub.dao.SignalDAOStub;
import com.ipsignal.stub.mem.MemcachedStub;

public class AutomateMock extends AutomateImpl {
	
	public enum Exception {
		SOCKET_TIMEOUT_EXCEPTION,
		IO_EXCEPTION,
		SAX_EXCEPTION
	}
	
	private static final String BODY = "<html><head><title>Title</title></head><body><h1>Test</h1><p>lorem ipsum</p></body></html>";
	public static final Response RESPONSE200 = Response.ok(new ByteArrayInputStream(BODY.getBytes(StandardCharsets.UTF_8))).build();
	public static final Response RESPONSE404 = Response.status(Status.NOT_FOUND).entity(new ByteArrayInputStream(BODY.getBytes(StandardCharsets.UTF_8))).build();
	public static final Response RESPONSEHUGE = Response.ok(new ByteArrayInputStream(new byte[MAX_BYTES+1])).build();

	private final int latency;
	private final int expiration;
	private final Response response;
	private final Exception exception;

	public AutomateMock(int latency, int expiration, Response response, Exception exception) {
		super(new SignalDAOStub(), new LogDAOStub(), new MailManagerMock(), new MemcachedStub());
		this.latency = latency;
		this.expiration = expiration;
		this.response = response;
		this.exception = exception;
	}
	
	@Override
	protected void doConnect(Socket socket, SocketAddress endpoint, int timeout) throws IOException {
		if (Exception.SOCKET_TIMEOUT_EXCEPTION.equals(exception)) {
			throw new SocketTimeoutException();
		} else if (Exception.IO_EXCEPTION.equals(exception)) {
			throw new IOException();
		}
		try {
			Thread.sleep(this.latency);
		} catch (InterruptedException ex) {
		}
	}

	@Override
	protected Response doGet(WebClient client) throws IOException {
		return this.response;
	}

	@Override
	protected long getExpirationTimestamp(HttpsURLConnection conn, URL url) throws IOException {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, this.expiration);
		return calendar.getTimeInMillis();
	}

	@Override
	protected Document doParse(String source) throws SAXException, IOException {
		if (Exception.SAX_EXCEPTION.equals(exception)) {
			throw new SAXException();
		}
		return super.doParse(source);
	}

}
