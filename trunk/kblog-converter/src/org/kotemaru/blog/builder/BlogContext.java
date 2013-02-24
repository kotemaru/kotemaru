package org.kotemaru.blog.builder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlogContext {

	private File contentsRoot;
	private File documentRoot;
	private String rootPath;
	private String contentTemplate;
	private String siteTemplate;
	private String templates;

	private List<Blog> blogs = new ArrayList<Blog>();
	private HashMap<String, List<Blog>> tags = new HashMap<String, List<Blog>>();

	public File getContentsRoot() {
		return contentsRoot;
	}
	public void setContentsRoot(File contentsRoot) {
		this.contentsRoot = contentsRoot;
	}
	public File getDocumentRoot() {
		return documentRoot;
	}
	public void setDocumentRoot(File documentRoot) {
		this.documentRoot = documentRoot;
	}
	public String getRootPath() {
		return rootPath;
	}
	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}
	public String getSiteTemplate() {
		return siteTemplate;
	}
	public void setSiteTemplate(String siteFrameTemplate) {
		this.siteTemplate = siteFrameTemplate;
	}
	public String getContentTemplate() {
		return contentTemplate;
	}
	public void setContentTemplate(String contentTemplate) {
		this.contentTemplate = contentTemplate;
	}
	public String getTemplates() {
		return templates;
	}
	public void setTemplates(String templates) {
		this.templates = templates;
	}
	public List<Blog> getBlogs() {
		return blogs;
	}
	public void setBlogs(List<Blog> blogs) {
		this.blogs = blogs;
	}
	public HashMap<String, List<Blog>> getTags() {
		return tags;
	}
	public void setTags(HashMap<String, List<Blog>> tags) {
		this.tags = tags;
	}
	

	
}
