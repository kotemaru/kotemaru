package org.kotemaru.blog.builder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.velocity.VelocityContext;


public class BuilderRecent implements Builder {

	public boolean build(BlogContext ctx) throws IOException {
		List<Blog> blogs = ctx.getBlogs();

		VelocityContext vctx = VelocityUtil.getVelocityContext(ctx, null);
		vctx.put(Blog.Subject, "最近の投稿");
		vctx.put("blogs", blogs);
		
		File outFile = new File(ctx.getDocumentRoot(), "recent.html");
		VelocityUtil.write(ctx, "recent.html", vctx, outFile);
		return true;
	}

}
