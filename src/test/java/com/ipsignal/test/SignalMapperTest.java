package com.ipsignal.test;

import java.util.Date;

import javax.inject.Inject;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ipsignal.Config;
import com.ipsignal.dto.impl.SignalDTO;
import com.ipsignal.entity.impl.SignalEntity;
import com.ipsignal.entity.impl.UserEntity;
import com.ipsignal.mapper.SignalMapper;
import com.ipsignal.mock.mapper.SignalMapperMock;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class SignalMapperTest {
	
	@Inject
	static Config config;
	
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
	private Date premium;

	@BeforeAll
	public static void init() throws Exception {
		mapper = new SignalMapperMock(config);
	}
	
	@BeforeEach
	public void setUp() {
		browser = RandomStringUtils.random(60);
		certificate = RandomUtils.nextInt();
		email = RandomStringUtils.random(60);
		expected = RandomStringUtils.random(60);
		interval = RandomUtils.nextInt();
		latency = RandomUtils.nextInt();
		notify = RandomStringUtils.random(60);
		path = RandomStringUtils.random(60);
		retention = RandomUtils.nextInt();
		url = RandomStringUtils.random(60);
		premium = new Date();
	}

	@Test
	public void testDtoToEntity1() {
		SignalDTO dto = new SignalDTO(url, certificate, latency, path, expected, email, browser, notify, interval, retention, null, null, null);
		SignalEntity entity = mapper.dtoToEntity(dto, null);
		
		Assertions.assertNotNull(entity);
		Assertions.assertNotNull(entity.getId());
		
		Assertions.assertEquals(dto.getUrl(), entity.getUrl());
		Assertions.assertEquals(dto.getCertificate(), entity.getCertificate());
		Assertions.assertEquals(dto.getLatency(), entity.getLatency());
		Assertions.assertEquals(dto.getPath(), entity.getPath());
		Assertions.assertEquals(dto.getExpected(), entity.getExpected());
		Assertions.assertEquals(dto.getEmail(), entity.getUser().getEmail());
		Assertions.assertEquals(dto.getBrowser(), entity.getBrowser());
		Assertions.assertEquals(dto.getPremium(), entity.getUser().getPremium());
		Assertions.assertEquals(dto.getNotify(), entity.getNotify());
		Assertions.assertEquals(dto.getInterval(), entity.getInterval());
		Assertions.assertEquals(dto.getRetention(), entity.getRetention());
	}
	
	@Test
	public void testDtoToEntity2() {
		UserEntity user = new UserEntity(email, premium);
		SignalEntity entity = new SignalEntity(user, null, true, url, browser, certificate, latency, path, expected, notify, interval, retention, null);
		SignalDTO dto = new SignalDTO(RandomStringUtils.random(60), RandomUtils.nextInt(), RandomUtils.nextInt(), RandomStringUtils.random(60), RandomStringUtils.random(60),
				RandomStringUtils.random(60), RandomStringUtils.random(60), RandomStringUtils.random(60), RandomUtils.nextInt(), RandomUtils.nextInt(), null, null, null);
		
		entity = mapper.dtoToEntity(dto, entity);
		
		Assertions.assertNotNull(entity);
		Assertions.assertNotNull(entity.getUser().getPremium());
		Assertions.assertNotEquals(dto.getUrl(), entity.getUrl());
		Assertions.assertNotEquals(dto.getEmail(), entity.getUser().getEmail());
		Assertions.assertNotEquals(dto.getNotify(), entity.getNotify());
		Assertions.assertEquals(dto.getCertificate(), entity.getCertificate());
		Assertions.assertEquals(dto.getLatency(), entity.getLatency());
		Assertions.assertEquals(dto.getPath(), entity.getPath());
		Assertions.assertEquals(dto.getExpected(), entity.getExpected());
		Assertions.assertEquals(dto.getBrowser(), entity.getBrowser());
		Assertions.assertEquals(dto.getInterval(), entity.getInterval());
		Assertions.assertEquals(dto.getRetention(), entity.getRetention());
	}
	
	@Test
	public void testEntityToDto() {
		UserEntity user = new UserEntity(email, premium);
		SignalEntity entity = new SignalEntity(user, null, true, url, browser, certificate, latency, path, expected, notify, interval, retention, null);
		
		SignalDTO dto = mapper.entityToDto(entity);
		
		Assertions.assertNotNull(dto);
		Assertions.assertNotNull(dto.getPremium());
		Assertions.assertEquals(entity.getUrl(), dto.getUrl());
		Assertions.assertEquals(entity.getCertificate(), dto.getCertificate());
		Assertions.assertEquals(entity.getLatency(), dto.getLatency());
		Assertions.assertEquals(entity.getPath(), dto.getPath());
		Assertions.assertEquals(entity.getExpected(), dto.getExpected());
		Assertions.assertEquals(entity.getUser().getEmail(), dto.getEmail());
		Assertions.assertEquals(entity.getBrowser(), dto.getBrowser());
		Assertions.assertEquals(entity.getNotify(), dto.getNotify());
		Assertions.assertEquals(entity.getInterval(), dto.getInterval());
		Assertions.assertEquals(entity.getRetention(), dto.getRetention());
	}
	
	@Test
	public void testBackupEntity() {
		UserEntity user = new UserEntity(email, premium);
		SignalEntity entity = new SignalEntity(user, null, true, url, browser, certificate, latency, path, expected, notify, interval, retention, null);
		
		SignalEntity backup = mapper.backupEntity(entity);
		
		Assertions.assertNotNull(backup);
		Assertions.assertNotEquals(entity.getId(), backup.getId());
		Assertions.assertEquals(entity.getUrl(), backup.getUrl());
		Assertions.assertEquals(entity.getCertificate(), backup.getCertificate());
		Assertions.assertEquals(entity.getLatency(), backup.getLatency());
		Assertions.assertEquals(entity.getPath(), backup.getPath());
		Assertions.assertEquals(entity.getExpected(), backup.getExpected());
		Assertions.assertEquals(entity.getUser().getEmail(), backup.getUser().getEmail());
		Assertions.assertEquals(entity.getBrowser(), backup.getBrowser());
		Assertions.assertEquals(entity.getUser().getPremium(), backup.getUser().getPremium());
		Assertions.assertEquals(entity.getNotify(), backup.getNotify());
		Assertions.assertEquals(entity.getInterval(), backup.getInterval());
		Assertions.assertEquals(entity.getRetention(), backup.getRetention());
	}

	@Test
	public void testRestoreEntity() {
		UserEntity user = new UserEntity(email, premium);
		SignalEntity child = new SignalEntity(new UserEntity(RandomStringUtils.random(60), null), null, false, RandomStringUtils.random(60), RandomStringUtils.random(60),
				RandomUtils.nextInt(),RandomUtils.nextInt(), RandomStringUtils.random(60), RandomStringUtils.random(60), RandomStringUtils.random(60), RandomUtils.nextInt(), RandomUtils.nextInt(), null);
		SignalEntity parent = new SignalEntity(user, null, true, url, browser, certificate, latency, path, expected, notify, interval, retention, null);
		
		mapper.restoreEntity(child, parent);
		
		Assertions.assertNotNull(parent);
		Assertions.assertNotEquals(child.getId(), parent.getId());
		Assertions.assertNotEquals(child.getUrl(), parent.getUrl());
		Assertions.assertNotEquals(child.getUser().getEmail(), parent.getUser().getEmail());
		Assertions.assertNotEquals(child.getUser().getPremium(), parent.getUser().getPremium());
		Assertions.assertNotEquals(child.getNotify(), parent.getNotify());
		Assertions.assertEquals(child.getCertificate(), parent.getCertificate());
		Assertions.assertEquals(child.getLatency(), parent.getLatency());
		Assertions.assertEquals(child.getPath(), parent.getPath());
		Assertions.assertEquals(child.getExpected(), parent.getExpected());
		Assertions.assertEquals(child.getBrowser(), parent.getBrowser());	
		Assertions.assertEquals(child.getInterval(), parent.getInterval());
		Assertions.assertEquals(child.getRetention(), parent.getRetention());
	}

}
