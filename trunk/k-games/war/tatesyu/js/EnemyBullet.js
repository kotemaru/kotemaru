
function EnemyBullet(game){this.initialize.apply(this, arguments)};
(function(Class, Super) {
	Util.extend(Class, Super);

	var entities = [];
	Class.getInstance = function(game) {
		if (entities.length == 0) return new Class(game);
		return entities.pop();
	} 
	
	Class.prototype.initialize = function(game) {
		Super.prototype.initialize.apply(this, arguments);
		//this.color = "#ff8800";
		this.color = "#ffff00";
	};
	
	Class.prototype.close = function() {
		entities.push(this);
		this.game.delEntity(this);
	}
	
	Class.prototype.action = function() {
		const coor = this.game.myShip.isHit(this);
		if (coor) {
			this.hit(coor);
			this.game.myShip.hit(this.weigh);
		}
		Super.prototype.action.apply(this, arguments);
	};

	
	const SPEED = 20;
	const LEN = 13;
	Class.shootT = function(me, target) {
		with (me) {
			const tx = target.x-x;
			const ty = target.y-y;
			const len = Math.sqrt(tx*tx+ty*ty);
			const bvx = tx * (SPEED / len);
			const bvy = ty * (SPEED / len);
			const lx = tx * (LEN / len);
			const ly = ty * (LEN / len);
			Class.getInstance(game).init(x+lx,y+ly, bvx,bvy, lx,ly);
		}
	}
	Class.shootXY = function(me, bvx, bvy, lx,ly) {
		with (me) {
			Class.getInstance(game).init(x+lx,y+ly, bvx,bvy, lx,ly);
		}
	}
	Class.bigBom = function(me) {
		with (me) {
			for (var i=0; i<16; i++) {
				var r = Math.PI*2 / 17 * i;
				var bvx = Math.cos(r) * SPEED;
				var bvy = Math.sin(r) * SPEED;
				var lx = Math.cos(r) * LEN;
				var ly = Math.sin(r) * LEN;
				Class.getInstance(game).init(x,y, bvx,bvy, lx,ly);
			}
		}
	}
	
	
	
})(EnemyBullet,Bullet);

