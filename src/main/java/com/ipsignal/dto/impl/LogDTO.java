package com.ipsignal.dto.impl;

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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.ipsignal.dto.DTO;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class LogDTO implements DTO {
	
	private String access;
	private Integer latency;
	private Integer certificate;
	private String browser;
	private Integer http;
	private String obtained;
	private String detail;
	private String source;
	
	public LogDTO() {
		// for the marshaller
	}
	
	public LogDTO(String access, Integer latency, Integer certificate, String browser, Integer http, String obtained, String detail, String source) {
		this.access = access;
		this.latency = latency;
		this.certificate = certificate;
		this.browser = browser;
		this.http = http;
		this.obtained = obtained;
		this.detail = detail;
		this.source = source;
	}
	
	public String getAccess() {
		return access;
	}

	public void setAccess(String access) {
		this.access = access;
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

}
