import org.kotemaru.xmlbean.*;

import java.io.*;

public class Test {
	public static void main(String[] args) throws Exception {
		XBParser parser = new XBParser();
		Object obj = parser.parse(new File(args[0]));
		
		XBSerializer serializer = new XBSerializer(new OutputStreamWriter(System.out));
		serializer.setIndent("  ");
		serializer.serialize(obj);
	}

}
