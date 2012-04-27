package org.kotemaru.gae.simplefs;
import org.kotemaru.gae.storedbean.StoredBeanService;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.users.*;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import com.google.appengine.api.datastore.*;

import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class SimpleFsServlet extends HttpServlet {
	private StoredBeanService sbs = new StoredBeanService("SimpleFS");
	private StoredBeanService usbs = new StoredBeanService("User");

	public void doPut(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		UserBean ub = getLoginUser(req,res);
		if (ub == null) {
			res.setStatus(403);
			res.setContentType("text/plain;charset=utf-8");
			res.getWriter().write("このファイルの所有者では有りません。");
			return;
		}
		String owner = ub.getEmail();
		
		// filename
		String pathInfo = req.getPathInfo();
		boolean isDir = pathInfo.endsWith("/");
		if (isDir) pathInfo = pathInfo.replaceFirst("/$","");
		
		FileBean fb = null;
		try {
			fb = (FileBean) sbs.get(pathInfo);
			if (!owner.equals(fb.getOwner())) {
				res.setStatus(403);
				res.setContentType("text/plain;charset=utf-8");
				res.getWriter().write("このファイルの所有者では有りません。");
				return;
			}
		} catch (EntityNotFoundException e) {
			fb = new FileBean();
		}
		
		int idx = pathInfo.lastIndexOf('/');
		fb.setParentName(pathInfo.substring(0,idx));
		fb.setLastName(pathInfo.substring(idx+1));
		fb.setDirectory(isDir);
		fb.setContentType(req.getContentType());
		fb.setLastModified(System.currentTimeMillis());
		fb.setOwner(owner);
		fb.setNickName(ub.getNickName());

		byte[] body;
		if (isDir) {
			body = new byte[0];
		} else {
			// body
			InputStream in = req.getInputStream();
			ByteArrayOutputStream bout = new ByteArrayOutputStream(4096);
			byte[] buff = new byte[4096];
			int n = 0;
			while ((n=in.read(buff)) >= 0) {
				bout.write(buff,0,n);
			}
			in.close();
			bout.flush();
			body = bout.toByteArray();
		}
		fb.setBody(body);
		fb.setLength((long)body.length);
		sbs.put(pathInfo, fb);
		
		res.setContentType("text/plain");
		res.getWriter().write(Integer.toString(body.length));
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
					throws IOException, ServletException {
		//if (checkLogin(req,res) == null) return;
		String pathInfo = req.getPathInfo();
		if (pathInfo.endsWith("/")) {
			doGetDir(req, res);
		} else {
			doGetFile(req, res);
		}
	}
	
	private void doGetFile(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		String pathInfo = req.getPathInfo();
		FileBean fb;
		try {
			fb = (FileBean) sbs.get(pathInfo);
		} catch (EntityNotFoundException e) {
			res.setStatus(404);
			res.setContentType("text/plain;charset=utf-8");
			res.getWriter().write("このファイル("+pathInfo+")は存在しません。");
			return;
		}
		byte[] body = fb.getBody();

		res.setContentType(fb.getContentType());
		res.setHeader("Last-Modified", toRFC822(fb.getLastModified()));
		res.setHeader("Cache-Control", "no-cache");

		if (body != null) {
			res.setContentLength(body.length);
			res.getOutputStream().write(body);
			res.getOutputStream().flush();
		} else {
			res.setContentLength(0);
		}
	}


	private void doGetDir(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		res.setContentType("application/json; charset=utf-8");
		res.setHeader("Cache-control","no-cache");
		Writer writer = res.getWriter();
		
		String pathInfo = req.getPathInfo().replaceFirst("/$", "");
		Iterator<Entity> ite = sbs.iterate("parentName", pathInfo);

		int count = 0;
		writer.write("{\n");
		while (ite.hasNext()) {
			Entity e = ite.next();
			String lastName = (String)e.getProperty("lastName");
			Long length = (Long)e.getProperty("length");
			Long lastModified = (Long)e.getProperty("lastModified");
			String nickName = (String)e.getProperty("nickName");
			Boolean directory = (Boolean)e.getProperty("directory");

			if (count++ > 0) writer.write(",");
			writer.write("\""+lastName+"\":{");
			writer.write("\"length\":"+length);
			writer.write(",\"lastModified\":"+lastModified);
			writer.write(",\"nickName\":\""+nickName+"\"");
			writer.write(",\"directory\":"+directory);
			writer.write("}\n");
		}
		writer.write("}");
	}
	private UserBean getLoginUser(HttpServletRequest req, HttpServletResponse res) 
				throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user == null) return null;
		return getByEmail(user.getEmail());
		//String url = userService.createLoginURL("/");
		//res.sendRedirect(url);
		//return null;
	}

	private static final String RFC822 = "EEE, d MMM yyyy HH:mm:ss Z";
	private String toRFC822(Long t) {
		DateFormat rfc822 = new SimpleDateFormat(RFC822);
		return rfc822.format(new Date(t));
	}
	
	private UserBean getByEmail(String email) {
		try {
			return (UserBean) usbs.get(email);
		} catch (EntityNotFoundException e) {
			return null;
		}
	}
}
