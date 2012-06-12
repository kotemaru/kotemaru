/**
@author kotemaru@kotemaru.org
*/

enchant.gocha.GochaMain = org.kotemaru.Class(enchant.gocha.GochaGroup, function(_class, _super){
	var Util = org.kotemaru.Util;

	_class.prototype.initialize = function(game, map) {
		_super.prototype.initialize.call(this);
		Util.copy(this, {
			game: game,
			map: map,
			bw: map.width/map.tileWidth,
			bh: map.height/map.tileHeight,
		});
		this.gochaMain = this;
		this.ctrl = new GochaCtrl(this);

		this.addChild(map);
		this.addChild(this.ctrl);
		Util.addListeners(this);

		var self = this;
		var originEvent = null;
		game.rootScene.addEventListener("touchend",function(ev){
			self.ctrl.on_touch(ev);
		});
		game.rootScene.addEventListener("touchstart",function(ev){
			originEvent = ev;
		});
		game.rootScene.addEventListener("touchmove",function(ev){
			// 横に少し動かすアクション
			if (Math.abs(originEvent.x-ev.x) > 50) {
				self.ctrl.show(true);
			}
		});

		this.tl.delay(0).then(function(){
			self.dispatchEvent(new enchant.Event("gocha.onload"));
			self.resetLines();
		}).delay(8).then(function(){
			self.dispatchEvent(new enchant.Event("gocha.ready"));
		});
		
	};
	_class.prototype.on_added = function(ev) {
		ev.target.resetLines();
	}
	_class.prototype.resetLines = function() {
		for (var i in this.entities) {
			var ent = this.entities[i];
			if (ent.isPlatoonClass) ent.doLines();
		}
	}
	
	_class.prototype.on_enterframe = function(ev) {
		var self = ev.target;
		self.action();
		self.draw();
	}

	_class.prototype.action = function() {
		with (this) {
			var actionList = getActionList();
			actionList.shuffle();
			for (var i=0; i<actionList.length; i++) {
				if (actionList[i].isActive) actionList[i].action();
			}
		}
	}
	
	_class.prototype.moveTestOnMap = function(tbx,tby,tbw,tbh) {
		if (tbx<0 || tby<1 || tbx+tbw>this.bw || tby+tbh>this.bh) {
			return false;
		}

		var map = this.map;
		var tw = map.tileWidth;
		var th = map.tileHeight;
		for (var xx=tbx;xx<tbx+tbw;xx++) {
			for (var yy=tby;yy<tby+tbh;yy++) {
				if (map.hitTest(xx*tw,yy*th)) {
					return false;
				}
			}
		}
		//console.log("map-true:",tbx,tby,tbw,tbh,xx,yy,xx*tw,yy*th);
		return true;
	}

	_class.prototype.moveTestConflict = function(me,tbx,tby,tbw,tbh) {
		var list = this.getConflicters();
		for (var i=0; i<list.length; i++) {
			if (list[i] != me) {
				if (list[i].isConflict(tbx,tby,tbw,tbh)) {
					// 既に被っている場合は衝突にしない。
					if (!list[i].isConflictEntity(me)) return false;
				}
			}
		}
		return true;
	}
	_class.prototype.moveTestConflictEnemy = function(me,tbx,tby,tbw,tbh) {
		return !this.getConflictEnemy(me,tbx,tby,tbw,tbh);
	}
	_class.prototype.getConflictEnemy = function(me,tbx,tby,tbw,tbh) {
		var list = this.getConflicters();
		for (var i=0; i<list.length; i++) {
			var tgt = list[i];
			if (tgt.team != me.team) {
				if (tgt.isConflict(tbx,tby,tbw,tbh)) {
					if (!tgt.isConflictEntity(me)) return tgt;
				}
			}
		}
		return null;
	}
	
});
