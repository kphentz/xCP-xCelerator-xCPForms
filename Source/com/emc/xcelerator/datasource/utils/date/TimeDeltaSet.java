package com.emc.xcelerator.datasource.utils.date;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeDeltaSet {

	private final Collection<TimeDelta>	deltas	= new ArrayList<TimeDelta>();

	public TimeDeltaSet() {

	}

	public void add(final TimeDelta delta) {
		deltas.add(delta);
	}

	public Date apply(final Date date) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar = apply(calendar);
		return calendar.getTime();
	}

	// Note sideeffect!
	public Calendar apply(final Calendar calendar) {
		for (final TimeDelta delta : deltas) {
			delta.apply(calendar);
		}
		return calendar;
	}
}
