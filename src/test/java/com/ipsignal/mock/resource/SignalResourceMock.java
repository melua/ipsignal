package com.ipsignal.mock.resource;

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

import com.ipsignal.mapper.impl.LogMapperImpl;
import com.ipsignal.mock.automate.AutomateMock;
import com.ipsignal.mock.mail.MailManagerMock;
import com.ipsignal.mock.mapper.SignalMapperMock;
import com.ipsignal.resource.impl.SignalResourceImpl;
import com.ipsignal.stub.dao.LogDAOStub;
import com.ipsignal.stub.dao.SignalDAOStub;
import com.ipsignal.stub.dao.UserDAOStub;
import com.ipsignal.stub.mem.MemcachedStub;

public class SignalResourceMock extends SignalResourceImpl {
	
	public SignalResourceMock() {
		super(new SignalMapperMock(), new LogMapperImpl(), new SignalDAOStub(), new UserDAOStub(), new LogDAOStub(), new MailManagerMock(), new AutomateMock(), new MemcachedStub());
	}

}
