package com.ipsignal.mapper;

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
import com.ipsignal.entity.impl.SignalEntity;

@Local(SignalMapper.class)
public interface SignalMapper extends Mapper<SignalEntity, SignalDTO> {
	
	/**
	 * Create a backup copy of the given signal
	 * @param parent signal to backup
	 * @return a backup copy
	 */
	SignalEntity backupEntity(SignalEntity parent);

	/**
	 * Restore the parent signal with the backup copy
	 * @param child signal used to restore
	 * @param parent signal to restore
	 */
	void restoreEntity(SignalEntity child, SignalEntity parent);
	
}
