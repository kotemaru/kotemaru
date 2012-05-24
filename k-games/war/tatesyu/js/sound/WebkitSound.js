function WebkitSound(){this.initialize.apply(this, arguments)};
(function(Class) {
	
	/**
	 * 音声データの読み込み。
	 * @param url    音声データのURL
	 * @param parts  部分定義 {効果音名:{s:開始秒,e:終了秒},…}
	 * @param callback 読込完了ハンドラ
	 */
	Class.load = function(url, parts, callback) {
		Class.context = new webkitAudioContext();
		Class.parts = parts;
		preload(url, callback);
	}
	function preload(url, callback) {
		var xreq = new XMLHttpRequest();
		xreq.open("GET", url, true);
		xreq.responseType = "arraybuffer";

		xreq.onload = function(){
			Class.context.decodeAudioData(xreq.response, function(buffer){
				Class.buffer = buffer;
				callback();
			});
		};
		xreq.send();
	}

	/**
	 * 効果音の再生。
	 * @param name   効果音の名前
	 */
	Class.play = function(name){
		var part = Class.parts[name];
		playPart(part.s, part.e, part.v, false);
	}
	
	var sources = {};
	Class.BGM = function(name){
		if (sources[name]) return;
		var part = Class.parts[name];
		sources[name] = playPart(part.s, part.e, part.v, true);
	}
	Class.stop = function(name){
		if (!sources[name]) return;
		sources[name].noteOff(0);
		delete sources[name];
	}

	function playPart(s,e,v,loop){
		var source = Class.context.createBufferSource();
		var gain = Class.context.createGainNode();
		source.buffer = Class.buffer;
		source.loop = loop;
		gain.gain.value = v;
		
		source.connect(gain);
		gain.connect(Class.context.destination);
		source.noteGrainOn(0,s,(e-s));
		return source;
	}

	
})(WebkitSound);

if (window.webkitAudioContext) {
	Sound = WebkitSound;
}
