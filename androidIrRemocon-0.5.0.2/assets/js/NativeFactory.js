if (undefined == window.NativeFactory) {
	// Chromeで確認用のダミー
	NativeFactory = {
		getIrrcUsbDriver : function() {
			return {
				isReady : function() {return false;}
			};
		},
		getOptions : function() {
			return {
				isSettingMode : function() {return true;}
			}
		},
		getIrDataDao : function() {return null;},
	};
}
