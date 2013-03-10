package org.kotemaru.blog.builder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Date;

import com.petebevin.markdown.MarkdownProcessor;

public class Blog extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;
	
	public static final String Subject = "subject";
	public static final String ContentType = "content-type";
	public static final String Tags = "tags";
	public static final String Date = "date";
	public static final String Public = "public";
	
	public static final String PLAIN_TEXT = "html/text";
	public static final String HTML_TEXT = "html/text";
	public static final String MARKDOWN_TEXT = "markdown/text";
	
	private long lastModified;
	private String relativePath;
	private boolean update;

	private Date date;
	private boolean publish;

	private String content;

	public Blog() {
	}
	
	public static Blog load(BlogContext ctx, File file) throws IOException, ParseException {
		Blog blog = new Blog();
		File root = ctx.getContentsRoot();
		int len = root.getAbsolutePath().replaceFirst("/$","").length();
		String relPath = file.getAbsolutePath().substring(len+1);
		blog.setRelativePath(relPath);
		blog.setLastModified(file.lastModified());
		blog.setUpdate(blog.isUpdate(ctx));
		return blog.load(file);
	}
	public static Blog loadNoBody(BlogContext ctx, File file) throws IOException, ParseException {
		Blog blog = load(ctx, file);
		blog.setContent(null);
		return blog;
	}

	public Blog load(File file) throws IOException, ParseException {
		InputStream in = new FileInputStream(file);
		Reader r1 = new InputStreamReader(in, "utf-8");
		BufferedReader r = new BufferedReader(r1);

		String line = r.readLine().trim();
		while (line.length() > 0) {
			int idx = line.indexOf(':');
			String name  = line.substring(0, idx).trim().toLowerCase();
			String value = line.substring(idx+1).trim();
			this.put(name, value);
			line = r.readLine().trim();
		}
		
		String rawText = getString(r);
		in.close();
		
		String cType = (String) get(ContentType);
		if (MARKDOWN_TEXT.equals(cType)) {
			MarkdownProcessor markdown = new MarkdownProcessor();
			setContent(markdown.markdown(rawText));
		} else {
			setContent(rawText);
		}
		
		
		String dateStr = ((String) this.get(Date)).trim();
		SimpleDateFormat fmt = (dateStr.length()<=10)
			? new SimpleDateFormat("yyyy/MM/dd")
			: new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date date = fmt.parse((String) this.get(Date));
		setDate(date);

		if (date.getTime() < System.currentTimeMillis()) {
			String _public = ((String) this.get(Public)).toLowerCase();
			setPublish("yes".equals(_public) || "true".equals(_public));
		}
		return this;
	}

	public static String getString(Reader r) throws IOException {
		StringBuilder sbuf = new StringBuilder();
		int n;
		char[] buff = new char[4096];
		while ((n=r.read(buff)) > 0) {
			sbuf.append(buff,0,n);
		}
		return sbuf.toString();
	}
	
	public String getContentPath() throws IOException {
		return relativePath.replaceFirst("[.]blog$", ".html");
	}
	public boolean isUpdate(BlogContext ctx) throws IOException {
		File file = new File(ctx.getDocumentRoot(), this.getContentPath());
		if (file.exists() == false) return true;
		return this.getLastModified()>file.lastModified();
	}

	//====================================================================
	// Setter/Getter
	
	public String getRelativePath() {
		return relativePath;
	}
	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setContent(String content) {
		this.content = content;
	}
	public String getContent() {
		return content;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public boolean isPublish() {
		return publish;
	}

	public void setPublish(boolean publish) {
		this.publish = publish;
	}

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}


	
}
