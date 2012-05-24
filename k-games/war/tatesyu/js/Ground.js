
function Ground(game){this.initialize.apply(this, arguments)};
(function(Class, Super) {
	Util.extend(Class, Super);
	const DETONOTE = Chip.add("detonate", "img/detonate.png");
	const RUBBLE = Chip.add("rubble", "img/rubble.png");

	Class.prototype.isEnemyGround = true;
	
	Class.prototype.initialize = function(game) {
		Super.prototype.initialize.apply(this, arguments);

		Util.copy(this,{
			layer:1, chip:null, x:0, y:0, hp:1, state:0, isClash:false, point:100
		});
	};
	Class.prototype.isActive = function() {
		with (this) {
			return (game.clipY <= y);
		}
	}
	Class.prototype.action = function() {
		with (this) {
			if (game.clipY+game.clipH+16 <= y) game.delEntity(this);
			if (state == 1) {
				state = 2;
				chip = RUBBLE;
			}
			if (state == 0 && hp<=0) {
				state = 1;
				chip = DETONOTE;
				isClash = true;
				game.score += point;
				Sound.play("boon");
			}
		}
	}
	Class.prototype.basicAction = Class.prototype.action;

	Class.prototype.isHit = function(bullet) {
		with (this) {
			const x1 = x - 24;
			const x2 = x + 24;
			const y1 = y - 24;
			const y2 = y + 24;
		}
		with (bullet) {
			return (x1<x && x<x2 && y1<y && y<y2);
		}
	};
	Class.prototype.hit = function(waigh){
		this.hp -= waigh;
		Sound.play("kan");
	}

})(Ground, Actor);
