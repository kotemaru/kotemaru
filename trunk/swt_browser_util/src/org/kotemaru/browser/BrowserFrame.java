/*******************************************************************************
 * Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 * Copyright (c) 2011- kotemaru@kotemaru.org
 ******************************************************************************/
package org.kotemaru.browser;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.graphics.Image;
import org.kotemaru.util.IOUtil;


import java.io.*;

/**
SWTのBrowserを使って必要最小限のブラウザを実装したもの。
<li>戻る、進む、ホーム、リロードのボタンを持つ。
<li>URL表示、入力テキストエリアを持つ。
<li>機能拡張の為のいくつかのabstractメソッドを持つ。

@author kotemaru@kotemaru.org
*/
public abstract class BrowserFrame {
	static final Logger LOG = Logger.getLogger(BrowserFrame.class);

	private Display display;
	private Shell shell;
	protected Browser browser;

	private Button prevBtn;
	private Button nextBtn;
	private Button homeBtn;
	private Button reloadBtn;
	private Text urlText;
	private String homeUrl = "";
	private String applicationUrl = null;

	public BrowserFrame(String title) {
		display = new Display();
		shell = new Shell(display);
		shell.setText(title);

		prevBtn = new Button(shell, SWT.PUSH);
		nextBtn = new Button(shell, SWT.PUSH);
		homeBtn = new Button(shell, SWT.PUSH);
		reloadBtn = new Button(shell, SWT.PUSH);
		urlText = new Text(shell, SWT.SINGLE | SWT.BORDER);
		browser = new Browser(shell, SWT.NONE);

		setupImages();
		setupListener();
	}


	/**
	 * onload イベント処理メソッド。
	 * <li>URL表示を更新する。
	 */
	public /*abstract*/ void onload(String url) {
		urlText.setText(url);
	}


	/**
	 * アクセス許可チェック。
	 * <li>セキュリティ上のアクセス制限をかけたい場合にfalseを返す。
	 * @param url 遷移しようとしているURL。
	 * @return true=アクセス出来るURL
	 */
	public abstract boolean isAllowUrl(String url);


	/**
	 * カスタムアプリ用URLチェック。
	 * <li>trueの場合、URLに遷移させず代りに doApplicationUrl() に呼び出す。
	 * @param url 遷移しようとしているURL。
	 * @return true=カスタムアプリ用URL
	 */
	public abstract boolean isApplicationUrl(String url);

	/**
	 * カスタムアプリ用URL処理。
	 * <li>isApplicationUrl()でtrueを返す場合のみ呼ばれる。
	 * @param url 遷移しようとしているURL。
	 */
	public abstract void doApplicationUrl(String url) throws Exception ;

	
	/**
	 * ブラウザを配置する。
	 * <li>
	 * @param w ブラウザの幅
	 * @param h ブラウザの高さ
	 */
	public void layout(int w, int h) {
		shell.setLayout(new GridLayout(6, false));
		GridData gd;

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		urlText.setLayoutData(gd);

		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 6;
		browser.setLayoutData(gd);
	
		shell.setSize(w, h);
	}

	/**
	 * ブラウザを実行する。
	 * <li>終了するまで戻らない。
	 */
	public void run() {
		open();
		try {
			dispatch();
		} finally {
			dispose();
		}
	}


	/**
	 * 指定のURLに遷移する。
	 * @param url 遷移しようとしているURL。
	 */
	public void setUrl(String url) {
		browser.setUrl(url);
	}
	/**
	 * ホームボタンのURLを設定する。
	 * @param url ホームボタンURL。
	 */
	public void setHome(String url) {
		this.homeUrl = url;
	}
	/**
	 * ブラウザでJavaScriptを実行する。
	 * @param script javascriptコード
	 */
	public void execute(String script) {
		browser.execute(script);
	}

	public void open() {
		shell.open();
	}
	
	public void dispatch() {
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
			doAppUrl();
		}
	}

	public void dispose() {
		shell.dispose();
		display.dispose();
	}


//---------------------------------------------------------------------
// private
	private void setupImages() {
		prevBtn.setImage(newImage("img/prev.png"));
		nextBtn.setImage(newImage("img/next.png"));
		homeBtn.setImage(newImage("img/house.png"));
		reloadBtn.setImage(newImage("img/reload.png"));
	}

	private Image newImage(String name) {
		try {
			InputStream in = BrowserFrame.class.getResourceAsStream(name);
			try {
				return new Image(display, in);
			} finally {
				in.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void setupListener() {
		prevBtn.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				if (browser.isBackEnabled()){
					browser.back();
				}
			}
		});
		
		nextBtn.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				if (browser.isForwardEnabled()){
					browser.forward();
				}
			}
		});
		
		homeBtn.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				browser.setUrl(homeUrl);
			}
		});
		
		reloadBtn.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				browser.refresh();
			}
		});

		urlText.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 13){
				   browser.setUrl(urlText.getText());
				}
			}
		});

		browser.addLocationListener(new LocationListener(){
            public void changing(LocationEvent event) {
            	LOG.info("go:"+event.location);
            	boolean isAppUrl = isAppUrl(event.location);
            	boolean isAllowUrl = isAllowUrl(event.location);
            	event.doit = !isAppUrl && isAllowUrl;
            	LOG.debug("doit:"+event.doit+":"+event.location);
            	if (isAllowUrl == false && isAppUrl == false) {
            		browser.setText("<h2>Access denied</h2>"
            				+"Location "+event.location
            				);
            	}
            }
			public void changed(LocationEvent event) {
				LOG.info("on:"+event.location);
            	onload(event.location);
			}
		});
	}

	private final boolean isAppUrl(String url) {
		boolean b = isApplicationUrl(url);
		if (b) applicationUrl = url;
		return b;
	}
	private final void doAppUrl() {
		if (applicationUrl == null) return;
		try {
			doApplicationUrl(applicationUrl);
		} catch (Throwable t) {
			t.printStackTrace();
			browser.setText(t.toString());
		}
		applicationUrl = null;
	}
	

}
