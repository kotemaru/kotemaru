package org.kotemaru.gae.simplefs;
import org.kotemaru.gae.storedbean.StoredBeanService;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.users.*;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import com.google.appengine.api.datastore.*;

import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class ScoreServlet extends HttpServlet {
	private StoredBeanService sbs = new StoredBeanService("Score");

	public void init(ServletConfig config) throws ServletException {
		sbs.setMemcacheEnable(false);
	}
    
	public void doGet(HttpServletRequest req, HttpServletResponse res)
					throws IOException, ServletException {


		int limit = Integer.valueOf(req.getParameter("limit"));
		boolean asc = Boolean.valueOf(req.getParameter("asc"));
		String game = req.getParameter("game")+":"+req.getParameter("stage");
		
		Writer writer = res.getWriter();
		writer.write("[\n");
		Iterator<Entity> ite = sbs.iterate("game", game, "score", asc);
		while (ite.hasNext() && limit-->0) {
			Entity ent = (Entity) ite.next();
			putScore(writer, ent);
			writer.write(", ");
		}
		writer.write("null");
		writer.write("\n]");
	}

	public void putScore(Writer writer, Entity ent) throws IOException {
		writer.write("{");
		putItem(writer, ent, "name");
		writer.write(", ");
		putItem(writer, ent, "score");
		writer.write(", ");
		putItem(writer, ent, "stage");
		writer.write("}\n");
	}
	
	
	private void putItem(Writer writer, Entity ent, String name) throws IOException {
		writer.write(toStr(name));
		writer.write(":");
		Object val = ent.getProperty(name);
		if (val == null) {
			writer.write("null");
		} else if (val instanceof String) {
			writer.write(toStr((String)val));
		} else if (val instanceof Number) {
			writer.write(val.toString());
		}
	}

	private String toStr(String val) {
		return "\""+val+"\"";//TODO:手抜き
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {

		ScoreBean sb = new ScoreBean();

		String game = req.getParameter("game")+":"+req.getParameter("stage");
		sb.setGame(game);
		sb.setName(req.getParameter("name"));
		sb.setStage(req.getParameter("stage"));
		sb.setScore(Long.valueOf(req.getParameter("score")));
		sbs.put(null, sb);

		boolean asc = Boolean.valueOf(req.getParameter("asc"));
		int no = 0;
		Iterator<Entity> ite = sbs.iterateKeys("game", game, "score", asc);
		while (ite.hasNext()) {
			Entity ent = (Entity) ite.next();
			if (no++ >= 20) {
				sbs.remove(ent.getKey());
			}
		}
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
