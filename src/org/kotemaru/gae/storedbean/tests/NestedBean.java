package org.kotemaru.gae.storedbean.tests;

import org.kotemaru.gae.storedbean.StoredBean;

import java.util.Arrays;

public class NestedBean implements StoredBean {

	SimpleBean bean1;
	java.util.List<SimpleBean> beans;


	public SimpleBean getBean1() {
		return bean1;
	}
	public void setBean1(SimpleBean bean1) {
		this.bean1 = bean1;
	}
	public java.util.List<SimpleBean> getBeans() {
		return beans;
	}
	public void setBeans(java.util.List<SimpleBean> beans) {
		this.beans = beans;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bean1 == null) ? 0 : bean1.hashCode());
		result = prime * result + ((beans == null) ? 0 : beans.hashCode());
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
		NestedBean other = (NestedBean) obj;
		if (bean1 == null) {
			if (other.bean1 != null)
				return false;
		} else if (!bean1.equals(other.bean1))
			return false;
		if (beans == null) {
			if (other.beans != null)
				return false;
		} else if (!beans.equals(other.beans))
			return false;
		return true;
	}

	public String toString() {
		return "NestedBean["+bean1+","+beans+"]";
	}
	
}
