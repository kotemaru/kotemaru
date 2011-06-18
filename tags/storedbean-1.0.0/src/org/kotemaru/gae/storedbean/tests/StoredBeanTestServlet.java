package org.kotemaru.gae.storedbean.tests;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import org.kotemaru.gae.storedbean.StoredBean;
import org.kotemaru.gae.storedbean.StoredBeanService;

public class StoredBeanTestServlet extends HttpServlet  {
	
	
	public void doGet(HttpServletRequest request,HttpServletResponse response)
			throws IOException, ServletException {

		PrintWriter out = new PrintWriter(response.getOutputStream());
		out.println("<h2>StoredBean test</h2>");
		try {
			doSample(out);
			/*
			StoredBeanTest test = new StoredBeanTest();
			test.testNull();
			test.testNested();
			test.testSimple();
*/
		} catch (Exception e) {
			e.printStackTrace(out);
		}
		out.flush();
	}
	
	private void doSample(PrintWriter out) throws Exception {
		// Bean に値を設定。
		TestBean bean = new TestBean();
		bean.setItem01(123);
		bean.setItem02("abc");

		StoredBeanService sbs = new StoredBeanService("StoredBean");
		String key = "key-name";
		// 保存
		sbs.put(key, bean);
		// 復元
		TestBean restoreBean = (TestBean) sbs.get(key);
		out.println("<div>"+restoreBean.getItem01()+","+restoreBean.getItem02()+"</div>");

	}
}