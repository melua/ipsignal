package com.ipsignal.test;

import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ipsignal.Config;
import com.ipsignal.automate.Browser;
import com.ipsignal.dto.impl.GenericDTO;
import com.ipsignal.dto.impl.SignalDTO;
import com.ipsignal.mapper.SignalMapper;
import com.ipsignal.mock.mapper.SignalMapperMock;
import com.ipsignal.mock.resource.SignalResourceMock;
import com.ipsignal.resource.SignalResource;
import com.ipsignal.stub.dao.LogDAOStub;
import com.ipsignal.stub.dao.SignalDAOStub;

public class SignalResourceTest {
	
	private static SignalResource resource;
	private static SignalMapper mapper;
	private String browser;
	private Integer certificate;
	private String email;
	private String expected;
	private Integer interval;
	private Integer latency;
	private String notify;
	private String path;
	private Integer retention;
	private String url;

	@BeforeClass
	public static void init() throws Exception {
		resource = new SignalResourceMock();
		mapper = new SignalMapperMock();
	}
	
	@Before	
	public void setUp() {
		browser = Browser.CHROME.toString();
		certificate = 7;
		email = "test@example.com";
		expected = RandomStringUtils.randomAlphanumeric(10);
		interval = 60;
		latency = 15;
		notify = "http://www.example.com/notify";
		path = RandomStringUtils.randomAlphanumeric(30);
		retention = 3;
		url = "http://www.example.com";
	}
	
	@Test
	public void testCreate1() {
		SignalDTO dto = new SignalDTO(url, certificate, latency, path, expected, email, browser, null, notify, interval, retention);
		
		Response response = resource.create(dto);
		
		Assert.assertNotNull(response);
		Assert.assertEquals(200, response.getStatus());
		Assert.assertTrue(response.getEntity() instanceof GenericDTO);
		
		GenericDTO res = (GenericDTO) response.getEntity();
		Assert.assertEquals(GenericDTO.OBJECTCREATED.getCode(), res.getCode());
		Assert.assertNotNull(res.getDetail());
		Assert.assertNotNull(res.getBeacon());
		
		Assert.assertEquals(dto.getUrl(), res.getBeacon().getUrl());
		Assert.assertEquals(dto.getEmail(), res.getBeacon().getEmail());
		Assert.assertEquals(dto.getNotify(), res.getBeacon().getNotify());
		Assert.assertEquals(dto.getCertificate(), res.getBeacon().getCertificate());
		Assert.assertEquals(dto.getLatency(), res.getBeacon().getLatency());
		Assert.assertEquals(dto.getPath(), res.getBeacon().getPath());
		Assert.assertEquals(dto.getExpected(), res.getBeacon().getExpected());
		Assert.assertEquals(dto.getBrowser(), res.getBeacon().getBrowser());
		Assert.assertEquals(dto.getInterval(), res.getBeacon().getInterval());
		Assert.assertEquals(dto.getRetention(), res.getBeacon().getRetention());
	}
	
	@Test
	public void testCreate2() {
		
		SignalDTO dto = new SignalDTO(url, certificate, latency, path, expected, email, browser, null, notify, interval, retention);
		
		byte[] data = mapper.DtoToTlv(dto);
		
		System.out.println(DatatypeConverter.printHexBinary(data));
		
		Response response = resource.create(data);
		
		Assert.assertNotNull(response);
		Assert.assertEquals(200, response.getStatus());
		Assert.assertTrue(response.getEntity() instanceof GenericDTO);
		
		GenericDTO res = (GenericDTO) response.getEntity();
		Assert.assertEquals(GenericDTO.OBJECTCREATED.getCode(), res.getCode());
		Assert.assertNotNull(res.getDetail());
		Assert.assertNotNull(res.getBeacon());
		
		Assert.assertEquals(url, res.getBeacon().getUrl());
		Assert.assertEquals(email, res.getBeacon().getEmail());
		Assert.assertEquals(notify, res.getBeacon().getNotify());
		Assert.assertEquals(certificate, res.getBeacon().getCertificate());
		Assert.assertEquals(latency, res.getBeacon().getLatency());
		Assert.assertEquals(path, res.getBeacon().getPath());
		Assert.assertEquals(expected, res.getBeacon().getExpected());
		Assert.assertEquals(browser, res.getBeacon().getBrowser());
		Assert.assertEquals(interval, res.getBeacon().getInterval());
		Assert.assertEquals(retention, res.getBeacon().getRetention());
	}
	
	@Test
	public void testDeleteById() {
		Response response = resource.deleteById(SignalDAOStub.FIRST.getUuid());
		
		Assert.assertNotNull(response);
		Assert.assertEquals(200, response.getStatus());
		Assert.assertTrue(response.getEntity() instanceof GenericDTO);
		
		GenericDTO res = (GenericDTO) response.getEntity();
		Assert.assertEquals(GenericDTO.OBJECTDISABLED.getCode(), res.getCode());
		Assert.assertNotNull(res.getDetail());
		Assert.assertNotNull(res.getBeacon());
	}
	
	@Test
	public void testGetById1() {
		Response response = resource.getById(SignalDAOStub.THIRD.getUuid());
		
		Assert.assertNotNull(response);
		Assert.assertEquals(200, response.getStatus());
		Assert.assertTrue(response.getEntity() instanceof SignalDTO);
		
		SignalDTO res = (SignalDTO) response.getEntity();
		Assert.assertEquals(SignalDAOStub.THIRD.getUrl(), res.getUrl());
		Assert.assertEquals(SignalDAOStub.THIRD.getCertificate(), res.getCertificate());
		Assert.assertEquals(SignalDAOStub.THIRD.getLatency(), res.getLatency());
		Assert.assertEquals(SignalDAOStub.THIRD.getPath(), res.getPath());
		Assert.assertEquals(SignalDAOStub.THIRD.getExpected(), res.getExpected());
		Assert.assertEquals(SignalDAOStub.THIRD.getUser().getEmail(), res.getEmail());
		Assert.assertEquals(SignalDAOStub.THIRD.getBrowser(), res.getBrowser());
		Assert.assertEquals(SignalDAOStub.THIRD.getUser().getPremium(),res.getPremium());
		Assert.assertEquals(SignalDAOStub.THIRD.getNotify(), res.getNotify());
		Assert.assertEquals(SignalDAOStub.THIRD.getInterval(), res.getInterval());
		Assert.assertEquals(SignalDAOStub.THIRD.getRetention(), res.getRetention());
	}
	
	@Test
	public void testGetById2() {
		Response response = resource.getById(SignalDAOStub.THIRD.getUuid(), LogDAOStub.FIRST.getUuid());
		
		Assert.assertNotNull(response);
		Assert.assertEquals(200, response.getStatus());
		Assert.assertTrue(response.getEntity() instanceof String);
		
		String res = (String) response.getEntity();
		Assert.assertEquals(LogDAOStub.FIRST.getSource(), res);
	}
	
	@Test
	public void testUpdateById() {
		SignalDTO dto = new SignalDTO(url, certificate, latency, path, expected, email, browser, null, notify, interval, retention);
		
		Response response = resource.updateById(SignalDAOStub.SECOND.getUuid(), dto);
		
		Assert.assertNotNull(response);
		Assert.assertEquals(200, response.getStatus());
		Assert.assertTrue(response.getEntity() instanceof GenericDTO);
		
		GenericDTO res = (GenericDTO) response.getEntity();
		Assert.assertEquals(GenericDTO.OBJECTUPDATED.getCode(), res.getCode());
		Assert.assertNotNull(res.getDetail());
		Assert.assertNotNull(res.getBeacon());
			
		Assert.assertNotEquals(dto.getPremium(), res.getBeacon().getPremium());		
		Assert.assertNotEquals(dto.getUrl(), res.getBeacon().getUrl());
		Assert.assertNotEquals(dto.getEmail(), res.getBeacon().getEmail());
		Assert.assertNotEquals(dto.getNotify(), res.getBeacon().getNotify());
		Assert.assertEquals(dto.getCertificate(), res.getBeacon().getCertificate());
		Assert.assertEquals(dto.getLatency(), res.getBeacon().getLatency());
		Assert.assertEquals(dto.getPath(), res.getBeacon().getPath());
		Assert.assertEquals(dto.getExpected(), res.getBeacon().getExpected());
		Assert.assertEquals(dto.getBrowser(), res.getBeacon().getBrowser());
		Assert.assertEquals(dto.getInterval(), res.getBeacon().getInterval());
		Assert.assertEquals(dto.getRetention(), res.getBeacon().getRetention());
	}
	
	@Test
	public void testGetCount() {
		Response response = resource.getCount();
		
		Assert.assertNotNull(response);
		Assert.assertEquals(200, response.getStatus());
		Assert.assertTrue(response.getEntity() instanceof Long);
		Assert.assertTrue(((Long) response.getEntity()) > 0);
	}
	
	@Test
	public void testUnsubscribeCertification() {
		Response response = resource.unsubscribeCertification(SignalDAOStub.FOURTH.getUuid());
		
		Assert.assertNotNull(response);
		Assert.assertEquals(200, response.getStatus());
		Assert.assertTrue(response.getEntity() instanceof String);
		
		String res = (String) response.getEntity();
		Assert.assertTrue(res.contains(SignalDAOStub.FOURTH.getUuid()));
		Assert.assertTrue(res.contains(Config.SERVICE_URL.toString()));
	}
	
	@Test
	public void testUnsubscribeNotification() {
		Response response = resource.unsubscribeNotification(SignalDAOStub.FIFTH.getUuid());
		
		Assert.assertNotNull(response);
		Assert.assertEquals(200, response.getStatus());
		Assert.assertTrue(response.getEntity() instanceof String);
		
		String res = (String) response.getEntity();
		Assert.assertTrue(res.contains(SignalDAOStub.FIFTH.getUuid()));
		Assert.assertTrue(res.contains(Config.SERVICE_URL + "/" + SignalDAOStub.FIFTH.getUuid()));
	}
}
