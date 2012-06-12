/**
@author kotemaru@kotemaru.org
*/


enchant.gocha.GochaLifeMark = org.kotemaru.Class(null, function(_class, _super){
	var Util = org.kotemaru.Util;
	const LIFES = enchant.gocha.Chip.create("img/life.png");
	const LIFE = [
	    [LIFES.sub(48,16,8,8), LIFES.sub(48,8,8,8), LIFES.sub(48,0,8,8)], // 0hp
	    [LIFES.sub(32,16,8,8), LIFES.sub(32,8,8,8), LIFES.sub(32,0,8,8)], // under 10hp
	    [LIFES.sub(24,16,8,8), LIFES.sub(24,8,8,8), LIFES.sub(24,0,8,8)], // 20%
	    [LIFES.sub(16,16,8,8), LIFES.sub(16,8,8,8), LIFES.sub(16,0,8,8)], // 40%
	    [LIFES.sub( 8,16,8,8), LIFES.sub( 8,8,8,8), LIFES.sub( 8,0,8,8)], // 60%
	    [LIFES.sub( 0,16,8,8), LIFES.sub( 0,8,8,8), LIFES.sub( 0,0,8,8)], // 80%
	];
	
	_class.prototype.initialize = function(actor) {
		this.actor = actor;
	}
	
	_class.prototype.draw = function() {
	
		with (this.actor) {
			var ctx = surface.context;
			var lifeLevel = Math.floor(4.999999*hp/max_hp)+1;
			if (hp<10) lifeLevel = 1;
			if (hp<=0) lifeLevel = 0;
			var lifeM = Math.floor(Math.abs(age%7-2)/2);
			LIFE[lifeLevel][lifeM].draw(ctx, width-8, 0);
		}
	};

});
