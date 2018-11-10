package com.ipsignal.invoker;

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

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.jobs.ee.ejb.EJB3InvokerJob;

public abstract class Invoker extends EJB3InvokerJob implements Job {
	
	@Override
	public final void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();
		dataMap.put(INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.LocalInitialContextFactory");
		dataMap.put(EJB_JNDI_NAME_KEY, getJndiName());
		dataMap.put(EJB_METHOD_KEY, "execute");
		if (getArgs() != null) {
			dataMap.put(EJB_ARGS_KEY, getArgs());	
		}
		super.execute(jobExecutionContext);
	}
	
	protected Object[] getArgs() {
		return null;
	}
	
	protected abstract String getJndiName();
	
}
