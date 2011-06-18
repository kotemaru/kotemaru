package org.kotemaru.gae.storedbean.tests;
import org.kotemaru.gae.storedbean.StoredBean;

import java.util.Arrays;


public class SimpleBean implements StoredBean {
	private int item01;
	private String item02;
	private boolean item03;
	private byte[] item04;
	private java.util.List<String> item05;
	private java.util.Set<String> item06;

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
	public byte[] getItem04() {
		return item04;
	}
	public void setItem04(byte[] item04) {
		this.item04 = item04;
	}
	public java.util.List<String> getItem05() {
		return item05;
	}
	public void setItem05(java.util.List<String> item05) {
		this.item05 = item05;
	}
	public java.util.Set<String> getItem06() {
		return item06;
	}
	public void setItem06(java.util.Set<String> item06) {
		this.item06 = item06;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + item01;
		result = prime * result + ((item02 == null) ? 0 : item02.hashCode());
		result = prime * result + (item03 ? 1231 : 1237);
		result = prime * result + Arrays.hashCode(item04);
		result = prime * result + ((item05 == null) ? 0 : item05.hashCode());
		result = prime * result + ((item06 == null) ? 0 : item06.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleBean other = (SimpleBean) obj;
		if (item01 != other.item01)
			return false;
		if (item02 == null) {
			if (other.item02 != null)
				return false;
		} else if (!item02.equals(other.item02))
			return false;
		if (item03 != other.item03)
			return false;
		if (!Arrays.equals(item04, other.item04))
			return false;
		if (item05 == null) {
			if (other.item05 != null)
				return false;
		} else if (!item05.equals(other.item05))
			return false;
		if (item06 == null) {
			if (other.item06 != null)
				return false;
		} else if (!item06.equals(other.item06))
			return false;
		return true;
	}


	public String toString() {
		return "SimpleBean["
			+item01
			+","+item02
			+","+item03
			+","+item04
			+","+item05
			+","+item06
		+"]";
	}

}
