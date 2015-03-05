package org.kotemaru.android.sample.activity;

import java.io.File;

import org.kotemaru.android.fw.dialog.DialogHelper;
import org.kotemaru.android.fw.dialog.DialogHelper.OnDialogButtonListener;
import org.kotemaru.android.fw.dialog.DialogHelper.OnDialogButtonListenerBase;
import org.kotemaru.android.fw.util.image.DefaultImageLoaderProducer;
import org.kotemaru.android.fw.util.image.ImageLoader;
import org.kotemaru.android.fw.FwActivity;
import org.kotemaru.android.fw.R;
import org.kotemaru.android.sample.MyApplication;
import org.kotemaru.android.sample.controller.Sample2Controller;
import org.kotemaru.android.sample.model.Sample2Model;
import org.kotemaru.android.sample.model.Sample2Model.Blog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Sample2Activity extends Activity implements FwActivity {

	private DialogHelper mDialogHelper = new DialogHelper(this);
	private Sample2Model mModel;
	private Sample2Controller mController;

	private DefaultImageLoaderProducer mImageLoaderProducer;
	private ImageLoader mImageLoader;
	private BlogListAdapter mBlogListAdapter;

	private ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sample2_activity);
		MyApplication app = (MyApplication) getApplication();
		mModel = app.getModel().getSample2Model();
		mController = app.getController().getSample2Controller();

		this.findViewById(R.id.load_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mController.mHandler.loadListData("http://blog.kotemaru.org/atom.json");
			}
		});
		
		mImageLoaderProducer = new DefaultImageLoaderProducer(this)
			.setBaseUri("http://blog.kotemaru.org")
			.setCacheDir(new File(getCacheDir() + "/thumbnail"))
			.setImageSize(new Point(100,100));
		mImageLoader = new ImageLoader(mImageLoaderProducer);

		mBlogListAdapter = new BlogListAdapter();
		mListView = (ListView) findViewById(R.id.listView);
		mListView.setAdapter(mBlogListAdapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		update();
	}
	@Override
	public void onPause() {
		mDialogHelper.clear();
		mImageLoader.clear();
		super.onPause();
	}

	@Override
	public void update() {
		if (!mModel.tryReadLock()) return;
		try {
			mDialogHelper.doDialog(mModel.getDialogModel(), mOnDialogButtonListener);
			mBlogListAdapter.notifyDataSetChanged();
		} finally {
			mModel.readUnlock();
		}
	}

	private OnDialogButtonListener mOnDialogButtonListener = new OnDialogButtonListenerBase() {

	};

	public class BlogListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mModel.getBlogList() == null) return 0;
			return mModel.getBlogList().size() - 1;
		}

		@Override
		public Object getItem(int position) {
			if (mModel.getBlogList() == null) return null;
			return mModel.getBlogList().get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.list_item, parent, false);
			}
			Blog blog = mModel.getBlogList().get(position);
			ImageView thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
			TextView title = (TextView) view.findViewById(R.id.title);
			TextView desc = (TextView) view.findViewById(R.id.description);

			title.setText(blog.title);
			desc.setText(blog.date);
			mImageLoader.setImage(thumbnail, blog.thumbnail);
			return view;
		}

	}

}
