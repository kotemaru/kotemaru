package org.kotemaru.blog.converter;

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

public class Blog extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;
	
	public static final String Subject = "subject";
	public static final String ContentType = "content-type";
	public static final String Tags = "tags";
	public static final String Date = "date";
	public static final String Public = "public";
	
	private long lastModified;
	private String relativePath;
	
	private Date date;
	
	private String content;

	public Blog() {
	}
	
	public static Blog load(BlogContext ctx, File file) throws IOException, ParseException {
		Blog blog = new Blog();
		File root = ctx.getContentsRoot();
		int len = root.getAbsolutePath().length();
		String relPath = file.getAbsolutePath().substring(len-1);
		blog.setRelativePath(relPath);
		blog.setLastModified(file.lastModified());
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
			String value = line.substring(idx+1);
			this.put(name, value);
			line = r.readLine().trim();
		}
		
		setContent(IOUtil.getString(r));
		in.close();
		
		String dateStr = ((String) this.get(Date)).trim();
		SimpleDateFormat fmt = (dateStr.length()<=10)
			? new SimpleDateFormat("yyyy/MM/dd")
			: new SimpleDateFormat("yyyy/MM/dd HH:mm");
		setDate(fmt.parse((String) this.get(Date)));
		return this;
	}
	
	public String getContentPath() throws IOException {
		return relativePath.replaceFirst("[.]blog$", ".html");
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


	
}
