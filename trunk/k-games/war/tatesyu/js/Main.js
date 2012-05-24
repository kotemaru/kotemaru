function Main() {}
(function(Class) {
	Class.thisGame = null;

	var preload = 2;
	function onPreload() {
		if (--preload<=0) onLoad2();
	}

	function onLoad(){
		var preload = 2;
		function onPreload() {
			if (--preload<=0) onLoad2();
		}
		Sound.load("sound/all.mp3",  {
		  // 音の名前    開始秒    終了秒
			kan    : {s:1.246, e:1.4   ,v:1.0},
			bon    : {s:1.929, e:2.065 ,v:1.0},
			boon   : {s:2.440, e:2.9   ,v:1.0},
			engine : {s:4.63,  e:5.25  ,v:0.3},
		}, onPreload );
		
		Chip.load(onPreload);
	}

	var INTERVAL = 50;
	function onLoad2() {
		runDemo();
		ticker();
	}

	function resizeWindow() {
		window.scrollTo(0,1);
		document.body.style.zoom = 
				Math.min(window.innerHeight/416, window.innerWidth/320);
	}

	function runDemo() {
		resizeWindow();
		INTERVAL = 10;
		Class.thisGame = Game.demo(runDemo);
		Input.modeDemo(runStage01);
	}

	function runStage01(ev) {
		resizeWindow();
		INTERVAL = Config.interval;
		Class.thisGame = Game.stage01(function(){
			var score = Class.thisGame.score;
			var result = "予科練";
			if (score>10000) result = "新兵";
			if (score>20000) result = "熟練パイロット";
			if (score>40000) result = "エース";
			if (score>60000) result = "大空のサムライ";
			if (score>70000) result = "ラバウルの魔王";
			if (score>80000) result = "零戦虎徹";
			if (Class.thisGame.isStageClear) result = "クリア！ "+result;

			end9leap(score, result);

			//alert("score:"+score+"\n"+result);
			runDemo();
		});
		Input.modePlay();
		Sound.BGM("engine");
	}

	var oldTime = new Date().getTime();
	function ticker() {
		setTimeout(ticker, INTERVAL);

		var t1 = new Date().getTime();
		Class.thisGame.ms = (new Date().getTime())-oldTime;
		oldTime = t1;
		
		Class.thisGame.action();
	}

	
	// nineleap.enchant.js より抽出。
	// - スコア登録画面へ転送。
    function end9leap(score, result) {
        if (location.hostname == 'r.jsgames.jp') {
            var id = location.pathname.match(/^\/games\/(\d+)/)[1]; 
            location.replace([
                'http://9leap.net/games/', id, '/result',
                '?score=', encodeURIComponent(score),
                '&result=', encodeURIComponent(result)
            ].join(''));
        }
    }


    Class.onLoad = onLoad;
})(Main);