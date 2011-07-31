package org.kotemaru.jsrpc;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.*;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.kotemaru.util.IOUtil;
import org.kotemaru.util.json.JSONParser;
import org.kotemaru.util.json.JSONSerializer;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

@SuppressWarnings("serial")
public class ResourceServlet extends HttpServlet {
	private static final String NS_MEMCACHE = ResourceServlet.class.getName();
	private static MemcacheService memcache = 
		MemcacheServiceFactory.getMemcacheService(NS_MEMCACHE);
	
	private static final String MIME_JSON = "application/json";
	private static final String MIME_JS = "application/javascript";
	private static final String MIME_TEXT = "text/plain";

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String pinfo = req.getPathInfo();
		if (pinfo == null) return;
		try {
			takeResource(req, resp);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}

	private void takeResource(HttpServletRequest req, 
			HttpServletResponse resp) throws Exception  {
		String pinfo = req.getPathInfo();

		String data = (String) memcache.get(pinfo);
		if (data == null) {
			data = IOUtil.getResource(ResourceServlet.class, pinfo);
			memcache.put(pinfo, data);
		}
		
		if (pinfo.endsWith(".js")) {
			resp.setContentType(MIME_JS);
		} else if (pinfo.endsWith(".json")) {
			resp.setContentType(MIME_JSON);
		} else  {
			resp.setContentType(MIME_TEXT);
		}
		
		resp.getWriter().write(data);
	}
}	