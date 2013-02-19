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
import org.apache.velocity.VelocityContext;

public class MakeTopPage extends MakePageBase  {

	protected List<Blog> blogs = new ArrayList<Blog>();
	
	@Override
	public void execute() throws BuildException {
		super.execute();
		VelocityContext vctx = new VelocityContext();
		vctx.put(Blog.Subject, "トップ");
		vctx.put("sub-title", "");
		makeIndexPage(getBlogContext(), vctx, "", blogs); 
	}

	public static void makeIndexPage(BlogContext ctx, VelocityContext vctx, 
			String path, List<Blog> blogs
	) {
		try {
			sortDate(blogs);
		
			vctx.put("root-path", ctx.getRootPath());
			vctx.put("tool", new Tool());
			int len = 8;
			for (int off=0; off<blogs.size(); off+=len ) {
				int toIndex = off+len<blogs.size() ? off+len : blogs.size();
				vctx.put("blogs", blogs.subList(off, toIndex));
				vctx.put("next-page", getPageName(off,len,  1, blogs.size()));
				vctx.put("prev-page", getPageName(off,len, -1, blogs.size()));
			
				File outFile = new File(ctx.getDocumentRoot(), 
						path+getPageName(off,len, 0, blogs.size()));
				VelocityUtil.write(ctx, "index.html", vctx, outFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException(e);
		}
	}	

	private static String getPageName(int off, int len, int delta, int max) {
		int pageNo = (off/len)+delta;
		if (pageNo<0 || (max/len)<pageNo ) return "";
		if (pageNo == 0) return "index.html";
		return "page-"+pageNo+".html";
	}
	
	@Override
	protected void execute(BlogContext ctx, File file) {
		try {
			//Blog blog = Blog.loadNoBody(ctx, file);
			Blog blog = Blog.load(ctx, file);
			blogs.add(blog);
		} catch (Exception e) {
			throw new BuildException(e);
		}
	}	
	

}