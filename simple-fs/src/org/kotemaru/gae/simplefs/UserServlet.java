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
public class UserServlet extends HttpServlet {
	private StoredBeanService sbs = new StoredBeanService("SimpleFS");
	private StoredBeanService usbs = new StoredBeanService("User");
	private StoredBeanService ursbs = new StoredBeanService("UserRv");

	public void doGet(HttpServletRequest req, HttpServletResponse res)
					throws IOException, ServletException {
		UserService userService = UserServiceFactory.getUserService();
		String retUrl = req.getParameter("url");
		if (retUrl == null) retUrl = "/";
		
		String pathInfo = req.getPathInfo();
		if (pathInfo.equals("/current")) {
			User user = userService.getCurrentUser();
			if (user == null) {
				res.setStatus(401);
				res.setContentType("text/plain; charset=utf-8");
				res.getWriter().write("Not login");
				return;
			}

			UserBean ub = getByEmail(user.getEmail());

			res.setContentType("application/json; charset=utf-8");
			res.setHeader("Cache-control","no-cache");
			res.getWriter().write("{\"email\":\""+user.getEmail()+"\"");
			if (ub != null) {
				res.getWriter().write(",\"nickName\":\""+ub.getNickName()+"\"");
				res.getWriter().write(",\"isRegister\":true");
			} else {
				String nn = user.getNickname();
				if (nn == null) nn = user.getEmail();
				res.getWriter().write(",\"nickName\":\""+nn+"\"");
				res.getWriter().write(",\"isRegister\":false");
			}
			res.getWriter().write("}");

		} else if (pathInfo.equals("/login")) {
			String url = userService.createLoginURL(retUrl);
			res.sendRedirect(url);
		} else if (pathInfo.equals("/logout")) {
			String url = userService.createLogoutURL(retUrl);
			res.sendRedirect(url);
		} else if (pathInfo.equals("/register")) {
			User user = userService.getCurrentUser();
			if (user == null) {
				res.setStatus(401);
				res.setContentType("text/plain; charset=utf-8");
				res.getWriter().write("Not login");
				return;
			}
		
			String nickName = req.getParameter("nickName");
			UserBean ub = getByNeckName(nickName);
			if (ub != null) {
				res.setStatus(403);
				res.setContentType("text/plain; charset=utf-8");
				res.getWriter().write("ニックネーム("+nickName+")は既に使われています。");
				return;
			}
			
			ub = new UserBean();
			ub.setEmail(user.getEmail());
			ub.setNickName(nickName);
			usbs.put(user.getEmail(), ub);
			ursbs.put(nickName, ub);

			FileBean fb = new FileBean();
			fb.setParentName("");
			fb.setLastName(nickName);
			fb.setDirectory(true);
			fb.setLastModified(System.currentTimeMillis());
			fb.setOwner(user.getEmail());
			fb.setNickName(nickName);
			sbs.put("/"+nickName, fb);

			
			res.sendRedirect(retUrl);
		}
	}

	private UserBean getByNeckName(String nickName) {
		try {
			return (UserBean) ursbs.get(nickName);
		} catch (EntityNotFoundException e) {
			return null;
		}
	}
	private UserBean getByEmail(String email) {
		try {
			return (UserBean) usbs.get(email);
		} catch (EntityNotFoundException e) {
			return null;
		}
	}
	
}
