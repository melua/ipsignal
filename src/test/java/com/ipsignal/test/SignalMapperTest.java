package com.ipsignal.test;

import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

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

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ipsignal.dto.impl.SignalDTO;
import com.ipsignal.entity.impl.SignalEntity;
import com.ipsignal.entity.impl.UserEntity;
import com.ipsignal.mapper.SignalMapper;
import com.ipsignal.mock.mapper.SignalMapperMock;

public class SignalMapperTest {
	
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

	@BeforeClass
	public static void init() throws Exception {
		mapper = new SignalMapperMock();
	}
	
	@Before	
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
		SignalDTO dto = new SignalDTO(url, certificate, latency, path, expected, email, browser, null, notify, interval, retention, null);
		
		SignalEntity entity = mapper.dtoToEntity(dto, null);
		
		Assert.assertNotNull(entity);
		Assert.assertNotNull(entity.getId());
		
		Assert.assertEquals(dto.getUrl(), entity.getUrl());
		Assert.assertEquals(dto.getCertificate(), entity.getCertificate());
		Assert.assertEquals(dto.getLatency(), entity.getLatency());
		Assert.assertEquals(dto.getPath(), entity.getPath());
		Assert.assertEquals(dto.getExpected(), entity.getExpected());
		Assert.assertEquals(dto.getEmail(), entity.getUser().getEmail());
		Assert.assertEquals(dto.getBrowser(), entity.getBrowser());
		Assert.assertEquals(dto.getPremium(), entity.getUser().getPremium());
		Assert.assertEquals(dto.getNotify(), entity.getNotify());
		Assert.assertEquals(dto.getInterval(), entity.getInterval());
		Assert.assertEquals(dto.getRetention(), entity.getRetention());
	}
	
	@Test
	public void testDtoToEntity2() {
		UserEntity user = new UserEntity(email, premium);
		SignalEntity entity = new SignalEntity(user, null, true, url, browser, certificate, latency, path, expected, notify, interval, retention, null);
		SignalDTO dto = new SignalDTO(RandomStringUtils.random(60), RandomUtils.nextInt(), RandomUtils.nextInt(), RandomStringUtils.random(60), RandomStringUtils.random(60),
				RandomStringUtils.random(60), RandomStringUtils.random(60), null, RandomStringUtils.random(60), RandomUtils.nextInt(), RandomUtils.nextInt(), null);
		
		entity = mapper.dtoToEntity(dto, entity);
		
		Assert.assertNotNull(entity);
		Assert.assertNotNull(entity.getUser().getPremium());
		Assert.assertNotEquals(dto.getUrl(), entity.getUrl());
		Assert.assertNotEquals(dto.getEmail(), entity.getUser().getEmail());
		Assert.assertNotEquals(dto.getNotify(), entity.getNotify());
		Assert.assertEquals(dto.getCertificate(), entity.getCertificate());
		Assert.assertEquals(dto.getLatency(), entity.getLatency());
		Assert.assertEquals(dto.getPath(), entity.getPath());
		Assert.assertEquals(dto.getExpected(), entity.getExpected());
		Assert.assertEquals(dto.getBrowser(), entity.getBrowser());
		Assert.assertEquals(dto.getInterval(), entity.getInterval());
		Assert.assertEquals(dto.getRetention(), entity.getRetention());
	}
	
	@Test
	public void testEntityToDto() {
		UserEntity user = new UserEntity(email, premium);
		SignalEntity entity = new SignalEntity(user, null, true, url, browser, certificate, latency, path, expected, notify, interval, retention, null);
		
		SignalDTO dto = mapper.entityToDto(entity);
		
		Assert.assertNotNull(dto);
		Assert.assertNotNull(dto.getPremium());
		Assert.assertEquals(entity.getUrl(), dto.getUrl());
		Assert.assertEquals(entity.getCertificate(), dto.getCertificate());
		Assert.assertEquals(entity.getLatency(), dto.getLatency());
		Assert.assertEquals(entity.getPath(), dto.getPath());
		Assert.assertEquals(entity.getExpected(), dto.getExpected());
		Assert.assertEquals(entity.getUser().getEmail(), dto.getEmail());
		Assert.assertEquals(entity.getBrowser(), dto.getBrowser());
		Assert.assertEquals(entity.getNotify(), dto.getNotify());
		Assert.assertEquals(entity.getInterval(), dto.getInterval());
		Assert.assertEquals(entity.getRetention(), dto.getRetention());
	}
	
	@Test
	public void testBackupEntity() {
		UserEntity user = new UserEntity(email, premium);
		SignalEntity entity = new SignalEntity(user, null, true, url, browser, certificate, latency, path, expected, notify, interval, retention, null);
		
		SignalEntity backup = mapper.backupEntity(entity);
		
		Assert.assertNotNull(backup);
		Assert.assertNotEquals(entity.getId(), backup.getId());
		Assert.assertEquals(entity.getUrl(), backup.getUrl());
		Assert.assertEquals(entity.getCertificate(), backup.getCertificate());
		Assert.assertEquals(entity.getLatency(), backup.getLatency());
		Assert.assertEquals(entity.getPath(), backup.getPath());
		Assert.assertEquals(entity.getExpected(), backup.getExpected());
		Assert.assertEquals(entity.getUser().getEmail(), backup.getUser().getEmail());
		Assert.assertEquals(entity.getBrowser(), backup.getBrowser());
		Assert.assertEquals(entity.getUser().getPremium(), backup.getUser().getPremium());
		Assert.assertEquals(entity.getNotify(), backup.getNotify());
		Assert.assertEquals(entity.getInterval(), backup.getInterval());
		Assert.assertEquals(entity.getRetention(), backup.getRetention());
	}

	@Test
	public void testRestoreEntity() {
		UserEntity user = new UserEntity(email, premium);
		SignalEntity child = new SignalEntity(new UserEntity(RandomStringUtils.random(60), null), null, false, RandomStringUtils.random(60), RandomStringUtils.random(60),
				RandomUtils.nextInt(),RandomUtils.nextInt(), RandomStringUtils.random(60), RandomStringUtils.random(60), RandomStringUtils.random(60), RandomUtils.nextInt(), RandomUtils.nextInt(), null);
		SignalEntity parent = new SignalEntity(user, null, true, url, browser, certificate, latency, path, expected, notify, interval, retention, null);
		
		mapper.restoreEntity(child, parent);
		
		Assert.assertNotNull(parent);
		Assert.assertNotEquals(child.getId(), parent.getId());
		Assert.assertNotEquals(child.getUrl(), parent.getUrl());
		Assert.assertNotEquals(child.getUser().getEmail(), parent.getUser().getEmail());
		Assert.assertNotEquals(child.getUser().getPremium(), parent.getUser().getPremium());
		Assert.assertNotEquals(child.getNotify(), parent.getNotify());
		Assert.assertEquals(child.getCertificate(), parent.getCertificate());
		Assert.assertEquals(child.getLatency(), parent.getLatency());
		Assert.assertEquals(child.getPath(), parent.getPath());
		Assert.assertEquals(child.getExpected(), parent.getExpected());
		Assert.assertEquals(child.getBrowser(), parent.getBrowser());	
		Assert.assertEquals(child.getInterval(), parent.getInterval());
		Assert.assertEquals(child.getRetention(), parent.getRetention());
	}

}
