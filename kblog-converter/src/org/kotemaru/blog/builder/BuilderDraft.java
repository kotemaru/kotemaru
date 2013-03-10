package org.kotemaru.blog.builder;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.velocity.VelocityContext;

public class BuilderDraft implements Builder {
	public static final String CONTENT_PATH = "draft.html";

	public boolean build(BlogContext ctx) throws IOException {
		List<Blog> blogs = ctx.getBlogs();
		
		for (int i=0; i<blogs.size(); i++ ) {
			Blog[] blog3 = new Blog[3];
			blog3[2] = (i>=1)?blogs.get(i-1):null;
			blog3[1] = blogs.get(i);
			blog3[0] = (i<blogs.size()-1)?blogs.get(i+1):null;
			buildSiglePage(ctx, blog3);
		}
		
		buildList(ctx);
		return true;
	}
	
	public boolean buildList(BlogContext ctx) throws IOException {
		List<Blog> blogs = ctx.getBlogs();

		VelocityContext vctx = VelocityUtil.getVelocityContext(ctx, null);
		vctx.put(Blog.Subject, "下書き一覧");
		vctx.put("blogs", blogs);
		vctx.put("content-path", CONTENT_PATH);
		
		File outFile = new File(ctx.getDocumentRoot(), CONTENT_PATH);
		VelocityUtil.write(ctx, "recent.html", vctx, outFile);
		return true;
	}

	
	public boolean buildSiglePage(BlogContext ctx, Blog[] blogs) throws IOException {
		Blog blog = blogs[1];
		if (!blog.isUpdate()) return false;

		VelocityContext vctx = VelocityUtil.getVelocityContext(ctx, blog);
		vctx.put("blog", blog);
		vctx.put("next-blog", blogs[0]);
		vctx.put("prev-blog", blogs[2]);
		vctx.put("content-path", blog.getContentPath());
		
		File outFile = new File(ctx.getDocumentRoot(), blog.getContentPath());
		VelocityUtil.write(ctx, "content.html", vctx, outFile);
		return true;
	}
	
}
