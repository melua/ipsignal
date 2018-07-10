package com.ipsignal.test;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.FastDateFormat;

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

import com.ipsignal.Config;
import com.ipsignal.dto.impl.LogDTO;
import com.ipsignal.entity.impl.LogEntity;
import com.ipsignal.entity.impl.SignalEntity;
import com.ipsignal.entity.impl.UserEntity;
import com.ipsignal.mapper.LogMapper;
import com.ipsignal.mapper.impl.LogMapperImpl;

public class LogMapperTest {
	
	private static LogMapper mapper;
	private String access;
	private Integer latency;
	private Integer certificate;
	private String browser;
	private Integer http;
	private String obtained;
	private String detail;
	private String source;
	private String arg;

	@BeforeClass
	public static void init() throws Exception {
		mapper = new LogMapperImpl();
	}
	
	@Before	
	public void setUp() {
		access = RandomStringUtils.random(60);
		latency = RandomUtils.nextInt();
		certificate = RandomUtils.nextInt();
		browser = RandomStringUtils.random(60);
		http = RandomUtils.nextInt();
		obtained = RandomStringUtils.random(60);
		detail = RandomStringUtils.random(60);
		source = RandomStringUtils.random(60);
		arg = RandomStringUtils.random(60);
	}
	
	@Test
	public void testEntityToDto() {
		SignalEntity signal = new SignalEntity(new UserEntity(RandomStringUtils.random(60), null), null, false, RandomStringUtils.random(60), RandomStringUtils.random(60),
				RandomUtils.nextInt(),RandomUtils.nextInt(), RandomStringUtils.random(60), RandomStringUtils.random(60), RandomStringUtils.random(60), RandomUtils.nextInt(), RandomUtils.nextInt());
		LogEntity entity = LogEntity.EQUALS.getInstance(signal, latency, certificate, browser, http, obtained, source, arg);
		
		LogDTO dto = mapper.entityToDto(entity);
		
		Assert.assertNotNull(dto);
		Assert.assertEquals(FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").format(entity.getAccess()), dto.getAccess());
		Assert.assertEquals(entity.getLatency(), dto.getLatency());
		Assert.assertEquals(entity.getCertificate(), dto.getCertificate());
		Assert.assertEquals(entity.getBrowser(), dto.getBrowser());
		Assert.assertEquals(entity.getHttp(), dto.getHttp());
		Assert.assertEquals(entity.getObtained(), dto.getObtained());
		Assert.assertEquals(String.format(LogEntity.EQUALS.getDetail(), arg), dto.getDetail());
		Assert.assertEquals(Config.SERVICE_URL + "/" + signal.getId() + "/" + entity.getId(), dto.getSource());
	}

	@Test
	public void testDtoToEntity() {
		LogDTO dto = new LogDTO(access, latency, certificate, browser, http, obtained, detail, source);
		LogEntity entity = new LogEntity();
		
		entity = mapper.dtoToEntity(dto, entity);
		
		Assert.assertNull(entity);
	}
	
}
