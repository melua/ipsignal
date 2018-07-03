package com.ipsignal.file.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.ipsignal.Config;
import com.ipsignal.dto.impl.SignalDTO;
import com.ipsignal.file.FileManager;
import com.ipsignal.mapper.SignalMapper;

/*
 * Copyright (C) 2015  Kevin Guignard
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

@Stateless
public class FileManagerImpl implements FileManager {
	
	private static final Logger LOGGER = Logger.getLogger(FileManagerImpl.class.getName());
	
	@EJB
	private SignalMapper mapper;
	
	public FileManagerImpl() {
		// For injection
	}
	
	protected FileManagerImpl(SignalMapper mapper) {
		// For tests
		this.mapper = mapper;
	}

	@Override
	public boolean writeToDisk(SignalDTO dto, final String uid) {
		
		byte[] bytes = mapper.DtoToTlv(dto);
		
		try {
			final File parent = new File(new File(Config.FILER_PATH, uid.substring(0, 1)), uid.substring(1, 3));
			parent.mkdirs();
			
			// Please check that java have write right
			File file = new File(parent, uid + ".bin");
			this.doWrite(file, bytes);

			return true;

		} catch (final IOException | NullPointerException ex) {
			LOGGER.log(Level.WARNING, "Error while creating on filesystem ", ex);
			return false;
		}
	}

	@Override
	public void writeToDiskAsync(SignalDTO dto, String uid) {
		this.writeToDisk(dto, uid);
	}
	
	protected void doWrite(File file, byte[] bytes) throws IOException {
		Files.write(file.toPath(), bytes);
	}

}