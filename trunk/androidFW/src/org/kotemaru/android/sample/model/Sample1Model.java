package org.kotemaru.android.sample.model;

import org.kotemaru.android.fw.ModelLock;
import org.kotemaru.android.fw.dialog.DialogModel;

public class Sample1Model extends ModelLock {
	private DialogModel mDialogModel = new DialogModel();

	public DialogModel getDialogModel() {
		return mDialogModel;
	}

	public void setDialogModel(DialogModel dialogModel) {
		mDialogModel = dialogModel;
	}

}
