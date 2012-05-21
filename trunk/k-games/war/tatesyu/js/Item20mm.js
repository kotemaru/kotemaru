function Item20mm(game){this.initialize.apply(this, arguments)};
(function(Class, Super) {
	Util.extend(Class, Super);

	const CHIP = Chip.add("item_20mm", "img/item_20mm.png");
	
	Class.prototype.initialize = function(game) {
		Super.prototype.initialize.apply(this, arguments);
		Util.copy(this,{
			layer:2, chip:CHIP, x:0, y:0, vx:0,vy:-10, rw:32
		});
	};
	
	Class.prototype.isActive = function() {
		return true;
	}
	Class.prototype.init = function(x,y) {
		this.x = x;
		this.y = y;
		return this;
	}

	Class.prototype.action = function() {
		with (this) {
			if (game.myShip.isHitPlane(this)) {
				game.myShip.count20mm = 200;
				game.delEntity(this);
			}
			y += vy;
			vy += 0.5;
			if (y > game.clipY+game.clipH) {
				game.delEntity(this);
			}
		}
	};

})(Item20mm, Actor);

