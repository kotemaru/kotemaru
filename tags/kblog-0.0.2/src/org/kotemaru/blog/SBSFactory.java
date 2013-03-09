package org.kotemaru.blog;

import org.kotemaru.gae.storedbean.StoredBean;
import org.kotemaru.gae.storedbean.StoredBeanService;

public class SBSFactory implements StoredBean {

	private static final long serialVersionUID = 1L;
	
	private static final StoredBeanService comment = new StoredBeanService("Comment");

	static {
		comment.getIndexSet().add("page");
		comment.getIndexSet().add("name");
		comment.getIndexSet().add("email");
		comment.getIndexSet().add("ipAddr");
		comment.getIndexSet().add("date");
	}

	public static StoredBeanService getComment() {
		return comment;
	}
	
}
