package org.kotemaru.util.json;

import java.util.*;

public class JSONData {
	private Map root;
	public JSONData(Map map) {
		this.root = map;
	}

	public Object get(String path) {
		String[] names = path.split("[.]");

		Object cur = root;
		for (int i=0; i<names.length; i++) {
			cur = get(cur, names[i]);
			//System.out.println("JSONData.get0:"+names[i]+"="+cur);
		}
		//System.out.println("JSONData.get:"+path+"="+cur);
		return cur;
	}
	public Object get(Object obj, String name) {
		if (obj instanceof Map) {
			return ((Map)obj).get(name);
		} else if (obj instanceof List) {
			return ((List)obj).get(Integer.valueOf(name));
		}
		throw new RuntimeException("Not map or array "+name);
	}
}
