package com.ipsignal.test;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class SignalResourceTest {
	
	@Inject
	static Config config;
	
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

	@BeforeAll
	public static void init() throws Exception {
		resource = new SignalResourceMock(config);
		mapper = new SignalMapperMock(config);
	}
	
	@BeforeEach
	public void setUp() {
		browser = Browser.CHROME.toString();
		certificate = 7;
		email = "test@example.com";
		expected = RandomStringUtils.random(255);
		interval = 60;
		latency = 15;
		notify = "http://www.example.com/notify";
		path = RandomStringUtils.random(255);
		retention = 3;
		url = "http://www.example.com";
	}
	
	@Test
	public void testCreate1() {
		SignalDTO dto = new SignalDTO(url, certificate, latency, path, expected, email, browser, notify, interval, retention, null, null, null);
		
		Response response = resource.create(dto);
		
		Assertions.assertNotNull(response);
		Assertions.assertEquals(200, response.getStatus());
		Assertions.assertTrue(response.getEntity() instanceof GenericDTO);
		
		GenericDTO res = (GenericDTO) response.getEntity();
		Assertions.assertEquals(GenericDTO.OBJECTCREATED.getCode(), res.getCode());
		Assertions.assertNotNull(res.getDetail());
		Assertions.assertNotNull(res.getBeacon());
		
		Assertions.assertEquals(dto.getUrl(), res.getBeacon().getUrl());
		Assertions.assertEquals(dto.getEmail(), res.getBeacon().getEmail());
		Assertions.assertEquals(dto.getNotify(), res.getBeacon().getNotify());
		Assertions.assertEquals(dto.getCertificate(), res.getBeacon().getCertificate());
		Assertions.assertEquals(dto.getLatency(), res.getBeacon().getLatency());
		Assertions.assertEquals(dto.getPath(), res.getBeacon().getPath());
		Assertions.assertEquals(dto.getExpected(), res.getBeacon().getExpected());
		Assertions.assertEquals(dto.getBrowser(), res.getBeacon().getBrowser());
		Assertions.assertEquals(dto.getInterval(), res.getBeacon().getInterval());
		Assertions.assertEquals(dto.getRetention(), res.getBeacon().getRetention());
	}
	
	@Test
	public void testCreate2() {
		
		SignalDTO dto = new SignalDTO(url, certificate, latency, path, expected, email, browser, notify, interval, retention, null, null, null);
		
		byte[] data = mapper.DtoToTlv(dto);
		
		System.out.println(DatatypeConverter.printHexBinary(data));
		
		Response response = resource.create(data);
		
		Assertions.assertNotNull(response);
		Assertions.assertEquals(200, response.getStatus());
		Assertions.assertTrue(response.getEntity() instanceof GenericDTO);
		
		GenericDTO res = (GenericDTO) response.getEntity();
		Assertions.assertEquals(GenericDTO.OBJECTCREATED.getCode(), res.getCode());
		Assertions.assertNotNull(res.getDetail());
		Assertions.assertNotNull(res.getBeacon());
		
		Assertions.assertEquals(url, res.getBeacon().getUrl());
		Assertions.assertEquals(email, res.getBeacon().getEmail());
		Assertions.assertEquals(notify, res.getBeacon().getNotify());
		Assertions.assertEquals(certificate, res.getBeacon().getCertificate());
		Assertions.assertEquals(latency, res.getBeacon().getLatency());
		Assertions.assertEquals(path, res.getBeacon().getPath());
		Assertions.assertEquals(expected, res.getBeacon().getExpected());
		Assertions.assertEquals(browser, res.getBeacon().getBrowser());
		Assertions.assertEquals(interval, res.getBeacon().getInterval());
		Assertions.assertEquals(retention, res.getBeacon().getRetention());
	}
	
	@Test
	public void testDeleteById() {
		Response response = resource.deleteById(SignalDAOStub.FIRST.getId());
		
		Assertions.assertNotNull(response);
		Assertions.assertEquals(200, response.getStatus());
		Assertions.assertTrue(response.getEntity() instanceof GenericDTO);
		
		GenericDTO res = (GenericDTO) response.getEntity();
		Assertions.assertEquals(GenericDTO.OBJECTDISABLED.getCode(), res.getCode());
		Assertions.assertNotNull(res.getDetail());
		Assertions.assertNotNull(res.getBeacon());
	}
	
	@Test
	public void testGetById1() {
		Response response = resource.getById(SignalDAOStub.THIRD.getId());
		
		Assertions.assertNotNull(response);
		Assertions.assertEquals(200, response.getStatus());
		Assertions.assertTrue(response.getEntity() instanceof SignalDTO);
		
		SignalDTO res = (SignalDTO) response.getEntity();
		Assertions.assertEquals(SignalDAOStub.THIRD.getUrl(), res.getUrl());
		Assertions.assertEquals(SignalDAOStub.THIRD.getCertificate(), res.getCertificate());
		Assertions.assertEquals(SignalDAOStub.THIRD.getLatency(), res.getLatency());
		Assertions.assertEquals(SignalDAOStub.THIRD.getPath(), res.getPath());
		Assertions.assertEquals(SignalDAOStub.THIRD.getExpected(), res.getExpected());
		Assertions.assertEquals(SignalDAOStub.THIRD.getUser().getEmail(), res.getEmail());
		Assertions.assertEquals(SignalDAOStub.THIRD.getBrowser(), res.getBrowser());
		Assertions.assertEquals(SignalDAOStub.THIRD.getUser().getPremium(),res.getPremium());
		Assertions.assertEquals(SignalDAOStub.THIRD.getNotify(), res.getNotify());
		Assertions.assertEquals(SignalDAOStub.THIRD.getInterval(), res.getInterval());
		Assertions.assertEquals(SignalDAOStub.THIRD.getRetention(), res.getRetention());
	}
	
	@Test
	public void testGetById2() {
		Response response = resource.getById(SignalDAOStub.THIRD.getId(), LogDAOStub.FIRST.getId());
		
		Assertions.assertNotNull(response);
		Assertions.assertEquals(200, response.getStatus());
		Assertions.assertTrue(response.getEntity() instanceof String);
		
		String res = (String) response.getEntity();
		Assertions.assertEquals(LogDAOStub.FIRST.getSource(), res);
	}
	
	@Test
	public void testUpdateById() {
		SignalDTO dto = new SignalDTO(url, certificate, latency, path, expected, email, browser, notify, interval, retention, null, null, null);
		
		Response response = resource.updateById(SignalDAOStub.SECOND.getId(), dto);
		
		Assertions.assertNotNull(response);
		Assertions.assertEquals(200, response.getStatus());
		Assertions.assertTrue(response.getEntity() instanceof GenericDTO);
		
		GenericDTO res = (GenericDTO) response.getEntity();
		Assertions.assertEquals(GenericDTO.OBJECTUPDATED.getCode(), res.getCode());
		Assertions.assertNotNull(res.getDetail());
		Assertions.assertNotNull(res.getBeacon());
			
		Assertions.assertNotEquals(dto.getPremium(), res.getBeacon().getPremium());		
		Assertions.assertNotEquals(dto.getUrl(), res.getBeacon().getUrl());
		Assertions.assertNotEquals(dto.getEmail(), res.getBeacon().getEmail());
		Assertions.assertNotEquals(dto.getNotify(), res.getBeacon().getNotify());
		Assertions.assertEquals(dto.getCertificate(), res.getBeacon().getCertificate());
		Assertions.assertEquals(dto.getLatency(), res.getBeacon().getLatency());
		Assertions.assertEquals(dto.getPath(), res.getBeacon().getPath());
		Assertions.assertEquals(dto.getExpected(), res.getBeacon().getExpected());
		Assertions.assertEquals(dto.getBrowser(), res.getBeacon().getBrowser());
		Assertions.assertEquals(dto.getInterval(), res.getBeacon().getInterval());
		Assertions.assertEquals(dto.getRetention(), res.getBeacon().getRetention());
	}
	
	@Test
	public void testGetCount() {
		Response response = resource.getCount();
		
		Assertions.assertNotNull(response);
		Assertions.assertEquals(200, response.getStatus());
		Assertions.assertTrue(response.getEntity() instanceof Long);
		Assertions.assertTrue(((Long) response.getEntity()) > 0);
	}
	
	@Test
	public void testUnsubscribeCertification() {
		Response response = resource.unsubscribeCertification(SignalDAOStub.FOURTH.getId());
		
		Assertions.assertNotNull(response);
		Assertions.assertEquals(200, response.getStatus());
		Assertions.assertTrue(response.getEntity() instanceof String);
		
		String res = (String) response.getEntity();
		Assertions.assertTrue(res.contains(SignalDAOStub.FOURTH.getId()));
		Assertions.assertTrue(res.contains(config.getServiceUrl().toString()));
	}
	
	@Test
	public void testUnsubscribeNotification() {
		Response response = resource.unsubscribeNotification(SignalDAOStub.FIFTH.getId());
		
		Assertions.assertNotNull(response);
		Assertions.assertEquals(200, response.getStatus());
		Assertions.assertTrue(response.getEntity() instanceof String);
		
		String res = (String) response.getEntity();
		Assertions.assertTrue(res.contains(SignalDAOStub.FIFTH.getId()));
		Assertions.assertTrue(res.contains(config.getServiceUrl() + "/" + SignalDAOStub.FIFTH.getId()));
	}
}
