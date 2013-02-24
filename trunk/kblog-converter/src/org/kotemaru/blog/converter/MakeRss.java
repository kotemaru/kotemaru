package org.kotemaru.blog.converter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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

public class MakeRss extends MakePageBase  {

	protected List<Blog> blogs = new ArrayList<Blog>();
	
	@Override
	public void execute() throws BuildException {
		super.execute();
		makeRss(getBlogContext(),new VelocityContext(), "", blogs); 
	}

	public static void makeRss(BlogContext ctx, VelocityContext vctx, 
			String path, List<Blog> blogs
	) {
		try {
			Collections.sort(blogs, new Comparator<Blog>(){
				@Override
				public int compare(Blog a, Blog b) {
					return b.getDate().compareTo(a.getDate());
				}
			});
		
			vctx.put("root-path", ctx.getRootPath());
			int toIndex = blogs.size()>15 ? 15 : blogs.size();
			vctx.put("blogs", blogs.subList(0, toIndex));
			vctx.put("tool", new Tool());
			vctx.put("date", new Date());
			vctx.put("build-date", new Date());
		
			File outFile = new File(ctx.getDocumentRoot(), "atom.xml");
			VelocityUtil.write(ctx, "rss.xml", vctx, outFile);

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