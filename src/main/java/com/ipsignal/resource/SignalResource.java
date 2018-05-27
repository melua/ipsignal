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

import javax.ejb.Local;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ipsignal.annotation.Cached;
import com.ipsignal.annotation.PATCH;
import com.ipsignal.dto.impl.SignalDTO;

@Local(SignalResource.class)
@Path("/")
public interface SignalResource extends Resource<SignalDTO> {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	Response create(SignalDTO dto);

	@GET
	@Cached
	@Path("{uuid}")
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	Response getById(@PathParam("uuid") String uuid);

	@GET
	@Cached
	@Path("{uuid}/{uuid2}")
	@Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	Response getById(@PathParam("uuid") String uuid, @PathParam("uuid2") String uuid2);
	
	@GET
	@Path("count")
	@Produces(MediaType.TEXT_PLAIN)
	Response getCount();

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	Response getVersion();

	@PATCH
	@Path("{uuid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	Response updateById(@PathParam("uuid") String uuid, SignalDTO dto);

	@DELETE
	@Path("{uuid}")
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	Response deleteById(@PathParam("uuid") String uuid);

	@GET
	@Path("unsub/notif/{uuid}")
	@Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	Response unsubscribeNotification(@PathParam("uuid") String uuid);

	@GET
	@Path("unsub/certif/{uuid}")
	@Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	Response unsubscribeCertification(@PathParam("uuid") String uuid);

}