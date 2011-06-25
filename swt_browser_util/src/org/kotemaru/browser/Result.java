/*******************************************************************************
 * Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 * Copyright (c) 2011- kotemaru@kotemaru.org
 ******************************************************************************/
package org.kotemaru.browser;

/**
Actionの戻り値。
@author kotemaru@kotemaru.org
*/
public abstract class Result {
	/** 汎用のリソース。*/
	protected String resource;
	
	public Result(String rs) {
		this.resource = rs;
	}

	public String getResource() {
		return resource;
	}

	/**
	 * Actionの結果となるHTMLを返す。
	 *
	 * @return HTMLを返さない場合はnull。
	 */
	/*abstract*/ public String getHtml() throws Exception {
		return null;
	}
}
