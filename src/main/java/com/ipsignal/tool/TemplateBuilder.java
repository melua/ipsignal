package com.ipsignal.tool;

import java.util.logging.Level;
import java.util.logging.Logger;
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

public class TemplateBuilder {

	private static final Logger LOGGER = Logger.getLogger(TemplateBuilder.class.getName());

	private static final String BEGIN_TAG = "{";
	private static final String CLOSE_TAG = "}";
	private static final String UNSET_TAG = "(n/a)";

	private String value;

	public TemplateBuilder(String template) {
		value = template;
	}

	public void replace(String original, Object replacement) {
		Matcher matcher = Pattern.compile(BEGIN_TAG + original + CLOSE_TAG, Pattern.LITERAL).matcher(value);
		if (matcher.find()) {
			value = matcher.replaceFirst(replacement != null ? String.valueOf(replacement) : UNSET_TAG);
		} else {
			LOGGER.log(Level.WARNING, "Missing template tag {0}", original);
		}
	}

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

	public void formatLog(LogEntity entity) {
		replace("access", entity.getAccess());
		replace("latency", entity.getLatency());
		replace("certificate", entity.getCertificate());
		replace("http", entity.getHttp());
		replace("obtained", entity.getObtained());
		replace("detail", entity.getDetail());
	}

	public void formatLink(String... values) {
		StringBuilder link = new StringBuilder();
		for (String val : values) {
			link.append("/");
			link.append(val);
		}
		replace("link", Config.SERVICE_URL + link.toString());
	}

	public void formatUnsubscribe(String unsubscribe) {
		replace("unsubscribe", unsubscribe);
	}

	public void formatHtml(String uuid, String cancel) {
		replace("uuid", uuid);
		replace("link", Config.SERVICE_URL + "/" + cancel);
	}

	public void formatDays(Integer days) {
		replace("days", days);
	}

	@Override
	public String toString() {
		return value;
	}

}
