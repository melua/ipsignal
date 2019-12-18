package com.ipsignal.tool;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ipsignal.Config;

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

import lombok.extern.java.Log;

@Log
public class TemplateBuilder {
	
	private static final String LINK_SEPARATOR = "/";
	public static final String BEGIN_TAG = "{";
	public static final String CLOSE_TAG = "}";
	public static final String UNSET_TAG = "(n/a)";

	private String value;
	private Config config;

	/**
	 * Create a new TemplateBuilder
	 * initialized with the given template
	 * @param template
	 */
	public TemplateBuilder(String template, Config config) {
		this.value = template;
		this.config = config;
		formatBrand(config.getBrandName());
	}

	/**
	 * Replace original (inside {@value #BEGIN_TAG} and {@value #CLOSE_TAG} enclosing tags)
	 * with replacement or {@value #UNSET_TAG} if replacement is null
	 * @param original
	 * @param replacement
	 */
	public void replace(String original, Object replacement) {
		Matcher matcher = Pattern.compile(BEGIN_TAG + original + CLOSE_TAG, Pattern.LITERAL).matcher(value);
		if (matcher.find()) {
			value = matcher.replaceFirst(replacement != null ? Matcher.quoteReplacement(String.valueOf(replacement)) : UNSET_TAG);
		} else {
			LOGGER.log(Level.WARNING, "Missing template tag {0}", original);
		}
	}

	/**
	 * Replace template with {@link SignalEntity} attributes
	 * @param entity
	 */
	public void formatSignal(SignalEntity entity) {
		replace("url", entity.getUrl());
		replace("latency", entity.getLatency());
		replace("certificate", entity.getCertificate());
		replace("path", entity.getPath());
		replace("expected", entity.getExpected());
		replace("interval", entity.getInterval());
		replace("notify", entity.getNotify());
		replace("retention", entity.getRetention());
	}

	/**
	 * Replace template with {@link LogEntity} attributes
	 * @param entity
	 */
	public void formatLog(LogEntity entity) {
		replace("access", entity.getAccess());
		replace("latency", entity.getLatency());
		replace("certificate", entity.getCertificate());
		replace("http", entity.getHttp());
		replace("obtained", entity.getObtained());
		replace("detail", entity.getDetail());
	}

	/**
	 * Replace template link with given values
	 * @param values
	 */
	public void formatLink(String... values) {
		StringBuilder link = new StringBuilder();
		for (String val : values) {
			link.append(LINK_SEPARATOR);
			link.append(val);
		}
		replace("link", config.getServiceUrl() + link.toString());
	}

	/**
	 * Replace template unsubscribe with given value
	 * @param unsubscribe
	 */
	public void formatUnsubscribe(String unsubscribe) {
		replace("unsubscribe", unsubscribe);
	}

	/**
	 * Replace template brand with given value
	 * @param brand
	 */
	private void formatBrand(String brand) {
		replace("brand", brand);
	}

	/**
	 * Replace template id and cancel link with given values
	 * @param id
	 * @param cancel
	 */
	public void formatHtml(String id, String cancel) {
		replace("id", id);
		replace("link", config.getServiceUrl() + LINK_SEPARATOR + cancel);
	}

	/**
	 * Replace template days with given value
	 * @param days
	 */
	public void formatDays(Integer days) {
		replace("days", days);
	}

	/**
	 * Replace template domain with given value
	 * @param url
	 */
	public void formatDomain(String url) {
		try {
			replace("domain", new URI(url).getHost());
		} catch (URISyntaxException ex) {
			LOGGER.log(Level.WARNING, "Invalid url {0} while resolving template", url);
		}
	}

	@Override
	public String toString() {
		return value;
	}

}
