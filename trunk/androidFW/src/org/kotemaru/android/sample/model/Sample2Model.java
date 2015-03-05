package org.kotemaru.android.sample.model;

import java.util.ArrayList;

import org.kotemaru.android.fw.ModelLock;
import org.kotemaru.android.fw.dialog.DialogModel;

public class Sample2Model extends ModelLock {
	private DialogModel mDialogModel = new DialogModel();
	private ArrayList<Blog> mBlogList = new ArrayList<Blog>();

	public DialogModel getDialogModel() {
		return mDialogModel;
	}

	public ArrayList<Blog> getBlogList() {
		return mBlogList;
	}

	public void setBlogList(ArrayList<Blog> blogList) {
		mBlogList = blogList;
	}

	public static class Blog {
		public String title;
		public String date;
		public String thumbnail;
	}
}
