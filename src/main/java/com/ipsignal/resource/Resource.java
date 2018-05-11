package com.ipsignal.resource;

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

import javax.ws.rs.core.Response;

import com.ipsignal.dto.DTO;

/**
 * Provide CRUD functions to manipulate DTO through network facades
 * @author Kevin Guignard
 *
 * @param <T> data transfer object to manipulate
 */
public interface Resource<T extends DTO> {

	/**
	 * Create a new resource
	 *
	 * POST is required by the specification to send a response code of 201 "Created"
	 * if a new resource was created on the server as a result of the request.
	 *
	 * @param dto of the resource representation
	 * @return appropriate response status
	 */
	Response create(T dto);

	/**
	 * Retrieve a resource
	 *
	 * If a successful HTTP response contains a message body, 200 "OK" is the response code.
	 * If the response doesn’t contain a message body, 204 "No Content" must be returned.
	 *
	 * @param uuid unique identifier
	 * @return appropriate response status
	 */
	Response getById(String uuid);

	/**
	 * Update a resource
	 *
	 * When a resource is updated with PUT, the HTTP specification requires
	 * that you send a response code of 200 "OK" and a response message body
	 * or a response code of 204 "No Content" without any response body.
	 *
	 * @param uuid unique identifier
	 * @param dto of the resource representation
	 * @return appropriate response status
	 */
	Response updateById(String uuid, T dto);

	/**
	 * Delete a resource
	 *
	 * When a resource is removed with DELETE, the HTTP specification requires
	 * that you send a response code of 200 "OK" and a response message body
	 * or a response code of 204 "No Content" without any response body.
	 *
	 * @param uuid unique identifier
	 * @return appropriate response status
	 */
	Response deleteById(String uuid);

}
