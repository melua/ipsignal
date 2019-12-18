package com.ipsignal.automate;

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

import com.ipsignal.entity.impl.LogEntity;
import com.ipsignal.entity.impl.SignalEntity;

public interface Automate {
	
	/**
	 * Execute a signal
	 * @param signal to test
	 * @param feedback if set to true also return a log when successful
	 * @return a log in case of failure
	 */
	LogEntity execute(SignalEntity signal, boolean feedback);

	/**
	 * Execute a signal when available
	 * without waiting for the return
	 * @param signal to test
	 */
	//@Asynchronous
	void executeAsync(SignalEntity signal);

}
