package com.ipsignal.mapper;

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

import com.ipsignal.dto.DTO;
import com.ipsignal.entity.Entity;

/**
 * Transform an object from persistence to a presentation object reciprocally
 * @author Kevin Guignard
 *
 * @param <E> entity
 * @param <D> data transfer object
 */
public interface Mapper<E extends Entity, D extends DTO> {

	/**
	 * Transform the entity to a data transfer object
	 * @param entity
	 * @return DTO
	 */
	D entityToDto(E entity);

	/**
	 * Transform the data transfer object to an entity
	 * @param dto
	 * @param entity
	 * @return entity
	 */
	E dtoToEntity(D dto, E entity);

}
