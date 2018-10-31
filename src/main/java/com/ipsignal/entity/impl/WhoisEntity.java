package com.ipsignal.entity.impl;

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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.ipsignal.entity.Entity;
import com.ipsignal.tool.IdFactory;

@javax.persistence.Entity
@Table(name = "whois")
@NamedQueries({ @NamedQuery(name = "Whois.findByDomain", query = "SELECT a FROM WhoisEntity a WHERE a.domain = :domain") })
public class WhoisEntity implements Entity {
	
	private static final int[] HEXID_LENGTH = {8,8};

	@Id
	@Column(name = "id")
	private String id;
	
	@Column(name = "domain")
	private String domain;

	@Column(name = "created")
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	
	@Column(name = "updated")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;
	
	@Column(name = "expires")
	@Temporal(TemporalType.TIMESTAMP)
	private Date expires;
	
	@Column(name = "access")
	@Temporal(TemporalType.TIMESTAMP)
	private Date access;
	
	@OneToMany(mappedBy = "whois", fetch=FetchType.LAZY)
    private List<SignalEntity> signals;
	
	public WhoisEntity() {
		// for manager
		this.id = IdFactory.generateId(HEXID_LENGTH);
		this.access = new Date();
	}
	
	public WhoisEntity(String domain, Date created, Date updated, Date expires) {
		this();
		this.domain = domain;
		this.created = created;
		this.updated = updated;
		this.expires = expires;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public Date getExpires() {
		return expires;
	}

	public void setExpires(Date expires) {
		this.expires = expires;
	}

	public Date getAccess() {
		return access;
	}

	public void setAccess(Date access) {
		this.access = access;
	}
	
	public List<SignalEntity> getSignals() {
		return signals;
	}

	public void setSignals(List<SignalEntity> signal) {
		this.signals = signal;
	}

}