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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.ipsignal.entity.Entity;
import com.ipsignal.tool.IdFactory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "signals")
@javax.persistence.Entity
@NamedQueries({ @NamedQuery(name = "Signal.findByEmailAndUrlAndPath", query = "SELECT a FROM SignalEntity a WHERE a.user.email LIKE :email AND a.url LIKE :url AND a.path LIKE :path"),
	@NamedQuery(name = "Signal.findByInterval", query = "SELECT a FROM SignalEntity a WHERE a.interval = :interval AND a.active = true"),
	@NamedQuery(name = "Signal.findExpired", query = "SELECT a FROM SignalEntity a WHERE a.lastaccess < :min"),
	@NamedQuery(name = "Signal.findCount", query = "SELECT COUNT(a) FROM SignalEntity a")})
public class SignalEntity implements Entity, Comparable<SignalEntity> {

	private static final int[] HEXID_LENGTH = {8,4,4};

	@Id
	@Column(name = "id")
	private String id;
	
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "user")
	private UserEntity user;
	
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "parent")
	private SignalEntity parent;
	
	@Column(name = "active")
	private Boolean active;
	
	@Column(name = "url")
	private String url;
	
	@Column(name = "browser")
	private String browser;
	
	@Column(name = "certificate")
	private Integer certificate;
	
	@Column(name = "latency")
	private Integer latency;
	
	@Column(name = "path")
	private String path;
	
	@Column(name = "expected")
	private String expected;
	
	@Column(name = "notify")
	private String notify;
	
	@Column(name = "intervaal")
	private Integer interval; // reserved keyword
	
	@Column(name = "retention")
	private Integer retention;
	
	@Column(name = "lastaccess")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastaccess;
	
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "whois")
	private WhoisEntity whois;
	
	@OneToMany(mappedBy = "signal", fetch=FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<LogEntity> logs;
	
	@OneToMany(mappedBy = "parent", fetch=FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<SignalEntity> children;
	
	public SignalEntity() {
		// for manager
		this.id = IdFactory.generateId(HEXID_LENGTH);
		this.logs = new ArrayList<>();
		this.children = new ArrayList<>();
		this.lastaccess = new Date();
	}

	public SignalEntity(UserEntity user, SignalEntity parent, Boolean active, String url, String browser, Integer certificate, Integer latency, String path, String expected,
			String notify, Integer interval, Integer retention, WhoisEntity whois) {
		this();
		this.user = user;
		this.parent = parent;
		this.active = active;
		this.url = url;
		this.browser = browser;
		this.certificate = certificate;
		this.latency = latency;
		this.path = path;
		this.expected = expected;
		this.notify = notify;
		this.interval = interval;
		this.retention = retention;
		this.whois = whois;
	}

	@Override
	public int compareTo(SignalEntity o) {
		if (this.getUser().getPremium() != null) {
			return o.getUser().getPremium() != null ? 0 : 1;
		} else {
			return o.getUser().getPremium() != null ? -1 : 0;
		}
	}

}
