package org.kotemaru.blog.builder;

import java.util.ArrayList;

public class Category extends ArrayList<Blog> {
	private static final long serialVersionUID = 1L;
	
	private String tag;
	private boolean update = false;
	
	public Category(String tag) {
		setTag(tag);
	}
	

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}


	public String getTag() {
		return tag;
	}


	public void setTag(String tag) {
		this.tag = tag;
	}
	
	
}
