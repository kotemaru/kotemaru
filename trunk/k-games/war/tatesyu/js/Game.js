
function Game(){this.initialize.apply(this, arguments)};
(function(Class) {
	const CONFIG_CHIP = Chip.add("config", "img/everaldo.com/config.png");
	const PAD_CHIP = Chip.add("pad", "img/pad.png");

	Class.prototype.initialize = function(finish) {
		var canvases = [Util.byId("canvas0"), Util.byId("canvas1")];
		var contexs = [canvases[0].getContext("2d"), canvases[1].getContext("2d")];
		Util.copy(this,{
			canvasNo: 0,
			canvases: canvases,
			canvas: canvases[0],
			contexs : contexs,
			ctx : contexs[0],
			clipX : 0,
			clipY : 6400-400-32,
			clipW : 320,
			clipH : 416,
			mapH : 6400-32,
			actors : {},
			layers : [{},{},{}],
			reserved : {},
			enemyPlanes : {},
			enemyGrounds : {},
			idCount : 0,
			//myShip : null,
			count: 0,
			countMax: NaN,
			score: 0,
			scroll: 2,
			boss: null,
			isRun: false,
			isDemo: false,
			isGameOver: false,
			isStageClear: false,
			finish: finish,
			ms: 0 
		});
		//if (window.innerHeight < this.clipH) {
		//	this.clipH = window.innerHeight;
		//	canvases[0].height = this.clipH;
		//	canvases[1].height = this.clipH;
		//}
	}

	Class.prototype.setMap = function(map){
		with (this) {
			this.map = map;
			mapH = map.height()-map.grid;
			clipY = mapH - clipH;
			//countMax = clipY/scroll-map.grid;
			addEntity(map);
		}
	}
	Class.prototype.setMyShip = function(myShip){
		myShip.y = this.clipY + this.clipH - 50;
		this.myShip = myShip;
		this.addEntity(myShip);
	}
	Class.prototype.addEntity = function(entity) {
		with (this) {
			var id = idCount++;
			entity.id = id;
			reserved[id] = entity;
		}
	}
	Class.prototype.delEntity = function(entity) {
		with (this) {
			const id = entity.id;
			delete enemyPlanes[id];
			delete enemyGrounds[id];
			delete actors[id];
			delete layers[entity.layer][id];
		}
	}
	Class.prototype.addActive = function(e) {
		with (this) {
			var id = e.id;
			if (id === undefined) {
				id = this.idCount++;
				e.id = id;
			}
			if (e.isEnemyPlane) enemyPlanes[id] = e;
			if (e.isEnemyGround) enemyGrounds[id] = e;
			if (e.action) actors[id] = e;
			if (e.paint) layers[e.layer][id] = e;
		}
	}
	Class.prototype.activate = function() {
		with (this) {
			for (var id in reserved) {
				var e = reserved[id];
				if (e.isActive()) {
					if (e.isEnemyPlane) enemyPlanes[id] = e;
					if (e.isEnemyGround) enemyGrounds[id] = e;
					if (e.action) actors[id] = e;
					if (e.paint) layers[e.layer][id] = e;
					delete reserved[id];
				}
			}
		}
	}
	
	
	Class.prototype.drawImage = function(chip, x, y){
		chip.draw(this.ctx, x-this.clipX, y-this.clipY);
	}
	
	Class.prototype.drawLine = function(x1, y1, x2, y2){
		const ox = this.clipX;
		const oy = this.clipY;
		with (this.ctx) {
			beginPath();
			moveTo(x1-ox,y1-oy);
			lineTo(x2-ox,y2-oy);
			stroke();
		}
	}
	Class.prototype.drawBigText = function(texts, opts){
		opts = Util.copy({color:"white",size:64,isBlink:false}, opts);
		with (this) {
			if (opts.isBlink && count%20>15) return;
			for (var i=0; i<texts.length; i++) {
				var text = texts[i];
				ctx.strokeStyle = "white";
				ctx.lineWidth =1;
				ctx.fillStyle = opts.color;
				ctx.font = opts.size+"px bold monospace";
				var metrix = ctx.measureText(text);
				var y = 200 + i * opts.size;
				ctx.fillText(text, 160-metrix.width/2 , y);
				ctx.strokeText(text, 160-metrix.width/2 ,y);
			}
		}
	}
	Class.prototype.paintInfos = function(){
		with (this) {
			const y = 12;
			ctx.lineWidth =1;
			ctx.font = '12px bold fixed';
			ctx.strokeStyle = "white";
			ctx.fillStyle = "white";
		
			ctx.fillText(""+ms, 300, y);
	
			var s1 = "00000000"+score;
			var scoreStr = s1.substr(s1.length-8);
			ctx.fillText(scoreStr, 4, y);

			ctx.fillText("HP:", 120, y);
			const hp = myShip.hp;
			if (hp>0) {
				ctx.fillStyle = "#44ff44";
				if (hp<=2) ctx.fillStyle = "red";
				else if (hp<=5) ctx.fillStyle = "yellow";
				ctx.fillRect(142,3, hp*4 ,10);
			}
			ctx.strokeRect(142,3, 10*4 ,10);

			if (Config.control == "pad") {
				PAD_CHIP.draw(ctx, 320-80, clipH-64);
			}

			
			if (boss) {
				ctx.fillStyle = "#880044";
				ctx.fillRect(10, 20, Math.floor(300*boss.hp/boss.hpMax) ,4);
				ctx.strokeRect(10,20, 300 ,4);
			}
			
			if (isDemo) {
				drawBigText(["START!"], {color:"yellow", isBlink:true});
				if (count%20<15) CONFIG_CHIP.draw(ctx, 16, clipH-64);
			} else if (count < 30) {
				drawBigText(["Stage 1"], {color:"white"});
			}
			if (isGameOver) drawBigText(["GAME","OVER"], {color:"blue"});
			if (isStageClear) {
				drawBigText(["STAGE CLEAR!","", "Score:"+scoreStr], {color:"green", size:32});
			}
		}
	}
	Class.prototype.action = function(){
		with (this) {
			if (clipY<50) {
				setLifeSpan(100);
				scroll = 0;
				isStageClear = true;
			}
			if (count>=countMax) finish(this);
			
			count++;
			clipY -= scroll;
			activate();
			
			for (var id in actors) actors[id].action();
			for (var id in layers[0]) layers[0][id].paint();
			for (var id in layers[1]) layers[1][id].paint();
			for (var id in layers[2]) layers[2][id].paint();
			paintInfos();
			
			canvas.style.visibility = "visible";
			canvasNo = 1-canvasNo;
			canvas = canvases[canvasNo];
			ctx = contexs[canvasNo];
			canvas.style.visibility = "hidden";
		}
		return true;
	}
	Class.prototype.setLifeSpan = function(n, isForce){
		if (isForce || isNaN(this.countMax)) {
			this.countMax = this.count+n;
		}
	}
	
	Class.demo = function(finish){
		var game = new Game(finish);
		var map = new Map(game);
		map.load(Stage01.map);
		game.setMap(map);
		Stage01.createActors(game);
		
		var myShip = new MyShipDummy(game);
		game.setMyShip(myShip);
		
		game.isDemo = true;
		game.countMax = 1000;
		return game;
	}

	Class.stage01 = function(finish){
		var game = new Game(finish);
		var map = new Map(game);
		map.load(Stage01.map);
		game.setMap(map);
		Stage01.createActors(game);
		
		var myShip = new MyShip(game);
		game.setMyShip(myShip);
		return game;
	}


})(Game);

