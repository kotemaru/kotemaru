function Actor(game){this.initialize.apply(this, arguments)};
(function(Class) {
	Class.prototype.initialize = function(game) {
		Util.copy(this,{
			game:game, layer:1, point:0
			// ,chip:null, x:0, y:0			
		});
	};

	Class.prototype.isActive = function() {
		return false;
	}
	
	Class.prototype.paint = function() {
		with (this) {
			// Note:クリッピングにまかせた方が多分早い。
			const img = chip.img;
			//const xx = x - (img.width/2) - game.clipX;
			//const yy = y - (img.height/2) - game.clipY;
			//game.ctx.putImageData(data, xx, yy);
			game.drawImage(chip, 
					x - (chip.w/2),
					y - (chip.h/2)
			);
			
		}
	};
})(Actor);
