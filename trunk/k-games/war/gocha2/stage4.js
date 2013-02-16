
var stage4 = (function(){
	var CHIP_MAP =Chip.create("img/FSM/stage4-map.png");
	var CHARS_0 = {
		//  味方
		leader: {
			chip:Chip.create("img/usui/Actor26.png"),
			hp:300, ar:50,ap:50, dr:50,dp:50,
			bx:3, by:15, dir:"R"
		},
		knight: {
			chip:Chip.create("img/usui/Actor22.png"),
			hp:250, ar:50,ap:50, dr:50,dp:40
		},
	};
	
	var CHARS_1 = {
		// 敵
		boss: {
			chip:Chip.create("img/usui/Actor_tn.png"),
			attack: Chip.create("img/attack-tn.png"),
			hp:10000, ar:30,ap:200, dr:1000,dp:1000,
			bx:13, by:9, dir:"D"
		},
	};
	
	function makeMap(game){
		var backgroundMap = new Map(16, 16);
		backgroundMap.image = game.assets[CHIP_MAP.src];
		backgroundMap.loadData([
            [33,33,22,33,33,208,72,72,72,72,72,72,80,81,82,72,72,72,72,72],
            [33,33,22,33,33,33,69,69,69,69,69,69,96,97,98,69,69,69,69,69],
            [33,33,22,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33],
            [33,33,22,33,33,212,33,33,33,33,33,213,33,33,33,33,33,33,33,33],
            [33,33,22,33,33,228,33,33,33,33,33,229,33,33,33,33,33,33,33,33],
            [54,54,38,33,33,243,244,244,244,244,244,246,33,33,33,33,33,33,33,33],
            [70,70,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33],
            [72,72,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33],
            [53,53,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33],
            [72,72,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33],
            [72,72,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33],
            [72,72,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33],
            [33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33],
            [33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33],
            [33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33],
            [33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33],
            [33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33],
            [33,33,33,33,33,33,33,33,179,180,181,182,183,33,33,179,180,181,182,183],
            [33,33,33,33,33,33,33,33,179,180,181,182,183,33,33,179,180,181,182,183],
            [33,33,33,33,33,33,33,33,195,196,197,198,199,33,33,195,196,197,198,199]
        ],[
            [-1,-1,-1,-1,-1,208,209,210,208,208,208,208,-1,-1,-1,-1,-1,-1,-1,-1],
            [-1,-1,-1,-1,-1,224,225,226,224,225,226,224,-1,-1,-1,-1,11,12,13,-1],
            [-1,89,-1,-1,-1,240,241,242,240,241,242,240,-1,-1,-1,90,27,28,29,-1],
            [-1,89,-1,-1,-1,-1,-1,-1,-1,-1,28,-1,59,-1,-1,-1,-1,-1,-1,-1],
            [-1,-1,-1,-1,-1,-1,136,116,116,131,133,-1,75,-1,-1,-1,-1,-1,-1,-1],
            [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
            [91,92,156,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
            [125,126,206,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
            [-1,-1,219,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
            [91,92,206,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
            [107,108,206,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
            [69,69,238,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
            [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
            [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
            [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
            [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
            [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
            [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
            [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
            [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1]
        ]);
        backgroundMap.collisionData = [
            [1,1,1,0,0,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0],
            [1,1,1,0,0,1,1,1,1,1,1,1,0,0,0,0,1,1,1,0],
            [1,1,1,0,0,1,1,1,1,1,1,1,0,0,0,0,1,1,1,0],
            [1,1,1,0,0,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0],
            [1,1,1,0,0,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0],
            [1,1,1,0,0,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0],
            [1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
            [1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
            [1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
            [1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
            [1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
            [1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
            [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
            [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
            [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
            [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
            [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
            [0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,1,1,1,1,1],
            [0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,1,1,1,1,1],
            [0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,1,1,1,1,1]
        ];
        return backgroundMap
	}
	function makeMap2(game){
		var backgroundMap = new Map(16, 16);
		backgroundMap.image = game.assets[CHIP_MAP.src];
		backgroundMap.loadData([
            [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
            [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
            [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
            [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
            [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
            [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
            [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
            [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
            [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
            [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
            [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
            [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
            [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
            [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
            [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
            [-1,-1,-1,-1,-1,-1,-1,-1,163,164,165,166,167,-1,-1,163,164,165,166,167],
            [-1,-1,-1,-1,-1,-1,-1,-1,179,180,181,182,183,-1,-1,179,180,181,182,183],
            [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
            [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
            [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1]
        ]);
        return backgroundMap
	}

	function makeActor(data) {
		var chip = new VXChip(data.chip, data.attack);
		var params = GochaUtil.copy({}, data);
		delete params.chip;
		delete params.attack;
		return new GochaActor(chip, params);
	}
	
	
	function makeStage(game) {
    	// 味方チーム作成
    	var plat0 = new GochaPlatoon(0);
    	plat0.setLeader(makeActor(CHARS_0.leader));
   		for (var i=0;i<10;i++) {
   			plat0.addGocha(makeActor(CHARS_0.knight));
   		}
	
    	// 敵チーム作成
    	var plat1 = new GochaPlatoon(1);
    	plat1.setLeader(makeActor(CHARS_1.boss));
  	
   		// マップとGochaMainを作成して登録。
    	var main = new GochaMain(game, makeMap(game));
    	main.addGocha(plat1);
    	main.addGocha(plat0);
    	
    	// オーバラップマップ
    	var fgMap = makeMap2(game);
    	//fgMap.opacity = 0.9;
    	fgMap._style.zIndex = 1000;
    	main.addChild(fgMap);
   	
    	// 開始準備OKの処理
   		main.addEventListener("gocha.ready", function(ev){
   	    	main.command(1,"attack",{"for":"all"}); // 敵に戦闘開始命令
   	    	main.ctrl.show(true); // 自軍命令メニュー表示
  		});
   		// キャラ死亡通知
   		main.addEventListener("gocha.dead", function(ev){
   			if (ev.isLeader) {
   				var ev;
   				if (ev.actor.team != 0) {
 					// 敵リーダー死亡、勝利
 					ev = new Event("gocha.win");
   					ev.message = "ダダン ダン♪  ダダン ダン♪〜";
   				} else {
   					// 味方リーダー死亡、敗北
   					ev = new Event("gocha.lost");
   					ev.message = "ダダン ダン♪  ダダン ダン♪〜";
  				}
				game.dispatchEvent(ev);
  			}
   		});
  		
   		
   		// ステージタイトルダイアログ
   	   	main.addEventListener("gocha.onload", function(ev){
   			game.pause();
   	    	GochaDialog.open(main, {
   	    		title:"第４話  ター◯ネーター vs 騎士", 
   	    		message:"突如現れた大男が市場で暴れている。<br/>"
   	    				+"騎士団、すぐに取り押えろ。",
   	    		ok:function(){game.resume();}
   	    	});
  		});
   		return main;
	}
	
	return makeStage;
})();