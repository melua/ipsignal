package com.ipsignal.job.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.ipsignal.Config;
import com.ipsignal.dao.WhoisDAO;
import com.ipsignal.entity.impl.WhoisEntity;
import com.ipsignal.job.UpdateWhoisJob;
import com.ipsignal.tool.IdFactory;
import com.ipsignal.whois.WhoisManager;

import lombok.extern.java.Log;

@Log
@Stateless
public class UpdateWhoisJobImpl implements UpdateWhoisJob {
	
	private static final int HEXID_LENGTH = 16;
	
	@EJB
	private WhoisDAO dao;
	@EJB
	private WhoisManager manager;

	public UpdateWhoisJobImpl() {
		// For injection
	}

	protected UpdateWhoisJobImpl(WhoisDAO dao, WhoisManager manager) {
		// For tests
		this.dao = dao;
		this.manager = manager;
	}

	@Override
	public void execute() {
		final String hexid = IdFactory.generateId(HEXID_LENGTH);

		final List<WhoisEntity> entities = dao.findExpired();
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
