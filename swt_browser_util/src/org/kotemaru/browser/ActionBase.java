/*******************************************************************************
 * Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 * Copyright (c) 2011- kotemaru@kotemaru.org
 ******************************************************************************/
package org.kotemaru.browser;

/**
Action の基本実装。
@author kotemaru@kotemaru.org
*/
public abstract class ActionBase implements Action {

	/** このActionを実行するActionBrowserFrame。*/
	private ActionBrowserFrame browserFrame;
	
	public void postBrowserFrame(ActionBrowserFrame frame) {
		this.browserFrame = frame;
	}

	/** 
	 * このActionを実行するActionBrowserFrame を得る。
	 */
	public ActionBrowserFrame takeBrowserFrame() {
		return this.browserFrame;
	}
	
	/** 
	 * Velocity の定義ファイルを使うActionの戻り値を返す。
	 * <li>VelocityへのパラメータはActionそのもの。
	 * <li>リソースはActionクラスを起点とする。
	 * @param vm Velocity定義ファイル。パッケージ内リソース名。
	 */
	protected Result velocity(String vm) {
		return new VelocityResult(this, vm);
	}

	/** 
	 * リダイレクトするActionの戻り値を返す。
	 * @param url 転送先URL
	 */
	protected Result redirect(String url) {
		return new RedirectResult(url);
	}
	
}
