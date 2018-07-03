package com.ipsignal.stub.file;

import java.io.File;
import java.io.IOException;

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

import com.ipsignal.file.impl.FileManagerImpl;
import com.ipsignal.mapper.impl.SignalMapperImpl;

public class FileManagerStub extends FileManagerImpl {
	
	public FileManagerStub() {
		super(new SignalMapperImpl());
	}
	
	@Override
	protected void doWrite(File file, byte[] bytes) throws IOException {
		System.out.println("Wrote " + bytes.length + " bytes to " + file.getAbsolutePath());
	}

}
