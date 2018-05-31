package com.ipsignal.mock.mail;

import java.io.IOException;
import java.util.Arrays;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

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

import com.ipsignal.mail.impl.MailManagerImpl;

public class MailManagerMock extends MailManagerImpl {

	@Override
	protected void send(MimeMessage message) throws MessagingException {
		System.out.println("From: " + Arrays.toString(message.getHeader("From")));
		System.out.println("To: " + Arrays.toString(message.getHeader("To")));
		if (message.getHeader("List-Unsubscribe") != null) {
			System.out.println("List-Unsubscribe: " + Arrays.toString(message.getHeader("List-Unsubscribe")));
		}
		try {
			System.out.println(message.getContent());
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
}
