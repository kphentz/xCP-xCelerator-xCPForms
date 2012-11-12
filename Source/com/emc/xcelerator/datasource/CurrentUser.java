package com.emc.xcelerator.datasource;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;

import com.documentum.bpm.IDfWorkitemEx;
import com.documentum.bpm.rtutil.WorkflowMethod;
import com.documentum.fc.client.IDfModule;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfUser;
import com.documentum.fc.client.IDfWorkitem;
import com.documentum.fc.client.DfSingleDocbaseModule;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfProperties;

public class CurrentUser extends DfSingleDocbaseModule{

	private static final String ERR_FAILURE = "The CurrentUserDataSourceActivity failed to create the values.";
	private static final String USER_NAME = "user_name";
	private static final String USER_LOGIN_NAME = "user_login_name";
	private static final String USER_ADDRESS = "user_address";

	private Collection<String> attributes;


	protected int doTask(IDfWorkitem workitem, IDfProperties iDfProperties, PrintWriter printWriter)
			throws Exception {
		
		IDfWorkitemEx wi = (IDfWorkitemEx) workitem;
		init(iDfProperties);
		
		try {
			IDfSession session = null;
			try {
				session = wi.getObjectSession();
				String username = session.getLoginUserName();
				IDfUser user = session.getUser(username);

				for (String extraAttr : getAttributes()) {
					String value = user.getString(extraAttr);
					wi.setPrimitiveObjectValue(extraAttr, value);
				}

				return 0;
			} finally {
				if (session != null) {
					wi.getSessionManager().release(session);
				}
			}
		} catch (final Exception e1) {
			DfException e = new DfException(e1);
			DfLogger.error(this, ERR_FAILURE + "\n" + e.getStackTraceAsString(), null, null);
			return 1;
		}
	}
	
	private void init(IDfProperties iDfProperties){
		attributes = new ArrayList<String>();
		attributes.add(USER_NAME);
		attributes.add(USER_LOGIN_NAME);
		attributes.add(USER_ADDRESS);
		try{
			addExtraAttributes(iDfProperties.getString("attributes"));
		}catch(DfException e){
			DfLogger.error(this, e.getStackTraceAsString(), null, null);
		}
	}
	
	protected void addExtraAttributes(String extraAttributes) {
		if (extraAttributes != null && !extraAttributes.isEmpty()) {
			String[] values = extraAttributes.split("(,|;| |\t|\n)");
			for (String value : values) {
				if (!value.isEmpty()) {
					attributes.add(value.trim());
				}
			}
		}
	}

	private Collection<String> getAttributes() {
		return attributes;
	}


}
