package org.kotemaru.util.json;

public interface JSONCustom {
	public Class getType();
	public void toJSON(JSONSerializer serializer, Object obj) throws java.io.IOException;
	public Object fromJSON(JSONParser parser) throws java.io.IOException;

}
