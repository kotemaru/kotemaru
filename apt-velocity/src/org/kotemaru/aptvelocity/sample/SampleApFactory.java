package org.kotemaru.aptvelocity.sample;

import org.kotemaru.aptvelocity.ApInfo;
import org.kotemaru.aptvelocity.ApFactoryBase;
import org.kotemaru.aptvelocity.sample.annotation.AutoBean;

public class SampleApFactory extends ApFactoryBase {

	static final ApInfo[] ANNO_INFOS = {
		new ApInfo(AutoBean.class, SampleAp.class)
	};


	@Override
	public ApInfo[] getAnnotationInfos() {
		return ANNO_INFOS;
	}
}
