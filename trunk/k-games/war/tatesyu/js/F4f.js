function F4f(game){this.initialize.apply(this, arguments)};
(function(Class, Super) {
	Util.extend(Class, Super);
	
	const CHIP = Chip.add("f4f", "img/f4f.png");

	Class.prototype.isEnemyPlane = true;

	Class.prototype.initialize = function(game) {
		Super.prototype.initialize.apply(this, arguments);
		this.chip = CHIP;
		this.hp = 5;
		this.point = 800;
	};

	
	Class.prototype.isHit = function(bullet) {
		with (this) {
			const x0 = x;
			const x1 = x - 14;
			const x2 = x + 14;
			const y1 = y - 15;
			const y2 = y + 10;
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
})(F4f, Plane);

