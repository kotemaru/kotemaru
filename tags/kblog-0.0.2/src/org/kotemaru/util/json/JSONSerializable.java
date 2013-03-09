package org.kotemaru.util.json;

public interface JSONSerializable {
	public void toJSON(JSONSerializer serializer) throws java.io.IOException;
	public Object fromJSON(JSONParser parser) throws java.io.IOException;

}
