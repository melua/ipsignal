package com.ipsignal.mapper.impl;

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
import com.ipsignal.entity.impl.LogEntity;
import com.ipsignal.entity.impl.SignalEntity;
import com.ipsignal.entity.impl.UserEntity;
import com.ipsignal.mapper.LogMapper;
import com.ipsignal.mapper.SignalMapper;
import com.ipsignal.tool.TLVParser;

@Stateless
public class SignalMapperImpl implements SignalMapper {
	
	private static final Format FORMATTER = FastDateFormat.getInstance("yyyy-MM-dd");

	@EJB
	LogMapper mapper;

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
		SignalDTO dto = new SignalDTO(entity.getUrl(), entity.getCertificate(), entity.getLatency(), entity.getPath(), entity.getExpected(), entity.getUser().getEmail(), entity.getBrowser(), premium, entity.getNotify(), entity.getInterval(), entity.getRetention());
		
		Collections.sort(entity.getLogs(), Collections.reverseOrder());

		/*
		 * The JSON provider doesn't
		 * skip empty arrays...
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
			return new SignalEntity(new UserEntity(dto.getEmail(), null), null, false, dto.getUrl(), dto.getBrowser(), dto.getCertificate(), dto.getLatency(), dto.getPath(), dto.getExpected(), dto.getNotify(), dto.getInterval(), dto.getRetention());
		} else {
			/*
			 * CAN'T PATCH
			 * uuid
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
				entity.getNotify(), entity.getInterval(), entity.getRetention());
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
		dto.setBrowser(TLVParser.readTLV(tlv, SignalDTO.T_BROWSER));
		dto.setCertificate(Integer.valueOf(TLVParser.readTLV(tlv, SignalDTO.T_CERTIFICATE)));
		dto.setEmail(TLVParser.readTLV(tlv, SignalDTO.T_EMAIL));
		dto.setExpected(TLVParser.readTLV(tlv, SignalDTO.T_EXPECTED));
		dto.setInterval(Integer.valueOf(TLVParser.readTLV(tlv, SignalDTO.T_INTERVAL)));
		dto.setLatency(Integer.valueOf(TLVParser.readTLV(tlv, SignalDTO.T_LATENCY)));
		dto.setNotify(TLVParser.readTLV(tlv, SignalDTO.T_NOTIFY));
		dto.setPath(TLVParser.readTLV(tlv, SignalDTO.T_PATH));
		dto.setUrl(TLVParser.readTLV(tlv, SignalDTO.T_URL));
		return dto;
	}

}
