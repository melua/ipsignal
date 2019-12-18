package com.ipsignal.test;

import javax.inject.Inject;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ipsignal.Config;
import com.ipsignal.dto.impl.LogDTO;
import com.ipsignal.entity.impl.LogEntity;
import com.ipsignal.entity.impl.SignalEntity;
import com.ipsignal.entity.impl.UserEntity;
import com.ipsignal.mapper.LogMapper;
import com.ipsignal.mapper.impl.LogMapperImpl;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class LogMapperTest {
	
	@Inject
	static Config config;
	
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

	@BeforeAll
	public static void init() throws Exception {
		mapper = new LogMapperImpl(config);
	}
	
	@BeforeEach
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
				RandomUtils.nextInt(),RandomUtils.nextInt(), RandomStringUtils.random(60), RandomStringUtils.random(60), RandomStringUtils.random(60), RandomUtils.nextInt(), RandomUtils.nextInt(), null);
		LogEntity entity = LogEntity.EQUALS.getInstance(signal, latency, certificate, browser, http, obtained, source, arg);
		
		LogDTO dto = mapper.entityToDto(entity);
		
		Assertions.assertNotNull(dto);
		Assertions.assertEquals(FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").format(entity.getAccess()), dto.getAccess());
		Assertions.assertEquals(entity.getLatency(), dto.getLatency());
		Assertions.assertEquals(entity.getCertificate(), dto.getCertificate());
		Assertions.assertEquals(entity.getBrowser(), dto.getBrowser());
		Assertions.assertEquals(entity.getHttp(), dto.getHttp());
		Assertions.assertEquals(entity.getObtained(), dto.getObtained());
		Assertions.assertEquals(String.format(LogEntity.EQUALS.getDetail(), arg), dto.getDetail());
		Assertions.assertEquals(config.getServiceUrl() + "/" + signal.getId() + "/" + entity.getId(), dto.getSource());
	}

	@Test
	public void testDtoToEntity() {
		LogDTO dto = new LogDTO(access, latency, certificate, browser, http, obtained, detail, source);
		LogEntity entity = new LogEntity();
		
		entity = mapper.dtoToEntity(dto, entity);
		
		Assertions.assertNull(entity);
	}
	
}
