function Server() {}
(function(Class) {
	const CONTEXT_PATH = "/fs/";

	Class.list = function(dir, errorHandler) {
		return Class.getJson(CONTEXT_PATH+dir+"/", errorHandler);
	}

	var cache = {};
	Class.file = function(name, errorHandler) {
		if (cache[name]) return cache[name];
		cache[name] = Class.getJson(CONTEXT_PATH+name, errorHandler);
		return cache[name];
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
	Class.remove = function(url, errorHandler) {
		var xreq = new XMLHttpRequest();
		xreq.open("DELETE", url, false);
		xreq.send();
		if (xreq.status >= 400) {
			if (errorHandler) {
				return errorHandler(xreq);
			} else {
				return false;
			}
		}
		return true;
	}
	
	Class.postScore = function(name, stage, time) {
		var url = "/score/post?game=bcoro&stage="
							+stage+"&name="+name+"&score="+time;
		var xreq = new XMLHttpRequest();
		xreq.open("POST", url, false);
		xreq.send();
		if (xreq.status >= 400) {
			return false;
		}
		return true;
	}

	Class.listScore = function(name, stage) {
		var url = "/score/post?game=bcoro&stage="+stage+"&limit=10";
		var text = Class.getText(url);
		if (text == null) return null;
		try {
			return JSON.parse(text);
		} catch (e) {
			alert(e+"\n"+text);
			throw e;
		}
	}
	
	
})(Server);
