
var stage3 = (function(){
	var CHIP_MAP =Chip.create("img/FSM/stage3-map.png");
	var CHARS_0 = {
		//  味方
		hime: {
			chip:Chip.create("img/usui/Actor113.png"),
			attack: Chip.create("img/nil.png"),
			hp:100, ar:10,ap:10, dr:100,dp:10,
			bx:13, by:1, dir:"D"
		},
		/*
		knight5: {
			chip:Chip.create("img/usui/Actor9.png"),
			hp:300, ar:50,ap:50, dr:50,dp:40
		},
		*/
		knight1: {
			chip:Chip.create("img/usui/Actor72.png"),
			hp:300, ar:50,ap:50, dr:50,dp:40
		},
		knight2: {
			chip:Chip.create("img/usui/Actor26.png"),
			hp:300, ar:50,ap:50, dr:50,dp:40
		},
		/*
		knight3: {
			chip:Chip.create("img/usui/Actor29.png"),
			hp:300, ar:50,ap:50, dr:50,dp:40
		},
		knight4: {
			chip:Chip.create("img/usui/Actor30.png"),
			hp:300, ar:50,ap:50, dr:50,dp:40
		},
		knight5: {
			chip:Chip.create("img/usui/Actor9.png"),
			hp:300, ar:50,ap:50, dr:50,dp:40
		},
		knight6: {
			chip:Chip.create("img/usui/Actor39.png"),
			hp:300, ar:50,ap:50, dr:50,dp:40
		},
		*/
		
		magic1: {
			chip:Chip.create("img/usui/Actor10.png"),
			hp:150, ar:10,ap:10, dr:50,dp:10,
			isShooter:true, bullet:GochaBulletFire,
			mp:100, mar:100,map:100, mdr:50,mdp:50
		},
		magic3: {
			chip:Chip.create("img/usui/Actor88.png"),
			hp:150, ar:10,ap:10, dr:50,dp:10,
			isShooter:true, bullet:GochaBulletFire,
			mp:100, mar:50,map:150, mdr:50,mdp:50
		},
		/*
		magic2: {
			chip:Chip.create("img/usui/Actor21.png"),
			hp:150, ar:10,ap:10, dr:50,dp:10,
			isShooter:true, bullet:GochaBulletFire,
			mp:100, mar:150,map:50, mdr:50,mdp:50
		},
		*/
	};
	
	var CHARS_1 = {
		// 敵
		boss: {
			chip:Chip.create("img/usui/Actor12.png"),
			hp:500, ar:100,ap:100, dr:100,dp:80,
			mdr:50, mdp:50,
			bx:9, by:18, dir:"U"
		},
		bandit: {
			chip:Chip.create("img/usui/Actor115.png"),
			hp:300, ar:40,ap:40, dr:60,dp:40,
		}

/*
		 	Chip.create("img/usui/Actor115.png"),
		 	Chip.create("img/usui/Actor22.png"),
		 	Chip.create("img/usui/Actor23.png"),
		 	Chip.create("img/usui/Actor31.png"),
		 	Chip.create("img/usui/Actor40.png"),
		 	Chip.create("img/usui/Actor42.png"),
		 	Chip.create("img/usui/Actor44.png"),
		 	Chip.create("img/usui/Actor57.png"),
		 	Chip.create("img/usui/Actor63.png"),
		 	Chip.create("img/usui/Actor7.png"),
		 	Chip.create("img/usui/Actor75.png"),
		 	Chip.create("img/usui/Actor9.png"),
*/
	};
	
	function makeMap(game){
		var backgroundMap = new Map(16, 16);
		backgroundMap.image = game.assets[CHIP_MAP.src];
		backgroundMap.loadData([
		                        [7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,71,7],
		                        [7,7,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,71,7],
		                        [7,7,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,71,7],
		                        [7,7,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,71,7],
		                        [7,7,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,71,7],
		                        [7,7,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,71,7],
		                        [7,7,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,71,7],
		                        [7,7,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,71,7],
		                        [7,7,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,71,7],
		                        [7,7,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,71,7],
		                        [7,7,0,1,1,1,20,17,17,17,17,17,17,17,81,81,17,17,71,7],
		                        [7,7,0,1,1,1,2,34,34,34,97,98,34,33,194,196,96,34,71,7],
		                        [7,7,16,17,17,17,18,112,5,49,113,114,49,49,194,196,112,49,71,7],
		                        [7,7,34,35,36,37,34,128,65,65,129,130,65,49,194,196,65,65,71,7],
		                        [7,7,48,51,52,53,48,71,7,7,7,7,7,7,194,196,71,7,7,7],
		                        [7,7,48,67,68,69,48,71,7,7,7,7,7,7,194,196,71,7,7,7],
		                        [7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7],
		                        [7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7],
		                        [7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7],
		                        [7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7]
		                    ],[
		                        [-1,-1,241,242,242,242,242,242,242,242,242,242,242,242,242,242,242,243,-1,-1],
		                        [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
		                        [47,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,79],
		                        [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
		                        [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
		                        [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
		                        [-1,79,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
		                        [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,47],
		                        [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
		                        [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
		                        [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1],
		                        [-1,47,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,230,231],
		                        [-1,-1,-1,-1,-1,-1,-1,91,92,93,94,95,-1,-1,-1,-1,-1,-1,246,247],
		                        [166,167,-1,-1,-1,-1,-1,107,108,109,110,111,-1,-1,-1,-1,-1,-1,-1,-1],
		                        [182,183,-1,-1,-1,-1,-1,123,124,125,126,-1,-1,-1,-1,-1,-1,-1,-1,-1],
		                        [-1,-1,127,-1,-1,-1,127,-1,140,141,142,-1,-1,-1,-1,-1,-1,-1,-1,-1],
		                        [-1,-1,143,-1,-1,-1,143,-1,156,157,158,-1,-1,-1,-1,-1,-1,-1,-1,-1],
		                        [-1,-1,-1,63,-1,-1,-1,-1,-1,47,-1,-1,-1,-1,-1,-1,-1,-1,79,-1],
		                        [-1,47,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,47,-1,-1,-1,-1,-1],
		                        [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1]
		                    ]);
		                    backgroundMap.collisionData = [
		                        [0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0],
		                        [0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0],
		                        [0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0],
		                        [0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0],
		                        [0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0],
		                        [0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0],
		                        [0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0],
		                        [0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0],
		                        [0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0],
		                        [0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0],
		                        [0,1,0,0,0,0,0,1,1,1,1,1,1,1,0,0,1,1,1,1],
		                        [1,1,0,0,0,0,0,1,1,1,1,1,1,1,0,0,1,1,1,1],
		                        [1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,1,1,1,1],
		                        [1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,1,1,0,0],
		                        [1,1,1,1,1,1,1,0,1,1,1,0,0,0,0,0,0,0,0,0],
		                        [0,0,1,0,0,0,1,0,1,1,1,0,0,0,0,0,0,0,0,0],
		                        [0,0,1,0,0,0,1,0,1,1,1,0,0,0,0,0,0,0,0,0],
		                        [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
		                        [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
		                        [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]
		                    ];
		return backgroundMap;
	}

	// 上向きの相対座標
	var LINES_POS = {
		fighter: [
		    {x:0,  y:-6},
		    {x:1,  y:-6},
		    {x:-1, y:-6},
		    {x:2,  y:-6},
		    {x:-2, y:-6},
		    {x:3,  y:-6},
		    {x:-3, y:-6},
		],
		shooter: [
		    {x: 0, y:-4},
		    {x: 1, y:-4},
		    {x:-1, y:-4},
		    {x: 2, y:-4},
		    {x:-2, y:-4},
		    {x: 3, y:-4},
		    {x:-3, y:-4},
		],
	};

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
    	plat0.lines.positions = LINES_POS;
    	for (var i in CHARS_0) {
   			if (i == "hime") {
   				plat0.setLeader(makeActor(CHARS_0[i]));
   			} else {
   				plat0.addGocha(makeActor(CHARS_0[i]));
   			}
   		}
	
    	// 敵チーム作成
    	var plat1 = new GochaPlatoon(1);
    	plat1.setLeader(makeActor(CHARS_1.boss));
   		for (var i=0;i<10;i++) {
   			plat1.addGocha(makeActor(CHARS_1.bandit));
   		}
  	
   		// マップとGochaMainを作成して登録。
    	var main = new GochaMain(game, makeMap(game));
    	main.addGocha(plat1);
    	main.addGocha(plat0);
    	
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
   					ev.message = "無事に姫を隣国に連れ出した騎士達はそこから反撃の狼煙を上げるのであった..";
   				} else {
   					// 味方リーダー死亡、敗北
   					ev = new Event("gocha.lost");
   					ev.message = "公国の血脈は絶たれてしまった。<br/>騎士達は途方に暮れた。";
  				}
				game.dispatchEvent(ev);
  			}
   		});
  		
   		
   		// ステージタイトルダイアログ
   	   	main.addEventListener("gocha.onload", function(ev){
   			game.pause();
   	    	GochaDialog.open(main, {
   	    		title:"第３話  山賊 vs 騎士と魔道士", 
   	    		message:"亡国の姫をつれて逃亡中に賞金目当ての山賊に見付かった。<br/>"
   	    				+"頼りは２人の魔道士だ。<br/>",
   	    		ok:function(){game.resume();}
   	    	});
  		});
   		return main;
	}
	
	return makeStage;
})();
