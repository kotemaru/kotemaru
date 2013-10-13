package org.kotemaru.blog.builder;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.velocity.VelocityContext;

public class BuilderSinglePage implements Builder {

	public boolean build(BlogContext ctx) throws IOException {
		List<Blog> blogs = ctx.getBlogs();
		
		for (int i=0; i<blogs.size(); i++ ) {
			Blog[] blog3 = new Blog[3];
			blog3[2] = (i>=1)?blogs.get(i-1):null;
			blog3[1] = blogs.get(i);
			blog3[0] = (i<blogs.size()-1)?blogs.get(i+1):null;
			buildSiglePage(ctx, blog3);
		}
		return true;
	}
	public boolean buildSiglePage(BlogContext ctx, Blog[] blogs) throws IOException {
		Blog blog = blogs[1];
		boolean isUpdate = isUpdate(ctx,blogs[0]) 
				|| isUpdate(ctx,blog) 
				|| isUpdate(ctx,blogs[2]);
		//Tool.log(blog.get(Blog.Subject)+":"
		//		+","+isUpdate(ctx,blogs[0])
		//		+","+isUpdate(ctx,blog) 
		//		+","+isUpdate(ctx,blogs[2])
		//);
		if (!isUpdate) return false;

		VelocityContext vctx = VelocityUtil.getVelocityContext(ctx, blog);
		vctx.put("blog", blog);
		vctx.put("next-blog", blogs[0]);
		vctx.put("prev-blog", blogs[2]);
		vctx.put("content-path", blog.getContentPath());
		vctx.put("categoryTag", "");
		vctx.put("recent-path", "");
		

		File outFile = new File(ctx.getDocumentRoot(), blog.getContentPath());
		VelocityUtil.write(ctx, "content.html", vctx, outFile);
		return true;
	}
	
	private static boolean isUpdate(BlogContext ctx, Blog blog) throws IOException {
		if (blog == null) return false;
		return blog.isUpdate();
	}

}
