var Eclipse = {};
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
};

window.onload = function() {
	Eclipse.fireEvent("load");
}

//EOF
