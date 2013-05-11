function Eclipse(){};
(function(){
	/**
	 * EclipseからBrowserに対してデータを設定する。
	 * - onloadのタイミングで呼ばれる。
	 * - データの解析はBrowser側で行う。
	 * @param content 入力ファイルの内容をUTF8で文字列化したもの。
	 */
	Eclipse.setContent = function(content) {
		alert("Abstract function Eclipse.setContent() not implemented.");
	};
	
	/**
	 * EclipseからBrowserに対してデータを取得する。
	 * - Eclipseの保存のタイミングで呼ばれる。
	 * @return 編集内容をUTF8で保存形式に文字列化したもの。
	 */
	Eclipse.getContent = function() {
		alert("Abstract function Eclipse.getContent() not implemented.");
	};
	
	/**
	 * BrowserからEclipseに対してイベントを通知する。
	 * - 現在の所、load と change のみ。
	 * @param type イベントタイプ。
	 */
	Eclipse.fireEvent = function(type) {
		window.status = type;
		window.status = null;
	};
	
	//window.onload = function() {
	//	Eclipse.fireEvent("load");
	//};
	window.onerror = function(err){
		window.status = "error "+err+"\n"+err.stack;
		window.status = null;
		throw err;
	};
	Eclipse.log = function(msg){
		//if (console) console.log(msg);
		window.status = "log "+msg;
		window.status = null;
		if (window.console) {
			window.console.log(msg);
		}
	};
	
	Eclipse.startup = function(pref) {
		alert("Abstract function Eclipse.startup() not implemented.");
	};
	
	Eclipse.preferences = {};
	
	Eclipse.getPreferences = function(key) {
		return ""+Eclipse.preferences[key];
	};
	Eclipse.setPreferences = function(key, val) {
		Eclipse.preferences[key] = val;
		Eclipse.fireEvent("syncPreferences");
	};
	Eclipse.syncPreferences = function() {
		Eclipse.fireEvent("syncPreferences");
	}
	
	var contents =[];
	Eclipse.openContent = function() {
		contents.length = 0;
	};
	Eclipse.addContent = function(base64) {
		contents.push(Base64.decode(base64));
	};
	Eclipse.closeContent = function() {
		Eclipse.setContent(contents.join());
		contents.length = 0;
	};
	Eclipse.failContent = function(msg) {
		alert(msg);
		contents.length = 0;
	};
	
	Eclipse.print = function() {
		alert("Abstract function Eclipse.print() not implemented.");
	};
	Eclipse.undo = function() {
		alert("Abstract function Eclipse.undo() not implemented.");
	};
	Eclipse.redo = function() {
		alert("Abstract function Eclipse.redo() not implemented.");
	};
	Eclipse.config = function() {
		alert("Abstract function Eclipse.config() not implemented.");
	};

})(Eclipse);


//EOF
