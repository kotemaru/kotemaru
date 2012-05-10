
function Actor(type, opts) {
	this.div = document.createElement("div");
	this.lifeBar = document.createElement("div");
	this.img = document.createElement("img");
	this.img.src = "images/char-"+type+".png";
	this.div.appendChild(this.img);
	this.div.className = "Actor";
	this.div.actor = this;
	this.div.appendChild(this.lifeBar);
	this.lifeBar.className = "ActorLifeBar";

	this.team = "???";
	this.dir  = "D";
	this.flip = 0;
	this.isYarare = false;
	this.isRecover = false;
	this.isDead = false;
	this.isEscape = false;
	this.deadCount = 0;
	this.moveReserve = [];
	this.tgtDir = null;
	this.tgtDir2 = null;
	this.rvCount = 0;

	this.x = 0;
	this.y = 0;
	this.w = 2;
	this.h = 1;

	this.max_hp = 300;
	this.hp = 300;
	this.rp = 10;
	this.ar = 0.5;
	this.ap = 50;
	this.car = 0.03;
	this.dr = 0.5;
	this.dp = 40;

	for (var key in opts) this[key] = opts[key];
}

(function(Class){with(Class){

	Class.IMG_POS = {
		D: [{x: "-0px", y:"-32px"},{x:"-32px", y:"-32px"}], // front
		U: [{x: "-0px", y: "-0px"},{x:"-32px", y: "-0px"}], // back
		L: [{x:"-64px", y: "-0px"},{x:"-96px", y: "-0px"}], // left
		R: [{x:"-64px", y:"-32px"},{x:"-96px", y:"-32px"}], // right
		y: [{x: "-0px", y:"-64px"},{x:"-32px", y:"-64px"}], // yarare
		d: [{x:"-64px", y:"-64px"},{x:"-96px", y:"-64px"}]  // dead
	};
	Class.DIR_INFO = {
		U:{b:'D', l:'L', r:'R', x:0,y:-1},
		D:{b:'U', l:'R', r:'L', x:0,y:1}, 
		R:{b:'L', l:'U', r:'D', x:1,y:0},
		L:{b:'R', l:'D', r:'U', x:-1,y:0}
	};
	Class.ATTACK_POS_RATE = {
		//Back      Front     Side
		"UU":4.0,		"UD":1.0, "UR":1.5, "UL":1.5,
		"DD":4.0,		"DU":1.0, "DR":1.5, "DL":1.5,
		"RR":4.0,		"RL":1.0, "RU":1.5, "RD":1.5,
		"LL":4.0,		"LR":1.0, "LU":1.5, "LD":1.5
	};


	prototype.deploy = function(parent, actors) {
		this.actors = actors;
		actors.push(this);
		parent.appendChild(this.div);
	}
	prototype.dispose = function(parent) {
		this.isDead = true;
		this.div.style.display = "none";
		for (var i=0; i<this.actors.length; i++) {
			if (this == this.actors[i]) {
				this.actors.splice(i,1);
			}
		}
	}

	prototype.action = function() {
		with (this) {
			if (isDead) {
				if (deadCount <= 0) dispose();
				actionFinish("d", (--deadCount > 4) ? 0 : 1);
			} else if (isYarare) {
				isYarare = false;
				actionFinish("y", flip);
			} else {
				if (isEscape && escapeAction()) {
					// nop.
				} else {
					defaultAction();
				}
				if (isRecover) {
					isRecover = false;
					actionFinish("d", 0);
				} else {
					actionFinish(dir, flip);
				}
			}
		}
	}
	prototype.actionFinish = function(curDir, curFlip) {
		with (this) {
			x = Math.floor(x);
			y = Math.floor(y);
			div.style.left = (x*16)+"px";
			div.style.top  = (y*16)+"px";
			div.style.zIndex  = 1000+y;

			var coor = Class.IMG_POS[curDir][curFlip];
			var css = img.style;
			css.marginLeft = coor.x;
			css.marginTop  = coor.y;

			flip = 1-flip;
		}
	}

	prototype.defaultAction = function() {
		with (this) {

			if (this.target == null || isRandom(0.2)) {
				setTarget(searchEnemy());
				tgtDir = null;
			}

			if (target == null) return;
			var rx = target.x - x;
			var ry = target.y - y;

			if (isTouch(target)) {
				this.attack(target);
				tgtDir = null;
				dir = getAdjustDir(rx, ry);
				return;
			}

			if (rx ==0 && ry == 0) return;

			if (tgtDir == null) {
				tgtDir = getAdjustDir(rx, ry);
			}
			if (moveTest(tgtDir, true)) {
				tgtDir2 = null;
				if (dir == 'U' || dir == 'D') {
					if (ry == 0) tgtDir = null;
				} else {
					if (rx == 0) tgtDir = null;
				}
				return ;
			}
			if (tgtDir2 == null) {
				var info = Class.DIR_INFO[tgtDir];
				tgtDir2 = (flip==0) ? info.l : info.r;
			}
			if (moveTest(tgtDir2,true)) return;
			tgtDir2 = null;
		}
	}
	prototype.escapeAction = function() {
		with (this) {
			isRecover = false;
			var enemyInfo = searchEnemy(true);
			if (enemyInfo.len < 5) {
				var tgt = enemyInfo.tgt;
				var rx = tgt.x - x;
				var ry = tgt.y - y;
				var escDir = getAdjustDir(-rx, -ry);
				if (moveTest(escDir, true)) return true;
				escDir = getAdjustDir(-ry, -rx);
				if (moveTest(escDir, true)) return true;
			} else {
				addHp(rp);
				isRecover = true;
				if (hp/max_hp > 0.5) isEscape = false;
				return true;
			}
			return false;
		}
	}

	prototype.getAdjustDir = function(rx, ry) {
		var rx0 = Math.abs(rx);
		var ry0 = Math.abs(ry);
		if (rx0 > ry0) {
			return (rx > 0) ? "R" : "L";
		} else if (rx0 < ry0) {
			return (ry > 0) ? "D" : "U";
		} else if (this.flip == 0) {
			return (rx > 0) ? "R" : "L";
		}
		return (ry > 0) ? "D" : "U";
	}


	prototype.moveTest = function(testDir, isAuto) {
		with (this) {
			var info = Class.DIR_INFO[testDir];
			x += info.x;
			y += info.y;

			var conflict = isConflictAll();
			if (conflict) {
				x -= info.x;
				y -= info.y;
				return (conflict.team != team);
			}
			if (isAuto) this.dir = testDir;
			return true;
		}
	}

	prototype.searchEnemy = function(mode) {
		var min = 1000000;
		var minTgt = null;

		for (var i=0; i<this.actors.length; i++) {
			var tgt = this.actors[i];
			if (tgt.team != this.team) {
				var rx = Math.abs(tgt.x - this.x);
				var ry = Math.abs(tgt.y - this.y);
				var len = Math.sqrt(rx*rx + ry*ry);
				if (len < min) {
					min = len;
					minTgt = tgt;
				}
			}
		}
		if (mode) return {len:min, tgt:minTgt};
		return minTgt;
	}

	prototype.getTouchEnemy = function() {
		for (var i=0; i<this.actors.length; i++) {
			var tgt = this.actors[i];
			if (tgt.team != this.team) {
				if (this.isTouch(tgt)) return tgt;
			}
		}
		return null;
	}
	prototype.isTouch = function(tgt) {
		if (tgt == null) return false;
		var rx = this.isTouchX(tgt);
		if (rx > 0) return false;
		var ry = this.isTouchY(tgt);
		if (ry > 0) return false;
		return (rx < 0 || ry < 0);
	}

	prototype.isTouchX = function(tgt) {
		if (this.x <= tgt.x) {
			return tgt.x - (this.x+this.w);
		} else {
			return this.x - (tgt.x+tgt.w);
		}
	}

	prototype.isTouchY = function(tgt) {
		if (this.y <= tgt.y) {
			return tgt.y - (this.y+this.h);
		} else {
			return this.y - (tgt.y+tgt.h);
		}
	}

	prototype.isConflictAll = function() {
		if (this.x<0 || this.y<0 
			||this.x+this.w>=32||this.y+this.h>=32
		) {
			return this;
		}

		for (var i=0; i<this.actors.length; i++) {
			var tgt = this.actors[i];
			if (tgt != this) {
				if (this.isConflict(tgt)) return tgt;
			}
		}
		return null;
	}

	prototype.isConflict = function(tgt) {
		if (tgt == null) return false;
		var rx = this.isConflictX(tgt);
		if (rx > 0) return false;
		var ry = this.isConflictY(tgt);
		if (ry > 0) return false;
		return (rx <= 0 || ry <= 0);
	}
	prototype.isConflictX = function(tgt) {
		if (this.x <= tgt.x) {
			return tgt.x - (this.x+this.w-1);
		} else {
			return this.x - (tgt.x+tgt.w-1);
		}
	}

	prototype.isConflictY = function(tgt) {
		if (this.y <= tgt.y) {
			return tgt.y - (this.y+this.h-1);
		} else {
			return this.y - (tgt.y+tgt.h-1);
		}
	}

	prototype.attack = function(tgt) {
		if (tgt.gotAttack && tgt.team != this.team) {
			tgt.gotAttack(this);
		}
	}

	prototype.gotAttack = function(attecker) {
		with (this) {
			if (isDead) return;
			var defPosRate = Class.ATTACK_POS_RATE[dir+attecker.dir];
			var rate = dr * attecker.ar * defPosRate;
			var isYarare = isRandom(rate);
			if (isYarare) {
				var isCrHit = isRandom(attecker.car);
				var damage = (attecker.ap < dp) ? 0 : -(attecker.ap - dp);
				addHp(damage);
				if (hp/max_hp < 0.3) isEscape = true;

				if (isCrHit)	moveTest(attecker.dir);

				if (hp <= 0) {
					hp = 0;	addHp(0);
					isDead = true;
					deadCount = 10;
				}
			}
			setTarget(attecker);
		}
	}

	prototype.addHp = function(point) {
		with (this) {
			hp = hp + point;
			lifeBar.style.width = Math.floor(32*hp/max_hp)+"px";
		}
	}

	prototype.setTarget = function(coor) {
		this.target = coor;
		this.tgtDir = null;
		this.tgtDir2 = null;
	}
	prototype.isRandom = function(rate) {
		return rate>Math.random();
	}

}})(Actor);
