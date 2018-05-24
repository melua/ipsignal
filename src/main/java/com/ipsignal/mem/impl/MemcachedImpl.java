package com.ipsignal.mem.impl;

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

import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;

import com.ipsignal.Config;
import com.ipsignal.mem.Memcached;

import net.spy.memcached.MemcachedClient;
import net.spy.memcached.transcoders.SerializingTranscoder;

@Stateful
public class MemcachedImpl implements Memcached {

	private static final Logger LOGGER = Logger.getLogger(Memcached.class.getName());
	private static final String KEYBASE = Config.SERVICE_URL.getHost().concat("/");

	private MemcachedClient client;
	private SerializingTranscoder trans;

	public MemcachedImpl() {
		trans = new SerializingTranscoder();
		trans.setCompressionThreshold(Integer.MAX_VALUE);
		if (Config.MEMC_TIME != 0) {
			try {
				client = new MemcachedClient(new InetSocketAddress(Config.MEMC_HOST, Config.MEMC_PORT));
			} catch (Exception ex) {
				LOGGER.log(Level.SEVERE, "Error with Memcached: {0}", ex.getMessage());
			}
		}
	}

	@Override
	public void store(String key, String value) {
		client.set(KEYBASE + key, Config.MEMC_TIME, value, trans);
	}

	@Override
	public void remove(String key) {
		client.delete(KEYBASE + key);
	}

}
