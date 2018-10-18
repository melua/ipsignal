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

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.ipsignal.entity.Entity;
import com.ipsignal.tool.IdFactory;

@javax.persistence.Entity
@Table(name = "logs")
@NamedQueries({ @NamedQuery(name = "Log.findExpired", query = "SELECT a FROM LogEntity a WHERE a.signal IS NULL AND a.access < :min") })
public class LogEntity implements Entity, Comparable<LogEntity> {
	
	public static final LogEntity LATENCY = new LogEntity("Latency higher than [%d] ms.");
	public static final LogEntity TIMEOUT = new LogEntity("Host timeout after %d seconds.");
	public static final LogEntity UNKNOWNHOST = new LogEntity("Unknown [%s] hostname.");
	public static final LogEntity EQUALS = new LogEntity("Content doesn't equal [%s].");
	public static final LogEntity MATCHES = new LogEntity("Content doesn't match regex [%s].");
	public static final LogEntity SSL = new LogEntity("Certificate expirates in less than [%d] days.");
	public static final LogEntity REACHABILITY = new LogEntity("Host [%s] not reachable.");
	public static final LogEntity PAGESIZE = new LogEntity("Page length higher than [%d] bytes.");
	public static final LogEntity HTTP = new LogEntity("Invalid HTTP code.");
	public static final LogEntity PARSER = new LogEntity("Content cannot be parsed.");
	public static final LogEntity URL = new LogEntity("Invalid URL.");
	public static final LogEntity XPATH = new LogEntity("Invalid XPath.");
	public static final LogEntity REGEX = new LogEntity("Invalid regex pattern.");
	public static final LogEntity BROWSER = new LogEntity("Invalid browser name.");
	public static final LogEntity SUCCESS = new LogEntity("Test successful.");

	private static final int[] HEXID_LENGTH = {4,12};

	@Id
	@Column(name = "id")
	private String id;
	
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "signaal")
	private SignalEntity signal; // reserved keyword

	@Column(name = "latency")
	private Integer latency;
	
	@Column(name = "certificate")
	private Integer certificate;
	
	@Column(name = "browser")
	private String browser;
	
	@Column(name = "http")
	private Integer http;
	
	@Column(name = "obtained")
	private String obtained;
	
	@Column(name = "detail")
	private String detail;
	
	@Column(name = "source")
	private String source;

	@Column(name = "access")
	@Temporal(TemporalType.TIMESTAMP)
	private Date access;
	
	public LogEntity() {
		// for manager
		this.id = IdFactory.generateId(HEXID_LENGTH);
		this.access = new Date();
	}
	
	private LogEntity(String detail) {
		this.detail = detail;
	}

	private LogEntity(SignalEntity signal, Integer latency, Integer certificate, String browser, Integer http, String obtained, String detail, String source) {
		this();
		this.signal = signal;
		this.latency = latency;
		this.certificate = certificate;
		this.browser = browser;
		this.http = http;
		this.obtained = obtained;
		this.detail = detail;
		this.source = source;
	}
	
	public LogEntity getInstance(SignalEntity signal, Integer latency, Integer certificate, String browser, Integer http, String obtained, String source, Object... args) {
		return new LogEntity(signal, latency, certificate, browser, http, obtained, String.format(this.getDetail(), args), source);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public SignalEntity getSignal() {
		return signal;
	}

	public void setSignal(SignalEntity signal) {
		this.signal = signal;
	}

	public Integer getLatency() {
		return latency;
	}

	public void setLatency(Integer latency) {
		this.latency = latency;
	}
	
	public Integer getCertificate() {
		return certificate;
	}

	public void setCertificate(Integer certificate) {
		this.certificate = certificate;
	}
	
	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	public Integer getHttp() {
		return http;
	}

	public void setHttp(Integer http) {
		this.http = http;
	}

	public String getObtained() {
		return obtained;
	}

	public void setObtained(String obtained) {
		this.obtained = obtained;
	}
	
	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Date getAccess() {
		return access;
	}

	public void setAccess(Date access) {
		this.access = access;
	}

	@Override
	public int compareTo(LogEntity o) {
		return this.getAccess().after(o.getAccess()) ? 1 : -1;
	}

}
