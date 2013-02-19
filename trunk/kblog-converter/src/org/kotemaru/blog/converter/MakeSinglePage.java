package org.kotemaru.blog.converter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.velocity.VelocityContext;

public class MakeSinglePage extends MakePageBase  {

	protected List<Blog> blogs = new ArrayList<Blog>();
	
	@Override
	public void execute() throws BuildException {
		super.execute();
		VelocityContext vctx = new VelocityContext();
		vctx.put("sub-title", "");
		makeSinglePage(getBlogContext(), vctx, "", blogs); 
	}

	public static void makeSinglePage(BlogContext ctx, VelocityContext vctx, 
			String path, List<Blog> blogs
	) {
		try {
			sortDate(blogs);
		
			vctx.put("root-path", ctx.getRootPath());
			vctx.put("tool", new Tool());
			for (int i=0; i<blogs.size(); i++ ) {
				Blog[] blog3 = new Blog[3];
				blog3[2] = (i>=1)?blogs.get(i-1):null;
				blog3[1] = blogs.get(i);
				blog3[0] = (i<blogs.size()-1)?blogs.get(i+1):null;
				execute(ctx, blog3);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException(e);
		}
	}	


	private static boolean isUpdate(Blog blog) throws IOException {
		if (blog == null) return false;
		File file = new File(blog.getContentPath());
		return blog.getLastModified()>file.lastModified();
	}
	
	private static void execute(BlogContext ctx, Blog[] blogs) {
		try {
			boolean isUpdate 
				= isUpdate(blogs[0]) || isUpdate(blogs[1]) || isUpdate(blogs[2]);
			if (!isUpdate) return;
			
			Blog blog = blogs[1];
			VelocityContext vctx = VelocityUtil.getVelocityContext(blog);
			vctx.put("root-path", ctx.getRootPath());
			vctx.put("tool", new Tool());
			vctx.put("blog", blog);
			vctx.put("next-blog", blogs[0]);
			vctx.put("prev-blog", blogs[2]);
			File outFile = new File(ctx.getDocumentRoot(), blog.getContentPath());
			VelocityUtil.write(ctx, "content.html", vctx, outFile);
		} catch (Exception e) {
			throw new BuildException(e);
		}
	}

	@Override
	protected void execute(BlogContext ctx, File file) {
		try {
			Blog blog = Blog.load(ctx, file);
			blogs.add(blog);
		} catch (Exception e) {
			throw new BuildException(e);
		}
	}
	
}