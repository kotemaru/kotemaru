package org.kotemaru.jsrpc;

import java.util.HashMap;

import org.slim3.controller.upload.FileItem;

public class MultiPartMap extends HashMap<String,Object> {

	public MultiPartMap() {
		super();
	}

	public String getString(String key) {
		Object val = super.get(key);
		if (val == null) return null;
		return val.toString();
	}
	public String getStringNull(String key) {
		Object val = super.get(key);
		if (val == null) return null;
		String str = val.toString().trim();
		if (str.length() == 0) return null;
		return str;
	}
	public Double getDouble(String key) {
		String val = getStringNull(key);
		if (val == null) return null;
		return Double.parseDouble(val);
	}
	public Integer getInteger(String key) {
		String val = getStringNull(key);
		if (val == null) return null;
		return Integer.parseInt(val);
	}
	public Long getLong(String key) {
		String val = getStringNull(key);
		if (val == null) return null;
		return Long.parseLong(val);
	}

	
	public FileItem getFileItem(String key) {
		Object val = super.get(key);
		if (val == null) return null;
		return (FileItem) val;
	}
	
}
