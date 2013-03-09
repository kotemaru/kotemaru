package org.kotemaru.blog.builder;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileResource;

public class BlogTask extends Task  {

	private BlogContext context = new BlogContext();
	
	// パラメータ
	private String build = "all";

	protected List<ResourceCollection> rclist = new ArrayList<ResourceCollection>();
	public void add(ResourceCollection rc) {
		rclist.add(rc);
	}
	
	@Override
	public void execute() throws BuildException {
		VelocityUtil.init(context);
		
		HashMap<String, Builder> builders = new HashMap<String, Builder>();
		builders.put("index",    new BuilderTopPage());
		builders.put("content",  new BuilderSinglePage());
		builders.put("category", new BuilderCategory());
		builders.put("arcive",   new BuilderArchive());
		builders.put("recent",   new BuilderRecent());
		builders.put("rss",      new BuilderRss());
		
		try {
			initBlogContext();

			if ("all".equals(build)) {
				for (String name : builders.keySet()) {
					builders.get(name).build(context);
				}
			} else {
				for (String name : Tool.parseCamma(build)) {
					builders.get(name).build(context);
				}
			}
			
			//new BuilderTopPage().build(context);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException(e);
		}
	}	
	
	public void initBlogContext() throws IOException, ParseException {
		List<Blog> blogs = context.getBlogs();
		
		for (ResourceCollection rc : rclist) {
			for (Iterator<?> ite = rc.iterator(); ite.hasNext();) {
				Resource resource = (Resource) ite.next();
				if (resource instanceof FileResource) {
					File file = ((FileResource) resource).getFile();
					Blog blog = Blog.load(context, file);
					blogs.add(blog);
				}
			}
		}
		
		sortDate(blogs);
		
		HashMap<String, Category> tags = context.getTags();
		
		for (Blog blog : blogs) {
			addTags(tags, blog);
		}
	}
	
	private void addTags(HashMap<String, Category> tagMap, Blog blog) throws IOException {
		boolean isUpdate = blog.isUpdate(context);
		
		String[] tags = ((String)blog.get(Blog.Tags)).split(",");
		for (int i=0; i<tags.length; i++) {
			String tag = tags[i].trim();
			if (!tag.isEmpty()) {
				Category category = tagMap.get(tag);
				if (category == null) {
					category = new Category(tag);
					tagMap.put(tag, category);
				}
				category.add(blog);
				if (isUpdate) category.setUpdate(true);
			}
		}
	}

	public static List<Blog> sortDate(List<Blog> blogs) {
		Collections.sort(blogs, new Comparator<Blog>(){
			@Override
			public int compare(Blog a, Blog b) {
				return b.getDate().compareTo(a.getDate());
			}
		});
		return blogs;
	}
	
	
	public static String getRelativePath(BlogContext ctx, File file) throws IOException {
		File root = ctx.getContentsRoot();
		int len = root.getAbsolutePath().length();
		String relPath = file.getAbsolutePath().substring(len-1);
		return relPath;
	}
	
	
	//--------------------------------------------------------------
	// Attributes
	public File getContentsRoot() {
		return context.getContentsRoot();
	}

	public void setContentsRoot(File contentsRoot) {
		context.setContentsRoot(contentsRoot);
	}

	public File getDocumentRoot() {
		return context.getDocumentRoot();
	}

	public void setDocumentRoot(File documentRoot) {
		context.setDocumentRoot(documentRoot);
	}

	public String getRootPath() {
		return context.getRootPath();
	}

	public void setRootPath(String rootPath) {
		context.setRootPath(rootPath);
	}

	public String getSiteTemplate() {
		return context.getSiteTemplate();
	}

	public void setSiteTemplate(String siteFrameTemplate) {
		context.setSiteTemplate(siteFrameTemplate);
	}

	public String getContentTemplate() {
		return context.getContentTemplate();
	}

	public void setContentTemplate(String contentTemplate) {
		context.setContentTemplate(contentTemplate);
	}

	public String getTemplates() {
		return context.getTemplates();
	}

	public void setTemplates(String templates) {
		context.setTemplates(templates);
	}

	public String getBuild() {
		return build;
	}

	public void setBuild(String build) {
		this.build = build;
	}
	
	public int getPagingSize() {
		return context.getPagingSize();
	}

	public void setPagingSize(int pagingSize) {
		context.setPagingSize(pagingSize);
	}


}