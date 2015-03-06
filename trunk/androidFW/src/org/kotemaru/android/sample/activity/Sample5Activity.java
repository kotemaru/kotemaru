package org.kotemaru.android.sample.activity;

import org.kotemaru.android.fw.FwActivity;
import org.kotemaru.android.fw.R;
import org.kotemaru.android.fw.widget.LoopHScrollView;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class Sample5Activity extends Activity implements FwActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sample5_activity);
		//MyApplication app = (MyApplication) getApplication();
		
		LoopHScrollView scrollView = (LoopHScrollView) findViewById(R.id.loopHScrollView);
		LayoutInflater infrater = LayoutInflater.from(this);
		ViewGroup child = (ViewGroup) infrater.inflate(R.layout.loop_scroll_item, null, false);
		scrollView.setChildViewGroup(child);
	}

	@Override
	public void update() {
	}

}
