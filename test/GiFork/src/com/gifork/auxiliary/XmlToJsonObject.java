/*
 * (c) Copyright Leonardo Company S.p.A.. All rights reserved.
 *
 * Any right of industrial and intellectual property on this document,
 * and of technical Know-how herein contained, belongs to
 * Leonardo Company S.p.A. and/or third parties.
 * According to the law, it is forbidden to disclose, reproduce or however
 * use this document and any data herein contained for any use without
 * previous written authorization by Leonardo Company S.p.A.
 *
 */
package com.gifork.auxiliary;

import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.fourflight.WP.ECI.edm.EdmMetaKeys;
import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode.HeaderType;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.RawDataFactory;
import com.gifork.commons.log.LoggerFactory;
import com.leonardo.infrastructure.Base64Helper;
import com.leonardo.infrastructure.log.ILogger;

/**
 * The Class XmlToJsonObject.
 *
 * @author ggiampietro
 * @version $Revision$
 */
public class XmlToJsonObject {

	/** The Constant logger. */
	private static final ILogger logger = LoggerFactory.CreateLogger(XmlToJsonObject.class);

	/**
	 * Instantiates a new xml to json object.
	 */
	private XmlToJsonObject() {
	}

	/**
	 * ATTENZIONE! Trasforma un json di RootData in un service, è un trasformatore non un convertitore
	 * generico.
	 *
	 * @param jsonText the json text
	 * @return the i raw data
	 */
	public static IRawData transformJSONServiceToRawData(String jsonText) {
		JSONObject json = new JSONObject(jsonText);
		IRawData rawdata = RawDataFactory.create();

		JSONObject service = json.getJSONObject(HeaderType.SERVICE.toString());
		if (service != null) {
			String type = service.getString(EdmMetaKeys.TYPE);
			rawdata.setType(type);
			JSONArray pairs = service.getJSONArray(EdmMetaKeys.NODE_P);
			for (Object object : pairs) {
				JSONObject jobj = (JSONObject) object;
				String key = jobj.getString(EdmModelKeys.Pairs.KEY);
				String data = jobj.getString(EdmModelKeys.Pairs.DATA);
				if (key.equals("DATA") && data.startsWith(EdmModelKeys.BASE64_PREFIX)) {
					data = Base64Helper.fromBase64(data.substring(EdmModelKeys.BASE64_PREFIX.length()), String.class);
				}
				rawdata.put(key.toUpperCase(), data);
			}
		}
		return rawdata;
	}

	/**
	 * Convert XML to raw data.
	 *
	 * @param XML the xml
	 * @return the i raw data
	 */
	public static IRawData convertXMLToRawData(String XML) {
		final String loggerCaller = "convertXMLToRawData()";

		var retJson = RawDataFactory.create();

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			StringReader sReader = new StringReader(XML);
			is.setCharacterStream(sReader);
			Document doc = db.parse(is);
			doc.getDocumentElement().normalize();

			if (doc.hasChildNodes()) {
				retJson = recursiveDecoding(doc.getChildNodes(), retJson);
				is.setCharacterStream(null);
				sReader.close();
			}
		} catch (Exception e) {
			logger.logDebug(loggerCaller, "WARNING!! ManagerProxy:: PARSING XML  :<" + XML);
		}

		return retJson;
	}

	/**
	 * **.
	 *
	 * @param nodeList the node list
	 * @param retJson  the ret json
	 * @return the i raw data
	 * @throws DOMException the DOM exception
	 */
	private static IRawData recursiveDecoding(NodeList nodeList, IRawData retJson) throws DOMException {
		for (int count = 0; count < nodeList.getLength(); count++) {
			boolean childsParsed = false;
			Node xmlNodeTag = nodeList.item(count);
			String nodeName = xmlNodeTag.getNodeName();
			if (xmlNodeTag.getNodeType() == Node.ELEMENT_NODE) {
				if (xmlNodeTag.hasAttributes()) {

					if (nodeName.equalsIgnoreCase("SERVICE")) {
						retJson = parseService(xmlNodeTag, retJson);
						childsParsed = true;
					}
				}
				if (xmlNodeTag.hasChildNodes() && childsParsed == false) {

					retJson = recursiveDecoding(xmlNodeTag.getChildNodes(), retJson);
				}
			}
			xmlNodeTag = null;
		}
		return retJson;
	}

	/**
	 * **.
	 *
	 * @param xmlNodeTag the xml node tag
	 * @param retJson    the ret json
	 * @return the i raw data
	 * @throws DOMException the DOM exception
	 */
	private static IRawData parseService(Node xmlNodeTag, IRawData retJson) throws DOMException {
		String key = "";
		String value = "";
		NamedNodeMap attributeMap = xmlNodeTag.getAttributes();
		for (int i = 0; i < attributeMap.getLength(); i++) {
			Node attribute = attributeMap.item(i);
			if (attribute.getNodeName().equalsIgnoreCase("TYPE")) {
				value = attribute.getNodeValue();
				retJson.setType(value);
			}
		}
		NodeList childNodes = xmlNodeTag.getChildNodes();
		for (int ii = 0; ii < childNodes.getLength(); ii++) {
			Node childNode = childNodes.item(ii);
			String childNodeName = childNode.getNodeName();
			if (childNodeName.equalsIgnoreCase("P")) {
				NamedNodeMap childNodesAttributes = childNode.getAttributes();
				for (int j = 0; j < childNodesAttributes.getLength(); j++) {
					Node childNodeAttribute = childNodesAttributes.item(j);
					String childNodeAttributeName = childNodeAttribute.getNodeName().toUpperCase();
					if (childNodeAttributeName.equals("KEY")) {
						key = childNodeAttribute.getNodeValue();
					}
					if (childNodeAttributeName.equals("DATA")) {
						value = childNodeAttribute.getNodeValue();
					}
					if (!key.isEmpty()) {
						retJson.put(key.toUpperCase(), value);
						key = "";
						value = "";
					}
				}
			}
		}
		return retJson;
	}
}
