package com.ipsignal.mock.automate;

import java.io.ByteArrayInputStream;

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
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;

import com.ipsignal.automate.impl.AutomateImpl;
import com.ipsignal.mock.mail.MailManagerMock;
import com.ipsignal.stub.dao.LogDAOStub;
import com.ipsignal.stub.dao.SignalDAOStub;
import com.ipsignal.stub.mem.MemcachedStub;

public class AutomateMock extends AutomateImpl {

	public AutomateMock() {
		super(new SignalDAOStub(), new LogDAOStub(), new MailManagerMock(), new MemcachedStub());
	}

	@Override
	protected void doConnect(Socket socket, SocketAddress endpoint, int timeout) throws IOException {
		try {
			Thread.sleep(150);
		} catch (InterruptedException ex) {
		}
	}

	@Override
	protected Response doGet(WebClient client) throws IOException {
		String body = "<html><head><title>Title</title></head><body><h1>Test</h1><p>lorem ipsum</p></body></html>";
		return Response.ok(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8))).build();
	}

	@Override
	protected long getExpirationTimestamp(HttpsURLConnection conn, URL url) throws IOException {
		return 25;
	}

}
