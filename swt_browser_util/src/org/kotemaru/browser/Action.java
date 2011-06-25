/*******************************************************************************
 * Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 * Copyright (c) 2011- kotemaru@kotemaru.org
 ******************************************************************************/
package org.kotemaru.browser;


/**
ActionBrowserFrame が実行可能な Action のインターフェース。
@author kotemaru@kotemaru.org
*/
public interface Action {
	/**
	 * ActionBrowserFrameインスタンスの受け取り用。
	 * @param frame ActionBrowserFrameインスタンス
	 */
	public void postBrowserFrame(ActionBrowserFrame frame);

	/**
	 * Actionの実行。
	 * <li>URLパラメータはsetterメソッドに設定された状態で呼ばれる。
	 */
	public Result execute() throws Exception;
}
