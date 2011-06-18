package org.kotemaru.gae.storedbean.tests;
import org.kotemaru.gae.storedbean.StoredBean;

public class TestBean implements StoredBean {
	private int item01;
	private String item02;

	public int getItem01() {return item01;}
	public void setItem01(int item01) {this.item01 = item01;}
	public String getItem02() {return item02;}
	public void setItem02(String item02) {this.item02 = item02;}
}
