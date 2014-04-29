package org.kotemaru.android.nnalert;

import android.content.Context;
import android.content.Intent;

public class Transit {

	public static void activity(Context context, Class<?> clazz) {
		Intent intent = new Intent(context, clazz);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public static void dialog(Context context, int messageId, String detail) {
		dialog(context, messageId, detail, DialogActivity.MODE_ALERT, null);
	}
	public static void waiting(Context context, String detail, String taskId) {
		dialog(context, R.string.message_doing, detail, DialogActivity.MODE_WATING, taskId);
	}
	public static void finish(Context context, int messageId, String detail) {
		dialog(context, messageId, detail, DialogActivity.MODE_FINISH, null);
	}

	public static void dialog(Context context, int messageId, String detail, int mode, String taskId) {
		Intent intent = new Intent(context, DialogActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra(DialogActivity.MESSAGE_ID_KEY, messageId);
		intent.putExtra(DialogActivity.MESSAGE_DETAIL_KEY, detail);
		intent.putExtra(DialogActivity.MODE_KEY, mode);
		intent.putExtra(DialogActivity.TASK_ID_KEY, taskId);
		context.startActivity(intent);
	}
	public static void preference(Context context) {
		Intent intent = new Intent(context, PrefActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}

	public static void exit(Context context) {
		Intent intent = new Intent(context, RegisterActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(RegisterActivity.EXIT_KEY, true);
		context.startActivity(intent);
	}

}
