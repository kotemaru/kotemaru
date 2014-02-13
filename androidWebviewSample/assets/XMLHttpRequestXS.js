

/**
 * クロスサイト可能なXMLHttpRequest
 * - jQuery.ajaxから使用する場合はxhrパラメータを使用する。
 * - 例：
		$.ajax({
			type: "GET", url: "http://外部サイト", 
			success: function(data){...},
			xhr: function() {return new XMLHttpRequestXS();}
		});
 */
function XMLHttpRequestXS() {
	this._native = XMLHttpRequestXSFactory.getXMLHttpRequestXS();
	this._async = false;
	this.onreadystatechange = null;
	this.readyState = 0;
	this.responseText = null;
	this.responseType = null;
	this.response = null;
	this.responseXML = null;
	this.status = 0;
	this.statusText = null;
	//this.withCredentials = true; // 無条件にCookieを送信
	XMLHttpRequestXS._staticRegister(this);
};

/**
 * Naitive側からJavaScriptのオブジェクトを参照するためのマップ。
 * - キーはNativeオブジェクトのidentityHashCode。
 */
XMLHttpRequestXS._staticInstances = {};

/**
 * インスタンスの登録。
 * @param {XMLHttpRequestXS} self 登録するインスタンス
 */
XMLHttpRequestXS._staticRegister = function(self) {
	XMLHttpRequestXS._staticInstances[self._native.identityHashCode()] = self;
};
/**
 * インスタンスの登録解除。
 * @param {XMLHttpRequestXS} self 登録解除するインスタンス
 */
XMLHttpRequestXS._staticUnRegister = function(self) {
	delete XMLHttpRequestXS._staticInstances[self._native.identityHashCode()];
};

/**
 * Native側から呼ばれる関数。
 * @param {Number} identity 呼び元のidentityHashCode
 */
XMLHttpRequestXS._staticNativeCallback = function(identity) {
	var self = XMLHttpRequestXS._staticInstances[identity];
	if (self) {
		self._nativeCallback();
	}
};

XMLHttpRequestXS.prototype = {
	UNSENT:0,
	OPENED:1,
	HEADERS_RECEIVED :2,
	LOADING :3,
	DONE :4,
	ERROR :5,

	/**
	 * user,passは未サポート
	 */
	open : function(method, url, async) {
		this._async = async;
		var error = this._native.open(method, url, async);
		this._nativeCallback();
		if (error) throw error;
	},
	send : function(body) {
		this._native.setResponseType(this.responseType);
		this._native.send(body);
		if (!this._async) {
			this._nativeCallback();
		}
	},
	abort : function() {
		this._native.abort();
		XMLHttpRequestXS._staticUnRegister(this);
	},
	getAllResponseHeaders : function() {
		return this._native.getAllResponseHeaders();
	},
	getResponseHeader : function(name) {
		return this._native.getResponseHeader(name);
	},
	overrideMimeType : function(mimeType) {
		this._native.overrideMimeType(mimeType);
	},
	setRequestHeader : function(name,value) {
		this._native.setRequestHeader(name,value);
	},
	
	/**
	 * 状態変更があるとNatavi側から呼ばれる。
	 * - JavaScript側のプロパティの値をNativeに同期させる。
	 * - onreadystatechange が設定されていれば呼び出す。
	 * - 終了/エラーの時は必ず登録解除する。メモリリークになるので。
	 */
	_nativeCallback : function() {
		this.readyState = this._native.getReadyState();
		this.responseText = this._native.getResponseText();
		this.responseXML = this._native.getResponseXML();
		this.status = this._native.getStatus();
		this.statusText = this._native.getStatusText();
		if (this.readyState == this.DONE) {
			var mimeType = this._native.getMimeType();
			if (mimeType.match(/[+\/]xml$/)) {
				this.responseXML = new DOMParser().parseFromString(this.responseText, mimeType);
			}
			if (this.responseType == "document") {
				this.response = this.responseXML;
			} if (this.responseType == "json") {
				this.response = JSON.parse(this.responseText);
			} else {
				this.response = this.responseText;
			} // Note: 未サポート "Blog" 他
		}
		if (this.onreadystatechange) {
			//var ev = new Event("readystatechange");
			//ev.target = this;
			this.onreadystatechange();
		}
		if (this.readyState == this.DONE || this.readyState == this.ERROR ) {
			XMLHttpRequestXS._staticUnRegister(this);
		}
	}
};


