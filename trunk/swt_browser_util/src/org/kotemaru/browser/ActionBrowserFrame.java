/*******************************************************************************
 * Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 * Copyright (c) 2011- kotemaru@kotemaru.org
 ******************************************************************************/
package org.kotemaru.browser;
import org.kotemaru.util.IOUtil;
import java.net.URI;
import java.net.URLDecoder;

import org.apache.commons.beanutils.BeanUtils;

/**
BrowserFrameを拡張してローカルでStruts風Actionクラスを
起動できるようにしたもの。
<li>http://action/ で始まるURLをフックしてローカルで実行する。

@author kotemaru@kotemaru.org
*/
public class ActionBrowserFrame extends BrowserFrame {
	static final String ACTION_PREFIX = "http://action/";

	/** Actionクラス検索の起点パッケージ名。*/
	private String basePkg;


	/** 
	 * コンストラクタ。
	 * <li>VelocityへのパラメータはActionそのもの。
	 * @param title Windowのタイトル
	 * @param pkg   Actionクラス検索の起点パッケージ名。
	 */
	public ActionBrowserFrame(String title, String pkg) {
		super(title);
		basePkg = pkg;
		setHome(ACTION_PREFIX+"Main");
	}
	
	/** 
	 * アクセスを許可するURLチェック。
	 * <li>about: のみ許可。
	 * @param url  チェックURL。
	 * @return true=許可、fale=不許可。
	 */
	public boolean isAllowUrl(String url) {
		return url.startsWith("about:");
	}

	/** 
	 * ActionクラスのURLチェック。
	 * <li>http://action/ で始まるURLはAction。
	 * @param url  チェックURL。
	 * @return true=Action。
	 */
	public boolean isApplicationUrl(String url) {
		return url.startsWith(ACTION_PREFIX);
	}


	/** 
	 * ActionクラスのURLを処理。
	 * <li>「.」を含む場合、パッケージ内リソースをそのまま返す。
	 * <li>それ以外はAction処理。
	 * @param urlStr  URL。
	 */
	public void doApplicationUrl(String urlStr) throws Exception {
		URI uri = new URI(urlStr);
		String name = uri.getPath();
		if (name.indexOf('.') == -1) {
			doAction(uri);
		} else {
			String html = IOUtil.getResource(this.getClass(), name);
			browser.setText(html);
		}
	}

	/** 
	 * Action処理。
	 * <li>URLのパス部分に Action を加えてクラス名とする。
	 * <li>Actionクラスのインスタンスを生成する。
	 * <li>クエリパラメータをActionのsetterにコピーする。
	 * <li>Action の execute() を呼び出す。
	 * <li>戻り値が RedirectResult ならそのURLに移動する。
	 * <li>戻り値が RedirectResult 以外ならHTMLを生成して設定する。
	 * @param uri  Action URI。
	 */
	protected void doAction(URI uri) throws Exception {
		String name = uri.getPath();
		String cname = basePkg+name.replace('/', '.')+"Action";
		Class cls = Class.forName(cname);
		Action action = (Action) cls.newInstance();
		action.postBrowserFrame(this);
		copyQuery(uri, action);
		Result navi = action.execute();
		if (navi == null) return;
		if (navi instanceof RedirectResult) {
			browser.setUrl(navi.getResource());
		} else {
			browser.setText(navi.getHtml());
		}
	}
	
	/** 
	 * クエリパラメータのコピー。
	 * <li>BeanUtils を使ってコピー。
	 * <li>クエリはここでパーズする。
	 * <li>文字コードは UTF8 に固定。
	 * @param uri  Action URI。
	 * @param action Actionインスタンス。
	 */
	protected void copyQuery(URI uri, Action action)  throws Exception{
		String query = uri.getQuery();
		if (query == null) return;
		String[] params = query.split("&");
		for (int i=0; i<params.length; i++) {
			String[] kv = params[i].split("=");
			String key = kv[0];
			if (kv.length == 2) {
				String val = URLDecoder.decode(kv[1], "UTF-8");
				BeanUtils.setProperty(action, key, val);
			} else if (params[i].indexOf('=') >= 0) {
				BeanUtils.setProperty(action, key, "");
			}
		}
	}
}
