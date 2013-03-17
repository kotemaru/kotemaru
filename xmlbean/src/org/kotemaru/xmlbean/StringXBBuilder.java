package org.kotemaru.xmlbean;

import java.lang.reflect.Type;

import org.w3c.dom.Element;

public class StringXBBuilder implements XBBuilder {
	public Object toInstance(Element elem, Class<?> type, Type[] subTypes) throws Exception {
		return XBParser.getValue(elem);
	}

	@Override
	public String toString(Object val) throws Exception {
		return val.toString();
	}
}
