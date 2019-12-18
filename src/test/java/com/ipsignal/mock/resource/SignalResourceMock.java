package com.ipsignal.mock.resource;

import com.ipsignal.Config;
import com.ipsignal.mock.automate.AutomateMock;
import com.ipsignal.mock.mail.MailManagerMock;
import com.ipsignal.mock.mapper.SignalMapperMock;
import com.ipsignal.resource.impl.SignalResourceImpl;
import com.ipsignal.stub.dao.LogDAOStub;
import com.ipsignal.stub.dao.SignalDAOStub;
import com.ipsignal.stub.dao.UserDAOStub;
import com.ipsignal.stub.dao.WhoisDAOStub;
import com.ipsignal.stub.file.FileManagerStub;

public class SignalResourceMock extends SignalResourceImpl {
		
	public SignalResourceMock(Config config) {
		super(new SignalMapperMock(config), new SignalDAOStub(), new WhoisDAOStub(), new UserDAOStub(), new LogDAOStub(), new MailManagerMock(config), new AutomateMock(120, 3, AutomateMock.RESPONSE200, null, config), new FileManagerStub(config), config);
	}

}
