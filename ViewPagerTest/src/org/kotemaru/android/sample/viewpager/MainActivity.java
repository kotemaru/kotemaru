package org.kotemaru.android.sample.viewpager;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ViewPager viewPager = new ViewPager(this);
		setContentView(viewPager);
		
		View[] views = new View[] {
			createTextView("AAAAAAAAAAAAAAAAA", Color.CYAN),
			createTextView("BBBBBBBBBBBBBBBBB", 0xffff8888),
			createTextView("CCCCCCCCCCCCCCCCC", Color.YELLOW),
		};
		LoopPagerAdapter pagerAdapter = new LoopPagerAdapter(viewPager, views);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setCurrentItem(1);
	}

	private TextView createTextView(String text, int bgColor) {
		TextView textView = new TextView(this);
		textView.setText(text);
		textView.setTextSize(200);
		textView.setBackgroundColor(bgColor);
		return textView;
	}
}
