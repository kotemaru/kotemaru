package org.kotemaru.android.sample.model;

import org.kotemaru.android.fw.ModelLock;
import org.kotemaru.android.fw.dialog.DialogModel;

public class Sample3Model extends ModelLock {
	private DialogModel mDialogModel = new DialogModel();
	private int playPosition;

	public DialogModel getDialogModel() {
		return mDialogModel;
	}

	public int getPlayPosition() {
		return playPosition;
	}

	public void setPlayPosition(int playPosition) {
		this.playPosition = playPosition;
	}
}
