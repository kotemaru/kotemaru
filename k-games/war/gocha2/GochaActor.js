/**
@author kotemaru@kotemaru.org
*/



enchant.gocha.GochaActor = org.kotemaru.Class(enchant.gocha.GochaEntity, function(_class, _super){
	var Util = org.kotemaru.Util;
	const IS_DEBUG = false;
	
	const BOX_MARK = [
	[
	    enchant.gocha.Chip.create("img/boxmark.png",0,0,6,6),
		enchant.gocha.Chip.create("img/boxmark.png",16-6,16-6,6,6),
	],[
	    enchant.gocha.Chip.create("img/boxmark.png",16,0,6,6),
		enchant.gocha.Chip.create("img/boxmark.png",32-6,16-6,6,6),
	],[
		enchant.gocha.Chip.create("img/boxmark.png",0,16,6,6),
		enchant.gocha.Chip.create("img/boxmark.png",16-6,32-6,6,6),
	]
	];
	const DIR_INFO = {
			U:{b:'D', l:'L', r:'R', x: 0, y:-1},
			D:{b:'U', l:'R', r:'L', x: 0, y: 1}, 
			R:{b:'L', l:'U', r:'D', x: 1, y: 0},
			L:{b:'R', l:'D', r:'U', x:-1, y: 0},
	};
	_class.DIR_INFO = DIR_INFO;
	
	const DEFENSE_RATE = {
			//Back 			Front     Side
			"UU":0.25,		"UD":4.0, "UR":0.5, "UL":0.5,
			"DD":0.25,		"DU":4.0, "DR":0.5, "DL":0.5,
			"RR":0.25,		"RL":4.0, "RU":0.5, "RD":0.5,
			"LL":0.25,		"LR":4.0, "LU":0.5, "LD":0.5,
	};
	
	_class.prototype.isActorClass = true;
	
	
	_class.prototype.initialize = function(chip, opts) {
		_super.prototype.initialize.call(this, {width:chip.w, height:chip.h});
		this.chip = chip;
		this.surface = new Surface(chip.w, chip.h);
		this.image = this.surface;
		this.execQueue = new ExecQueue(this);
		this.ai = opts.isShooter ? new GochaAiShooter(this) : new GochaAiFighter(this);
	
		Util.copy(this,{
			team: 0,
			dir  : "D",
			isDead : false,
			isEscape : false,
			isAttack : false,
			isYarare : false,
			platoon: null,
			primDir: null,
			primFrame: 0,
			target: null,
			postPosition: null,
			bullet: null,
			
			hp : 300,	// hit point
			rp : 10,	// recover point
			ar : 50,	// attack rate
			ap : 50,	// attack point
			dr : 50,	// defense rate
			dp : 40,	// defense point

			mp : 100,
			mdr : 50,
			mdp : 40,
		});
		Util.copy(this, opts);
		this.max_hp = this.hp;
		this.max_mp = this.mp;

		this.lifeMark = (this.team==0?new enchant.gocha.GochaLifeMark(this):null);
	}
	_class.prototype.draw = function() {
		with (this) {
			var ctx = surface.context;
			surface.clear();
			if (primDir) {
				chip.draw(ctx, primDir,primFrame);
				if (!isDead) primDir = null;
			} else {
				chip.draw(ctx, dir,age);
			}
			if (team == 0) lifeMark.draw();

			if (parent.leader == this) {
				BOX_MARK[team][0].draw(ctx, 0,0);
				BOX_MARK[team][1].draw(ctx, width-6,height-6);
			}
			
			if (IS_DEBUG) {
				ctx.font = "8px";
				ctx.strokeStyle = "white";
				ctx.strokeText(""+uid,0,8);
			}
		
			_super.prototype.draw.call(this);
		}
	};
	_class.prototype.setParent = function(group){
		_super.prototype.setParent.call(this, group);
		if (this.gochaMain == null) return;
		this.bh = this.height / this.gochaMain.map.tileHeight -1;
	}


	//------------------------------------------------------------
	// actor basic action
	
	_class.prototype.dead = function() {
		const self = this;
		this.isDead = true;
		this.execQueue.after(1, function(){
			self.primDir = "d";
			self.primFrame = 0;
		});
		this.execQueue.after(5,function(){
			self.primDir = "d";
			self.primFrame = 1;
		});
		this.execQueue.after(9,function(){
			self.dispose();

			// 死亡イベント
			var ev = new enchant.Event("gocha.dead");
			ev.actor = self;
			ev.isLeader = (self == self.parent.leader);
			self.dispatchEvent(ev);
			self.parent.dispatchEvent(ev);
			self.gochaMain.dispatchEvent(ev);
		});
	}
	
	_class.prototype.yarare = function() {
		this.primDir = "y";
		this.primFrame = this.age;
		this.isYarare = true;
	}
	
	_class.prototype.recover = function(rate) {
		if (rate>=1.0) {
			this.primDir = "r";
			this.primFrame = this.age;
		}
		this.hp += this.rp * rate;
		if (this.hp>this.max_hp) this.hp = this.max_hp;
	}
	_class.prototype.attack = function(tgt) {
		if (this.isYarare) return;
		const self = this;
		var dir = "a"+this.dir;
		self.primDir = dir;
		self.primFrame = this.age;
		if (tgt.gotAttack) {
			tgt.gotAttack(self);
		}
	}

	_class.prototype.gotAttack = function(attecker) {
		with (this) {
			if (isDead) return;
			var defenceRate = dr * DEFENSE_RATE[dir+attecker.dir];
			var attackRate = attecker.ar / (attecker.ar + defenceRate);
			var hit = isRandom(attackRate);
			if (hit) {
				var damage = attecker.ap * (attecker.ap / (attecker.ap + dp));
				damage *= Math.random() + 0.3; // バラツキ

				hp -= damage;
				yarare();
				if (hp/max_hp < 0.3) isEscape = true;
				if (hp <= 0) {
					hp = 0;
					dead();
				}
				
				var knockback = Math.ceil(damage/dp);
				for (var i=0; i<knockback; i++) moveTest(attecker.dir, true);
			}
			setTarget(attecker);
			dir = getAdjustDir(target.bx-bx, target.by-by);
		}
	}

	_class.prototype.shoot = function(tgt) {
		var bullet = new this.bullet(this, tgt);
		if (this.mp < bullet.mp) return false;
		
		this.gochaMain.addGocha(bullet);
		this.mp -= bullet.mp;
		return true;
	}

	_class.prototype.gotAttackMagic = function(attecker) {
		with (this) {
			if (isDead) return;
			var defenceRate = mdr * 1.0;
			var attackRate = attecker.ar / (attecker.ar + defenceRate);

			var hit = isRandom(attackRate);
			if (hit) {
				var xap = attecker.ap * Math.random() * 1.5; // 1.5はバラツキ
				var damage = (xap < mdp) ? 0 : (xap - mdp);
				hp -= damage;
				yarare();
				if (hp/max_hp < 0.3) isEscape = true;
				if (hp <= 0) {
					hp = 0;
					dead();
				}
				moveTest(attecker.dir, true);
				moveTest(attecker.dir, true);
			}
		}
	}

	_class.prototype.helpMe = function() {
		/*
		for (var i in this.group.actors) {
			var tgt = this.group.actors[i];
			if (!tgt.isDead && tgt != this && tgt.team == this.team) {
				tgt.gotHelp(this);
			}
		}*/
	}

	_class.prototype.gotHelp = function(friend) {
		if (this.target == null) this.setTarget(friend.target);
	}
	
	_class.prototype.command = function(team, cmd, opts){
		if (this.team != team) return;

		if (cmd == "attack" || cmd == "defense") {
			this.isAttack = (cmd=="attack");
			this.isEscape = false;
		} else if (cmd == "escape") {
			this.isEscape = true;
		}
	}

	//-------------------------------------------------------------
	// think algorithm
	_class.prototype.action = function() {
		this.doThink();
		this.execQueue.exec();
		this.bx = Math.floor(this.bx);
		this.by = Math.floor(this.by);
	}
	
	_class.prototype.doThink = function() {
		if (this.isDead) return;
		if (this.isEscape) {
			this.ai.thinkEscape();
		} else if (this.isAttack) {
			this.ai.thinkAttack();
		} else {
			this.ai.thinkDefense();
		}
		this.isYarare = false;
		if (this.mp<this.max_mp) {
			this.mp += 10;
		}
	}
	
	//-------------------------------------------------------------
	// helper
	_class.prototype.moveTest = function(testDir, isAuto) {
		with (this) {
			var info = DIR_INFO[testDir];
			var xx = this.bx + info.x;
			var yy = this.by + info.y;
			
			// マップ外 or 障害物
			if (!gochaMain.moveTestOnMap(xx,yy,bw,bh)) return false;

			// 他のキャラ
			if (!isAttack) {
				// 逃げの時は味方はすり抜けられる。
				if (!gochaMain.moveTestConflictEnemy(this,xx,yy,bw,bh)) return false;
			} else {
				if (!gochaMain.moveTestConflict(this,xx,yy,bw,bh)) return false;
			}
			
			// ALL OK
			if (isAuto) {
				dir = testDir;
				bx = xx;
				by = yy;
			}
			return true;
		}
	}
	
	_class.prototype.getEntityDir = function(entity, defo) {
		var rbx = entity.bx - this.bx;
		var rby = entity.by - this.by;
		return this.getAdjustDir(rbx, rby, defo);
	}
	_class.prototype.getAdjustDir = function(rbx, rby, defo) {
		var rbx0 = Math.abs(rbx);
		var rby0 = Math.abs(rby);
		if (defo != null && rbx0 == 0 && rby0 == 0) return defo;
			
		if (rbx0 > rby0) {
			return (rbx > 0) ? "R" : "L";
		} else if (rbx0 < rby0) {
			return (rby > 0) ? "D" : "U";
		} else if (this.age%2 == 0) {
			return (rbx > 0) ? "R" : "L";
		}
		return (rby > 0) ? "D" : "U";
	}

	_class.prototype.getPostPosition = function() {
		const leader = this.parent.leader;
		if (this == leader && this.targetPosition) {
			return {
				bx:Math.floor(this.targetPosition.x),
				by:Math.floor(this.targetPosition.y)
			};
		} else if (this.postPosition) {
			return {
				bx:Math.floor(leader.bx+this.postPosition.x), 
				by:Math.floor(leader.by+this.postPosition.y)
			};
		} else {
			return this;
		}
	}

	_class.prototype.setTarget = function(coor) {
		this.target = coor;
	}
	function isRandom(rate) {
		return rate>Math.random();
	}

});
