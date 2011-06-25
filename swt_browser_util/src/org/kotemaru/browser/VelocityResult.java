/*******************************************************************************
 * Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 * Copyright (c) 2011- kotemaru@kotemaru.org
 ******************************************************************************/
package org.kotemaru.browser;
import org.kotemaru.util.IOUtil;
import java.io.*;
import org.apache.velocity.context.Context;
import org.apache.velocity.*;
import org.apache.velocity.app.*;
import org.apache.commons.beanutils.BeanUtils;

/**
Verocity用のActionの戻り値。
@author kotemaru@kotemaru.org
*/
public class VelocityResult extends Result {
	private Action action;
	
	public VelocityResult(Action action, String rs) {
		super(rs);
		this.action = action;
	}

	@Override
	public String getHtml() throws Exception {
		String name = getResource();
		String vm = IOUtil.getResource(action.getClass(), name);
		Context context = new VelocityContext(BeanUtils.describe(action));
		StringWriter sw = new StringWriter();
		Velocity.evaluate(context, sw, name, vm);
		return sw.toString();
	}
}
