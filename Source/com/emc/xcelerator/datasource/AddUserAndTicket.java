package com.emc.xcelerator.datasource;

import com.documentum.fc.client.DfSingleDocbaseModule;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;

public class AddUserAndTicket extends DfSingleDocbaseModule{

	private static final String			ERR_URL_TEMPLATE_IS_REQUIRED	= "The Adaptor Init Parameter \"url_template\" is required.";
	
	private static final String						DEFAULT_USER_VARIABLE			= "\\$\\{user\\}";
	private static final String						DEFAULT_TICKET_VARIABL			= "\\$\\{ticket\\}";


	public String generateUrl(String urlTemplate, String userVariable, String ticketVariable){
		final String generatedUrl;
		try{
			if(urlTemplate == null || urlTemplate.length() == 0){
				throw new DfException(ERR_URL_TEMPLATE_IS_REQUIRED);
			}
			generatedUrl = generateURL(urlTemplate, userVariable, ticketVariable);
		}catch(Exception e1){
			DfException e = new DfException(e1);
			DfLogger.error(this, e.getStackTraceAsString(), null, null);
			return null;
		}
		return generatedUrl;
	}
	
	public String generateUrl(String urlTemplate){
		return generateUrl(urlTemplate, DEFAULT_USER_VARIABLE, DEFAULT_TICKET_VARIABL);
	}
	
	
	protected String generateURL(String urlTemplate, String userVariable, String ticketVariable) throws DfException {
		IDfSession session = getSession();
		try {
			final String username = getName(session);
			final String ticket = getTicket(session);
			String url = urlTemplate.replaceAll(userVariable, username);
			url = url.replaceAll(ticketVariable, ticket);
			return url;
		} finally {
			if (session != null) {
				releaseSession(session);
			}
		}

	}

	protected String getTicket(final IDfSession session) throws DfException {
		final String username = session.getLoginUserName();
		return session.getLoginTicketForUser(username);
	}

	protected String getName(final IDfSession session) throws DfException {
		final String name = session.getLoginUserName();
		return name;
	}

}

