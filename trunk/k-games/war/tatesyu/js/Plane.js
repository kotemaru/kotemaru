function Plane(game){this.initialize.apply(this, arguments)};
(function(Class, Super) {
	Util.extend(Class, Super);

	const DETONOTE = Chip.add("detonate", "img/detonate.png");
	const SMOKE = Chip.add("smoke", "img/smoke.png");
	const SPEED = 20;
	const LEN = 13;
	
	Class.prototype.isEnemyPlane = true;

	Class.prototype.initialize = function(game) {
		Super.prototype.initialize.apply(this, arguments);
		Util.copy(this,{
			layer:2, chip:null, x:0, y:0, vx:0,vy:0, rw:12, grad: 0, tick:false, hp:10, count:0
		});
	};
	
	Class.prototype.isActive = function() {
		with (this) {
			return (game.mapH-game.clipH-game.count*2 <= y);
		}
	}

	Class.prototype.action = function() {
	};
	
	Class.prototype.paint = function(game) {
		Super.prototype.paint.apply(this, arguments);
		with (this) {
			var cc = game.count%10;
			if (hp<=4 && cc>5) game.drawImage(SMOKE, x-5, y-32);
			if (hp<=1 && cc<5) game.drawImage(SMOKE, x-5, y-48);
			if (hp<=0) {
				game.drawImage(DETONOTE, x-16, y-16);
				game.delEntity(this);
				game.score += point;
			}
		}
	};
	
	Class.prototype.hit = function(waigh){
		this.hp -= waigh;
	}
	
	//------------------------------------------------------
	Class.prototype.furafura1 = function() {
		with (this) {
			if( count >= 220 ){ game.delEntity(this); return;}
			if (game.myShip.isHitPlane(this)) {
				hit(100);
				game.myShip.hit(5);
			}
			
			y += 2;
			if( count % 30 == 0 ){
				this.vx = 2;
				EnemyBullet.shootT(this, game.myShip);
			}
			if( count % 60 == 0 ){
				this.vx = -2;
			}
			x += vx;
			count++;
		}
	};

	
	Class.prototype.dive = function() {
		with (this) {
			if( count >= 120 ){  game.delEntity(this); return;}
			if (game.myShip.isHitPlane(this)) {
				hit(100);
				game.myShip.hit(5);
			}
			
			y += 8;
			
			if( count % 5 == 0 ){
				EnemyBullet.shootXY(this, 0, SPEED, 0, LEN);
			}
			count++;
		}
	}
	
	Class.prototype.stalk = function() {
		with (this) {
			const tx = game.myShip.x ;
			//const ty = game.myShip.y ;
			if (game.myShip.isHitPlane(this)) {
				hit(100);
				game.myShip.hit(5);
			}

			if( count >= 120 ){  game.delEntity(this); return;}
			if( count == 0 ) this.mode = 0;
			y += 3;
			if (mode == 0) {
				x += (x<tx ? 3 : -3);
				if (Math.abs(x-tx)<5) {
					mode = 1;
					this.shootCount = 6*4;
				}
			} else {
				x -= (x<tx ? 3 : -3);
				if (this.shootCount-->0 && count%4 == 0) {
					EnemyBullet.shootXY(this, 0, SPEED, 0, LEN);
				}
			}
			count++;
		}
	}

	
})(Plane, Actor);

