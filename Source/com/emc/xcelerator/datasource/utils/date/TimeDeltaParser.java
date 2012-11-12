/**
 * 
 */
package com.emc.xcelerator.datasource.utils.date;

import java.util.Calendar;

public class TimeDeltaParser {

	private static final char	MILLISECOND			= 'S';
	private static final char	SECOND				= 's';
	private static final char	HOUR				= 'H';
	private static final char	MINUTE				= 'm';
	private static final char	DAY					= 'd';
	private static final char	MONTH				= 'M';
	private static final char	YEAR				= 'y';
	private static final String	ERR_INVALID_FORMAT	= "Invalid format character, expected one of \"yMdmHsS0123456789 \".";

	private final TimeDeltaSet	deltas				= new TimeDeltaSet();

	private void addDelta(final StringBuilder numberBuffer, final boolean negate, final int unit) {
		try {
			final int number = Integer.parseInt(numberBuffer.toString());
			final int value = negate ? -number : number;
			deltas.add(new TimeDelta(value, unit));
			numberBuffer.setLength(0);
		} catch (final NumberFormatException nfe) {
			throw new RuntimeException(nfe);
		}
	}

	public TimeDeltaSet getDeltas() {
		return deltas;
	}

	public void parse(final String deltaSpecification) {
		final StringBuilder numberBuffer = new StringBuilder();
		boolean negate = false;
		for (final char c : deltaSpecification.toCharArray()) {
			switch (c) {
			case MILLISECOND:
				addDelta(numberBuffer, negate, Calendar.MILLISECOND);
				negate = false;
				break;
			case SECOND:
				addDelta(numberBuffer, negate, Calendar.SECOND);
				negate = false;
				break;
			case MINUTE:
				addDelta(numberBuffer, negate, Calendar.MINUTE);
				negate = false;
				break;
			case HOUR:
				addDelta(numberBuffer, negate, Calendar.HOUR);
				negate = false;
				break;
			case DAY:
				addDelta(numberBuffer, negate, Calendar.DAY_OF_YEAR);
				negate = false;
				break;
			case MONTH:
				addDelta(numberBuffer, negate, Calendar.MONTH);
				negate = false;
				break;
			case YEAR:
				addDelta(numberBuffer, negate, Calendar.YEAR);
				negate = false;
				break;
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				numberBuffer.append(c);
				break;
			case ' ':
				break;
			case '-':
				negate = true;
				break;
			default:
				throw new IllegalArgumentException(ERR_INVALID_FORMAT);

			}
		}
	}

}