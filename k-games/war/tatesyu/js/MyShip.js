function MyShip(game){this.initialize.apply(this, arguments)};
(function(Class, Super) {
	Util.extend(Class, Super);
	Util.addClass(Class);

	const DETONOTE = Chip.add("detonate", "img/detonate.png");
	const SMOKE = Chip.add("smoke", "img/smoke.png");
	const AIM = Chip.add("aim", "img/aim.png");
	const CHIPS = [];
	(function() {
		const names = ["f_zeroL2","f_zeroL1","f_zero","f_zeroR1","f_zeroR2"];
		for (var i=0; i<names.length; i++) {
			CHIPS.push(Chip.add(names[i], "img/"+names[i]+".png"));
		}
	})();

	Class.prototype.isMyShip = true;

	Class.prototype.initialize = function(game) {
		Super.prototype.initialize.apply(this, arguments);

		Util.copy(this,{
			layer:2, chip:CHIPS[2], x:160, y:0, grad: 0, tick:false, 
			count20mm:0, hp: 10
		});
	};
	Class.prototype.isActive = function() {
		return true;
	}

	Class.prototype.action = function() {
		with (this) {
			if (hp<=0) {
				if (!game.isGameOver) {
					Sound.play("boon");
					Sound.stop("engine");
				}
				game.isGameOver = true;
				game.setLifeSpan(100);
				y += 6;
				return;
			}

			y -= game.scroll;
			tick = !tick;
			if (Input.left) {
				if (x>16)	x += grad*4;
				if (grad > -2) grad--;
			} else if (Input.right) {
				if (x<304)	x += grad*4;
				if (grad < 2) grad++;
			} else {
				if (grad > 0) grad--;
				if (grad < 0) grad++;
			}
			
			if (Input.up) {
				if (y>game.clipY+32)	y -= 5;
			} else if (Input.down) {
				if (y<game.clipY+game.clipH-16) y += 6 ;
			}

			autoGroundTarget();
			if (Input.btn2) {
				MyBulletG.getInstance(game).init(
						x+(game.count % 2 == 0?-3:3),y, 
						grad*1, -20, 
						grad*1, -8);
				if (game.count % 2 == 0 && count20mm-->0) {
					MyBullet.getInstance(game).init(
						x+(game.count % 4 == 0?10:-10),y-10, 
						grad*8, -20,
						grad*1, -12, 8);
				}
			} else {// if (btn1) {
				MyBullet.getInstance(game).init(
						x+(game.count % 2 == 0?-3:3),y-10, 
						grad*8, -40, 
						grad*1, -10, 2);
				if (game.count % 2 == 0 && count20mm-->0) {
					MyBullet.getInstance(game).init(
						x+(game.count % 4 == 0?10:-10),y-8, 
						grad*8, -40,
						grad*1, -15, 8);
				}
			}

			chip = CHIPS[grad+2];
		}
	};

	var aimCoor = {x:0,y:0};
	Class.prototype.autoGroundTarget = function() {
		with (this) {
			aimCoor.x = x+grad*4;
			aimCoor.y = y-80;

			Input.btn2 = false;
			const grounds = this.game.enemyGrounds;
			for (var id in grounds) {
				var g = grounds[id];
				if (g.isClash) continue;
				var coor = g.isHit(aimCoor);
				if (coor != false) {
					Input.btn2 = true;
					break;
				}
			}
		}
	}
	
	Class.prototype.paint = function(game) {
		Super.prototype.paint.apply(this, arguments);
		with (this) {
			game.drawImage(AIM, x-8+grad*4, y-8-80);
			var cc = game.count % 10;
			if (hp<=6 && cc>5) game.drawImage(SMOKE, x-5, y+16);
			if (hp<=3 && cc<5) game.drawImage(SMOKE, x-5, y+32);
			if (hp<=0) game.drawImage(DETONOTE, x-16, y-16);
		}
	};

	Class.prototype.isHit = function(bullet) {
		with (this) {
			const x0 = x;
			const x1 = x - 12;
			const x2 = x + 12;
			const y1 = y - 15;
			const y2 = y + 10;
			const yy = y - 4;
		}
		with (bullet) {
			if (vy != 0 && (y <= yy && yy <= y+vy || y >= yy && yy >= y+vy) ) {
				const X = (yy-y) * (vx/vy) + x;
				if (x1 <= X && X <= x2) return {x:X, y:yy};
			}
			if (vx != 0 && (x <= x0 && x0 <= x+vx || x >= x0 && x0 >= x+vx) ) {
				const Y = (x0-x) * (vy/vx) + y;
				if (y1 <= Y && Y <= y2) return {x:x0, y:Y};
			}
			return false;
		}
	};
	Class.prototype.isHitPlane = function(plane) {
		with (this) {
			const rw = plane.rw;
			const x1 = x - 12 - rw;
			const x2 = x + 12 + rw;
			const y1 = y - 15 - rw;
			const y2 = y + 10 + rw;
		}
		with (plane) {
			return (x1 <= x && x <= x2 && y1 <= y && y <= y2);
		}
	};
	Class.prototype.hit = function(waigh){
		if (!Config.muteki) this.hp -= waigh;
		Sound.play("kan");
	}
	
})(MyShip, Actor);

