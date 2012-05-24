function DummySound(){this.initialize.apply(this, arguments)};
(function(Class) {
	/**
	 * 音声データの読み込み。
	 * @param url    音声データのURL
	 * @param parts  部分定義 {効果音名:{s:開始秒,e:終了秒},…}
	 * @param callback 読込完了ハンドラ
	 */
	Class.load = function(url, parts, callback) {
		callback();
	}

	/**
	 * 効果音の再生。
	 * @param name   効果音の名前
	 */
	Class.play = function(name){
	}
	Class.BGM = function(name){
	}

})(DummySound);

if (!window.Sound) {
	Sound = DummySound;
}
