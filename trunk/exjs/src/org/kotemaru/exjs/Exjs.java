/*******************************************************************************
 * Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 * Copyright (c) 2011- kotemaru@kotemaru.org
 ******************************************************************************/
package org.kotemaru.exjs;

import java.io.* ;
import java.util.* ;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import org.kotemaru.util.* ;
import org.kotemaru.util.jsg.*;
import org.kotemaru.util.jsg.Source;


public class Exjs {
	public static boolean isDebug = false;

	public static void main(String args[]) throws Exception {
		String bnf = "exjs.bnf";
		String jsg = "exjs.jsg";
		String filter = null;

		ArrayList<String> params = new ArrayList<String>();
		for (int i=0; i<args.length; i++) {
			String arg = args[i];
			if (arg.equals("-bnf")) {
				bnf = args[++i];
			} else if (arg.equals("-filter")) {
				filter = args[++i];
			} else if (arg.equals("-jsg")) {
				jsg = args[++i];
			} else if (arg.equals("-debug")) {
				isDebug = true;
			} else {
				params.add(arg);
			}
		}
		if (jsg == null) {
			usage();
			return;
		}

		BnfParser parser = parseBnf(bnf);
		Transformer trans = parseJsg(jsg);

		for (String inFile : params) {
			if (!inFile.endsWith(".exjs")) continue;
			compile(parser, trans, inFile);
		}
	}
	public static void compile(BnfParser parser, Transformer trans, String inFile) {
		try {
			System.out.println("Compile for "+inFile+"...");
			String outFile = inFile.replaceFirst("[.]exjs$", ".js");
			InputStream in = new FileInputStream(inFile);
			OutputStream out = new FileOutputStream(outFile);
		
			StreamSource inSource = parseExjs(parser, in);
			StreamResult outResult = new StreamResult(out);
			trans.transform(inSource, outResult);
			
		} catch(Exception e) {
			System.out.println("Compile error "+inFile+" : "+e.getMessage());
		}
	}

	public static Transformer parseJsg(String jsgFile) throws Exception {
		String jsg = IOUtil.getResource(Exjs.class, jsgFile);
		JsgParser parser = new JsgParser(new Source(jsg));
		if (parser.parse() == null) {
			System.err.println(parser.getDebugString());
			throw parser.getLastRollback();
		}
//System.out.println("--->"+parser.getString());
		StreamSource src =
			new StreamSource(new StringReader(parser.getString()));
		Transformer trans =
			TransformerFactory.newInstance().newTransformer(src) ;
		return trans;
	}

	public static BnfParser parseBnf(String bnfFile) throws Exception {
		String bnf = IOUtil.getResource(Exjs.class, bnfFile);

		BnfParser parser = new BnfParser(new Source(bnf));
		if (parser.parse() == null) {
			System.err.println(parser.getDebugString());
			throw parser.getLastRollback();
		}
		HashMap defineMap = parser.getDefineMap();
		if (isDebug) {
			Iterator ite = defineMap.keySet().iterator();
			System.out.println("---- BNF ---------------------------------------------");
			while (ite.hasNext()) {
				Object key = ite.next();
				System.out.println(key +" ::= "+defineMap.get(key)+";");
			}
			System.out.println("------------------------------------------------------");
		}
		return parser;
	}
	
	public static StreamSource parseExjs(BnfParser parser, InputStream inFile) throws Exception {
		
		String insrc = IOUtil.streamToString(inFile, "UTF-8");
		Tokenizer tokenizer = new org.kotemaru.exjs.JsTokenizer();

		BnfDriver driver = new BnfDriver(parser, new Source(insrc), tokenizer);
		//driver.setDebug(isDebug);
		if (driver.parse() == null) {
			System.err.println(driver.getDebugString());
			throw driver.getLastRollback();
		}
		String xml = driver.getString();
		if (isDebug) {
			System.out.println(xml);
		}
		return new StreamSource(new StringReader(xml));
	}

	private static StreamSource filter(String filter, StreamSource inSource)
				throws Exception 
	{
		StreamSource src =	new StreamSource(new FileInputStream(filter));
		Transformer trans =
			TransformerFactory.newInstance().newTransformer(src) ;

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		StreamResult outResult = new StreamResult(bout);
		trans.transform(inSource, outResult);
		return new StreamSource(new ByteArrayInputStream(bout.toByteArray()));
	}

	private static void usage() {
		System.err.println("Usage: Exjs [-bnf <bnf-file>] [-filter <xslt-file>] -jsg <jsg-file>");
	}


}	
