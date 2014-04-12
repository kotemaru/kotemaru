/*******************************************************************************
 * Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 * Copyright (c) 2014- kotemaru@kotemaru.org
 ******************************************************************************/
package org.kotemaru.android.irrc;

import java.io.IOException;

import org.kotemaru.android.irrc.UsbReceiver.UsbReceiverActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * 
 * @author kotemaru@kotemaru.org
 */
public class RemoconActivity extends FragmentActivity implements UsbReceiverActivity,RemoconConst {
	private static final String TAG = "RemoconActivity";


	/** Activityの継続性を維持するためのID */
	private String activityId = null;

	private IrrcUsbDriver irrcUsbDriver;
	private IrDataDao irDataDao = new IrDataDao(this);
	private Options options = new Options();

	private UsbReceiver usbReceiver;
	private ViewPager viewPager;
	private WebViewPagerAdapter viewPagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remocon_activity);

		// Activityの継続性を維持するためのID取得。@see #onSaveInstanceState()
		String aid = (savedInstanceState != null) ? savedInstanceState.getString(ACTIVITY_ID) : null;
		activityId = (aid != null) ? aid : ("@" + this.hashCode());

		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPagerAdapter = new WebViewPagerAdapter(this, viewPager, RemoconResource.getRemoconList(this));
		viewPager.setAdapter(viewPagerAdapter);
		viewPager.setOnPageChangeListener(viewPagerAdapter.getOnPageChangeListener());

		irrcUsbDriver = ((RemoconApplication) getApplication()).getIrrcUsbDriver(this);
		usbReceiver = UsbReceiver.init(this, irrcUsbDriver);
	}

	public WebViewContainer getCurrentWebViewFragment() {
		return viewPagerAdapter.getWebViewContainer(viewPager.getCurrentItem());
	}

	@Override
	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	protected void onResume() {
		super.onResume();
		if (!irrcUsbDriver.hasDevice()) {
			if (!irrcUsbDriver.findDevice()) {
				Log.i(TAG,"Not found USB device.");
				errorDialog("Not found USB device.");
			}
		}
		//viewPagerAdapter.onResume();
	}

	/**
	 * Activityの再構築に備えて自身にIDを保存して置く。
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(ACTIVITY_ID, activityId);
	}

	/**
	 * 拡張XMLHttpRequestのファクトリにWebViewが無効になっていることを通知する。
	 */
	@Override
	protected void onDestroy() {
		usbReceiver.destroy();
		viewPagerAdapter.onDestroy();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.remocon_option, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int menuId = item.getItemId();
		if (menuId == R.id.menu_setting_mode) {
			viewPagerAdapter.getWebViewContainer(viewPager.getCurrentItem()).callbackJs("onChangeSettingMode()");
		} else if (menuId == R.id.menu_find_device) {
			if (irrcUsbDriver.findDevice()) {
				alertDalog("OK!", "Found USB device.");
			} else {
				errorDialog("Not found USB device.");
			}
		} else if (menuId == R.id.menu_backup) {
			try { // TODO: ファイル選択
				this.irDataDao.backup(BACKUP_DIR, BACKUP_FILE);
			} catch (IOException e) {
				errorDialog(e.getMessage());
			}
		} else if (menuId == R.id.menu_restore) {
			try { // TODO: ファイル選択
				this.irDataDao.restore(BACKUP_DIR, BACKUP_FILE);
			} catch (IOException e) {
				errorDialog(e.getMessage());
			}
		}
		return super.onOptionsItemSelected(item);
	}
 
	@Override
	public void errorDialog(String message) {
		alertDalog("Error!",message);
	}
	public void alertDalog(String title, String message) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.show();
	}

	public Options getOptions() {
		return options;
	}

	public IrDataDao getIrDataDao() {
		return irDataDao;
	}
}
