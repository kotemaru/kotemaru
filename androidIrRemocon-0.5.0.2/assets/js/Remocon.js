
var Remocon = {
	/**
	 * 画面サイズに合わせてボタンのサイズを補正。
	 * - フォントサイズ、アイコンサイズの調整も行う。
	 * @param {int} cols ボタンの横個数
	 * @param {int} rows ボタンの縦個数
	 */
	setButtonGrid : function (cols, rows) {
		var $body = $(document.body);
		var w = $body.width() / cols;
		var h = $body.height() / rows;
		var base = Math.min(w, h);

		CssLib.setCssRule("button", {
			width : (w * 0.9) + "px",
			height : (h * 0.9) + "px",
			"font-size" : (base * 0.5) + "px",
			"border-radius" : (base * 0.1) + "px",
		});
		CssLib.setCssRule("button > img", {
			width : (base * 0.5) + "px"
		});
	}	
};



window.onerror = function(errorMsg, url, lineNumber, column, errorObj) {
	alert(errorMsg);
}

$(function(){
	var isSettingMode = false;
	Native.onChangeSettingMode = function() {
		isSettingMode = !isSettingMode;
		CssLib.setCssRule("button", {"border-color": (isSettingMode?"#0ff":null)});
	};
	
	$("button").bind("click", function() {
		var $button = $(this);
		var buttonId = this.id;
		if (buttonId == null) return;
		if (!Native.isDeviceReady()) {
			Dialog.openErrorDialog("デバイスが接続されていません。");
			return;
		}
		if (isSettingMode) {
			Native.startReceiveIr(function(isSuccess){
				if (isSuccess) {
					var asyncTask = Native.getReceiveIrData(function(data){
						Native.endReceiveIr();
						if (data != null) {
							Native.putIrData(location.href, buttonId, data, null);
							Dialog.close();
						} else {
							Dialog.openErrorDialog("登録に失敗しました。("+asyncTask.getErrorMessage()+")");
						}
					}, 10000);
					Dialog.openRegisterButtonDialog($button, function(dialogButton) {
						if (dialogButton.id == "cancel") {
							asyncTask.cancel();
							Native.endReceiveIr();
						}
					});
				}
			});
		} else {
			var data = Native.getIrData(location.href, buttonId);
			if (data == null) {
				Dialog.openErrorDialog("未登録です。");
			} else {
				Native.sendData(data);
			}
		}
	});

});
