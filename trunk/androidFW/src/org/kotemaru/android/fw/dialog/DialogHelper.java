package org.kotemaru.android.fw.dialog;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;

public class DialogHelper {
	public enum State {
		NOP, SHOW, HIDE
	};

	public interface OnDialogButtonListener {
		public void onProgressCancel(DialogInterface dialog, DialogModel model);
		public void onAlertOk(DialogInterface dialog, DialogModel model);
		public void onConfirmOk(DialogInterface dialog, DialogModel model);
		public void onConfirmCancel(DialogInterface dialog, DialogModel model);
	}

	public static class OnDialogButtonListenerBase implements OnDialogButtonListener {
		// @formatter:off
		public void onProgressCancel(DialogInterface dialog, DialogModel model){}
		public void onAlertOk(DialogInterface dialog, DialogModel model){}
		public void onConfirmOk(DialogInterface dialog, DialogModel model){}
		public void onConfirmCancel(DialogInterface dialog, DialogModel model){}
		// @formatter:on
	}

	private Activity mActivity;

	private ProgressDialog mProgress;
	private AlertDialog mAlertDialog;
	private AlertDialog mConfirmDialog;
	private Dialog mCustomDialog;

	public DialogHelper(Activity activity) {
		mActivity = activity;
	}
	public boolean doDialog(DialogModel model, OnDialogButtonListener listener) {
		model.readLock();
		try {
			switch (model.getMode()) {
			case ALERT:
				showAlertDialog(model, listener);
				return true;
			case CONFIRM:
				showConfirmDialog(model, listener);
				return true;
			case PROGRESS:
				showProgressDialog(model, listener);
				return true;
			case CUSTOM:
				showCustomDialog(model, listener);
				return true;
			default:
				clear();
			}
			return false;
		} finally {
			model.readUnlock();
		}
	}

	public void clear() {
		mProgress = (ProgressDialog) clear(mProgress);
		mAlertDialog = clear(mAlertDialog);
		mConfirmDialog = clear(mConfirmDialog);
		mCustomDialog = clear(mCustomDialog);
	}
	private AlertDialog clear(Dialog dialog) {
		if (dialog != null) dialog.dismiss();
		return null;
	}

	public void showProgressDialog(final DialogModel model, final OnDialogButtonListener listener) {
		mProgress = new ProgressDialog(mActivity);
		mProgress.setMessage(model.getTitle());
		mProgress.setMessage(model.getMessage());
		if (model.isCancelable()) {
			mProgress.setButton(DialogInterface.BUTTON_NEGATIVE, model.getCancelButtonLabel(),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if (listener != null) listener.onProgressCancel(dialog, model);
							dialog.cancel();
							model.clear();
						}
					});
		}
		mProgress.setCanceledOnTouchOutside(false);
		mProgress.setCancelable(model.isCancelable());
		mProgress.show();
	}
	public void showAlertDialog(final DialogModel model, final OnDialogButtonListener listener) {
		AlertDialog.Builder builer = new AlertDialog.Builder(mActivity)
				.setTitle(model.getTitle()).setMessage(model.getMessage());
		builer.setCancelable(false);
		builer.setPositiveButton(model.getOkButtonLabel(), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (listener != null) listener.onAlertOk(dialog, model);
				dialog.dismiss();
			}
		});
		mAlertDialog = builer.show();
	}
	public void showConfirmDialog(final DialogModel model, final OnDialogButtonListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
				.setTitle(model.getTitle()).setMessage(model.getMessage());
		builder.setCancelable(false);
		builder.setPositiveButton(model.getOkButtonLabel(), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (listener != null) listener.onConfirmOk(dialog, model);
				dialog.dismiss();
				model.clear();
			}
		});
		builder.setNegativeButton(model.getCancelButtonLabel(), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (listener != null) listener.onConfirmCancel(dialog, model);
				dialog.dismiss();
				model.clear();
			}
		});
		mConfirmDialog = builder.show();
	}
	private void showCustomDialog(DialogModel model, OnDialogButtonListener listener) {
		mCustomDialog = model.getDialogBuilder().build();
		mCustomDialog.show();
	}

}
