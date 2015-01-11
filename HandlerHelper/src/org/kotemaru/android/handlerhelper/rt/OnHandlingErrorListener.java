package org.kotemaru.android.handlerhelper.rt;


public interface OnHandlingErrorListener {
	public void onHandlingError(Throwable t, String methodName, Object... arguments);
}
