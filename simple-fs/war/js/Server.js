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
		if (send(xreq,undefined,errorHandler)) {
			return xreq.responseText;
		}
		return null;
	}
	
	Class.putJson = function(url, json, errorHandler) {
		var xreq = new XMLHttpRequest();
		xreq.open("PUT", url, false);
		xreq.setRequestHeader("Content-type", "application/json; charset=utf-8");
		return send(xreq,json,errorHandler);
	}
	
	Class.remove = function(url, errorHandler) {
		var xreq = new XMLHttpRequest();
		xreq.open("DELETE", url, false);
		return send(xreq,undefined,errorHandler);
	}
	
	Class.postScore = function(name, stage, time) {
		var url = "/score/post?game=bcoro&stage="
					+stage+"&name="+name+"&score="+time+"&asc=true";
		var xreq = new XMLHttpRequest();
		xreq.open("POST", url, false);
		return send(xreq);
	}

	Class.listScore = function(name, stage) {
		var url = "/score/get?game=bcoro&stage="+stage+"&limit=10&asc=true";
		var text = Class.getText(url);
		if (text == null) return null;
		try {
			return JSON.parse(text);
		} catch (e) {
			alert(e+"\n"+text);
			throw e;
		}
	}
	
	Class.clearScore = function(stage, errorHandler) {
		var url = "/score/get?game=bcoro&stage="+stage+"&limit=10&asc=true";
		var xreq = new XMLHttpRequest();
		xreq.open("DELETE", url, false);
		return send(xreq);
	}
	
	function send(xreq, body, errorHandler){
		try {
			xreq.send(body);
		} catch (e) {
			if (errorHandler) {
				return errorHandler(xreq, e);
			} else {
				alert("通信に失敗しました。");
			}
			return false;
		}
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
