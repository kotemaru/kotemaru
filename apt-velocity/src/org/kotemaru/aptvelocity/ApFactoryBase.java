package org.kotemaru.aptvelocity;

import java.util.ArrayList;
import java.util.Set;
import java.util.Collection;
import java.util.Collections;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessors;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;

public abstract class ApFactoryBase implements AnnotationProcessorFactory {
	private static final Collection NO_OPT = Collections.emptySet();


	public abstract ApInfo[] getAnnotationInfos();


	public Collection<String> supportedAnnotationTypes() {
		ApInfo[] infos = getAnnotationInfos();
		Collection<String> list = new ArrayList<String>(infos.length);
		for (int i=0; i<infos.length; i++) {
			list.add(infos[i].getName());
		}
		return list;
	}

	public Collection supportedOptions() {
		return NO_OPT;
	}

	public AnnotationProcessor getProcessorFor(Set atds, AnnotationProcessorEnvironment env) {
		ApInfo[] infos = getAnnotationInfos();
		for (int i=0; i<infos.length; i++) {
			if (atds.contains(infos[i].getTypeDeclaration(env))) {
				try {
					ApBase ap = (ApBase) infos[i].getProcessor().newInstance();
					ap.init(this, env);
					System.out.println("--->"+ap);
					return ap;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return AnnotationProcessors.NO_OP;
	}
}
