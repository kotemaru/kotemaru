package org.kotemaru.android.asyncrotate;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// 3秒後にメッセージを書き換えるタスクを起動
		TextView m = (TextView) this.findViewById(R.id.message);
		new SlowAsyncTask(this).execute(m.getText()+"<3sec>");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
