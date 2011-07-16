package org.kotemaru.aptvelocity.sample;

import java.lang.annotation.Annotation;

import org.kotemaru.aptvelocity.ApBase;
import org.kotemaru.aptvelocity.TargetClassInfo;
import org.kotemaru.aptvelocity.sample.annotation.AutoBean;

import com.sun.mirror.declaration.TypeDeclaration;

public class SampleAp extends ApBase {

	@Override
	public TargetClassInfo[] getTargetClassInfos(TypeDeclaration classDecl) {
		boolean hasAnnotation = (classDecl.getAnnotation(AutoBean.class) != null);
		if (!hasAnnotation) return null;

		String pkg = classDecl.getPackage().getQualifiedName();
		pkg = pkg.replaceFirst("[^.]*$", "autobean");
		String className = classDecl.getSimpleName()+"Bean";
		String templName = "autoBean.vm";

		return new TargetClassInfo[]{
				new TargetClassInfo(pkg, className, templName)
		};
	}

}
