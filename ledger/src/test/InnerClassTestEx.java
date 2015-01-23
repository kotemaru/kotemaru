package test;

import test.InnerClassTestEx.BaseStatusEx;


public class InnerClassTestEx extends InnerClassTest<BaseStatusEx> {

	public static class BaseStatusEx extends test.InnerClassTest.BaseStatus {
		
	}
	
	public void test() {
		BaseStatusEx status = super.getStatus();
	}
}
