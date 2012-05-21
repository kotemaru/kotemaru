function Bullet(game){this.initialize.apply(this, arguments)};
(function(Class, Super) {
	Util.extend(Class, Super);

	const ASTER_CHIP = Chip.add("aster", "img/aster.png");

	Class.prototype.initialize = function(game) {
		Super.prototype.initialize.apply(this, arguments);
		this.layer = 2;
		this.state = 0;
		this.count = 0;
		this.color = "orange";
		this.rw = 1;
	};
	Class.prototype.init = function(x, y, vx, vy, lx,ly, weigh){
		this.state = 0;
		this.count = 0;
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
		this.lx = lx;
		this.ly = ly;
		this.weigh = (weigh===undefined ? 1 : weigh);
		this.game.addActive(this);
	}

	Class.prototype.close = function() {
		game.delEntity(this);
	}
	
	Class.prototype.action = function() {
		with (this) {
			if (x<game.clipX || x>game.clipX+game.clipW 
				|| y<game.clipY || y>game.clipY+game.clipH) {
				close();
			}
			x += vx;
			y += vy;
		}
	};
	
	Class.prototype.hit = function(coor) {
		with (this) {
			state = 1;
			x = coor.x;
			y = coor.y;
			vx = 0;
			vy = 0;
		}
	};

	Class.prototype.paint = function() {
		with (this) {
			if (state>0) return this.paintNext();
			const ctx = game.ctx;
			ctx.strokeStyle = color;
			ctx.lineWidth = 3;
			game.drawLine(x,y, x+lx, y+ly);
		}
	};
	
	Class.prototype.paintNext = function() {
		with (this) {
			game.drawImage(ASTER_CHIP, x-6,y-6);
			if (state++ > 2) {
				close();
			}
		}
	};
	
})(Bullet,Actor);

