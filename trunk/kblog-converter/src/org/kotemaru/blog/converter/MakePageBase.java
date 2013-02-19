package org.kotemaru.blog.converter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public abstract class MakePageBase extends Task  {
	private String contentsRoot;
	private String documentRoot;
	private String rootPath;
	private String templates;

	/** build.xmlで指定されたFileSet等の一覧 */
	protected List<ResourceCollection> rclist = new ArrayList<ResourceCollection>();

	public void add(ResourceCollection rc) {
		rclist.add(rc);
	}
	
	/** 独自タスクの実行 */
	@Override
	public void execute() throws BuildException {
		try {
		BlogContext ctx = this.getBlogContext();
		for (ResourceCollection rc : rclist) {
			for (Iterator<?> ite = rc.iterator(); ite.hasNext();) {
				Resource resource = (Resource) ite.next();
				if (resource instanceof FileResource) {
					File file = ((FileResource) resource).getFile();
					execute(ctx, file);
				}
			}
		}
		executeFinish(ctx);
		} catch (BuildException e) {
			e.printStackTrace();
			throw e;
		}
	}	
	
	protected void executeFinish(BlogContext ctx) {
		// nop.
	}

	abstract protected void execute(BlogContext ctx, File file);

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

	
	public BlogContext getBlogContext() throws BuildException {
		//try {
			BlogContext ctx = new BlogContext();
			ctx.setContentsRoot(new File(getContentsRoot()));
			ctx.setDocumentRoot(new File(getDocumentRoot()));
			ctx.setRootPath(getRootPath());
			ctx.setTemplates(getTemplates());
			return ctx;
		//} catch (IOException e) {
		//	throw new BuildException(e);
		//}
	}
	
	//--------------------------------------------------------------
	// Attributes
	public String getContentsRoot() {
		return contentsRoot;
	}

	public void setContentsRoot(String contentsRoot) {
		this.contentsRoot = contentsRoot;
	}

	public String getDocumentRoot() {
		return documentRoot;
	}

	public void setDocumentRoot(String documentRoot) {
		this.documentRoot = documentRoot;
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public String getTemplates() {
		return templates;
	}

	public void setTemplates(String templates) {
		this.templates = templates;
	}


}