function Server() {}
(function(Class) {
	const CONTEXT_PATH = "/fs/";

	Class.list = function(dir, errorHandler) {
		return Class.getJson(CONTEXT_PATH+dir+"/", errorHandler);
	}
	Class.file = function(name, errorHandler) {
		return Class.getJson(CONTEXT_PATH+name, errorHandler);
	}

	Class.getJson = function(url, errorHandler) {
		var text = Class.getText(url, errorHandler);
		if (text == null) return null;
		try {
			return JSON.parse(text);
		} catch (e) {
			alert(e+"\n"+text);
			throw e;
		}
	}
	Class.getText = function(url, errorHandler) {
		url = url.replace(/\/\//,"/");
		var xreq = new XMLHttpRequest();
		xreq.open("GET", url, false);
		xreq.send();
		if (xreq.status >= 400) {
			if (errorHandler) {
				return errorHandler(xreq);
			} else {
				return null;
			}
		}
		return xreq.responseText;
	}
	
	Class.putJson = function(url, json, errorHandler) {
		var xreq = new XMLHttpRequest();
		xreq.open("PUT", url, false);
		xreq.setRequestHeader("Content-type", "application/json; charset=utf-8");
		xreq.send(json);
		if (xreq.status >= 400) {
			if (errorHandler) {
				return errorHandler(xreq);
			} else {
				return false;
			}
		}
		return true;
	}
	
})(Server);
