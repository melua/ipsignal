package com.ipsignal.resource.impl;

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

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.ipsignal.Config;
import com.ipsignal.automate.Automate;
import com.ipsignal.dao.LogDAO;
import com.ipsignal.dao.SignalDAO;
import com.ipsignal.dao.UserDAO;
import com.ipsignal.dto.Restrictive.Constraint;
import com.ipsignal.dto.impl.GenericDTO;
import com.ipsignal.dto.impl.LogDTO;
import com.ipsignal.dto.impl.SignalDTO;
import com.ipsignal.entity.impl.LogEntity;
import com.ipsignal.entity.impl.SignalEntity;
import com.ipsignal.entity.impl.UserEntity;
import com.ipsignal.mail.MailManager;
import com.ipsignal.mapper.LogMapper;
import com.ipsignal.mapper.SignalMapper;
import com.ipsignal.mem.Memcached;
import com.ipsignal.resource.SignalResource;
import com.ipsignal.tool.TemplateBuilder;

@Stateless
public class SignalResourceImpl implements SignalResource {

	private static final String SANDBOX_PATH = "sandbox";

	@EJB
	private SignalMapper mapper;
	@EJB
	private LogMapper logmap;
	@EJB
	private SignalDAO dao;
	@EJB
	private UserDAO user;
	@EJB
	private LogDAO log;
	@EJB
	private MailManager mailer;
	@EJB
	private Automate automate;
	@EJB
	private Memcached mem;

	public SignalResourceImpl() {
		// For injection
	}

	protected SignalResourceImpl(SignalMapper mapper, LogMapper logmap, SignalDAO dao, UserDAO user, LogDAO log, MailManager mailer, Automate automate, Memcached mem) {
		// For tests
		this.mapper = mapper;
		this.logmap = logmap;
		this.dao = dao;
		this.user = user;
		this.log = log;
		this.mailer = mailer;
		this.automate = automate;
		this.mem = mem;
	}

	@Override
	public Response create(final SignalDTO dto) {
		// Check fields
		GenericDTO error = dto.checkConstraints(Constraint.NOTNULL, Constraint.SIZE, Constraint.EMAIL, Constraint.URL, Constraint.RESERVED, Constraint.BROWSER);
		if (error != null ) {
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		}
		
		// Check uniqueness
		final SignalEntity existing = dao.findByEmailAndUrlAndPath(dto.getEmail(), dto.getUrl(), dto.getPath());
		if (existing != null) {
			return Response.status(Status.CONFLICT).entity(GenericDTO.OBJECTCONFLICT.format(dto.getEmail(), dto.getUrl(), dto.getPath())).build();
		}
		
		// Check non premium
		final UserEntity owner = user.findByEmail(dto.getEmail());
		if (owner == null || owner.getPremium() == null || owner.getPremium().before(new Date())) {
			GenericDTO forbidden = dto.checkConstraints(Constraint.PREMIUM);
			if (forbidden != null ) {
				return Response.status(Status.BAD_REQUEST).entity(forbidden).build();
			}
		}
		
		// Create entity from DTO
		final SignalEntity entity = mapper.dtoToEntity(dto, null);

		// Check automate
		LogEntity failed = automate.execute(entity);
		
		if (failed != null) {
			LogDTO fail = logmap.entityToDto(failed);
			if (failed.getSource() != null) {
				// Set source url
				fail.setSource(Config.SERVICE_URL + "/" + SANDBOX_PATH + "/" + failed.getUuid());

				if (Config.MEMC_TIME != 0) {
					// Persist in cache
					mem.store(SANDBOX_PATH + "/" + failed.getUuid(), failed.getSource());
				} else {
					// Persist log without signal
					failed.setSignal(null);
					log.add(failed);
				}
			}
			return Response.status(Status.BAD_REQUEST).entity(GenericDTO.OBJECTFAILED.log(fail)).build();
		}

		// Send email
		if (!mailer.sendSignalCreation(entity)) {
			return Response.serverError().entity(GenericDTO.SENDMAILFAILED.format(dto.getEmail())).build();
		}

		// Persist new user in database
		if (owner == null) {
			user.add(entity.getUser());
		} else {
			entity.setUser(owner);
		}
		
		// Persist in database
		dao.add(entity);

		// Send response
		return Response.ok().entity(GenericDTO.OBJECTCREATED.signal(mapper.entityToDto(entity))).build();
	}

	@Override
	public Response getById(final String uuid) {
		// Retrieve entity from database
		final SignalEntity entity = dao.findById(uuid);
		
		// Nothing found in db
		if (entity == null) {
			return Response.status(Status.NOT_FOUND).entity(GenericDTO.OBJECTNOTFOUND.format(uuid)).build();
		}
		
		// Rollback
		SignalEntity parent = entity.getParent();
		if (parent != null) {
			mapper.restoreEntity(entity, parent);
			dao.update(parent);
			dao.delete(entity);
			return Response.ok().entity(GenericDTO.OBJECTRESTORED.signal(mapper.entityToDto(parent))).build();
		}
		
		// Update last access
		entity.setLastaccess(new Date());
		
		// Set active
		if (!entity.getActive()) {
			entity.setActive(true);
		}
		
		// Update entity
		dao.update(entity);

		// Retrieve dto from entity
		SignalDTO dto = mapper.entityToDto(entity);
	
		// Send response
		return Response.ok(dto).build();
	}
	
	@Override
	public Response getById(final String uuid, final String uuid2) {
		// Retrieve entity from database
		final LogEntity entity = log.findById(uuid2);

		// Nothing found in db
		if (entity == null) {
			return Response.status(Status.NOT_FOUND).type(MediaType.APPLICATION_JSON).entity(GenericDTO.OBJECTNOTFOUND.format(uuid2)).build();
		}

		// Check signal uuid
		if (uuid.equals(SANDBOX_PATH) && entity.getSignal() != null
				|| !entity.getSignal().getUuid().equals(uuid)) {
			return Response.status(Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(GenericDTO.OBJECTNOTLINKED.format(uuid2, uuid)).build();
		}

		// Check source
		if (entity.getSource() == null) {
			return Response.status(Status.NO_CONTENT).build();
		}

		// Send response
		return Response.ok().type(MediaType.TEXT_PLAIN).entity(entity.getSource()).build();
	}

	@Override
	public Response updateById(final String uuid, final SignalDTO dto) {
		// Nothing found in db
		final SignalEntity entity = dao.findById(uuid);
		if (entity == null) {
			return Response.status(Status.NOT_FOUND).entity(GenericDTO.OBJECTNOTFOUND.format(uuid)).build();
		}
		
		// Is read only
		if (Config.READ_ONLY.contains(entity.getUuid())) {
			return Response.status(Status.BAD_REQUEST).entity(GenericDTO.OBJECTREADONLY.format(uuid)).build();
		}

		// Is backup
		if (entity.getParent() != null) {
			return Response.status(Status.BAD_REQUEST).entity(GenericDTO.OBJECTBACKUP.format(uuid)).build();
		}

		// Check fields
		GenericDTO error = dto.checkConstraints(Constraint.SIZE, Constraint.EMAIL, Constraint.URL, Constraint.RESERVED, Constraint.BROWSER);
		if (error != null ) {
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		}
		
		// Check uniqueness
		final SignalEntity existing = dao.findByEmailAndUrlAndPath(dto.getEmail(), dto.getUrl(), dto.getPath());
		if (existing != null) {
			return Response.status(Status.CONFLICT).entity(GenericDTO.OBJECTCONFLICT.format(dto.getEmail(), dto.getUrl(), dto.getPath())).build();
		}
		
		// Check non premium
		if (entity.getUser().getPremium() == null || entity.getUser().getPremium().before(new Date())) {
			GenericDTO forbidden = dto.checkConstraints(Constraint.PREMIUM);
			if (forbidden != null ) {
				return Response.status(Status.BAD_REQUEST).entity(forbidden).build();
			}
		}
		
		// Backup entity
		SignalEntity backup = mapper.backupEntity(entity);
		
		// Update entity
		mapper.dtoToEntity(dto, entity);
		
		// Check automate
		if (Config.CHECK_ON_UPDATE) {
			LogEntity failed = automate.execute(entity);
		
			if (failed != null) {
				LogDTO fail = logmap.entityToDto(failed);
				if (failed.getSource() != null) {
					// Set source url
					fail.setSource(Config.SERVICE_URL + "/" + SANDBOX_PATH + "/" + failed.getUuid());

					if (Config.MEMC_TIME != 0) {
						// Persist in cache
						mem.store(SANDBOX_PATH + "/" + failed.getUuid(), failed.getSource());
					} else {
						// Persist log without signal
						failed.setSignal(null);
						log.add(failed);
					}
				}
				return Response.status(Status.BAD_REQUEST).entity(GenericDTO.OBJECTFAILED.log(fail)).build();
			}
		}
		
		// Send email
		if (!mailer.sendSignalModification(entity, backup)) {
			return Response.serverError().entity(GenericDTO.SENDMAILFAILED.format(dto.getEmail())).build();
		}

		// Update in database
		dao.add(backup);
		dao.update(entity);

		// Send response
		return Response.ok(GenericDTO.OBJECTUPDATED.signal(mapper.entityToDto(entity))).build();
	}
	
	@Override
	public Response getCount() {
		// Retrieve count from database
		Long count = dao.findCount();
		
		// Send response
		return Response.ok(count).build();
	}

	@Override
	public Response deleteById(String uuid) {
		// Nothing found in db
		final SignalEntity entity = dao.findById(uuid);
		if (entity == null) {
			return Response.status(Status.NOT_FOUND).entity(GenericDTO.OBJECTNOTFOUND.format(uuid)).build();
		}

		// Is read only
		if (Config.READ_ONLY.contains(entity.getUuid())) {
			return Response.status(Status.BAD_REQUEST).entity(GenericDTO.OBJECTREADONLY.format(uuid)).build();
		}

		// Is backup
		if (entity.getParent() != null) {
			return Response.status(Status.BAD_REQUEST).entity(GenericDTO.OBJECTBACKUP.format(uuid)).build();
		}

		// Is disabled
		if (!entity.getActive()) {
			return Response.status(Status.BAD_REQUEST).entity(GenericDTO.OBJECTDELETED.format(uuid)).build();
		}

		// Set inactive
		entity.setActive(false);

		// Send email
		if (!mailer.sendSignalDeletion(entity)) {
			return Response.serverError().entity(GenericDTO.SENDMAILFAILED.format(entity.getUser().getEmail())).build();
		}

		// Update in database
		dao.update(entity);

		// Send response
		return Response.ok().entity(GenericDTO.OBJECTDISABLED.signal(mapper.entityToDto(entity))).build();
	}

	@Override
	public Response unsubscribeNotification(String uuid) {
		// Retrieve entity from database
		final SignalEntity entity = dao.findById(uuid);

		// Nothing found in db
		if (entity == null) {
			return Response.status(Status.NOT_FOUND).type(MediaType.APPLICATION_JSON).entity(GenericDTO.OBJECTNOTFOUND.format(uuid)).build();
		}

		// Is read only
		if (Config.READ_ONLY.contains(entity.getUuid())) {
			return Response.status(Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(GenericDTO.OBJECTREADONLY.format(uuid)).build();
		}

		// Is disabled
		if (!entity.getActive()) {
			return Response.status(Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(GenericDTO.OBJECTDELETED.format(uuid)).build();
		}

		// Check if notify is set
		if (entity.getNotify() != null) {
			return Response.status(Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(GenericDTO.OBJECTUSBS.format(uuid)).build();
		}

		// Set inactive
		entity.setActive(false);

		// Update in database
		dao.update(entity);

		// Build response
		TemplateBuilder body = new TemplateBuilder(Config.HTML_UNSUBSCRIBE_NOTIFICATION);
		body.formatHtml(entity.getUuid(), entity.getUuid());

		// Send response
		return Response.ok().type(MediaType.TEXT_PLAIN).entity(body.toString()).build();
	}

	@Override
	public Response unsubscribeCertification(String uuid) {
		// Retrieve entity from database
		final SignalEntity entity = dao.findById(uuid);

		// Nothing found in db
		if (entity == null) {
			return Response.status(Status.NOT_FOUND).type(MediaType.APPLICATION_JSON).entity(GenericDTO.OBJECTNOTFOUND.format(uuid)).build();
		}

		// Is read only
		if (Config.READ_ONLY.contains(entity.getUuid())) {
			return Response.status(Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(GenericDTO.OBJECTREADONLY.format(uuid)).build();
		}

		// Is disabled
		if (!entity.getActive()) {
			return Response.status(Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(GenericDTO.OBJECTDELETED.format(uuid)).build();
		}

		// Check if certificate is set
		if (entity.getCertificate() == null) {
			return Response.status(Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(GenericDTO.OBJECTNTLS.format(uuid)).build();
		}

		// Backup entity
		SignalEntity backup = mapper.backupEntity(entity);

		// Disable TLS
		entity.setCertificate(null);

		// Update in database
		dao.add(backup);
		dao.update(entity);

		// Build response
		TemplateBuilder body = new TemplateBuilder(Config.HTML_UNSUBSCRIBE_CERTIFICATE);
		body.formatHtml(entity.getUuid(), backup.getUuid());

		// Send response
		return Response.ok().type(MediaType.TEXT_PLAIN).entity(body.toString()).build();
	}

}
