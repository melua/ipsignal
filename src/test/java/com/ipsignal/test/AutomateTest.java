package com.ipsignal.test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Assert;
import org.junit.Test;

import com.ipsignal.automate.Automate;
import com.ipsignal.entity.impl.LogEntity;
import com.ipsignal.entity.impl.SignalEntity;
import com.ipsignal.mock.automate.AutomateMock;

public class AutomateTest {
	
	@Test
	public void testLatency() {
		int latency = 15;
		SignalEntity signal = new SignalEntity(null, null, true, "https://localhost", null, 30, latency, "//*", "/.*/", null, 720, 3, null);
		Automate automate = new AutomateMock(2000, 45, AutomateMock.RESPONSE200, null);
		LogEntity log = automate.execute(signal, false);
		Assert.assertNotNull(log);
		Assert.assertEquals(LogEntity.LATENCY.getInstance(null, null, null, null, null, null, null, latency).getDetail(), log.getDetail());
	}
	
	@Test
	public void testTimeout() {
		int timeout = AutomateMock.TIMEOUT;
		SignalEntity signal = new SignalEntity(null, null, true, "https://localhost", null, 30, 15, "//*", "/.*/", null, 720, 3, null);
		Automate automate = new AutomateMock(2000, 30, AutomateMock.RESPONSE200, AutomateMock.Exception.SOCKET_TIMEOUT_EXCEPTION);
		LogEntity log = automate.execute(signal, false);
		Assert.assertNotNull(log);
		Assert.assertEquals(LogEntity.TIMEOUT.getInstance(null, null, null, null, null, null, null, timeout).getDetail(), log.getDetail());
	}
	
	@Test
	public void testUnknownHost() {
		String host = "wrongdomain.tld";
		SignalEntity signal = new SignalEntity(null, null, true, "https://" + host, null, 30, 15, "//*", "/.*/", null, 720, 3, null);
		Automate automate = new AutomateMock(2000, 30, AutomateMock.RESPONSE200, null);
		LogEntity log = automate.execute(signal, false);
		Assert.assertNotNull(log);
		Assert.assertEquals(LogEntity.UNKNOWNHOST.getInstance(null, null, null, null, null, null, null, host).getDetail(), log.getDetail());
	}

	@Test
	public void testEquals() {
		String value = "totopouet";
		SignalEntity signal = new SignalEntity(null, null, true, "https://localhost", null, 30, 15, "//*", value, null, 720, 3, null);
		Automate automate = new AutomateMock(2000, 30, AutomateMock.RESPONSE200, null);
		LogEntity log = automate.execute(signal, false);
		Assert.assertNotNull(log);
		Assert.assertEquals(LogEntity.EQUALS.getInstance(null, null, null, null, null, null, null, value).getDetail(), log.getDetail());
	}
	
	@Test
	public void testMatches() {
		String matches = "totopouet";
		SignalEntity signal = new SignalEntity(null, null, true, "https://localhost", null, 30, 15, "//*", "/" + matches + "/", null, 720, 3, null);
		Automate automate = new AutomateMock(2000, 30, AutomateMock.RESPONSE200, null);
		LogEntity log = automate.execute(signal, false);
		Assert.assertNotNull(log);
		Assert.assertEquals(LogEntity.MATCHES.getInstance(null, null, null, null, null, null, null, matches).getDetail(), log.getDetail());
	}
	
	@Test
	public void testSsl() {
		int ssl = 30;
		SignalEntity signal = new SignalEntity(null, null, true, "https://localhost", null, ssl, 15, "//*", "/.*/", null, 720, 3, null);
		Automate automate = new AutomateMock(2000, 7, AutomateMock.RESPONSE200, null);
		LogEntity log = automate.execute(signal, false);
		Assert.assertNotNull(log);
		Assert.assertEquals(LogEntity.SSL.getInstance(null, null, null, null, null, null, null, ssl).getDetail(), log.getDetail());
	}
	
	@Test
	public void testHttp() {
		int http = 404;
		SignalEntity signal = new SignalEntity(null, null, true, "https://localhost", null, 30, 15, "//*", "/.*/", null, 720, 3, null);
		Automate automate = new AutomateMock(2000, 30, AutomateMock.RESPONSE404, null);
		LogEntity log = automate.execute(signal, false);
		Assert.assertNotNull(log);
		Assert.assertEquals(LogEntity.HTTP.getInstance(null, null, null, null, http, null, null).getDetail(), log.getDetail());
	}
	
	@Test
	public void testReachability() throws UnknownHostException {
		String ip = InetAddress.getByName("www.example.com").getHostAddress();
		SignalEntity signal = new SignalEntity(null, null, true, "https://www.example.com", null, 30, 15, "//*", "/.*/", null, 720, 3, null);
		Automate automate = new AutomateMock(2000, 30, AutomateMock.RESPONSE200, AutomateMock.Exception.IO_EXCEPTION);
		LogEntity log = automate.execute(signal, false);
		Assert.assertNotNull(log);
		Assert.assertEquals(LogEntity.REACHABILITY.getInstance(null, null, null, null, null, null, null, ip).getDetail(), log.getDetail());
	}
	
	@Test
	public void testPageSize() {
		int max = AutomateMock.MAX_BYTES;
		SignalEntity signal = new SignalEntity(null, null, true, "https://www.example.com", null, 30, 15, "//*", "/.*/", null, 720, 3, null);
		Automate automate = new AutomateMock(2000, 30, AutomateMock.RESPONSEHUGE, null);
		LogEntity log = automate.execute(signal, false);
		Assert.assertNotNull(log);
		Assert.assertEquals(LogEntity.PAGESIZE.getInstance(null, null, null, null, null, null, null, max).getDetail(), log.getDetail());
	}
	
	@Test
	public void testParser() {
		SignalEntity signal = new SignalEntity(null, null, true, "https://localhost", null, 30, 15, "//*", "/.*/", null, 720, 3, null);
		Automate automate = new AutomateMock(2000, 30, AutomateMock.RESPONSE200, AutomateMock.Exception.SAX_EXCEPTION);
		LogEntity log = automate.execute(signal, false);
		Assert.assertNotNull(log);
		Assert.assertEquals(LogEntity.PARSER.getInstance(null, null, null, null, null, null, null).getDetail(), log.getDetail());
	}
	
	@Test
	public void testUrl() {
		String url = "hppts://localhost";
		SignalEntity signal = new SignalEntity(null, null, true, url, null, 30, 15, "//*", "/.*/", null, 720, 3, null);
		Automate automate = new AutomateMock(2000, 30, AutomateMock.RESPONSE200, null);
		LogEntity log = automate.execute(signal, false);
		Assert.assertNotNull(log);
		Assert.assertEquals(LogEntity.URL.getInstance(null, null, null, null, null, null, null).getDetail(), log.getDetail());
	}
	
	@Test
	public void testXPath() {
		String xpath = "[[";
		SignalEntity signal = new SignalEntity(null, null, true, "https://localhost", null, 30, 15, xpath, "/.*/", null, 720, 3, null);
		Automate automate = new AutomateMock(2000, 30, AutomateMock.RESPONSE200, null);
		LogEntity log = automate.execute(signal, false);
		Assert.assertNotNull(log);
		Assert.assertEquals(LogEntity.XPATH.getInstance(null, null, null, null, null, null, null).getDetail(), log.getDetail());
	}
	
	@Test
	public void testRegex() {
		String regex = "/[[/";
		SignalEntity signal = new SignalEntity(null, null, true, "https://localhost", null, 30, 15, "//*", regex, null, 720, 3, null);
		Automate automate = new AutomateMock(2000, 30, AutomateMock.RESPONSE200, null);
		LogEntity log = automate.execute(signal, false);
		Assert.assertNotNull(log);
		Assert.assertEquals(LogEntity.REGEX.getInstance(null, null, null, null, null, null, null).getDetail(), log.getDetail());
	}
	
	@Test
	public void testbrowser() {
		String browser = "VIVALDI";
		SignalEntity signal = new SignalEntity(null, null, true, "https://localhost", browser, 30, 15, "//*", "/.*/", null, 720, 3, null);
		Automate automate = new AutomateMock(2000, 30, AutomateMock.RESPONSE200, null);
		LogEntity log = automate.execute(signal, false);
		Assert.assertNotNull(log);
		Assert.assertEquals(LogEntity.BROWSER.getInstance(null, null, null, null, null, null, null).getDetail(), log.getDetail());
	}
	
	@Test
	public void testSuccess() {
		boolean feedback = true;
		SignalEntity signal = new SignalEntity(null, null, true, "https://localhost", null, 30, 15, "//*", "/.*/", null, 720, 3, null);
		Automate automate = new AutomateMock(10, 45, AutomateMock.RESPONSE200, null);
		LogEntity log = automate.execute(signal, feedback);
		Assert.assertNotNull(log);
		Assert.assertEquals(LogEntity.SUCCESS.getInstance(null, null, null, null, null, null, null).getDetail(), log.getDetail());
	}

}
