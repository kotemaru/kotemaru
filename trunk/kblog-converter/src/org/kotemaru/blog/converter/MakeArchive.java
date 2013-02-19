package org.kotemaru.blog.converter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.velocity.VelocityContext;

public class MakeArchive extends MakePageBase  {

	protected List<Blog> blogs = new ArrayList<Blog>();
	
	@Override
	public void execute() throws BuildException {
		super.execute();
		makeArchive(this.getBlogContext(), new VelocityContext(), "", blogs);
	}
	
	
	public static void makeArchive(BlogContext ctx, VelocityContext vctx, 
			String path, List<Blog> blogs
	) {
		try {
			sortDate(blogs);
			vctx.put(Blog.Subject, "アーカイブ");
			vctx.put("root-path", ctx.getRootPath());
			vctx.put("blogs", blogs);
			vctx.put("tool", new Tool());
			File outFile = new File(ctx.getDocumentRoot(), "archive.html");
			VelocityUtil.write(ctx, "archive.html", vctx, outFile);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException(e);
		}
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