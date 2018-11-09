package com.ipsignal.job.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.ipsignal.Config;
import com.ipsignal.dao.WhoisDAO;
import com.ipsignal.entity.impl.WhoisEntity;
import com.ipsignal.job.RunWhoisJob;
import com.ipsignal.tool.IdFactory;
import com.ipsignal.whois.WhoisManager;

@Stateless
public class RunWhoisJobImpl implements RunWhoisJob {
	
	private static final int HEXID_LENGTH = 16;
	private static final Logger LOGGER = Logger.getLogger(RunWhoisJobImpl.class.getName());
	
	@EJB
	private WhoisDAO dao;
	@EJB
	private WhoisManager manager;

	public RunWhoisJobImpl() {
		// For injection
	}

	protected RunWhoisJobImpl(WhoisDAO dao, WhoisManager manager) {
		// For tests
		this.dao = dao;
		this.manager = manager;
	}

	@Override
	public void execute() {
		final String hexid = IdFactory.generateId(HEXID_LENGTH);

		final List<WhoisEntity> entities = dao.findWaiting();
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.log(Level.FINE, "Job [{0}] found {1} whois to process", new Object[] { hexid, entities.size() });
		}

		if (!Config.DISABLE_JOBS && !entities.isEmpty()) {

			long start = Calendar.getInstance().getTimeInMillis();

			for (WhoisEntity entity : entities) {
				// Execute whois
				manager.execute(entity);
				
				// Update access
				entity.setAccess(new Date());
				
				// Persist in database
				dao.update(entity);	
			}

			long end = Calendar.getInstance().getTimeInMillis();
			long duration = (end - start) / 1000;
			LOGGER.log(Level.INFO, "Job [{0}] processed {1} signals in {2} seconds", new Object[] { hexid, entities.size(), duration });
		}
	}
}
