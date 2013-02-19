package org.kotemaru.blog.converter;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public class VelocityUtil {
	private static final String UTF8 = "utf-8";
	
	static {
		Velocity.init();
	}
	
	public static VelocityContext getVelocityContext(HashMap<String,?> params) {
		VelocityContext context = new VelocityContext();
	  	for (String key : params.keySet()) {
	  		context.put(key, params.get(key));
	  	}
	  	return context;
	}
	
	public static String getString(BlogContext ctx, String tmpl, VelocityContext vctx) {
	  	Template template = Velocity.getTemplate(
	  			ctx.getTemplates()+"/"+tmpl, UTF8);
	  	StringWriter sw = new StringWriter();
	  	template.merge(vctx,sw);
	  	return sw.toString();
	}
	public static void write(BlogContext ctx, String tmpl, VelocityContext vctx, File file) 
			throws IOException {
		file.getParentFile().mkdirs();
		OutputStream out = new FileOutputStream(file);
		try {
		  	Template template = Velocity.getTemplate(
		  			ctx.getTemplates()+"/"+tmpl, UTF8);
			Writer w = new OutputStreamWriter(out, UTF8);
		  	template.merge(vctx,w);
		  	w.close();
		} finally {
			out.close();
		}
	}

	
}
