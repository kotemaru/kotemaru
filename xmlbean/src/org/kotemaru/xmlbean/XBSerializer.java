package org.kotemaru.xmlbean;

import java.lang.reflect.Array;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.*;

import org.kotemaru.xmlbean.ReflectUtil.PropertyInfo;


public class XBSerializer {
    private Map<Class<?>,String> builderMap = new HashMap<Class<?>,String>();
	private Writer writer;
	private String indent = null;
	private int nestLevel = 0;
	private boolean isLineTop = true;;
	
	public XBSerializer(Writer writer) {
		this.writer = writer;
		
		builderMap.put(String.class, StringXBBuilder.class.getName());
		builderMap.put(Date.class, DateXBBuilder.class.getName());
	}
	public void serialize(Object bean) throws Exception {
		Map<String,String> attrs = new HashMap<String,String>();
		attrs.put("class", bean.getClass().getName());
		String name = bean.getClass().getSimpleName();
		writeBegin(name, attrs);
		sBeanInner(bean);
		writeEnd(name);
		writer.flush();
	}

	public void sBeanInner(Object bean) throws Exception {
		Map<String,PropertyInfo> map = ReflectUtil.getPropertyMap(bean);
		
		for (PropertyInfo info : map.values()) {
			Object val = info.getter.invoke(bean);
			if (val != null) {
				sObject(val, info);
			}
		}
	}
	
	private void sObject(Object val, PropertyInfo info) throws Exception {
		Class<?> type = info.type;
		
		String builderName = builderMap.get(type);
		XBBuilder builder = null;
		if (builderName != null) {
			builder = (XBBuilder) Class.forName(builderName).newInstance();
		}
		
		if (builder != null) {
			sPrim(builder.toString(val), info);
		} else if (type.isPrimitive()) {
			sPrim(val, info);
		} else if (ReflectUtil.isWrapperType(type)) {
			sPrim(val, info);
		} else if (type.isArray()) {
			sArray(val, info);
		} else if (List.class.isAssignableFrom(type)) {
			sList(val, info);
		} else if (type.isEnum()) {
			sEnum(val, info);
		} else {
			sBean(val, info);
		}
	}


	private void sBean(Object val, PropertyInfo info) throws Exception {
		writeBegin(info.name);
		sBeanInner(val);
		writeEnd(info.name);
	}

	private void sPrim(Object val, PropertyInfo info) throws Exception {
		writeValue(info.name, val);
	}

	private void sArray(Object ary, PropertyInfo info) throws Exception {
		PropertyInfo itemInfo = new PropertyInfo("item");
		itemInfo.type = info.type.getComponentType();
		
		writeBegin(info.name);
		int len = Array.getLength(ary);
		for (int i=0; i<len; i++) {
			sObject(Array.get(ary, i), itemInfo);
		}
		writeEnd(info.name);
	}
	
	private void sList(Object val, PropertyInfo info) throws Exception {
		PropertyInfo itemInfo = new PropertyInfo("item");
		itemInfo.type = info.type.getComponentType();
		@SuppressWarnings("unchecked")
		List<Object> list = (List<Object>)val;
		
		writeBegin(info.name);
		for (int i=0; i<list.size(); i++) {
			sObject(list.get(i), itemInfo);
		}
		writeEnd(info.name);
	}
	
	@SuppressWarnings("rawtypes")
	private void sEnum(Object val, PropertyInfo info) throws Exception {
		writeValue(info.name, ((Enum)val).name());
	}
	
	private void writeBegin(String name, Map<String,String> attrs) throws IOException {
		writeLF(0);
		write("<");
		write(name);
		for (Map.Entry<String, String> attr : attrs.entrySet()) {
			write(' ');
			write(attr.getKey());
			write("=\"");
			write(escAttr(attr.getValue()));
			write('"');
		}
		write(">");
		writeLF(1);
		nestLevel++;
	}

	private void writeBegin(String name) throws IOException {
		writeLF(0);
		write("<");
		write(name);
		write(">");
		writeLF(1);
		nestLevel++;
	}
	private void writeEnd(String name) throws IOException {
		nestLevel--;
		writeLF(0);
		write("</");
		write(name);
		write(">");
		writeLF(1);
	}
	
	
	private void writeValue(String name, Object value) throws IOException {
		writeLF(0);
		String val = value.toString();
		boolean isBig = val.length()>40 || val.indexOf('\n')>=0;

		if (isBig) {
			writeBegin(name);
			write(escHtml(val));
			writeEnd(name);
		} else {
			write("<");
			write(name);
			write(" value=\"");
			write(escAttr(val));
			write("\" />");
		}
		writeLF(1);
	}
	private void write(String str) throws IOException {
		writer.write(str);
		isLineTop  = false;
	}
	private void write(char ch) throws IOException {
		writer.write(ch);
		isLineTop = false;
	}
		
	private void writeLF(int mode) throws IOException {
		if (indent == null) return;
		if (isLineTop == false) {
			writer.write('\n');
			isLineTop = true;
		}
		if (mode == 0) {
			for (int i=0; i<nestLevel; i++) {
				writer.write(indent);
			}
		}
	}
	private String escHtml(String val) {
		return val.replaceAll("&","&amp;")
				.replaceAll("<","&lt;")
				.replaceAll(">","&bg;");
	}

	private String escAttr(String val) {
		return escHtml(val)
				.replaceAll("\"","&qute;")
				.replaceAll("\'","&apos;");
	}
	public String getIndent() {
		return indent;
	}
	public void setIndent(String indent) {
		this.indent = indent;
	}

	
}
