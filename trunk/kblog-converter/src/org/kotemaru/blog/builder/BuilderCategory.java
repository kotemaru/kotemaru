package org.kotemaru.blog.builder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.velocity.VelocityContext;

public class BuilderCategory extends BuilderTopPage {
	public static final String CONTENT_PATH = "category.html";

	public boolean build(BlogContext ctx) throws IOException {
		VelocityContext vctx = VelocityUtil.getVelocityContext(ctx, null);
		vctx.put("tags", sortTags(ctx.getTags()));
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
				BlogTask.sortDate(category);
				buildIndexPages(ctx, vctx, path, category);
			}
		}
		return true;
	}
	
	private List<Category> sortTags(Map<String,Category> map) {
		List<Category> list = new ArrayList<Category>(map.size());
		list.addAll(map.values());
		Collections.sort(list, new Comparator<Category>(){
			@Override
			public int compare(Category a, Category b) {
				return b.size() - a.size();
			}
		});
		return list;
	}

}
