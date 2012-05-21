
function Arsenal(game){this.initialize.apply(this, arguments)};
(function(Class, Super) {
	Util.extend(Class, Super);

	const CHIP = Chip.add("arsenal", "img/arsenal.png");

	Class.prototype.initialize = function(game) {
		Super.prototype.initialize.apply(this, arguments);
		this.chip = CHIP;
		this.point = 300;
	};
	
	
	//-----------------------------------------------------------
	Class.prototype.base2 = function() {
		with (this) {
			basicAction();
			if (state == 1) {
				EnemyBullet.bigBom(this);
				game.addEntity(new Item20mm(game).init(x,y));
			}
		}
	}
	
	Class.prototype.base3 = function() {
		with (this) {
			basicAction();
			if (state == 1) {
				game.addEntity(new ItemSpanner(game).init(x,y));
			}
		}
	}

})(Arsenal, Ground);
