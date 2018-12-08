package com.ipsignal.mapper.impl;

import java.nio.ByteBuffer;

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

import java.text.Format;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.time.FastDateFormat;

import com.ipsignal.dto.impl.LogDTO;
import com.ipsignal.dto.impl.SignalDTO;
import com.ipsignal.dto.impl.WhoisDTO;
import com.ipsignal.entity.impl.LogEntity;
import com.ipsignal.entity.impl.SignalEntity;
import com.ipsignal.entity.impl.UserEntity;
import com.ipsignal.mapper.LogMapper;
import com.ipsignal.mapper.SignalMapper;
import com.ipsignal.mapper.WhoisMapper;
import com.ipsignal.tool.TLVParser;

@Stateless
public class SignalMapperImpl implements SignalMapper {
	
	private static final Format FORMATTER = FastDateFormat.getInstance("yyyy-MM-dd");
	private static final int BUFFER_MAX = 4096;

	@EJB
	LogMapper mapper;
	@EJB
	WhoisMapper wmapper;

	public SignalMapperImpl() {
		// For injection
	}

	protected SignalMapperImpl(LogMapper mapper) {
		// For tests
		this.mapper = mapper;
	}
	
	@Override
	public SignalDTO entityToDto(final SignalEntity entity) {
		if (entity == null) {
			return null;
		}
		
		String premium = entity.getUser().getPremium() != null ? FORMATTER.format(entity.getUser().getPremium()) : null;
		WhoisDTO whois = entity.getWhois() != null && entity.getWhois().getAccess() != null ? wmapper.entityToDto(entity.getWhois()) : null;
		SignalDTO dto = new SignalDTO(entity.getUrl(), entity.getCertificate(), entity.getLatency(), entity.getPath(), entity.getExpected(), entity.getUser().getEmail(), entity.getBrowser(), entity.getNotify(), entity.getInterval(), entity.getRetention(), premium, whois, null);
		
		Collections.sort(entity.getLogs(), Collections.reverseOrder());

		/*
		 * The JSON provider doesn't
		 * skip empty arrays
		 */
		if (!entity.getLogs().isEmpty()) {
			dto.setLogs(new LinkedList<LogDTO>());
			for (LogEntity log : entity.getLogs()) {
				dto.getLogs().add(mapper.entityToDto(log));
			}
		}

		return dto;
	}
	
	@Override
	public SignalEntity dtoToEntity(final SignalDTO dto, final SignalEntity entity) {
		if (dto == null) {
			return null;
		}

		if (entity == null) {
			return new SignalEntity(new UserEntity(dto.getEmail(), null), null, false, dto.getUrl(), dto.getBrowser(), dto.getCertificate(), dto.getLatency(), dto.getPath(), dto.getExpected(), dto.getNotify(), dto.getInterval(), dto.getRetention(), null);
		} else {
			/*
			 * CAN'T PATCH
			 * id
			 * url
			 * email
			 * notify
			 */
			if (dto.getBrowser() != null) {
				entity.setBrowser(dto.getBrowser());
			}
			if (dto.getPath() != null) {
				entity.setPath(dto.getPath());
			}
			if (dto.getExpected() != null) {
				entity.setExpected(dto.getExpected());
			}
			if (dto.getInterval() != null) {
				entity.setInterval(dto.getInterval());
			}
			if (dto.getRetention() != null) {
				entity.setRetention(dto.getRetention());
			}
			if (dto.getCertificate() != null) {
				entity.setCertificate(dto.getCertificate());
			}
			if (dto.getLatency() != null) {
				entity.setLatency(dto.getLatency());
			}
			
			entity.setLastaccess(new Date());
		}
		return entity;
	}

	@Override
	public SignalEntity backupEntity(SignalEntity entity) {
		return new SignalEntity(entity.getUser(), entity, false, entity.getUrl(), entity.getBrowser(), entity.getCertificate(), entity.getLatency(), entity.getPath(),entity.getExpected(),
				entity.getNotify(), entity.getInterval(), entity.getRetention(), entity.getWhois());
	}
	
	@Override
	public void restoreEntity(SignalEntity child, SignalEntity parent) {
		parent.setBrowser(child.getBrowser());
		parent.setPath(child.getPath());
		parent.setExpected(child.getExpected());
		parent.setInterval(child.getInterval());
		parent.setRetention(child.getRetention());
		parent.setCertificate(child.getCertificate());
		parent.setLatency(child.getLatency());
	}

	@Override
	public SignalDTO tlvToDto(byte[] tlv) {
		SignalDTO dto = new SignalDTO();
		String browser = TLVParser.readTLV(tlv, SignalDTO.T_BROWSER);
		String certificate = TLVParser.readTLV(tlv, SignalDTO.T_CERTIFICATE);
		String email = TLVParser.readTLV(tlv, SignalDTO.T_EMAIL);
		String expected = TLVParser.readTLV(tlv, SignalDTO.T_EXPECTED);
		String interval = TLVParser.readTLV(tlv, SignalDTO.T_INTERVAL);
		String latency = TLVParser.readTLV(tlv, SignalDTO.T_LATENCY);
		String notify = TLVParser.readTLV(tlv, SignalDTO.T_NOTIFY);
		String path = TLVParser.readTLV(tlv, SignalDTO.T_PATH);
		String url = TLVParser.readTLV(tlv, SignalDTO.T_URL);
	
		if (browser != null) {
			dto.setBrowser(browser);
		}
		if (certificate != null) {
			dto.setCertificate(Integer.valueOf(certificate));
		}
		if (email != null) {
			dto.setEmail(email);
		}
		if (expected != null) {
			dto.setExpected(expected);
		}
		if (interval != null) {
			dto.setInterval(Integer.valueOf(interval));
		}
		if (latency != null) {
			dto.setLatency(Integer.valueOf(latency));
		}
		if (notify != null) {
			dto.setNotify(notify);
		}
		if (path != null) {
			dto.setPath(path);
		}
		if (url != null) {
			dto.setUrl(url);
		}

		return dto;
	}

	@Override
	public byte[] DtoToTlv(SignalDTO dto) {
		
		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_MAX);
		
		if (dto.getBrowser() != null) {
			buffer.put(TLVParser.writeTLV(dto.getBrowser(), SignalDTO.T_BROWSER));
		}
		
		if (dto.getCertificate() != null) {
			buffer.put(TLVParser.writeTLV(dto.getCertificate(), SignalDTO.T_CERTIFICATE));
		}
		
		if (dto.getEmail() != null) {
			buffer.put(TLVParser.writeTLV(dto.getEmail(), SignalDTO.T_EMAIL));
		}

		if (dto.getExpected() != null) {
			buffer.put(TLVParser.writeTLV(dto.getExpected(), SignalDTO.T_EXPECTED));
		}
		
		if (dto.getInterval() != null) {
			buffer.put(TLVParser.writeTLV(dto.getInterval(), SignalDTO.T_INTERVAL));
		}
		
		if (dto.getLatency() != null) {
			buffer.put(TLVParser.writeTLV(dto.getLatency(), SignalDTO.T_LATENCY));
		}
		
		if (dto.getNotify() != null) {
			buffer.put(TLVParser.writeTLV(dto.getNotify(), SignalDTO.T_NOTIFY));
		}
		
		if (dto.getPath() != null) {
			buffer.put(TLVParser.writeTLV(dto.getPath(), SignalDTO.T_PATH));
		}
		
		if (dto.getUrl() != null) {
			buffer.put(TLVParser.writeTLV(dto.getUrl(), SignalDTO.T_URL));
		}
		
		buffer.flip();
		byte[] result = new byte[buffer.limit()];
		buffer.get(result, 0, buffer.limit());
		return result;
	}

}
