package org.kotemaru.ichimemo.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.*;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.kotemaru.ichimemo.jsrpc.IchiMemo;
import org.kotemaru.ichimemo.model.ImageModel;
import org.kotemaru.util.IOUtil;
import org.kotemaru.util.json.JSONParser;
import org.kotemaru.util.json.JSONSerializer;

@SuppressWarnings("serial")
public class ImageServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String id = req.getParameter("id");
		if (id == null) return;

		ImageModel model = IchiMemo.getImage(Long.parseLong(id));
		if (model == null) {
			resp.setStatus(404);
			return;
		}
		byte[] data = model.getData();
		resp.setContentType(model.getContentType());
		resp.setContentLength(data.length);
		resp.getOutputStream().write(data);
		resp.getOutputStream().flush();
	}
}	