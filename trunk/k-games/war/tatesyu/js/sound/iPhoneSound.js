/**
 * iPhone用効果音ライブラリ。
 * - iPhone の制限を回避して任意のタイミングで効果音を鳴らすライブラリ。
 * - iPhone の safari には audio タグに以下の制限がある。
 *   - ユーザのアクションから起動しないと音声データを読み込めない。
 *   - 同時に１つの音声データしか保持できない。
 * - 解決方
 *   - 複数の効果音を１つの音声データにまとめて一部を再生する。
 *   - 読み込みは起動時に何らかのユーザアクションをさせるしかない。
 *
使用例：
	xxxx.addEventListener("click", function(){ // 何かのタップで
		iPhoneSound.load("all.mp3",  {
		  // 音の名前    開始秒    終了秒
			bootup    : {s:0.0,   e:1.55,   },
			shotdown  : {s:1.9,   e:2.48,   },
			click     : {s:3.293, e:3.8,    },
		}, function(){
			onload();
		});
	);

	function onload() {
		// ロード後は何時でも呼べる。
		iPhoneSound.play("bootup");
	}
 *
 * @author kotemaru@kotemru.org
 */
function iPhoneSound(){};
(function(Class){

	/**
	 * 音声データの読み込み。
	 * @param url    音声データのURL
	 * @param parts  部分定義 {効果音名:{s:開始秒,e:終了秒},…}
	 * @param callback 読込完了ハンドラ
	 */
	Class.load = function(url, parts, callback) {
		Class.audio = new Audio(url);
		Class.parts = parts;
		Class.audio.addEventListener("loadedmetadata", function(){
			callback();
		});
		Class.audio.load();
		//Class.audio.play();
	}
	/**
	 * 効果音の再生。
	 * @param name   効果音の名前
	 */
	Class.play = function(name){
		if (Class.audio == null) return;
		var part = Class.parts[name];
		playPart(part.s, part.e, 1.0);
	}
	Class.BGM = function(name){
		// no sapport
	}
	Class.stop = function(name){
		// no sapport
	}
	
	function playPart(s,e,v){
		Class.audio.pause();
		Class.audio.currentTime = s;
		Class.audio.volume = v;
		Class.endTime = e;
		Class.audio.play();
		setTimeout(autoStop, (e-s)*1000);
	}
	function autoStop(){
		if (Class.audio.paused) return;
		if (Class.audio.currentTime >= Class.endTime) {
			Class.audio.pause();
		} else {
			setTimeout(autoStop, 10);
		}
	}
	Class.autoStop = autoStop;

})(iPhoneSound);

if (IS_IPHONE) {
	//var Sound = iPhoneSound;
}
