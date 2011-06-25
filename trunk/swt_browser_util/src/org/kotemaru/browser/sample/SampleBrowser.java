/*******************************************************************************
 * Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 * Copyright (c) 2011- kotemaru@kotemaru.org
 ******************************************************************************/
package org.kotemaru.browser.sample;
import org.kotemaru.browser.*;

/**
サンプルの ActionBrowserFrame。
*/
public class SampleBrowser extends ActionBrowserFrame {

	public SampleBrowser() {
		super("Sample",
			SampleBrowser.class.getPackage().getName());
	}

	/**
	 * 外部サイトはyohooのみ許可。
	 */
	@Override
	public boolean isAllowUrl(String url) {
		return super.isAllowUrl(url)
			|| url.startsWith("http://yahoo.co.jp/");
	}
	
	public static void main(String[] args) {
		SampleBrowser frame = new SampleBrowser();
		frame.setHome("http://action/Main");
		frame.setUrl("http://action/Main"); // MainActionクラスが初期値
		frame.layout(800, 600);
		frame.run();
	}

}
