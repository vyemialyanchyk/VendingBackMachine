package com.vending.back.machine.domain.util;

import static com.vending.back.machine.helper.DateFormats.US_DATE_PATTERN;

/**
 * vyemialyanchyk on 25.11.16.
 */
public enum Format {

	Numeric("[0-9]+"),
	Alphanumeric(""), //("[a-zA-Z0-9 ]+"),
	Alpha(""), //("[a-zA-Z ]+"),
	Date(US_DATE_PATTERN);

	private String match;

	Format(String match) {
		this.match = match;
	}

	public String getMatch() {
		return match;
	}
}
