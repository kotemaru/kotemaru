package org.kotemaru.blog.builder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.velocity.VelocityContext;

public class BuilderCategory extends BuilderTopPage {
	public static final String CONTENT_PATH = "category.html";

	public boolean build(BlogContext ctx) throws IOException {
		VelocityContext vctx = VelocityUtil.getVelocityContext(ctx, null);
		vctx.put("tags", ctx.getTags());
		vctx.put("content-path", CONTENT_PATH);
		
		File outFile = new File(ctx.getDocumentRoot(), CONTENT_PATH);
		VelocityUtil.write(ctx, "category.html", vctx, outFile);
		
		
		// 各カテゴリのindex.html
		for (String tag : ctx.getTags().keySet()) {
			vctx.put(Blog.Subject, tag);
			vctx.put("sub-title", "【カテゴリ: "+tag+"】");
			
			String path = "category/"+Tool.encode(tag)+"/";
			vctx.put("content-path", path);
			Category category = ctx.getTags().get(tag);
			if (category.isUpdate()) {
				buildIndexPages(ctx, vctx, path, category);
			}
		}
		return true;
	}
	

}
