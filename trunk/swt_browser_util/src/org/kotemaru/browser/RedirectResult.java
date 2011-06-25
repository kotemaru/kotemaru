/*******************************************************************************
 * Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 * Copyright (c) 2011- kotemaru@kotemaru.org
 ******************************************************************************/
package org.kotemaru.browser;

/**
リダイレクト用のActionの戻り値。
@author kotemaru@kotemaru.org
*/

public class RedirectResult extends Result {
	
	public RedirectResult(String url) {
		super(url);
	}

	public String getUrl() {
		return getResource();
	}
}
