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

		CommentBean sb = new CommentBean();
		
		sb.setPage(req.getParameter("page"));
		sb.setName(req.getParameter("name"));
		sb.setEmail(req.getParameter("email"));
		sb.setPasswd(req.getParameter("passwd"));
		sb.setBody(req.getParameter("body").replaceAll("<","&lt;"));
		
		sb.setIpAddr(req.getRemoteAddr());
		sb.setDate(new Date());
		sbs.put(null, sb);
	}
	
	public void doDelete(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {

		String game = req.getParameter("game") + ":"
				+ req.getParameter("stage");
		Iterator<Entity> ite = sbs.iterateKeys("game", game);
		while (ite.hasNext()) {
			Entity ent = (Entity) ite.next();
			sbs.remove(ent.getKey());
		}
	}

}
