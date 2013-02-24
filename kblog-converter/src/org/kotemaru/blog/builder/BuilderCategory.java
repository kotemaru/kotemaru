package org.kotemaru.blog.builder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.velocity.VelocityContext;

public class BuilderCategory extends BuilderTopPage {

	public boolean build(BlogContext ctx) throws IOException {
		VelocityContext vctx = VelocityUtil.getVelocityContext(ctx, null);
		vctx.put("tags", ctx.getTags());
		//Tool.log("tags=",vctx.get("tags"));
		File outFile = new File(ctx.getDocumentRoot(), "category.html");
		VelocityUtil.write(ctx, "category.html", vctx, outFile);
		
		
		// 各カテゴリのindex.html
		for (String tag : ctx.getTags().keySet()) {
			vctx.put(Blog.Subject, tag);
			vctx.put("sub-title", "【カテゴリ: "+tag+"】");
			List<Blog> blogs = ctx.getTags().get(tag);
			buildIndexPages(ctx, vctx, "category/"+Tool.encode(tag)+"/", blogs);
		}
		return true;
	}
	

}
