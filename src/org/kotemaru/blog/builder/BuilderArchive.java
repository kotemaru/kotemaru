package org.kotemaru.blog.builder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.velocity.VelocityContext;


public class BuilderArchive implements Builder {
	public static final String CONTENT_PATH = "archive.html";

	public boolean build(BlogContext ctx) throws IOException {
		List<Blog> blogs = ctx.getBlogs();

		VelocityContext vctx = VelocityUtil.getVelocityContext(ctx, null);
		vctx.put(Blog.Subject, "アーカイブ");
		vctx.put("blogs", blogs);
		vctx.put("content-path", CONTENT_PATH);
		
		File outFile = new File(ctx.getDocumentRoot(), CONTENT_PATH);
		VelocityUtil.write(ctx, "archive.html", vctx, outFile);
		return true;
	}

}
