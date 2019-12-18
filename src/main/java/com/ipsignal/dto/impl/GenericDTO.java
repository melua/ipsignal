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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(staticName = "from", access = AccessLevel.PRIVATE)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GenericDTO implements DTO {
	
	// Success
	public static final GenericDTO OBJECTCREATED = from("OK-RES-CREATED", "The resource was successfully created. You will receive an email with the information.", null);
	public static final GenericDTO OBJECTUPDATED = from("OK-RES-UPDATED", "The resource was successfully updated.", null);
	public static final GenericDTO OBJECTRESTORED = from("OK-RES-RESTORED", "The resource was successfully restored.", null);
	public static final GenericDTO OBJECTDISABLED = from("OK-RES-RESTORED", "The resource was successfully deactivated.", null);
	
	// Resource
	public static final GenericDTO OBJECTNOTFOUND = from("ERR-RES-FIND", "The resource [%s] is not found.", null);
	public static final GenericDTO OBJECTDELETED = from("ERR-RES-DEL", "The resource [%s] is already deleted.", null);
	public static final GenericDTO OBJECTREADONLY = from("ERR-RES-RO", "The resource [%s] is read only.", null);
	public static final GenericDTO OBJECTNOTLINKED = from("ERR-RES-LINK", "The resource [%s] is not linked to the resource [%s].", null);
	public static final GenericDTO OBJECTBACKUP = from("ERR-RES-BCK", "The resource [%s] cannot be modified.", null);
	public static final GenericDTO OBJECTCONFLICT = from("ERR-RES-DUP", "The resource for [%s] and [%s] and [%s] already exists.", null);
	public static final GenericDTO OBJECTUSBS = from("ERR-RES-USBS", "The resource [%s] notifies by URL.", null);
	public static final GenericDTO OBJECTNTLS = from("ERR-RES-NTLS", "The resource [%s] do not check certificate.", null);
	
	// Attribute
	public static final GenericDTO INVALIDEMAIL = from("ERR-ATT-MAIL", "The email address [%s] is not correct.", null);
	public static final GenericDTO INVALIDSIZE = from("ERR-ATT-SIZE", "The field [%s] must not exceed [%d] characters.", null);
	public static final GenericDTO INVALIDNULL = from("ERR-ATT-NULL", "The field [%s] must be filled in.", null);
	public static final GenericDTO INVALIDURL = from("ERR-ATT-URL", "The url [%s] is not correct.", null);
	public static final GenericDTO RESERVEDNUMBER = from("ERR-ATT-INT", "The field [%s] only accept numbers %s.", null);
	public static final GenericDTO RESERVEDSTRING = from("ERR-ATT-STR", "The field [%s] only accept strings %s.", null);
	public static final GenericDTO INVALIDREGEXP = from("ERR-ATT-RGXP", "The regexp [%s] is not correct.", null);
	public static final GenericDTO INVALIDXPATH = from("ERR-ATT-XPTH", "The xpath [%s] is not correct.", null);
	public static final GenericDTO PREMIUMNUMBER = from("ERR-PRE-INT", "The numbers %s for field [%s] requires a premium account.", null);
	public static final GenericDTO PREMIUMSTRING = from("ERR-PRE-STR", "The strings %s for field [%s] requires a premium account.", null);
	
	// Server
	public static final GenericDTO SENDMAILFAILED = from("ERR-SRV-MAIL", "Sending e-mail to [%s] failed, please try later.", null);
	public static final GenericDTO SERVERVERSION = from("OK-SRV-VERSION", "%s", null);

	private String code;
	private String detail;
	private SignalDTO beacon;
	
	public GenericDTO format(final Object... args) {
		return from(this.getCode(), String.format(this.getDetail(), args), null);
	}
	
	public GenericDTO signal(final SignalDTO signal) {
		return from(this.getCode(), this.getDetail(), signal);
	}

}
