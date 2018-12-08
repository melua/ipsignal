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
@Table(name = "users")
@javax.persistence.Entity
@NamedQueries({ @NamedQuery(name = "User.findByEmail", query = "SELECT a FROM UserEntity a WHERE a.email LIKE :email"),
				@NamedQuery(name = "User.findByPremium", query = "SELECT a FROM UserEntity a WHERE a.premium = :date"),
				@NamedQuery(name = "User.findAlone", query = "SELECT a FROM UserEntity a WHERE a.signals IS EMPTY AND a.premium IS NULL"),
				@NamedQuery(name = "User.findExpired", query = "SELECT a FROM UserEntity a WHERE a.premium < :min") })
public class UserEntity implements Entity {

	private static final int HEXID_LENGTH = 16;

	@Id
	@Column(name = "id")
	private String id;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "premium")
	@Temporal(TemporalType.TIMESTAMP)
	private Date premium;
	
	@OneToMany(mappedBy = "user", fetch=FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<SignalEntity> signals;
	
	public UserEntity() {
		// for manager
		this.id = IdFactory.generateId(HEXID_LENGTH);
		this.signals = new ArrayList<>();
	}

	public UserEntity(String email, Date premium) {
		this();
		this.email = email;
		this.premium = premium;
	}

}
