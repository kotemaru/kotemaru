package org.kotemaru.blog.redirector;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class BlogRedirectorServlet extends HttpServlet {

	private String toUrl;

	@Override
	public void init(ServletConfig config) throws ServletException {
		toUrl = config.getInitParameter("toUrl");
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String url = toUrl + req.getRequestURI();
		if (req.getQueryString() != null) {
			url = url + "?" + req.getQueryString();
		}
		resp.sendRedirect(url);
	}
}
