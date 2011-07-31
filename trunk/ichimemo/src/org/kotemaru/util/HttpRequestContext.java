/*******************************************************************************
 * Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 * Copyright (c) 2011- kotemaru@kotemaru.org
 ******************************************************************************/
package org.kotemaru.util;

import java.io.* ;
import java.net.* ;
import java.util.* ;
import org.kotemaru.util.* ;
import javax.servlet.*;
import javax.servlet.http.*;

public class HttpRequestContext  {
	private final Servlet servlet;
	private	final HttpServletRequest request;
	private final HttpServletResponse response;

	public HttpRequestContext(Servlet servlet, 
			HttpServletRequest request, HttpServletResponse response) 
	{
		this.servlet = servlet;
		this.request = request;
		this.response = response;
	}

	public Servlet getServlet(){
		return servlet;
	}
	public HttpServletRequest getRequest(){
		return request;
	}
	public HttpServletResponse getResponse(){
		return response;
	}

}
