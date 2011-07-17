var AutoBean = Package.org.kotemaru.aptvelocity.sample.AutoBean;
var AptUtil = Package.org.kotemaru.aptvelocity.AptUtil;

function processClass(classDesc, processor) {
	if(classDecl.getAnnotation(AutoBean) == null) return;

	var context = processor.initVelocity();
	context.put("masterClassDecl", classDecl);
	//context.put("annotation", getAnnotationMap(classDecl));
	context.put("util", new AptUtil());

	var pkgName = classDecl.package.qualifiedName.replace(/[^.]*$/, "autobean");
	var className = classDecl.simpleName+"Bean";
	var templName = "test/autoBean.vm";

	processor.applyTemplate(context, pkgName, className, templName);
}