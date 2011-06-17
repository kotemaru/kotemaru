package org.kotemaru.gae.storedbean.tests;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import com.google.appengine.api.datastore.*;
import com.google.apphosting.api.*;

public class StoredBeanTestServlet extends HttpServlet  {
	
	
	public void doGet(HttpServletRequest request,HttpServletResponse response)
			throws IOException, ServletException {
		StoredBeanTest test = new StoredBeanTest();

		PrintWriter out = new PrintWriter(response.getOutputStream());
		try {
			test.testNull();
			test.testNested();
			test.testSimple();
		} catch (Exception e) {
			e.printStackTrace(out);
		}
	}
}