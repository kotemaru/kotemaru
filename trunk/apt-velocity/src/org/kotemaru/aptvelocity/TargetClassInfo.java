package org.kotemaru.aptvelocity;

public class TargetClassInfo {
	private String packageName;
	private String simpleName;
	private String templateName;

	public TargetClassInfo() {
	}

	public TargetClassInfo(String pkgName, String name, String templ) {
		this.setPackageName(pkgName);
		this.setSimpleName(name);
		this.setTemplateName(templ);
	}


	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getSimpleName() {
		return simpleName;
	}
	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getName() {
		return getPackageName()+'.'+getSimpleName();
	}
}
