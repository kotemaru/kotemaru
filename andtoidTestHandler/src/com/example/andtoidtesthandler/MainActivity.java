package com.example.andtoidtesthandler;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity {

	Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.e("Test", "onCreate:" + Thread.currentThread().getName());

		final MyApplication app = (MyApplication) this.getApplication();

		Button button1 = (Button) this.findViewById(R.id.button1);
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				app.postUi(new Runnable() {
					@Override
					public void run() {
						Log.e("Test", "UI:" + Thread.currentThread().getName());
					}
				});
			}
		});
		Button button2 = (Button) this.findViewById(R.id.button2);
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				app.postAsync(new Runnable() {
					@Override
					public void run() {
						Log.e("Test", "async:" + Thread.currentThread().getName());
						app.postUi(new Runnable() {
							@Override
							public void run() {
								Log.e("Test", "UI2:" + Thread.currentThread().getName());
							}
						});
					}
				});
			}
		});

		ListView listView = (ListView) this.findViewById(R.id.listView1);
		listView.setAdapter(new TestBaseAdapter(this));

		String[] tabs = new String[] {
				"TOP", "無料連載", "コミック", "週刊少年ジャンプ"
		};
		((CustomTabView2) this.findViewById(R.id.tabView)).setTabs(tabs);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private static class TestBaseAdapter extends BaseAdapter {
		private Context context;
		private LayoutInflater mInflater;

		public TestBaseAdapter(Context context) {
			super();
			this.context = context;
			this.mInflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			return 10;
		}
		@Override
		public Object getItem(int position) {
			return position;
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.list_item, null);
			} else {
				// nop.
			}
			return convertView;
		}
	}
}
