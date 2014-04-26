package org.kotemaru.nnalert;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import nanoxml.XMLElement;

public class Xml {
	private XMLElement root;

	public Xml(XMLElement root) {
		this.root = root;
	}

	public String getContent(String path) {
		List<XMLElement> elems = getXMLElements(path);
		if (elems.isEmpty()) return null;
		return elems.get(0).getContent();
	}

	public List<String> getContents(String path) {
		List<XMLElement> elems = getXMLElements(path);
		List<String> contents = new ArrayList<String>(elems.size());
		for (XMLElement elem : elems) {
			contents.add(elem.getContent());
		}
		return contents;
	}

	public List<XMLElement> getXMLElements(String path) {
		List<XMLElement> list = new ArrayList<XMLElement>(4);
		String[] names = path.split("/");
		getXMLElements(root, names, 0, list);
		return list;
	}

	private void getXMLElements(XMLElement origin, String[] names, int pos, List<XMLElement> result) {
		if (pos >= names.length) {
			result.add(origin);
		} else {
			List<XMLElement> list = findTags(origin, names[pos]);
			for (XMLElement elem : list) {
				getXMLElements(elem, names, pos + 1, result);
			}
		}
	}

	private List<XMLElement> findTags(XMLElement xml, String name) {
		List<XMLElement> list = new ArrayList<XMLElement>(4);
		@SuppressWarnings("unchecked")
		Enumeration<XMLElement> children = (Enumeration<XMLElement>) xml.enumerateChildren();
		while (children.hasMoreElements()) {
			XMLElement child = children.nextElement();
			if (name.equals(child.getName())) {
				list.add(child);
			}
		}
		return list;
	}

}
