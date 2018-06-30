package com.ipsignal.file;

import javax.ejb.Asynchronous;

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

import javax.ejb.Local;

import com.ipsignal.dto.impl.SignalDTO;

@Local(FileManager.class)
public interface FileManager {

	/**
	 * Write TLV on disk
	 * @param dto to write
	 * @param uid of the signal
	 * @return true if successful, false otherwise
	 */
	boolean writeToDisk(SignalDTO dto, String uid);
	
	
	/**
	 * Write TLV on disk when available
	 * without waiting for the return
	 * @param dto to write
	 * @param uid of the signal
	 */
	@Asynchronous
	void writeToDiskAsync(SignalDTO dto, String uid);

}