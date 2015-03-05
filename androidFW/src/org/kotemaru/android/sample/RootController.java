package org.kotemaru.android.sample;

import org.kotemaru.android.fw.FwControllerBase;
import org.kotemaru.android.sample.controller.Sample1Controller;
import org.kotemaru.android.sample.controller.Sample2Controller;
import org.kotemaru.android.sample.controller.Sample3Controller;
import org.kotemaru.android.sample.controller.Sample4Controller;

public class RootController extends FwControllerBase<MyApplication> {
	private final Sample1Controller mSample1Controller;
	private final Sample2Controller mSample2Controller;
	private final Sample3Controller mSample3Controller;
	private final Sample4Controller mSample4Controller;

	public RootController(MyApplication app) {
		super(app);
		mSample1Controller = new Sample1Controller(app);
		mSample2Controller = new Sample2Controller(app);
		mSample3Controller = new Sample3Controller(app);
		mSample4Controller = new Sample4Controller(app);
	}

	public Sample1Controller getSample1Controller() {
		return mSample1Controller;
	}

	public Sample2Controller getSample2Controller() {
		return mSample2Controller;
	}
	public Sample3Controller getSample3Controller() {
		return mSample3Controller;
	}
	public Sample4Controller getSample4Controller() {
		return mSample4Controller;
	}

}
