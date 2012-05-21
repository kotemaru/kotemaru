function ItemSpanner(game){this.initialize.apply(this, arguments)};
(function(Class, Super) {
	Util.extend(Class, Super);

	const CHIP = Chip.add("item_spanner", "img/item_spanner.png");
	
	Class.prototype.initialize = function(game) {
		Super.prototype.initialize.apply(this, arguments);
		Util.copy(this,{
			layer:2, chip:CHIP, x:0, y:0, vx:0,vy:-20, rw:20
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
				game.myShip.hp = 10;
				game.delEntity(this);
			}
			y += vy;
			vy += 1;
			if (y > game.clipY+game.clipH) {
				game.delEntity(this);
			}
		}
	};

})(ItemSpanner, Actor);

