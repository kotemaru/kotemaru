package org.kotemaru.blog.builder;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.velocity.VelocityContext;


public class BuilderRss implements Builder {

	public boolean build(BlogContext ctx) throws IOException {
		List<Blog> blogs = ctx.getBlogs();

		VelocityContext vctx = VelocityUtil.getVelocityContext(ctx, null);
		vctx.put("blogs", blogs);
		vctx.put("build-date", new Date());
		
		File outFile = new File(ctx.getDocumentRoot(), "atom.xml");
		VelocityUtil.write(ctx, "rss.xml", vctx, outFile);
		return true;
	}

}
