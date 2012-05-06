package org.kotemaru.gae.bcoro;
import java.util.Date;

import org.kotemaru.gae.storedbean.StoredBean;
import org.kotemaru.gae.storedbean.StoredBeanService;


public class SBSFactory implements StoredBean {
	private static final StoredBeanService simplefs = new StoredBeanService("bc_SimpleFS");
	private static final StoredBeanService user = new StoredBeanService("bc_User");
	private static final StoredBeanService userrv = new StoredBeanService("bc_UserRv");
	private static final StoredBeanService score = new StoredBeanService("bc_Score");

	static {
		simplefs.getIndexSet().add("parentName");
		simplefs.getIndexSet().add("lastModified");
		simplefs.getIndexSet().add("directory");
		simplefs.getIndexSet().add("length");

		score.getIndexSet().add("game");
		score.getIndexSet().add("score");
		
		userrv.getIndexSet().add("email");
	}

	public static StoredBeanService getSimplefs() {
		return simplefs;
	}

	public static StoredBeanService getUser() {
		return user;
	}

	public static StoredBeanService getUserrv() {
		return userrv;
	}

	public static StoredBeanService getScore() {
		return score;
	}


	
	
}
