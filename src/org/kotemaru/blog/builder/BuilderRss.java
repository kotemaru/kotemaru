package org.kotemaru.blog.builder;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.velocity.VelocityContext;


public class BuilderRss implements Builder {
	public static final String CONTENT_PATH = "atom.xml";

	public boolean build(BlogContext ctx) throws IOException {
		List<Blog> blogs = ctx.getBlogs();

		VelocityContext vctx = VelocityUtil.getVelocityContext(ctx, null);
		vctx.put("blogs", blogs);
		vctx.put("build-date", new Date());
		vctx.put("content-path", CONTENT_PATH);
		
		File outFile = new File(ctx.getDocumentRoot(), CONTENT_PATH);
		VelocityUtil.write(ctx, "rss.xml", vctx, outFile);
		return true;
	}

}
