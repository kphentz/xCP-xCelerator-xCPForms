package com.emc.xcelerator.datasource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.documentum.fc.client.DfSingleDocbaseModule;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;


public class RepositoryXMLDataSource extends DfSingleDocbaseModule {
	
	public Result[] getXPathValue(String objectId, String xPathNamePairs[]){

		final Result[] xPathValues;
		IDfSession session = null;
		try{
			init(xPathNamePairs);
			session = getSession();
			final IDfSysObject object = (IDfSysObject) session.getObject(new DfId(objectId));
			final Document srcDocument = getDocument(object);
			final Collection<String[]> values = extractValues(srcDocument, xPathExpressions);
			xPathValues = new Result[values.size()];
			int i = 0;
			Iterator<String[]> iter = values.iterator();
			while (iter.hasNext()) {
				String[] valu = (String[]) iter.next();
				xPathValues[i++] = new Result(name(valu), value(valu));
			}
			return xPathValues;
		}catch(Exception e1){
			DfException e = new DfException(e1);
			DfLogger.error(this, e.getStackTraceAsString(), null, null);
			return null;
		}finally{
			if(session != null){
				releaseSession(session);
			}
		}
	}
	
	protected Collection<String[]> extractValues(final Document document, final Collection<String[]> namedXPathExpresssions)
			throws XPathExpressionException {
		final Collection<String[]> values = new ArrayList<String[]>(namedXPathExpresssions.size());
		final XPath xpath = getXPathExpressionEvaluator();
		for (final String[] expression : namedXPathExpresssions) {
			final String value = xpath.evaluate(value(expression), document);
			values.add(newNameValuePair(name(expression), value));
		}
		return values;
	}
	
	private void init(String[] xPathNamePairs) throws IOException {
		StringBuilder xpSb = new StringBuilder();
		for(int i = 0; i < xPathNamePairs.length; i++){
			xpSb.append(xPathNamePairs[i]).append("\n");
		}
		final String xpaths = xpSb.toString();
		final Properties props = new Properties();
		final ByteArrayInputStream reader = new ByteArrayInputStream(xpaths.getBytes());
		props.load(reader);
		for (final Map.Entry<Object, Object> e : props.entrySet()) {
			xPathExpressions.add(newNameValuePair((String) e.getKey(), (String) e.getValue()));
		}
	}

	protected Document getDocument(final IDfSysObject object) throws DfException, ParserConfigurationException, SAXException, IOException {
		final InputStream content = object.getContent();
		final Document document = parse(content);
		content.close();
		return document;
	}
	
	private XPath getXPathExpressionEvaluator() {
		final XPathFactory xpathFactory = XPathFactory.newInstance();
		final XPath xPath = xpathFactory.newXPath();
		return xPath;
	}
	
	private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder = factory.newDocumentBuilder();
		return builder;
	}
	
	private Document parse(final InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
		final DocumentBuilder builder = getDocumentBuilder();
		final Document document = builder.parse(inputStream);
		return document;
	}
	
	private String[] newNameValuePair(final String left, final String right) {
		return new String[] { left, right };
	}

	private String name(final String[] pair) {
		return pair[0];
	}

	private String value(final String[] pair) {
		return pair[1];
	}
	
	private final Collection<String[]> xPathExpressions = new ArrayList<String[]>();
	
	public class Result{
		public Result(String label, String value){
			this.label = label;
			this.value = value;
		}
		public String getLabel(){
			return this.label;
		}
		public String getValue(){
			return this.value;
		}
		private String value;
		private String label;
	}
}
