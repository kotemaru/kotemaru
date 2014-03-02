var Native = {
	_irrcUsbDriver : NativeFactory.getIrrcUsbDriver(),
	_options : NativeFactory.getOptions(),
	_irDataDao : NativeFactory.getIrDataDao(),

	/**
	 * デバイスの状態確認。
	 * @return {bool} true=準備OK
	 */
	isDeviceReady : function() {
		return Native._irrcUsbDriver.isReady();
	},
	/**
	 * 受信モード開始。
	 * @param {function({bool})} callback 完了通知。
	 */
	startReceiveIr : function(callback) {
		Native._onStartReceiveIr = callback;
		return Native._irrcUsbDriver.startReceiveIr();
	},
	/**
	 * 受信モード終了。
	 * @param {function({bool})} callback 完了通知。
	 */
	endReceiveIr : function(callback) {
		Native._onEndReceiveIr = callback;
		return Native._irrcUsbDriver.endReceiveIr();
	},
	/**
	 * 受信赤外線データ取得。リモコンが操作されるまで通知は来ない。
	 * @param {function({Bytes})} callback 取得データ通知。function({Bytes} data)
	 * @param {int} timeout ミリ秒
	 */
	getReceiveIrData : function(callback, timeout) {
		timeout = (timeout == null) ? 5000 : timeout;
		Native._onGetReceiveIrData = callback;
		return Native._irrcUsbDriver.getReceiveIrData(timeout);
	},
	/**
	 * 赤外線データ送信。
	 * @param {Bytes} data 赤外線データ(JavaオブジェクトのBytes)
	 */
	sendData : function(data) {
		return Native._irrcUsbDriver.sendData(data);
	},

	/**
	 * DBへデータ登録。
	 * @param {string} pageName HTMLページ名
	 * @param {string} btnId ボタンID
	 * @param {Bytes} data 赤外線データ(JavaオブジェクトのBytes)
	 * @param {string} attr 追加属性 JSON 文字列
	 */
	putIrData : function(pageName, btnId, data, attrs) {
		Native._irDataDao.putIrData(pageName, btnId, data, attrs);
	},
	/**
	 * DBからデータ取得。
	 * @param {string} pageName HTMLページ名
	 * @param {string} btnId ボタンID
	 * @return {Bytes} 赤外線データ、nullは該当データ無し。
	 */
	getIrData : function(pageName, btnId) {
		return Native._irDataDao.getIrData(pageName, btnId);
	},

	// Callback from java.
	/**
	 * 「登録モード on/off」のメニュー選択コールバック。
	 * @param isSuccess false=処理失敗
	 */
	onChangeSettingMode : function() {
		/* abstract */
	},
	/**
	 * startReceiveIr()のコールバック。
	 * @param isSuccess false=処理失敗
	 */
	onStartReceiveIr : function(isSuccess) {
		if (Native._onStartReceiveIr) {
			Native._onStartReceiveIr(isSuccess);
		}
	},
	/**
	 * endReceiveIr()のコールバック。
	 * @param isSuccess false=処理失敗
	 */
	onEndReceiveIr : function(isSuccess) {
		if (Native._onEndReceiveIr) {
			Native._onEndReceiveIr(isSuccess);
		}
	},
	/**
	 * getReceiveIrData()のコールバック。
	 * @param isSuccess false=処理失敗
	 */
	onGetReceiveIrData : function(isSuccess) {
		if (Native._onGetReceiveIrData) {
			Native._onGetReceiveIrData(Native._irrcUsbDriver.getIrData());
		}
	},
};
