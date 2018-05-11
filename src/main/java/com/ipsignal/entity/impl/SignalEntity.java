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

import com.ipsignal.UID;
import com.ipsignal.entity.Entity;

@javax.persistence.Entity
@Table(name = "signals")
@NamedQueries({ @NamedQuery(name = "Signal.findByEmailAndUrlAndPath", query = "SELECT a FROM SignalEntity a WHERE a.user.email LIKE :email AND a.url LIKE :url AND a.path LIKE :path"),
	@NamedQuery(name = "Signal.findByInterval", query = "SELECT a FROM SignalEntity a WHERE a.interval = :interval AND a.active = true"),
	@NamedQuery(name = "Signal.findExpired", query = "SELECT a FROM SignalEntity a WHERE a.lastaccess < :min"),
	@NamedQuery(name = "Signal.findCount", query = "SELECT COUNT(a) FROM SignalEntity a")})
public class SignalEntity implements Entity, Comparable<SignalEntity> {

	@Id
	@Column(name = "id")
	private String uuid;
	
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
	
	@OneToMany(mappedBy = "signal", fetch=FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<LogEntity> logs;
	
	@OneToMany(mappedBy = "parent", fetch=FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<SignalEntity> children;
	
	public SignalEntity() {
		// for manager
		this.uuid = UID.randomUID(8,4,4);
		this.logs = new ArrayList<>();
		this.children = new ArrayList<>();
		this.lastaccess = new Date();
	}

	public SignalEntity(UserEntity user, SignalEntity parent, Boolean active, String url, String browser, Integer certificate, Integer latency, String path, String expected,
			String notify, Integer interval, Integer retention) {
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
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	public Integer getCertificate() {
		return certificate;
	}

	public void setCertificate(Integer certificate) {
		this.certificate = certificate;
	}
	
	public Integer getLatency() {
		return latency;
	}

	public void setLatency(Integer latency) {
		this.latency = latency;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getExpected() {
		return expected;
	}

	public void setExpected(String expected) {
		this.expected = expected;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public String getNotify() {
		return notify;
	}

	public void setNotify(String notify) {
		this.notify = notify;
	}

	public Integer getInterval() {
		return interval;
	}

	public void setInterval(Integer interval) {
		this.interval = interval;
	}

	public Integer getRetention() {
		return retention;
	}

	public void setRetention(Integer retention) {
		this.retention = retention;
	}

	public Date getLastaccess() {
		return lastaccess;
	}

	public void setLastaccess(Date lastaccess) {
		this.lastaccess = lastaccess;
	}

	public List<LogEntity> getLogs() {
		return logs;
	}

	public void setLogs(List<LogEntity> logs) {
		this.logs = logs;
	}

	public SignalEntity getParent() {
		return parent;
	}

	public void setParent(SignalEntity parent) {
		this.parent = parent;
	}

	public List<SignalEntity> getChildren() {
		return children;
	}

	public void setChildren(List<SignalEntity> children) {
		this.children = children;
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
