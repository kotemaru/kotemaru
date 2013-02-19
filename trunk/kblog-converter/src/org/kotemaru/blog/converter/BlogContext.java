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

public class BlogContext {

	private File contentsRoot;
	private File documentRoot;
	private String rootPath;
	private String contentTemplate;
	private String siteTemplate;
	private String templates;

	
	
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
	

	
}
