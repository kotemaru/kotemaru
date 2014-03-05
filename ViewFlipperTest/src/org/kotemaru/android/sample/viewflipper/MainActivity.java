package org.kotemaru.android.sample.viewflipper;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
	private FlingViewFlipper viewFlipper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View[] views = new View[] {
				createTextView("AAAAAAAAAAAAAAAAA", Color.CYAN),
				createTextView("BBBBBBBBBBBBBBBBB", 0xffff8888),
				createTextView("CCCCCCCCCCCCCCCCC", Color.YELLOW),
				createTextView("DDDDDDDDDDDDDDDDD", Color.GREEN),
		};
		viewFlipper = new FlingViewFlipper(this, null);
		viewFlipper.setViews(views);
		setContentView(viewFlipper);
	}
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		viewFlipper.onTouchEvent(ev);
		return false;
	}

	private TextView createTextView(String text, int bgColor) {
		TextView textView = new TextView(this);
		textView.setText(text);
		textView.setTextSize(200);
		textView.setBackgroundColor(bgColor);
		return textView;
	}

}
