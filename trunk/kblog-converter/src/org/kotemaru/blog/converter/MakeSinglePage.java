package org.kotemaru.blog.converter;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.velocity.VelocityContext;

public class MakeSinglePage extends MakePageBase  {
	Blog prevBlog[] = new Blog[3];
	
	@Override
	protected void execute(BlogContext ctx, File file) {
		shiftExec(ctx, file);
	}
	@Override
	protected void executeFinish(BlogContext ctx) {
		shiftExec(ctx, null);
		shiftExec(ctx, null);
	}
	
	protected void shiftExec(BlogContext ctx, File file) {
		try {
			prevBlog[2] = prevBlog[1];
			prevBlog[1] = prevBlog[0];
			prevBlog[0] = (file==null) ? null : Blog.load(ctx, file);
			if (prevBlog[1] != null) {
				execute(ctx, prevBlog);
			}
		} catch (Exception e) {
			throw new BuildException(e);
		}
	}
	
	private boolean isUpdate(Blog blog) throws IOException {
		if (blog == null) return false;
		File file = new File(blog.getContentPath());
		return blog.getLastModified()>file.lastModified();
	}
	
	private void execute(BlogContext ctx, Blog[] blogs) {
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
	
}