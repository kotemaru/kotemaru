package org.kotemaru.xmlbean;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Element;

public class DateXBBuilder implements XBBuilder {
	private static final String DEFAULT_FMT = "yyyy-MM-dd'T'HH:mm:ssZ";
	public Object toInstance(Element elem, Class<?> type, Type[] subTypes) throws Exception {
		String format = elem.getAttribute("format");
		format = format.isEmpty() ? DEFAULT_FMT : format;
		SimpleDateFormat fmt = new SimpleDateFormat(format);
		return fmt.parse(XBParser.getValue(elem));
	}

	@Override
	public String toString(Object val) throws Exception {
		SimpleDateFormat fmt = new SimpleDateFormat(DEFAULT_FMT);
		return fmt.format((Date)val);
	}
}
