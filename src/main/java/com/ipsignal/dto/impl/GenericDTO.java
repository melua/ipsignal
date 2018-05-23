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
public class GenericDTO implements DTO {
	
	// Success
	public static final GenericDTO OBJECTCREATED = new GenericDTO("OK-RES-CREATED", "The resource was successfully created. You will receive an email with the information.");
	public static final GenericDTO OBJECTUPDATED = new GenericDTO("OK-RES-UPDATED", "The resource was successfully updated.");
	public static final GenericDTO OBJECTRESTORED = new GenericDTO("OK-RES-RESTORED", "The resource was successfully restored.");
	public static final GenericDTO OBJECTDISABLED = new GenericDTO("OK-RES-RESTORED", "The resource was successfully deactivated.");
	
	// Resource
	public static final GenericDTO OBJECTNOTFOUND = new GenericDTO("ERR-RES-FIND", "The resource [%s] is not found.");
	public static final GenericDTO OBJECTDELETED = new GenericDTO("ERR-RES-DEL", "The resource [%s] is already deleted.");
	public static final GenericDTO OBJECTREADONLY = new GenericDTO("ERR-RES-RO", "The resource [%s] is read only.");
	public static final GenericDTO OBJECTNOTLINKED = new GenericDTO("ERR-RES-LINK", "The resource [%s] is not linked to the resource [%s].");
	public static final GenericDTO OBJECTBACKUP = new GenericDTO("ERR-RES-BCK", "The resource [%s] cannot be modified.");
	public static final GenericDTO OBJECTCONFLICT = new GenericDTO("ERR-RES-DUP", "The resource for [%s] and [%s] and [%s] already exists.");
	public static final GenericDTO OBJECTUSBS = new GenericDTO("ERR-RES-USBS", "The resource [%s] notifies by URL.");
	public static final GenericDTO OBJECTNTLS = new GenericDTO("ERR-RES-NTLS", "The resource [%s] do not check certificate.");
	
	// Attribute
	public static final GenericDTO INVALIDEMAIL = new GenericDTO("ERR-ATT-MAIL", "The email address [%s] is not correct.");
	public static final GenericDTO INVALIDSIZE = new GenericDTO("ERR-ATT-SIZE", "The field [%s] must not exceed [%d] characters.");
	public static final GenericDTO INVALIDNULL = new GenericDTO("ERR-ATT-NULL", "The field [%s] must be filled in.");
	public static final GenericDTO INVALIDURL = new GenericDTO("ERR-ATT-URL", "The url [%s] is not correct.");
	public static final GenericDTO RESERVEDNUMBER = new GenericDTO("ERR-ATT-INT", "The field [%s] only accept numbers %s.");
	public static final GenericDTO RESERVEDSTRING = new GenericDTO("ERR-ATT-STR", "The field [%s] only accept strings %s.");
	public static final GenericDTO INVALIDREGEXP = new GenericDTO("ERR-ATT-RGXP", "The regexp [%s] is not correct.");
	public static final GenericDTO INVALIDXPATH = new GenericDTO("ERR-ATT-XPTH", "The xpath [%s] is not correct.");
	public static final GenericDTO PREMIUMNUMBER = new GenericDTO("ERR-PRE-INT", "The numbers %s for field [%s] requires a premium account.");
	public static final GenericDTO PREMIUMSTRING = new GenericDTO("ERR-PRE-STR", "The strings %s for field [%s] requires a premium account.");
	
	// Server
	public static final GenericDTO SENDMAILFAILED = new GenericDTO("ERR-SRV-MAIL", "Sending e-mail to [%s] failed, please try later.");
	
	private String code;
	private String detail;
	private LogDTO log;
	private SignalDTO beacon;
	
	public GenericDTO() {
		// for the marshaller
	}

	private GenericDTO(final String code, final String detail) {
		this.code = code;
		this.detail = detail;
	}
	
	private GenericDTO(final String code, final String detail, final LogDTO log) {
		this.code = code;
		this.detail = detail;
		this.log = log;
	}
	
	private GenericDTO(final String code, final String detail, final SignalDTO signal) {
		this.code = code;
		this.detail = detail;
		this.beacon = signal;
	}

	public GenericDTO format(final Object... args) {
		return new GenericDTO(this.getCode(), String.format(this.getDetail(), args));
	}
	
	public GenericDTO log(final LogDTO log) {
		return new GenericDTO(this.getCode(), this.getDetail(), log);
	}
	
	public GenericDTO signal(final SignalDTO signal) {
		return new GenericDTO(this.getCode(), this.getDetail(), signal);
	}

	public String getCode() {
		return code;
	}

	public String getDetail() {
		return detail;
	}

	public LogDTO getLog() {
		return log;
	}

	public SignalDTO getBeacon() {
		return beacon;
	}

}
