package io.mosip.testrig.apirig.testrunner;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.mosip.testrig.apirig.utils.AuthTestsUtil;

/**
 * The class is to handle all XML file or message process activity
 * 
 * @author Vignesh
 *
 */
public class XmlPrecondtion extends MessagePrecondtion {
	private static final Logger XMLPRECONDTION_LOGGER = Logger.getLogger(XmlPrecondtion.class);
	private static Document xmlDocument;
	private static final String FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
	private static final String EXTERNAL_DTD_FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
//	private static final String SAX_EXTERNAL_GENERAL_FEATURE = "http://xml.org/sax/features/external-general-entities";
//	private static final String SAX_EXTERNAL_PARAMETER_FEATURE = "http://xml.org/sax/features/external-parameter-entities";

	/**
	 * The method get node value from xml file
	 * 
	 * @param path,       xml file path
	 * @param expression, xml xpath
	 * @return value, xml node or attribute value
	 */
	public static String getValueFromXmlFile(String path, String expression) {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			File inputFile = new File(path);
			builderFactory.setFeature(FEATURE, true);
			builderFactory.setFeature(EXTERNAL_DTD_FEATURE, false);
			builderFactory.setXIncludeAware(false);
			builderFactory.setExpandEntityReferences(false);
			builder = builderFactory.newDocumentBuilder();
			Document doc = builder.parse(inputFile);
			doc.getDocumentElement().normalize();
			XPath xPath = XPathFactory.newInstance().newXPath();
			return xPath.compile(expression).evaluate(doc);
		} catch (Exception e) {
			XMLPRECONDTION_LOGGER.error("Exception in xml precondtion : " + e.getMessage());
			return "Cannot retrieve data or content for the xpath from  XML";
		}
	}

	/**
	 * The method get node value from xml file
	 * 
	 * @param path,       xml file path
	 * @param expression, xml xpath
	 * @return value, xml node or attribute value
	 */
	public static Map<String, String> getValueFromXmlFile(String path, Map<String, String> expressionMap) {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Map<String, String> returnMap = null;
		try {
			returnMap = new HashMap<>();
			File inputFile = new File(path);
			builderFactory.setFeature(FEATURE, true);
			builderFactory.setFeature(EXTERNAL_DTD_FEATURE, false);
			builderFactory.setXIncludeAware(false);
			builderFactory.setExpandEntityReferences(false);
			builder = builderFactory.newDocumentBuilder();
			Document doc = builder.parse(inputFile);
			doc.getDocumentElement().normalize();
			XPath xPath = XPathFactory.newInstance().newXPath();
			for (Entry<String, String> entry : expressionMap.entrySet()) {
				String value = xPath.compile(entry.getValue()).evaluate(doc);
				if (value != null)
					returnMap.put(entry.getValue(), value);
				else
					returnMap.put(entry.getValue(), "null");
			}
			return returnMap;
		} catch (Exception e) {
			XMLPRECONDTION_LOGGER
					.error("Exception in xml precondtion, while retrieving the value from xml : " + e.getMessage());
			return returnMap;
		}
	}

	/**
	 * The method get node value from xml content
	 * 
	 * @param content,    XML content
	 * @param expression, XML xpath
	 * @return value, xml node or attribute value
	 */
	public static String getValueFromXmlContent(String content, String expression) {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			StringBuilder xmlStringBuilder = new StringBuilder();
			xmlStringBuilder.append(content);
			try (ByteArrayInputStream input = new ByteArrayInputStream(
					xmlStringBuilder.toString().getBytes(StandardCharsets.UTF_8))) {
				builderFactory.setFeature(FEATURE, true);
				builderFactory.setFeature(EXTERNAL_DTD_FEATURE, false);
				builderFactory.setXIncludeAware(false);
				builderFactory.setExpandEntityReferences(false);
				builder = builderFactory.newDocumentBuilder();
				Document doc = builder.parse(input);
				doc.getDocumentElement().normalize();
				XPath xPath = XPathFactory.newInstance().newXPath();
				return xPath.compile(expression).evaluate(doc);
			}
		} catch (Exception e) {
			XMLPRECONDTION_LOGGER.error("Exception in xml precondtion : " + e.getMessage());
			return e.getMessage();
		}
	}

	/**
	 * The method get the value from XML using mapping dic
	 * 
	 * @param inputFilePath,    XML file path
	 * @param mappingFileName,  Mapping file path
	 * @param mappingFieldName, Mapping field name
	 * @return value, xml node or attribute value
	 */
	public static String getValueFromXmlUsingMapping(String inputFilePath, String mappingFilePath,
			String mappingFieldName) {
		try {
			String xpath;
			if (mappingFieldName.contains(":")) {
				String keys[] = mappingFieldName.split(":");
				String valueFromProperty = AuthTestsUtil.getPropertyFromFilePath(mappingFilePath).getProperty(keys[0]);
				xpath = valueFromProperty.replace("$" + keys[1] + "$", keys[2]);
			} else
				xpath = AuthTestsUtil.getPropertyFromFilePath(mappingFilePath).getProperty(mappingFieldName);
			return getValueFromXmlContent(new String(Files.readAllBytes(Paths.get(inputFilePath))), xpath);
		} catch (Exception exception) {
			XMLPRECONDTION_LOGGER
					.error("Exception Occured in retrieving the value from xml file: " + exception.getMessage());
			return exception.toString();
		}
	}

	/**
	 * The method wil update the xml file and generate in output file
	 * 
	 * @author Athila
	 * @param inputFilePath
	 * @param fieldvalue
	 * @param outputFilePath
	 * @param propFileName
	 * @return true or false
	 */
	public Map<String, String> parseAndWriteFile(String inputFilePath, Map<String, String> fieldvalue,
			String outputFilePath, String propFileName) {
		return Collections.emptyMap();
	}

	private static NodeList evaluateXpath(XPath xpath, String xpathStr) {
		try {
			return (NodeList) xpath.evaluate(xpathStr, xmlDocument, XPathConstants.NODESET);
		} catch (Exception exp) {
			XMLPRECONDTION_LOGGER
					.error("Exception occured in xpath. Check correct xpath used in mapping" + exp.getMessage());
		}
		return null;
	}

	private static String normalisedXpath(String xpath) {
		String normalisedXpath = "//";
		xpath = xpath.replace("//", "");
		String[] values = xpath.split(Pattern.quote("/"));
		for (int i = 0; i < values.length; i++) {
			if (values[i].contains("@") && values[i].contains(":"))
				normalisedXpath = normalisedXpath + "@"
						+ values[i].substring(values[i].indexOf(":") + 1, values[i].length()) + "/";
			else
				normalisedXpath = normalisedXpath + values[i].substring(values[i].indexOf(":") + 1, values[i].length())
						+ "/";
		}
		normalisedXpath = normalisedXpath.substring(0, normalisedXpath.length() - 1);
		return normalisedXpath;
	}

	private static void updateNodeValue(XPath xpath, String normalisedExpression, String newValue) {
		NodeList nodes = null;
		try {
			if (newValue.equalsIgnoreCase("$REMOVE$")) {
				normalisedExpression = normalisedExpression.replace("/text()", "");
				nodes = evaluateXpath(xpath, normalisedExpression);
				if (nodes != null) {
					for (int i = 0, len = nodes.getLength(); i < len; i++) {
						Node node = nodes.item(i);
						node.getParentNode().removeChild(node);
					}
				}
			} else if (!newValue.equalsIgnoreCase("$IGNORE$")) {
				nodes = evaluateXpath(xpath, normalisedExpression);
				if (nodes != null) {
					for (int i = 0, len = nodes.getLength(); i < len; i++) {
						Node node = nodes.item(i);
						node.setTextContent(newValue);
					}
				}
			}
		} catch (Exception e) {
			XMLPRECONDTION_LOGGER.error(e.getMessage());
		}
	}

	private static void updateAttributeValue(XPath xpath, String expression, String normalisedExpression,
			String newValue) {
		try {
			String attr = expression.substring(expression.indexOf("@") + 1, expression.length());
			normalisedExpression = normalisedExpression.substring(0, normalisedExpression.indexOf("@") - 1);
			NodeList nodes = evaluateXpath(xpath, normalisedExpression);
			if (nodes != null) {
				for (int i = 0, len = nodes.getLength(); i < len; i++) {
					Node node = nodes.item(i);
					NamedNodeMap attrNode = node.getAttributes();
					Node nodeAttr = attrNode.getNamedItem(attr);
					if (nodeAttr != null)
						nodeAttr.setTextContent(newValue);
					else
						((Element) node).setAttribute(attr, newValue);
					if (newValue.equalsIgnoreCase("$REMOVE$"))
						node.getAttributes().removeNamedItem(attr);
				}
			}
		} catch (Exception e) {
			XMLPRECONDTION_LOGGER.error(e.getMessage());
		}
	}

	@Override
	public String parseAndUpdateJson(String inputJson, Map<String, String> fieldvalue, String propFileName) {
		return null;
	}
}
