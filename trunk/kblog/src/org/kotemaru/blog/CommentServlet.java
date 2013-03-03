package org.kotemaru.blog;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.kotemaru.gae.storedbean.StoredBean;
import org.kotemaru.gae.storedbean.StoredBeanService;
import org.kotemaru.util.json.JSONSerializer;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;

@SuppressWarnings("serial")
public class CommentServlet extends HttpServlet {
	
	//private static final String UTF8 = "utf-8";
	private StoredBeanService sbs = SBSFactory.getComment();

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		try {
			_doGet(req, res);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
	private void _doGet(HttpServletRequest req, HttpServletResponse res)
			throws EntityNotFoundException, Exception {

		int limit = Integer.valueOf(req.getParameter("limit"));
		boolean asc = Boolean.valueOf(req.getParameter("asc"));
		String page = req.getParameter("page");
		
		List<CommentBean> list = new ArrayList<CommentBean>();
		Iterator<Entity> ite = sbs.iterate("page", page, "date", asc);
		while (ite.hasNext() && limit-->0) {
			Entity ent = ite.next();
			CommentBean comment = (CommentBean)sbs.entity2bean(ent);
			comment.setKey(ent.getKey().getId());
			comment.setPasswd(null);
			comment.setPage(null);
			comment.setIpAddr(null);
			list.add(comment);
		}
		
		res.setContentType("application/json");
		JSONSerializer serializer = new JSONSerializer();
		serializer.serialize(list, res.getOutputStream());
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		if ("DELETE".equals(req.getParameter("_action"))) {
			_doDelete(req, res);
			return;
		}
		
		CommentBean sb = new CommentBean();
	
		String page = req.getParameter("page");
		if (page == null || page.isEmpty()) {
			res.setStatus(403);
			res.getWriter().write("page not found.");
			return;
		}
		
		sb.setPage(page);
		sb.setName(req.getParameter("name"));
		sb.setEmail(req.getParameter("email"));
		sb.setPasswd(req.getParameter("passwd"));
		sb.setBody(req.getParameter("body").replaceAll("<","&lt;"));
		
		sb.setIpAddr(req.getRemoteAddr());
		sb.setDate(new Date());
		sbs.put(null, sb);
		
		//res.sendRedirect("/"+sb.getPage());
	}
	
	public void _doDelete(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {

		try {
			Long key = Long.valueOf(req.getParameter("key"));
			String passwd = req.getParameter("passwd");
			CommentBean comment = (CommentBean) sbs.get(key);
			if (passwd.equals(comment.getPasswd())) {
				sbs.remove(key);
			} else {
				res.setStatus(403);
			}
		} catch (EntityNotFoundException e) {
			throw new ServletException(e);
		}
	}

}
