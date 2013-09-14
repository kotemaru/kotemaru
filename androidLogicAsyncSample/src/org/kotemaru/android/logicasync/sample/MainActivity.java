package org.kotemaru.android.logicasync.sample;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements UIAction {
	
	private MyLogic logic = new MyLogic(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final EditText textUrl = (EditText)findViewById(R.id.text_url);
		Button btnGo = (Button)findViewById(R.id.btn_go);
		btnGo.setOnClickListener(new OnClickListener() {
			@Override	public void onClick(View btn) {
				String url = textUrl.getText().toString();
				logic.async.doGetHtml(url);
			}
		});
	}

	@Override
	public void updateView(String html) {
		TextView  textHtml = (TextView)findViewById(R.id.text_html);
		textHtml.setText(html);
	}

	@Override
	public void errorDialog(String message) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Error!");
		dialog.setMessage(message);
		dialog.show();
    }

}
