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
		Intent intent = new Intent(context, DialogActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(DialogActivity.MESSAGE_ID_KEY, messageId);
		intent.putExtra(DialogActivity.MESSAGE_DETAIL_KEY, detail);
		context.startActivity(intent);
	}
	public static void waiting(Context context, String detail) {
		Intent intent = new Intent(context, DialogActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(DialogActivity.MESSAGE_ID_KEY, R.string.message_doing);
		intent.putExtra(DialogActivity.MESSAGE_DETAIL_KEY, detail);
		intent.putExtra(DialogActivity.BUTTONS_KEY, DialogActivity.NONE);
		context.startActivity(intent);
	}

}
