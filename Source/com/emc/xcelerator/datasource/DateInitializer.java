package com.emc.xcelerator.datasource;

import java.util.Calendar;
import java.util.Date;

import com.documentum.fc.client.DfSingleDocbaseModule;
import com.emc.xcelerator.datasource.utils.date.TimeDeltaParser;
import com.emc.xcelerator.datasource.utils.date.TimeDeltaSet;

public class DateInitializer extends DfSingleDocbaseModule {

	public Date getInitialDate(String deltaSpecification, Date inDate) {
		final Calendar calendar = Calendar.getInstance();
		if(inDate != null){
			calendar.setTime(inDate);
		}
		final TimeDeltaParser tdp = new TimeDeltaParser();
		tdp.parse(deltaSpecification);
		TimeDeltaSet deltas = tdp.getDeltas();
		return deltas.apply(calendar).getTime();
	}
	
	public Date getInitialDate(String deltaSpecification){
		return getInitialDate(deltaSpecification, null);
	}

}
