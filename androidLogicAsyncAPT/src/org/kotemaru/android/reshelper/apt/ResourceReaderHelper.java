package org.kotemaru.android.reshelper.apt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.kotemaru.android.reshelper.annotation.PreferenceReader;
import org.kotemaru.apthelper.AptUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class ResourceReaderHelper extends AptUtil {
	public static final String RESOURCE_PATH = "org.kotemaru.android.reshelper.res";

	private TypeElement classDecl;
	private PreferenceReader preferenceReader;
	private ProcessingEnvironment environment;
	private Document document;
	private XPath xpathEngine;

	public ResourceReaderHelper(TypeElement classDecl,
			ProcessingEnvironment env) {
		this.classDecl = classDecl;
		this.preferenceReader = classDecl.getAnnotation(PreferenceReader.class);
		this.environment = env;
		this.init();
	}

	private void init() {
		String resPath = environment.getOptions().get(RESOURCE_PATH);
		if (resPath == null) {
			throw new RuntimeException("Require APT option " + RESOURCE_PATH);
		}
		try {
			File xmlFile = new File(new File(resPath), preferenceReader.xml());
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			XPathFactory factory = XPathFactory.newInstance();
			this.document = builder.parse(xmlFile);
			this.xpathEngine = factory.newXPath();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	public Node getNode(String xpath) throws XPathExpressionException {
		return (Node) getNode(document, xpath);
	}
	public List<Node> getNodes(String xpath) throws XPathExpressionException {
		return (List<Node>) getNodes(document, xpath);
	}

	public Node getNode(Node node, String xpath) throws XPathExpressionException {
		return (Node) xpathEngine.evaluate(xpath, node, XPathConstants.NODE);
	}
	public List<Node> getNodes(Node node, String xpath) throws XPathExpressionException {
		NodeList nodeList = (NodeList) 
				xpathEngine.evaluate(xpath, node, XPathConstants.NODESET);
		List<Node> listNode = new ArrayList<Node>(nodeList.getLength());
		for (int i = 0; i < nodeList.getLength(); i++) {
			listNode.add(nodeList.item(i));
		}
		return listNode;
	}
	
}
