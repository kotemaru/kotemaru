package org.kotemaru.blog.converter;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class Main {
	
	public static void main(String[] args) throws Exception {
		Main obj = new Main();
		obj.contentsRoot = new File(args[0]);
		obj.documentRoot = new File(args[1]);
		obj.rootPath = args[2];
		obj.template = IOUtil.getText(new File(obj.contentsRoot,"template.html"));
		obj.convert();
	}
	
	private File contentsRoot;
	private File documentRoot;
	private String rootPath;
	private String template;
	
	public void convert() throws IOException, ParseException {
		StringBuffer sbuf = new StringBuffer();
		
		List<Properties> props = getBlogProps(contentsRoot);
		for (int i=0; i<props.size(); i++){
			String content = convert(props.get(i), i);
			if (i<7) sbuf.append(content);
		}
		
		Properties prop = new Properties();
		prop.setProperty("content", sbuf.toString());
		String html = convertHtml(prop, template);
		IOUtil.putText(new File(documentRoot,"index.html"), html);
		
		putTags(props);
		putArcive(props);
	}

	private void putTags(List<Properties> props) throws IOException {
		HashMap<String,List<Properties>> map = new HashMap<String,List<Properties>>();
		for (Properties prop : props) {
			String[] tags = prop.getProperty("tags").split(",");
			for (int i=0; i<tags.length; i++){
				String tag = tags[i].trim();
				List<Properties> list = map.get(tag);
				if (list == null) {
					list = new ArrayList<Properties>();
					map.put(tag, list);
				}
				list.add(prop);
			}
		}
		
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("<ul>\n");
		for (String tag : map.keySet()) {
			sbuf.append("<li>"+esc(tag)+" ("+map.get(tag).size()+")</li>\n");
		}
		sbuf.append("</ul>\n");
	
		IOUtil.putText(new File(documentRoot,"category.html"), sbuf.toString());
	}
	
	
	
	private void putArcive(List<Properties> props) throws IOException, ParseException {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("<ul>\n");
		for (Properties prop : props) {
			Date date = fmt.parse(prop.getProperty("posted.date"));
			sbuf.append("<li>"+fmt.format(date)+" "+prop.getProperty("subject")+"</li>\n");
		}
		sbuf.append("</ul>\n");
	
		IOUtil.putText(new File(documentRoot,"arcive.html"), sbuf.toString());
	}
	
	private String esc(String str) {
		return str.replaceAll("&","&amp;").replaceAll("<","&lt;");
	}
	
	private String convert(Properties prop, int idx) throws IOException {
		File baseDir = new File(prop.getProperty("baseDir"));
		int len = contentsRoot.getAbsolutePath().length();
		String relPath = baseDir.getAbsolutePath().substring(len)+"/index.html";
		relPath = relPath.replaceFirst("^[/]", "");
		prop.setProperty("relative.path", relPath);
		
		String content = getContentText(prop);
		prop.setProperty("content", content);

		String html = convertHtml(prop, template);
		
		File outFile = new File(documentRoot, relPath);
		IOUtil.putText(outFile, html);
		
		return content;
	}

	private String convertHtml(Properties prop, String html) throws IOException {
		for (Object _key : prop.keySet()) {
			String key = (String)_key;
			String val = prop.getProperty(key);
			String entName = "&"+key.replaceAll("[.]", "[.]")+";";
			IOUtil.debug(entName);
			html = html.replaceAll(entName, val);
		}

		html = html.replaceAll("&root[.]path;", this.rootPath);
		return html;
	}

	private String getContentText(Properties prop)  throws IOException {
		File baseDir = new File(prop.getProperty("baseDir"));
		File contentFile = new File(baseDir, 
				"content."+prop.getProperty("content.type"));
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("<h2><a href='"
				+rootPath+"/"+prop.getProperty("relative.path")+"'>"
				+prop.getProperty("subject")
				+"</a></h2>\n");
		StringBuffer content = IOUtil.getText(contentFile, sbuf);
		return content.toString();
	}

	public List<Properties> getBlogProps(File root) throws IOException {
		List<File> list = IOUtil.find(root, "blog.props");
		List<Properties> props = new ArrayList<Properties>();
		for (File file : list) {
			Properties pr = IOUtil.getProps(file);
			pr.setProperty("baseDir", file.getParent());
			props.add(pr);
		}
		Collections.sort(props, new Comparator<Properties>(){
			@Override
			public int compare(Properties a, Properties b) {
				return a.getProperty("posted.date").compareTo(
						b.getProperty("posted.date"));
			}
		});
		return props;
	}

	
	
}
