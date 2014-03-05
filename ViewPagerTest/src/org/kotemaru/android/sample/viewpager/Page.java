package org.kotemaru.android.sample.viewpager;
import android.view.View;

public class Page {
	private View view;
	private boolean isValid = true;

	public Page(View view) {
		this.view = view;
	}
	public View getView() {
		return view;
	}
	public boolean isValid() {
		return isValid;
	}
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
}
