/*******************************************************************************
 * Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 * Copyright (c) 2011- kotemaru@kotemaru.org
 ******************************************************************************/
package org.kotemaru.util.jsg;

import java.io.* ;
import java.util.* ;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import org.kotemaru.util.* ;


public class JSG {
	public static boolean isDebug = false;

	public static void main(String args[]) throws Exception {
		String bnf = null;
		String jsg = null;
		String filter = null;

		InputStream inFile = System.in;
		OutputStream outFile = System.out;
		ArrayList params = new ArrayList();
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

		long t0 = System.currentTimeMillis();

		StreamSource inSource = null;
		StreamResult outResult = new StreamResult(outFile);
		if (bnf == null) {
			inSource = new StreamSource(inFile);
		} else {
			inSource = parseBnf(bnf, inFile);
		}
		long t1 = System.currentTimeMillis();

		if (filter != null) {
			inSource = filter(filter, inSource);
		}
		long t2 = System.currentTimeMillis();

		Transformer trans = parseJsg(jsg);
		long t3 = System.currentTimeMillis();
		trans.transform(inSource, outResult);
		long t4 = System.currentTimeMillis();

		System.err.println("\nparseBnf  :"+(t1-t0)+"ms");
		System.err.println("filter    :"+(t2-t1)+"ms");
		System.err.println("parseJsg  :"+(t3-t2)+"ms");
		System.err.println("transform :"+(t4-t3)+"ms");
	}

	private static Transformer parseJsg(String jsgFile) throws Exception {
		String jsg = IOUtil.getFile(jsgFile);
		JsgParser parser = new JsgParser(new Source(jsg));
		if (parser.parse() == null) {
			System.err.println(parser.getDebugString());
			throw parser.lastRollback;
		}
//System.out.println("--->"+parser.getString());
		StreamSource src =
			new StreamSource(new StringReader(parser.getString()));
		Transformer trans =
			TransformerFactory.newInstance().newTransformer(src) ;
		return trans;
	}

	private static StreamSource parseBnf(String bnfFile, InputStream inFile) throws Exception {
		String bnf = IOUtil.getFile(bnfFile);

		BnfParser parser = new BnfParser(new Source(bnf));
		if (parser.parse() == null) {
			System.err.println(parser.getDebugString());
			throw parser.lastRollback;
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

		String insrc = IOUtil.streamToString(inFile, "UTF-8");
		Tokenizer tokenizer = new org.kotemaru.exjs.JsTokenizer();

		BnfDriver driver = new BnfDriver(parser, new Source(insrc), tokenizer);
		driver.setDebug(isDebug);
		if (driver.parse() == null) {
			System.err.println(driver.getDebugString());
			throw driver.lastRollback;
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
		System.err.println("Usage: JSG [-bnf <bnf-file>] [-filter <xslt-file>] -jsg <jsg-file>");
	}


}	
