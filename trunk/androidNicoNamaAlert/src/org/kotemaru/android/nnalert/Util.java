package org.kotemaru.android.nnalert;

import android.content.Context;
import android.content.Intent;

public class Util {

	public static void transition(Context context, Class<?> clazz) {
		Intent intent = new Intent(context, clazz);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public static void dialog(Context context, int messageId, String detail) {
		dialog(context, messageId, detail, DialogActivity.OK);
	}
	public static void waiting(Context context, String detail) {
		dialog(context, R.string.message_doing, detail, DialogActivity.NONE);
	}
	public static void finish(Context context, String detail) {
		dialog(context, R.string.message_finish, detail, DialogActivity.FINISH);
	}

	public static void dialog(Context context, int messageId, String detail, int buttonType) {
		Intent intent = new Intent(context, DialogActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(DialogActivity.MESSAGE_ID_KEY, messageId);
		intent.putExtra(DialogActivity.MESSAGE_DETAIL_KEY, detail);
		intent.putExtra(DialogActivity.BUTTONS_KEY, buttonType);
		context.startActivity(intent);
	}

	public static void exit(Context context) {
		Intent intent = new Intent(context, RegisterActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(RegisterActivity.EXIT_KEY, true);
		context.startActivity(intent);
	}

	
	
}
