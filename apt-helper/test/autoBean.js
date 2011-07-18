var AutoBean = Packages.test.annotation.AutoBean;
var AptUtil = Packages.org.kotemaru.aptvelocity.AptUtil;

function processClass(classDecl, processor) {
	java.lang.System.out.println("--->"+classDecl+";"+classDecl.getAnnotation(AutoBean));
	if (classDecl.getAnnotation(AutoBean) == null) return;

	var packageName = (""+classDecl.getPackage().qualifiedName).replace(/[^.]*$/, "autobean");
	var className = classDecl.simpleName+"Bean";

	var context = copy(processor.initVelocity(),{
		masterClassDecl: classDecl,
		packageName: packageName,
		className: className,
		util: AptUtil
	});
	processor.applyTemplate(context, packageName, className, "test/autoBean.vm");
}

function copy(context, params) {
	for (name in params) {
		context.put(name, params[name]);
	}
	return context
}