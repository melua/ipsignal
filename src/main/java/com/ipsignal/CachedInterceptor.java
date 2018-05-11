package com.ipsignal;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

import com.ipsignal.annotation.Cached;
import com.ipsignal.dto.impl.GenericDTO;

@Cached
@Provider
public class CachedInterceptor implements WriterInterceptor {
	
	@Context
	private UriInfo uriInfo;

    @Override
    public void aroundWriteTo(WriterInterceptorContext context)
            throws IOException {
    	
    	if (Config.MEMC_TIME == 0 || context.getEntity() instanceof GenericDTO) {
    		context.proceed();
    		return;
    	}

        OutputStream original = context.getOutputStream();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try {
            context.setOutputStream(buffer);
            context.proceed();
            Memcached.add(uriInfo.getPath(), buffer.toString(Charset.defaultCharset().toString()));
            original.write(buffer.toByteArray());
        } finally {
            context.setOutputStream(original);
        }
    }
}