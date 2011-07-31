package org.kotemaru.jsrpc;

import java.util.List;
import java.util.Map;

import org.kotemaru.jsrpc.annotation.JsRpc;

@JsRpc()

public class Test {
	public static Test foo(int i, String str, boolean b, Map map, List list){
		System.out.println("-->"
				+i
				+","+str
				+","+b
				+","+map
				+","+list
		);

		Test res = new Test();
		res.item01 = i;
		res.item02 = str;
		res.item03 = b;
		res.item04 = map;
		res.item05 = list;
		res.recur = new Test();

		return res;
	}
	
	private int item01;
	private String item02;
	private boolean item03;
	private Map item04;
	private List item05;
	private Test recur;
	public int getItem01() {
		return item01;
	}
	public void setItem01(int item01) {
		this.item01 = item01;
	}
	public String getItem02() {
		return item02;
	}
	public void setItem02(String item02) {
		this.item02 = item02;
	}
	public boolean isItem03() {
		return item03;
	}
	public void setItem03(boolean item03) {
		this.item03 = item03;
	}
	public Map getItem04() {
		return item04;
	}
	public void setItem04(Map item04) {
		this.item04 = item04;
	}
	public List getItem05() {
		return item05;
	}
	public void setItem05(List item05) {
		this.item05 = item05;
	}
	public Test getRecur() {
		return recur;
	}
	public void setRecur(Test recur) {
		this.recur = recur;
	}

	
	
	
}
