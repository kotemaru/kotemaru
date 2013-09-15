package org.kotemaru.blog.builder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.velocity.VelocityContext;


public class BuilderRecent implements Builder {
	public static final String CONTENT_PATH = "recent.html";

	public boolean build(BlogContext ctx) throws IOException {
		List<Blog> blogs = ctx.getBlogs();
		return build(ctx, "", blogs);
	}
	
	public static boolean build(BlogContext ctx, String path, List<Blog> blogs)
			throws IOException 
	{
		VelocityContext vctx = VelocityUtil.getVelocityContext(ctx, null);
		vctx.put(Blog.Subject, "最近の投稿");
		vctx.put("blogs", blogs);
		vctx.put("content-path", CONTENT_PATH);
		
		File outFile = new File(ctx.getDocumentRoot(), path+CONTENT_PATH);
		VelocityUtil.write(ctx, "recent.html", vctx, outFile);
		return true;
	}

}
