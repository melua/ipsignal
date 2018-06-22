package com.ipsignal.test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;

import com.ipsignal.automate.Automate;
import com.ipsignal.automate.Browser;
import com.ipsignal.entity.impl.LogEntity;
import com.ipsignal.entity.impl.SignalEntity;
import com.ipsignal.mock.automate.AutomateMock;

public class AutomateTest {
	
	@Test
	public void testLatency() {
		int latency = 15;
		String body = "<html><head><title>Title</title></head><body><h1>Test</h1><p>lorem ipsum</p></body></html>";
		Response response = Response.ok(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8))).build();
		SignalEntity signal = new SignalEntity(null, null, true, "https://localhost", Browser.CHROME.toString(), 30, latency, "//*", "/.*/", null, 720, 3);
		Automate automate = new AutomateMock(2000, 45, response, null);
		LogEntity log = automate.execute(signal, false);
		Assert.assertNotNull(log);
		Assert.assertEquals(LogEntity.LATENCY.getInstance(null, null, null, null, null, null, null, latency).getDetail(), log.getDetail());
	}
	
	@Test
	public void testTimeout() {
		String body = "<html><head><title>Title</title></head><body><h1>Test</h1><p>lorem ipsum</p></body></html>";
		Response response = Response.ok(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8))).build();
		SignalEntity signal = new SignalEntity(null, null, true, "https://localhost", Browser.CHROME.toString(), 30, 15, "//*", "/.*/", null, 720, 3);
		Automate automate = new AutomateMock(2000, 30, response, AutomateMock.Exception.SOCKET_TIMEOUT_EXCEPTION);
		LogEntity log = automate.execute(signal, false);
		Assert.assertNotNull(log);
		Assert.assertEquals(LogEntity.TIMEOUT.getInstance(null, null, null, null, null, null, null, 2000).getDetail(), log.getDetail());
	}
	
	@Test
	public void testUnknownHost() {
		String body = "<html><head><title>Title</title></head><body><h1>Test</h1><p>lorem ipsum</p></body></html>";
		Response response = Response.ok(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8))).build();
		SignalEntity signal = new SignalEntity(null, null, true, "https://wrongdomain.tld", Browser.CHROME.toString(), 30, 15, "//*", "/.*/", null, 720, 3);
		Automate automate = new AutomateMock(2000, 30, response, null);
		LogEntity log = automate.execute(signal, false);
		Assert.assertNotNull(log);
		Assert.assertEquals(LogEntity.UNKNOWNHOST.getInstance(null, null, null, null, null, null, null, "wrongdomain.tld").getDetail(), log.getDetail());
	}
	
	@Test
	public void testMatches() {
		String body = "<html><head><title>Title</title></head><body><h1>Test</h1><p>lorem ipsum</p></body></html>";
		Response response = Response.ok(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8))).build();
		SignalEntity signal = new SignalEntity(null, null, true, "https://localhost", Browser.CHROME.toString(), 30, 15, "//*", "/totopouet/", null, 720, 3);
		Automate automate = new AutomateMock(2000, 30, response, null);
		LogEntity log = automate.execute(signal, false);
		Assert.assertNotNull(log);
		Assert.assertEquals(LogEntity.MATCHES.getInstance(null, null, null, null, null, null, null, "totopouet").getDetail(), log.getDetail());
	}
	
	@Test
	public void testSsl() {
		String body = "<html><head><title>Title</title></head><body><h1>Test</h1><p>lorem ipsum</p></body></html>";
		Response response = Response.ok(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8))).build();
		SignalEntity signal = new SignalEntity(null, null, true, "https://localhost", Browser.CHROME.toString(), 30, 15, "//*", "/.*/", null, 720, 3);
		Automate automate = new AutomateMock(2000, 7, response, null);
		LogEntity log = automate.execute(signal, false);
		Assert.assertNotNull(log);
		Assert.assertEquals(LogEntity.SSL.getInstance(null, null, null, null, null, null, null, 30).getDetail(), log.getDetail());
	}
	
	@Test
	public void testHttp() {
		
	}
	
	@Test
	public void testReachability() {
		
	}
	
	@Test
	public void testPageSize() {
		
	}
	
	@Test
	public void testHtml() {
		
	}
	
	@Test
	public void testUrl() {
		
	}
	
	@Test
	public void testXPath() {
		
	}
	
	@Test
	public void testRegex() {
		
	}
	
	@Test
	public void testbrowser() {
		
	}
	
	@Test
	public void testSuccess() {
		
	}

}
