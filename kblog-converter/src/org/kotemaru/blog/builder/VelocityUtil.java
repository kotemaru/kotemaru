package org.kotemaru.blog.builder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public class VelocityUtil {
	private static final String UTF8 = "utf-8";
	
	public static void init(BlogContext ctx) {
		Velocity.setProperty("file.resource.loader.path", ctx.getTemplates());
		Velocity.init();
	}
	
	public static VelocityContext getVelocityContext(BlogContext ctx, HashMap<String,?> params) {
		VelocityContext vctx = new VelocityContext();
		vctx.put("sub-title", "");
		vctx.put("root-path", ctx.getRootPath());
		vctx.put("tool", new Tool());
		vctx.put("date", new Date());
		vctx.put("timestamp", new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
		vctx.put("content-path", "");
		
		if (params != null) {
		  	for (String key : params.keySet()) {
		  		vctx.put(key, params.get(key));
		  	}
		}
	  	return vctx;
	}
	
	public static String getString(BlogContext ctx, String tmpl, VelocityContext vctx) {
	  	Template template = Velocity.getTemplate(tmpl, UTF8);
	  	StringWriter sw = new StringWriter();
	  	template.merge(vctx,sw);
	  	return sw.toString();
	}
	public static void write(BlogContext ctx, String tmpl, VelocityContext vctx, File file) 
			throws IOException {
		Tool.log("Generate:", file);
		
		file.getParentFile().mkdirs();
		OutputStream out = new FileOutputStream(file);
		try {
		  	Template template = Velocity.getTemplate(tmpl, UTF8);
			Writer w = new OutputStreamWriter(out, UTF8);
		  	template.merge(vctx,w);
		  	w.close();
		} finally {
			out.close();
		}
	}

	
}
