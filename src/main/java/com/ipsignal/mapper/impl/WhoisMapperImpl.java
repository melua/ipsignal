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

import com.ipsignal.dto.impl.WhoisDTO;
import com.ipsignal.entity.impl.WhoisEntity;
import com.ipsignal.mapper.WhoisMapper;

@Stateless
public class WhoisMapperImpl implements WhoisMapper {
	
	private static final Format FORMATTER = FastDateFormat.getInstance("yyyy-MM-dd");

	@Override
	public WhoisDTO entityToDto(final WhoisEntity entity) {
		if (entity == null) {
			return null;
		}
		
		String created = FORMATTER.format(entity.getCreated());
		String updated = FORMATTER.format(entity.getUpdated());
		String expires = FORMATTER.format(entity.getExpires());
		return new WhoisDTO(created, updated, expires);
	}

	@Override
	public WhoisEntity dtoToEntity(WhoisDTO dto, WhoisEntity entity) {
		return null;
	}

}
