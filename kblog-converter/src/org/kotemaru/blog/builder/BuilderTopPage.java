package org.kotemaru.blog.builder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.velocity.VelocityContext;

public class BuilderTopPage implements Builder {

	public boolean build(BlogContext ctx) throws IOException {
		VelocityContext vctx = VelocityUtil.getVelocityContext(ctx, null);
		vctx.put(Blog.Subject, "トップ");
		vctx.put("categoryTag", "");
		buildIndexPages(ctx, vctx, "", ctx.getBlogs());
		return true;
	}
	
	public void buildIndexPages(BlogContext ctx, VelocityContext vctx,
			String path, List<Blog> blogs)
			throws IOException 
	{
		int len = ctx.getPagingSize();
		for (int off=0; off<blogs.size(); off+=len ) {
			int toIndex = off+len<blogs.size() ? off+len : blogs.size();
			vctx.put("blogs", blogs.subList(off, toIndex));
			vctx.put("next-page", getPageName(off,len,  1, blogs.size()));
			vctx.put("prev-page", getPageName(off,len, -1, blogs.size()));
		
			File outFile = new File(ctx.getDocumentRoot(), 
					path+getPageName(off,len, 0, blogs.size()));
			VelocityUtil.write(ctx, "index.html", vctx, outFile);
		}
	}	

	private static String getPageName(int off, int len, int delta, int max) {
		int pageNo = (off/len)+delta;
		if (pageNo<0 || (max/len)<pageNo ) return "";
		if (pageNo == 0) return "index.html";
		return "page-"+pageNo+".html";
	}
	
}
