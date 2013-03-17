package org.kotemaru.xmlbean;

import java.io.*;
import java.lang.reflect.Type;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.*;

import org.w3c.dom.*;

/**
 * XML->Bean変換ツール。
 *
 */
public class XBParser {
    private static final DocumentBuilderFactory FACTORY = DocumentBuilderFactory.newInstance();

    private static final String CLASS     = "class";
    private static final String SUBCLASS  = "subclass";
    private static final String BUILDER   = "builder";
    private static final String VALUE     = "value";

    private Map<Class<?>,String> builderMap = new HashMap<Class<?>,String>();

	public XBParser() {
		builderMap.put(String.class, StringXBBuilder.class.getName());
		builderMap.put(Date.class, DateXBBuilder.class.getName());
	}


	public Object parse(File file) throws Exception {
		FileInputStream in = new FileInputStream(file);
		try {
			return this.parse(in);
		} finally {
			in.close();
		}
	}

	public Object parse(InputStream in)  throws Exception {
		DocumentBuilder builder = FACTORY.newDocumentBuilder();
		Document doc = builder.parse(in);
		return this.parse(doc);
	}

	public Object parse(Document doc) throws Exception {
		return parseElement(doc.getDocumentElement(), null, null);
	}



	private Object parseElement(Element elem, Class<?> type, Type[] subTypes) throws Exception {
		String builder = elem.getAttribute(BUILDER);

		XBBuilder xbBuilder = (builder.isEmpty())
				? new DefaultXBBuilder()
				: (XBBuilder) Class.forName(builder).newInstance();
		return xbBuilder.toInstance(elem, type, subTypes);
	}

	private class DefaultXBBuilder implements XBBuilder {
		@Override
		public Object toInstance(Element elem, Class<?> type, Type[] subTypes) throws Exception {
			String className = elem.getAttribute(CLASS);
			String builder = builderMap.get(type);

			if (builder != null) {
				XBBuilder xbBuilder = (XBBuilder) Class.forName(builder).newInstance();
				return xbBuilder.toInstance(elem, type, subTypes);
			} else if (!className.isEmpty()){
				Object obj = Class.forName(className).newInstance();
				return parseBean(elem, obj);
			} else if (type == null) {
				throw new XBException("Need class attribute by root element.");
			} else if (type.isPrimitive()) {
				return parsePrim(elem, type);
			} else if (ReflectUtil.isWrapperType(type)) {
				return parsePrim(elem, type);
			} else if (type.isArray()) {
				return parseArray(elem, type);
			} else if (List.class.isAssignableFrom(type)) {
				return parseList(elem, type, subTypes);
			} else if (type.isEnum()) {
				return parseEnum(elem, type);
			} else {
				return parseBean(elem, type.newInstance());
			}
		}

		@Override
		public String toString(Object val) throws Exception {
			// Dummy.
			return null;
		}
	}

	private Object parseBean(Element elem, Object obj) throws Exception {
		NodeList children = elem.getChildNodes();
		for (int i=0; i<children.getLength(); i++) {
			Node child = children.item(i);
			if (child instanceof Element) {
				String name = child.getNodeName();
				Class<?> type = ReflectUtil.getPropertyType(obj, name);
				if (type == null) {
					throw new XBException("Unknown type elem "+name);
				}
				Type[] subTypes = ReflectUtil.getPropertyGenericTypes(obj, name);;

				Object val = parseElement((Element)child, type, subTypes);
				ReflectUtil.setProperty(obj, name, type, val);
			}
		}
		return obj;
	}


	@SuppressWarnings("unchecked")
	private Object parseArray(Element elem, Class<?> type) throws Exception {
		Class<?> ctype = type.getComponentType();
		if (!ctype.isPrimitive()) {
			Type[] subTyes = new Type[]{ctype};
			ArrayList<Object> list = (ArrayList<Object>) parseList(elem, ctype, subTyes);
			return list.toArray((Object[]) Array.newInstance(ctype, list.size()));
		}

		int size = 0;
		NodeList children = elem.getChildNodes();
		for (int i=0; i<children.getLength(); i++) {
			Node child = children.item(i);
			if (child instanceof Element) size++;
		}
		int idx = 0;
		Object array = Array.newInstance(ctype, size);
		for (int i=0; i<children.getLength(); i++) {
			Node child = children.item(i);
			if (child instanceof Element) {
				Object val = parseElement((Element)child, ctype, null);
				Array.set(array, idx, val);
			}
		}
		return array;
	}

	private Object parseList(Element elem, Class<?> type, Type[] subTypes) throws Exception {
		ArrayList<Object> list = new ArrayList<Object>();
		NodeList children = elem.getChildNodes();
		for (int i=0; i<children.getLength(); i++) {
			Node child = children.item(i);
			if (child instanceof Element) {
				Type[] childSubTypes = null;
				if (subTypes != null && subTypes[0] instanceof ParameterizedType) {
					ParameterizedType childType = (ParameterizedType) subTypes[0];
					childSubTypes = childType.getActualTypeArguments();
				}
				Object val = parseElement((Element)child, (Class<?>)subTypes[0], childSubTypes);
				list.add(val);
			}
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	private Object parseEnum(Element elem, @SuppressWarnings("rawtypes") Class type) throws Exception {
		String value = getValue(elem);
		return Enum.valueOf(type, value);
	}

	private Object parsePrim(Element elem, Class<?> type) {
		return ReflectUtil.valueOf(type, getValue(elem));
	}

	public static String getValue(Element elem) {
		String value = elem.getAttribute(VALUE);
		if (value == null) {
			value = elem.getTextContent();
		}
		return value;
	}

}
