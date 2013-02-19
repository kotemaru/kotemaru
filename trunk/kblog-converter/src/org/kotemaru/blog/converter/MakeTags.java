package org.kotemaru.blog.converter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.velocity.VelocityContext;

public class MakeTags extends MakePageBase  {

	private HashMap<String, List<Blog>> blogs = new HashMap<String, List<Blog>>();
	
	@Override
	public void execute() throws BuildException {
		super.execute();
		makeCategoryPage();
		
		VelocityContext vctx = new VelocityContext();
		for (String tag : blogs.keySet()) {
			vctx.put("subject", tag);
			vctx.put("sub-title", "【カテゴリ: "+tag+"】");
			MakeTopPage.makeIndexPage(getBlogContext(), vctx, 
					"category/"+tag+"/", blogs.get(tag)); 
		}
		
	}

	
	protected void makeCategoryPage() {
		try {
			BlogContext ctx = this.getBlogContext();
			VelocityContext vctx = new VelocityContext();
			vctx.put("root-path", ctx.getRootPath());
			vctx.put("blogs", blogs);
			File outFile = new File(ctx.getDocumentRoot(), "category.html");
			VelocityUtil.write(ctx, "category.html", vctx, outFile);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException(e);
		}
	}	

	@Override
	protected void execute(BlogContext ctx, File file) {
		try {
			Blog blog = Blog.load(ctx, file);
		
			String[] tags = ((String)blog.get(Blog.Tags)).split(",");
			for (int i=0; i<tags.length; i++) {
				String tag = tags[i].trim();
				if (!tag.isEmpty()) {
					List<Blog> list = blogs.get(tag);
					if (list == null) {
						list = new ArrayList<Blog>();
						blogs.put(tag, list);
					}
					list.add(blog);
				}
			}
		} catch (Exception e) {
			throw new BuildException(e);
		}
	}

}