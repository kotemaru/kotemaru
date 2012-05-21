
function Bridge(game){this.initialize.apply(this, arguments)};
(function(Class, Super) {
	Util.extend(Class, Super);

	const CHIP = Chip.add("bridge", "img/bridge.png");
	const SPEED = 20;
	const LEN = 13;
	
	Class.prototype.isEnemyPlane = true;

	Class.prototype.initialize = function(game) {
		Super.prototype.initialize.apply(this, arguments);
		Util.copy(this,{
			layer:1, chip:CHIP, x:0, y:0, vx:0,vy:0, hp:500, hpMax:500, count:0
		});
	};
	Class.prototype.isActive = function() {
		with (this) {
			return (game.clipY <= y);
		}
	}

	Class.prototype.action = function() {
	};
	
	Class.prototype.isHit = function(bullet) {
		with (this) {
			const x0 = x;
			const x1 = x - 16;
			const x2 = x + 16;
			const y1 = y - 32;
			const y2 = y + 32;
			const yy = y + 4;
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
	Class.prototype.hit = function(waigh){
		this.hp -= waigh;
	}
	
	//-------------------------------------------------------------------------
	Class.prototype.boss = function() {
		with (this) {
			if (hp<=0) {
				game.scroll = 2;
				game.delEntity(this);
				game.boss = null;
				return;
			}
			const cc = count % 200;
			if( 40 < cc && cc < 60 && cc%2 == 0) {
			    EnemyBullet.shootT(this, game.myShip );
			}
			if(120 < cc && cc < 140){
				var c3 = cc % 4;
				if (c3 == 0) EnemyBullet.getInstance(game).init(x+5,y+30, 0,SPEED, 0,LEN);
				if (c3 == 2) EnemyBullet.getInstance(game).init(x-5,y+30, 0,SPEED, 0,LEN);
				//if (c3 == 2) shootXY(this,  4, SPEED, 1, LEN);
				//if (c3 == 4) shootXY(this, -4, SPEED, 1, LEN);
			}
			if( cc == 0 ){
				EnemyBullet.bigBom(this);
			}
			if (count == 100) {
				game.scroll = 0;
			}
			if (count == 0) {
				game.boss = this;
			}
			count++;
			
		}
	}

})(Bridge, Actor);
