package org.kotemaru.android.handlerhelper.rt;


public interface OnDelegateHandlerErrorListener {
	public void onDelegateHandlerError(Throwable t, String methodName, Object... arguments);
}
