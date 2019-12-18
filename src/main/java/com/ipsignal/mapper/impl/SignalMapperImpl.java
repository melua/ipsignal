package com.ipsignal.mapper.impl;

import java.io.IOException;

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
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.melua.MiniTLV;

import com.ipsignal.dto.impl.LogDTO;
import com.ipsignal.dto.impl.SignalDTO;
import com.ipsignal.dto.impl.WhoisDTO;
import com.ipsignal.entity.impl.LogEntity;
import com.ipsignal.entity.impl.SignalEntity;
import com.ipsignal.entity.impl.UserEntity;
import com.ipsignal.mapper.LogMapper;
import com.ipsignal.mapper.SignalMapper;
import com.ipsignal.mapper.WhoisMapper;

import lombok.extern.java.Log;

@Log
@ApplicationScoped
public class SignalMapperImpl implements SignalMapper {
	
	private static final Format FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
	private static final int BUFFER_SIZE = 4096;

	LogMapper mapper;
	WhoisMapper wmapper;

	@Inject
	public SignalMapperImpl(LogMapper mapper, WhoisMapper wmapper) {
		this.mapper = mapper;
		this.wmapper = wmapper;
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

		try {

			Map<Integer, String> map = MiniTLV.parseAll(tlv);
			for (Entry<Integer, String> entry : map.entrySet()) {
				switch(entry.getKey()) {
					case (int) SignalDTO.T_BROWSER:
						dto.setBrowser(entry.getValue());
						break;
					case (int) SignalDTO.T_CERTIFICATE:
						dto.setCertificate(Integer.valueOf(entry.getValue()));
						break;
					case (int) SignalDTO.T_EMAIL:
						dto.setEmail(entry.getValue());
						break;
					case (int) SignalDTO.T_EXPECTED:
						dto.setExpected(entry.getValue());
						break;
					case (int) SignalDTO.T_INTERVAL:
						dto.setInterval(Integer.valueOf(entry.getValue()));
						break;
					case (int) SignalDTO.T_LATENCY:
						dto.setLatency(Integer.valueOf(entry.getValue()));
						break;
					case (int) SignalDTO.T_NOTIFY:
						dto.setNotify(entry.getValue());
						break;
					case (int) SignalDTO.T_PATH:
						dto.setPath(entry.getValue());
						break;
					case (int) SignalDTO.T_URL:
						dto.setUrl(entry.getValue());
						break;
					default:
				}
			}

		} catch (IOException ioex) {
			LOGGER.log(Level.WARNING, "Error while parsing TLV: {0}", ioex.getMessage());
		}

		return dto;
	}

	@Override
	public byte[] DtoToTlv(SignalDTO dto) {
		Map<byte[], Object> map = new HashMap<>();
		map.put(new byte[]{SignalDTO.T_BROWSER}, dto.getBrowser());
		map.put(new byte[]{SignalDTO.T_CERTIFICATE}, dto.getCertificate());
		map.put(new byte[]{SignalDTO.T_EMAIL}, dto.getEmail());
		map.put(new byte[]{SignalDTO.T_EXPECTED}, dto.getExpected());
		map.put(new byte[]{SignalDTO.T_INTERVAL}, dto.getInterval());
		map.put(new byte[]{SignalDTO.T_LATENCY}, dto.getLatency());
		map.put(new byte[]{SignalDTO.T_NOTIFY}, dto.getNotify());
		map.put(new byte[]{SignalDTO.T_PATH}, dto.getPath());
		map.put(new byte[]{SignalDTO.T_URL}, dto.getUrl());

		return MiniTLV.serializeAll(map, BUFFER_SIZE);
	}

}
