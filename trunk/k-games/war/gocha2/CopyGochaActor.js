/**
@author kotemaru@kotemaru.org
*/



enchant.gocha.GochaActor = org.kotemaru.Class(enchant.gocha.GochaEntity, function(_class, _super){
	var Util = org.kotemaru.Util;

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
		
		Util.copy(this,{
			team: 0,
			dir  : "D",
			isDead : false,
			isEscape : false,
			isYarare : false,
			platoon: null,
			primDir: null,
			primFrame: 0,
			target: null,
			
			max_hp : 300,	// max hit point
			hp : 300,	// hit point
			rp : 10,	// recover point
			ar : 50,	// attack rate
			ap : 50,	// attack point
			dr : 50,	// defense rate
			dp : 40,	// defense point
			max_mp : 100,
			mp : 100,
			mdr : 50,
			mdp : 40,
		});
		Util.copy(this, opts);
		
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
				chip.draw(ctx, dir,frame);
			}
			if (lifeMark) lifeMark.draw();
			
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
		});
	}
	
	_class.prototype.yarare = function() {
		this.primDir = "y";
		this.primFrame = this.age;
		this.isYarare = true;
	}
	
	_class.prototype.recover = function() {
		this.primDir = "r";
		this.primFrame = this.age;
		this.hp += this.rp;
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
				var xap = attecker.ap * Math.random() * 1.5; // 1.5はバラツキ
				var damage = (xap < dp) ? 0 : (xap - dp);
				hp -= damage;
				yarare();
				if (hp/max_hp < 0.3) isEscape = true;
				if (hp <= 0) {
					hp = 0;
					dead();
				}
				//if (damage>dp*0.25) moveTest(attecker.dir, true);
				//if (damage>dp*0.5) moveTest(attecker.dir, true);
			}
			setTarget(attecker);
			dir = getAdjustDir(target.bx-bx, target.by-by);
		}
	}

	_class.prototype.shoot = function(tgt) {
		if (this.mp < GochaBulletFire.MP) return false;

		var bullet = new enchant.gocha.GochaBulletFire(this, tgt);
		this.gochaMain.addGocha(bullet);
		this.mp -= 50;
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
		for (var i in this.group.actors) {
			var tgt = this.group.actors[i];
			if (!tgt.isDead && tgt != this && tgt.team == this.team) {
				tgt.gotHelp(this);
			}
		}
	}

	_class.prototype.gotHelp = function(friend) {
		if (this.target == null) this.setTarget(friend.target);
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
			this.thinkEscape();
		} else if (this.isShooter) {
			this.thinkShooter();
		} else {
			this.thinkFighter();
		}
		this.isYarare = false;
		if (this.mp<this.max_mp) {
			this.mp += 10;
		}
	}
	
	_class.prototype.thinkFighter = function() {
		with (this) {
			// 敵がいなければ敵を探す。たまに別の敵を探してみる。
			if (target == null || isRandom(0.2)) {
				setTarget(gochaMain.searchEnemy(this));
			}
			if (target == null) return;
			if (target.isDead) {
				target = null;
				return;
			}

			
			var rbx = target.bx - bx;
			var rby = target.by - by;
			// 接敵していれば攻撃する。
			if (isTouchEntity(target)) {
				dir = getAdjustDir(rbx, rby);
				this.attack(target);
				return;
			}


			if (rbx ==0 && rby == 0) return;

			var backDir = DIR_INFO[dir].b;
			// 敵に無かって移動する
			var tgtDir = getAdjustDir(rbx, rby);
			if (tgtDir != backDir && moveTest(tgtDir, true)) return;
			// 敵に無かって移動できなければ一旦その敵は諦める。
			target = null;
			
			// 現在の進行方向に進めればとりあえず進む。
			var info = DIR_INFO[tgtDir];
			if (dir==info.l && moveTest(info.l, true)) return;
			if (dir==info.r && moveTest(info.r, true)) return;

			// 現在の進行方向に進めなければ左右に曲がる。
			var info = DIR_INFO[dir];
			if (isRandom(0.5)) {
				if (info.l != backDir && moveTest(info.l, true)) return;
				if (info.r != backDir && moveTest(info.r, true)) return;
			} else {
				if (info.r != backDir && moveTest(info.r, true)) return;
				if (info.l != backDir && moveTest(info.l, true)) return;
			}

			// 左右も進め無い場合は下がる。
			if (moveTest(info.b, true)) return;
		}
	}
	
	_class.prototype.thinkShooter = function() {
		with (this) {
			// 敵がいなければ敵を探す。たまに別の敵を探してみる。
			if (target == null || isRandom(0.2)) {
				setTarget(gochaMain.searchEnemy(this));
			}
			if (target == null) return;
			if (target.isDead) {
				target = null;
				return;
			}

			var rbx = target.bx - bx;
			var rby = target.by - by;
			
			// 敵が近い場合は遠ざかる
			var len = getDistance(target);
			if (len<3) {
				var escDir = getAdjustDir(-rbx, -rby);
				if (moveTest(escDir, true)) return true;
				escDir = getAdjustDir(-rby, -rbx);
				if (moveTest(escDir, true)) return true;
			}
			// 敵が多い場合は近付く
			if (len>7) {
				var tgtDir = getAdjustDir(rbx, rby);
				if (moveTest(tgtDir, true)) return true;
				tgtDir = getAdjustDir(rby, rbx);
				if (moveTest(tgtDir, true)) return true;
			}

			// 適当な距離なら攻撃する。
			dir = getAdjustDir(rbx, rby);
			shoot(target);
		}
	}
	
	_class.prototype.thinkEscape = function() {
		with (this) {
			var enemyInfo = gochaMain.searchEnemy(this,true);
			var tgt = enemyInfo.enemy;
			if (tgt == null) return;
			setTarget(tgt);
			var rbx = tgt.bx - bx;
			var rby = tgt.by - by;
			
			// 敵が近くにいれば遠ざかる
			if (enemyInfo.len < 3) {
				var escDir = getAdjustDir(-rbx, -rby);
				if (moveTest(escDir, true)) return true;
				escDir = getAdjustDir(-rby, -rbx);
				if (moveTest(escDir, true)) return true;
			}
			
			// 逃げれない場合は反撃する
			if (isTouchEntity(tgt)) {
				dir = getAdjustDir(rbx, rby);
				this.attack(target);
				this.helpMe(); // 仲間に助けを求める。
				return true;
			}
				
			{
				// 敵がいなければ回復する。
				recover();
				// 回復したら戦線復帰する。
				if (hp/max_hp > 0.5) isEscape = false;
			}
			return false;
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
			if (isEscape) {
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
		var rbx = entity.bx - bx;
		var rby = entity.by - by;
		return getAdjustDir(rbx, rby, defo);
	}
	_class.prototype.getAdjustDir = function(rbx, rby, defo) {
		var rbx0 = Math.abs(rbx);
		var rby0 = Math.abs(rby);
		if (defo && rbx0 == 0 && rbx0 == 0) return defo;
			
		if (rbx0 > rby0) {
			return (rbx > 0) ? "R" : "L";
		} else if (rbx0 < rby0) {
			return (rby > 0) ? "D" : "U";
		} else if (this.flip == 0) {
			return (rbx > 0) ? "R" : "L";
		}
		return (rby > 0) ? "D" : "U";
	}


	_class.prototype.setTarget = function(coor) {
		this.target = coor;
	}
	function isRandom(rate) {
		return rate>Math.random();
	}

});
