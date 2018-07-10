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

import javax.ejb.Stateless;

import org.apache.commons.lang3.time.FastDateFormat;

import com.ipsignal.Config;
import com.ipsignal.dto.impl.LogDTO;
import com.ipsignal.entity.impl.LogEntity;
import com.ipsignal.mapper.LogMapper;

@Stateless
public class LogMapperImpl implements LogMapper {
	
	private static final Format FORMATTER = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

	@Override
	public LogDTO entityToDto(final LogEntity entity) {
		if (entity == null) {
			return null;
		}

		String access = FORMATTER.format(entity.getAccess());
		String source = entity.getSource() != null ? Config.SERVICE_URL + "/" + entity.getSignal().getId() + "/" + entity.getId() : null;
		return new LogDTO(access, entity.getLatency(), entity.getCertificate(), entity.getBrowser(), entity.getHttp(), entity.getObtained(), entity.getDetail(), source);
	}

	@Override
	public LogEntity dtoToEntity(LogDTO dto, LogEntity entity) {
		return null;
	}

}
