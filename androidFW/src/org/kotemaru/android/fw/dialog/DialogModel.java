package org.kotemaru.android.fw.dialog;

import org.kotemaru.android.fw.ModelLock;


public class DialogModel extends ModelLock {
	public enum Mode {
		NONE, ALERT, CONFIRM, PROGRESS, CUSTOM
	}

	private Mode mMode = Mode.NONE;
	private CharSequence mTitle;
	private CharSequence mMessage;
	private int mProgress;
	private boolean mIsCancelable;
	private int mWhat;
	private DialogBuilder mDialogBuilder;

	private CharSequence mErrorTitle = "Error!";
	private CharSequence mOkButtonLabel = "OK";
	private CharSequence mCancelButtonLabel = "Cancel";

	public DialogModel clear() {
		this.writeLock();
		try {
			mMode = Mode.NONE;
			return this;
		} finally {
			this.writeUnlock();
		}
	}
	private DialogModel setAlert(Mode mode, CharSequence title, CharSequence messgae) {
		this.writeLock();
		try {
			mTitle = title;
			mMessage = messgae;
			mIsCancelable = false;
			mMode = mode;
			return this;
		} finally {
			this.writeUnlock();
		}
	}

	public DialogModel setAlert(CharSequence title, CharSequence messgae) {
		return setAlert(Mode.ALERT, title, messgae);
	}
	public DialogModel setError(Throwable t) {
		return setAlert(Mode.ALERT, "Error!", t.getMessage());
	}
	public DialogModel setConfirm(CharSequence title, CharSequence messgae) {
		return setAlert(Mode.CONFIRM, title, messgae);
	}
	public DialogModel setProgress(String messgae, boolean isCancelable, int progress) {
		this.writeLock();
		try {
			setAlert(Mode.PROGRESS, null, messgae);
			mProgress = progress;
			mIsCancelable = isCancelable;
			return this;
		} finally {
			this.writeUnlock();
		}
	}
	public DialogModel setProgress(String message, boolean isCancelable) {
		return setProgress(message, isCancelable, -1);
	}
	public DialogModel setCustom(DialogBuilder builder) {
		this.writeLock();
		try {
			setDialogBuilder(builder);
			mMode = Mode.CUSTOM;
			return this;
		} finally {
			this.writeUnlock();
		}
	}

	// ----------------------------------------------------------------------------
	// Setter/Getter
	public Mode getMode() {
		return mMode;
	}
	public void setMode(Mode mode) {
		mMode = mode;
	}
	public CharSequence getTitle() {
		return mTitle;
	}
	public void setTitle(CharSequence title) {
		mTitle = title;
	}
	public CharSequence getMessage() {
		return mMessage;
	}
	public void setMessage(CharSequence message) {
		mMessage = message;
	}
	public int getProgress() {
		return mProgress;
	}
	public void setProgress(int progress) {
		mProgress = progress;
	}
	public boolean isCancelable() {
		return mIsCancelable;
	}
	public void setCancelable(boolean isCancelable) {
		mIsCancelable = isCancelable;
	}
	public CharSequence getErrorTitle() {
		return mErrorTitle;
	}
	public void setErrorTitle(CharSequence errorTitle) {
		mErrorTitle = errorTitle;
	}
	public CharSequence getOkButtonLabel() {
		return mOkButtonLabel;
	}
	public void setOkButtonLabel(CharSequence okButtonLabel) {
		mOkButtonLabel = okButtonLabel;
	}
	public CharSequence getCancelButtonLabel() {
		return mCancelButtonLabel;
	}
	public void setCancelButtonLabel(CharSequence cancelButtonLabel) {
		mCancelButtonLabel = cancelButtonLabel;
	}
	public int getWhat() {
		return mWhat;
	}
	public void setWhat(int what) {
		mWhat = what;
	}
	public DialogBuilder getDialogBuilder() {
		return mDialogBuilder;
	}
	public void setDialogBuilder(DialogBuilder dialogBuilder) {
		mDialogBuilder = dialogBuilder;
	}

}
