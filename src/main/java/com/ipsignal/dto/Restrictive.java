package com.ipsignal.dto;

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

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;

import com.ipsignal.Browser;
import com.ipsignal.annotation.BrowserName;
import com.ipsignal.annotation.EmailAddress;
import com.ipsignal.annotation.Premium;
import com.ipsignal.annotation.Regexp;
import com.ipsignal.annotation.Reserved;
import com.ipsignal.annotation.UrlAddress;
import com.ipsignal.annotation.Xpath;
import com.ipsignal.dto.impl.GenericDTO;

public abstract class Restrictive {

	public static final String REGEX_SEPARATOR = "/";

	public enum Constraint {
		SIZE, NOTNULL, EMAIL, URL, RESERVED, PREMIUM, REGEXP, XPATH, BROWSER
	}
	
	private static final Logger LOGGER = Logger.getLogger(Restrictive.class.getName());

	public GenericDTO checkConstraints(Constraint... types) {

		for (Field field : this.getClass().getDeclaredFields()) {

			field.setAccessible(true);
			Object member = null;

			try {
				member = field.get(this);
			} catch (IllegalArgumentException | IllegalAccessException ex) {
				LOGGER.log(Level.SEVERE, "Error while accessing DTO field: {0}", ex.getMessage());
			}

			for (Constraint type : types) {
				switch (type) {
				case NOTNULL:
					if (field.isAnnotationPresent(NotNull.class) && member == null) {
						return GenericDTO.INVALIDNULL.format(field.getName());
					}
					break;
					
				case SIZE:
					if (member != null && field.isAnnotationPresent(Size.class)) {
						int max = field.getAnnotation(Size.class).max();
						if (((String) member).length() > max) {
							return GenericDTO.INVALIDSIZE.format(field.getName(), max);
						}
					}
					break;
				case EMAIL:
					if (member != null && field.isAnnotationPresent(EmailAddress.class) && !EmailValidator.getInstance().isValid((String) member)) {
						return GenericDTO.INVALIDEMAIL.format((String) member);
					}
					break;
					
				case URL:
					if (member != null && field.isAnnotationPresent(UrlAddress.class) && !UrlValidator.getInstance().isValid((String) member)) {
						return GenericDTO.INVALIDURL.format((String) member);
					}
					break;
					
				case RESERVED:
					if (member != null && field.isAnnotationPresent(Reserved.class)) {
						List<Integer> numbers = Arrays.asList(ArrayUtils.toObject(field.getAnnotation(Reserved.class).numbers()));
						if (!numbers.get(0).equals(Integer.MIN_VALUE) && !numbers.contains(member)) {
							return GenericDTO.RESERVEDNUMBER.format(field.getName(), numbers);
						}
						List<String> strings = Arrays.asList(field.getAnnotation(Reserved.class).strings());
						if (!"null".equals(strings.get(0)) && !strings.contains(member)) {
							return GenericDTO.RESERVEDSTRING.format(field.getName(), strings);
						}
					}
					break;
					
				case PREMIUM:
					if (member != null && field.isAnnotationPresent(Premium.class)) {
						List<Integer> numbers = Arrays.asList(ArrayUtils.toObject(field.getAnnotation(Premium.class).numbers()));
						if (!numbers.get(0).equals(Integer.MIN_VALUE) && numbers.contains(member)) {
							return GenericDTO.PREMIUMNUMBER.format(numbers, field.getName());
						}
						List<String> strings = Arrays.asList(field.getAnnotation(Premium.class).strings());
						if (!"null".equals(strings.get(0)) && strings.contains(member)) {
							return GenericDTO.PREMIUMSTRING.format(strings, field.getName());
						}
					}
					break;
					
				case REGEXP:
					if (member != null && field.isAnnotationPresent(Regexp.class)) {
						String value = (String) member;
						if (value.length() > 2 && value.startsWith(REGEX_SEPARATOR) && value.endsWith(REGEX_SEPARATOR)) {
							String regex = value.substring(1, value.length()-1);
							try {
								Pattern.compile(regex);
							} catch (PatternSyntaxException psex) {
								if (LOGGER.isLoggable(Level.FINE)) {
									LOGGER.log(Level.FINE, "Invalid regular expression: {0}", psex.getMessage());
								}
								return GenericDTO.INVALIDREGEXP.format(regex);
							}
						}
					}
					break;
				case XPATH:
					if (member != null && field.isAnnotationPresent(Xpath.class)) {
						String regex = (String) member;
						try {
							XPathFactory.newInstance().newXPath().compile(regex);
						} catch (XPathExpressionException xpex) {
							if (LOGGER.isLoggable(Level.FINE)) {
								LOGGER.log(Level.FINE, "Invalid xpath: {0}", xpex.getMessage());
							}
							return GenericDTO.INVALIDXPATH.format(regex);
						}
					}
					break;
				case BROWSER:
					if (member != null && field.isAnnotationPresent(BrowserName.class)) {
						String name = (String) member;
						try {
							Browser.valueOf(name);
						} catch (IllegalArgumentException iaex) {
							if (LOGGER.isLoggable(Level.FINE)) {
								LOGGER.log(Level.FINE, "Invalid browser: {0}", iaex.getMessage());
							}
							return GenericDTO.RESERVEDSTRING.format(field.getName(), Arrays.toString(Browser.values()));
						}
					}
					break;
				}
			}

		}
		return null;
	}

}
