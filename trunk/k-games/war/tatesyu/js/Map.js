
function Map(game){this.initialize.apply(this, arguments)};
(function(Class, Super) {
	Util.extend(Class, Super);

	var matrix = [];

	Class.prototype.initialize = function(game) {
		Util.copy(this,{game:game, layer:0});
	};
	Class.prototype.isActive = function() {
		return true;
	};
	
	Class.prototype.load = function(data) {
		var matrix = new Array(data.h);
		for (var y=0; y<data.h; y++) {
			var line = data.matrix[y];
			matrix[y] = new Array(data.w);
			for (var x=0; x<data.w; x++) {
				matrix[y][x] = data.chars[line.charAt(x)];
			}
		}
		this.matrix = matrix;
		this.w = data.w;
		this.h = data.h;
		this.grid = data.grid;
	}
	Class.prototype.height = function() {
		return this.grid * this.h;
	}

	Class.prototype.paint = function() {
		const matrix = this.matrix;
		const chips = Chip.cache;
		const ctx = this.game.ctx;
		const margin = this.game.clipY % this.grid;
		const top = Math.floor(this.game.clipY /32);
		const h = Math.ceil(this.game.clipH / this.grid)+1;
		
		for (var y=0; y<h; y++) {
			var yy = y*32 - margin;
			for (var x = 0; x < 10; x++) {
				var name = matrix[y+top][x];
				var chip = chips[name];
				ctx.putImageData(chip.data, x * 32, yy);
				//ctx.drawImage(chip.img, x * 32, yy);
			}
		}
	};
})(Map);

