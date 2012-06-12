/**
@author kotemaru@kotemaru.org
*/



enchant.gocha.GochaCtrl = org.kotemaru.Class(enchant.Group, function(_class, _super){
	var Util = org.kotemaru.Util;
	
	var CHIPS  = enchant.gocha.Chip.create("img/ctrl.png");

	_class.prototype.initialize = function(gochaMain) {
		_super.prototype.initialize.call(this);
		const self = this;
		this.gochaMain = gochaMain;
		
		var bg = makeBackGroud();
		var b1 = makeButton(0,CHIPS,function(){
			gochaMain.command(0,"attack");
			self.touchIgnore = true;
			self.show(false);
		});
		var b2 = makeButton(1,CHIPS,function(){
			gochaMain.command(0,"defense");
			self.touchIgnore = true;
			self.show(false);
		});
		var b3 = makeButton(2,CHIPS,function(){
			gochaMain.command(0,"lines");
			self.touchIgnore = true;
			self.show(false);
		});
		var b4 = makeButton(3,CHIPS,function(){
			gochaMain.game.fps = (gochaMain.game.fps==5?10:5);
			gochaMain.game.pause();
			gochaMain.game.resume();
			self.touchIgnore = true;
			self.show(false);
		});

		this.addChild(bg);
		this.addChild(b1);
		this.addChild(b2);
		this.addChild(b3);
		this.addChild(b4);
	
		this.x = 320 / 2 - 168/2;
		this.y = 320 / 2 -  48/2;
		this.show(false);
	}
	
	_class.prototype.on_touch = function(ev) {
		const game = this.gochaMain.game;
		if (!this.visible && !this.touchIgnore && game._intervalID != null) {
			this.gochaMain.command(0,"moveLeader",{x:ev.x/16, y:ev.y/16});
		}
		this.touchIgnore = false;
	}
	
	_class.prototype.show = function(b) {
		const game = this.gochaMain.game;
		if (b) {
			game.pause();
		} else if (game._intervalID == null) {
			game.resume();
		}
		this.visible = b;
		for (var i in this.childNodes) {
			this.childNodes[i].visible = b;
		}
	}

	
	function makeButton(no, chip, onclick) {
		var btn = new enchant.Sprite(32, 32);
		btn._style.zIndex = 10000;
		btn.x = 36 * no + 12;
		btn.y = 8;
		btn.frame = no;
		btn.image = chip.image;
		btn.addEventListener("touchstart", onclick);
		return btn;
	}

	function makeBackGroud() {
		var bg = new enchant.Sprite(168, 48);
		bg._style.zIndex = 10000;
		bg.image = new Surface(bg.width, bg.height);
		bg.opacity = 0.5;
		var ctx = bg.image.context;
		ctx.fillStyle= "black";
		ctx.fillRect(0,0, bg.width, bg.height);
		ctx.lineWidth = 2;
		ctx.strokeStyle= "white";
		ctx.strokeRect(0,0, bg.width, bg.height);
		return bg;
	}

	
	
	
});
