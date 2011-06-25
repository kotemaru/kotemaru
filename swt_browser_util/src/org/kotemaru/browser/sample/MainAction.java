/*******************************************************************************
 * Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 * Copyright (c) 2011- kotemaru@kotemaru.org
 ******************************************************************************/
package org.kotemaru.browser.sample;
import org.kotemaru.browser.*;

/**
サンプルの Action。
<li>http://action/Main に遷移するとこのクラスの execute() が呼ばれる。
<li>setter にはURLパラメータの内容が設定される。
<li>getter は sample.vm のパラメータとして参照できる。
*/
public class MainAction extends ActionBase {
	
	private String item01 = "";
	private int item02;
	private String submit;
	
	public Result execute() throws Exception {
		if ("Yahoo".equals(submit)) {
			return redirect("http://yahoo.co.jp/");
		} else {
			return velocity("sample.vm");
		}
	}

//-------------------------------------------------
// setter/getter
	public String getSubmit() {
		return submit;
	}
	public void setSubmit(String submit) {
		this.submit = submit;
	}

	public String getItem01() {
		return item01;
	}
	public void setItem01(String val) {
		item01 = val;
	}
	
	public int getItem02() {
		return item02;
	}
	public void setItem02(int val) {
		item02 = val;
	}

}
