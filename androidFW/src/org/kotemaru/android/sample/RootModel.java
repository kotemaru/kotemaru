package org.kotemaru.android.sample;

import org.kotemaru.android.sample.model.Sample1Model;
import org.kotemaru.android.sample.model.Sample2Model;
import org.kotemaru.android.sample.model.Sample3Model;
import org.kotemaru.android.sample.model.Sample4Model;


public class RootModel {
	private Sample1Model mSample1Model = new Sample1Model();
	private Sample2Model mSample2Model = new Sample2Model();
	private Sample3Model mSample3Model = new Sample3Model();
	private Sample4Model mSample4Model = new Sample4Model();

	public Sample1Model getSample1Model() {
		return mSample1Model;
	}

	public Sample2Model getSample2Model() {
		return mSample2Model;
	}

	public Sample3Model getSample3Model() {
		return mSample3Model;
	}

	public Sample4Model getSample4Model() {
		return mSample4Model;
	}
}
