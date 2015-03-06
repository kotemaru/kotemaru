package org.kotemaru.android.sample.activity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.kotemaru.android.fw.FwActivity;
import org.kotemaru.android.fw.R;
import org.kotemaru.android.fw.dialog.DialogHelper;
import org.kotemaru.android.fw.dialog.DialogHelper.OnDialogButtonListener;
import org.kotemaru.android.fw.dialog.DialogHelper.OnDialogButtonListenerBase;
import org.kotemaru.android.fw.widget.IndexerBar;
import org.kotemaru.android.fw.widget.IndexerBar.OnSelectSectionListener;
import org.kotemaru.android.fw.widget.IndexerListView;
import org.kotemaru.android.fw.widget.IndexerListView.IndexerItem;
import org.kotemaru.android.sample.MyApplication;
import org.kotemaru.android.sample.controller.Sample4Controller;
import org.kotemaru.android.sample.model.Sample4Model;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Sample4Activity extends Activity implements FwActivity {

	private DialogHelper mDialogHelper = new DialogHelper(this);
	private Sample4Model mModel;
	private Sample4Controller mController;
	private IndexerListView.IndexerAdapter<String> mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sample4_activity);
		MyApplication app = (MyApplication) getApplication();
		mModel = app.getModel().getSample4Model();
		mController = app.getController().getSample4Controller();

		IndexerListView.IndexerAdapter<String> adapter = new IndexerListView.IndexerAdapter<String>() {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView view;
				if (convertView == null) {
					view = new TextView(Sample4Activity.this);
				} else {
					view = (TextView) convertView;
				}
				IndexerItem<String> item = getIndexerItem(position);
				if (item.isHeader()) {
					view.setText(item.getSectionName());
					view.setBackgroundColor(Color.GRAY);
				} else {
					view.setText(item.getData());
					view.setBackgroundColor(Color.WHITE);
				}
				return view;
			}

			@Override
			public String getSectionName(String data) {
				return data.substring(0, 1);
			}
		};
		List<String> names = Arrays.asList(SAMPLE_NAMES);
		Collections.sort(names);
		adapter.setListData(names);

		IndexerListView listView = (IndexerListView) this.findViewById(R.id.indexerListView);
		IndexerBar indexerBar = (IndexerBar) this.findViewById(R.id.indexerBar);
		listView.setAdapter(adapter);
		indexerBar.setListViewInfo(listView, adapter);
		indexerBar.setItemLayoutId(R.layout.indexer_bar_item);
		indexerBar.setOnSelectSectionListener(new OnSelectSectionListener(){
			@Override
			public void onStartSelect(IndexerBar view) {
				Log.d("DEBUG","onStartSelect");
			}
			@Override
			public void onSelectSection(IndexerBar view, int sectionIndex) {
				Log.d("DEBUG","onSelectSection="+view.getSectionIndexer().getSections()[sectionIndex]);
			}
			@Override
			public void onFinishSelect(IndexerBar view) {
				Log.d("DEBUG","onFinishSelect");
			}
		});

		mAdapter = adapter;
	}
	
	public void onClickShort(View view) {
		List<String> names = Arrays.asList(SAMPLE_NAMES);
		Collections.sort(names);
		mAdapter.setListData(names.subList(0, names.size()/4) );
	}
	public void onClickMiddle(View view) {
		List<String> names = Arrays.asList(SAMPLE_NAMES);
		Collections.sort(names);
		mAdapter.setListData(names.subList(0, names.size()/2) );
	}
	public void onClickLong(View view) {
		List<String> names = Arrays.asList(SAMPLE_NAMES);
		Collections.sort(names);
		mAdapter.setListData(names);
	}
	

	@Override
	public void onResume() {
		super.onResume();
		update();
	}
	@Override
	public void onPause() {
		mDialogHelper.clear();
		super.onPause();
	}

	@Override
	public void update() {
		if (!mModel.tryReadLock()) return;
		try {
			mDialogHelper.doDialog(mModel.getDialogModel(), mOnDialogButtonListener);
		} finally {
			mModel.readUnlock();
		}
	}

	private OnDialogButtonListener mOnDialogButtonListener = new OnDialogButtonListenerBase() {

	};

	private static final String[] SAMPLE_NAMES = {
			"Lannie Boney",
			"Taneka Hubbard",
			"Rheba Elsen",
			"Dorinda Farrelly",
			"Adelina Bellon",
			"Whitney Pennington",
			"Jasper Wison",
			"Selena Vazguez",
			"Chasidy Schorr",
			"Maranda Hanrahan",
			"Camila Mcie",
			"Kenya Blakeley",
			"Carroll Mcgeorge",
			"Delsie Baran",
			"Layne Mango",
			"Elda Millender",
			"Kathi Remaley",
			"Mai Degraff",
			"Cecil Delaney",
			"Deandrea Lemmond",
			"Jessia Raney",
			"Frank Jemison",
			"Dominick Plasencia",
			"Pilar Mart",
			"Candis Luk",
			"Ivelisse Grissett",
			"Kristian Desai",
			"Willa Kueter",
			"Roscoe Kottke",
			"Angeline Wysocki",
			"Herschel Kincaid",
			"Melodi Mcfetridge",
			"Serafina Verret",
			"Marquetta Heenan",
			"Linette Maxwell",
			"Polly Jan",
			"Taren Millis",
			"Gudrun Usher",
			"Felicia Klink",
			"Jennine Ruppe",
			"Roxann Kellems",
			"Tamar Ledwell",
			"Chana Berra",
			"Soo Harvell",
			"Erlene Maguire",
			"Chrystal Hirano",
			"Megan Bourassa",
			"Gertrud Mcneilly",
			"Lahoma Batie",
			"Myrna Krzeminski",
	};

}
