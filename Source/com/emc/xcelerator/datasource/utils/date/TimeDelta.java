/**
 * 
 */
package com.emc.xcelerator.datasource.utils.date;

import java.util.Calendar;

public class TimeDelta {
	final private int	unit;
	final private int	value;

	public TimeDelta(final int value, final int unit) {
		this.value = value;
		this.unit = unit;
	}

	public void apply(final Calendar calendar) {
		calendar.add(unit, value);
	}
}