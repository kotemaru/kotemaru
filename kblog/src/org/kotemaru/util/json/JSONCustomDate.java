package org.kotemaru.util.json;

import java.text.SimpleDateFormat;

public class JSONCustomDate implements JSONCustom {
	public final Class getType() {
		return java.util.Date.class;
	}
	public void toJSON(JSONSerializer serializer, Object obj) throws java.io.IOException {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		serializer.sString(fmt.format((java.util.Date)obj));
	}
	public Object fromJSON(JSONParser parser) throws java.io.IOException {
		return null;
	}

}
