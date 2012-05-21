
function Map(){this.initialize.apply(this, arguments)};
(function(Class) {

	Class.prototype.initialize = function(view, opts, layer) {
		this.name = opts.name;
		this.$elem = view.$elem;
		this.grid = opts.grid;
		this.resize(view.w, view.h);
		this.layer = layer;
	}

	Class.prototype.resize = function(w,h) {
		var grid = this.grid;
		var gw = Math.ceil(w/grid);
		var gh = Math.ceil(h/grid);
	
		var array = new Array(gh);
		for (var y=0; y<gh; y++) {
			array[y] = new Array(gw);
		}

		if (this.map) {
			for (var y=0; y<gh && y<this.gh; y++) {
				for (var x=0; x<gw && x<this.gw; x++) {
					array[y][x] = this.map[y][x];
				}
			}
		}
		
		this.grid = grid;
		this.gw = gw;
		this.gh = gh;
		this.map = array;
		this.$elem.width(w).height(h);
	}
	
	Class.prototype.add = function(chip, x,y) {
		with (this) {
			var gx = Math.floor(x/grid);
			var gy = Math.floor(y/grid);
			if (0>gx || gx>gw) return;
			if (0>gy || gh>gh) return;

			if (map[gy][gx]) {
				map[gy][gx].parge();
			}
			map[gy][gx] = chip;
			chip.appendTo($elem);
			chip.setPos(gx*grid, gy*grid);
		}
	}
	Class.prototype.get = function(x,y) {
		with (this) {
			var gx = Math.floor(x/grid);
			var gy = Math.floor(y/grid);
			if (0>gx || gx>gw) return null;
			if (0>gy || gh>gh) return null;

			return map[gy][gx];
		}
	}

	Class.prototype.remove = function(x,y) {
		with (this) {
			var gx = Math.floor(x/grid);
			var gy = Math.floor(y/grid);
			if (0>gx || gx>gw) return;
			if (0>gy || gh>gh) return;

			if (map[gy][gx]) {
				map[gy][gx].parge();
			}
		}
	}

	var CHARS = "abcdefghijklmnopqrstuvwxyz"
		+"ABCDEFGHIJKLMNOPQRSTUVWXYZ"
		+"0123456789";
	Class.prototype.save = function() {
		with (this) {
			// make char mapping
			var charMap = {};
			var charMapRv = {};
			var charsPos = 0;
			for (var y=0; y<gh; y++) {
				for (var x=0; x<gw; x++) {
					var chip = map[y][x];
					if (chip != null && !charMap[chip.name]) {
						var ch = CHARS.charAt(charsPos++);
						charMap[chip.name] = ch;
						charMapRv[ch] = chip.name;
					}
				}
			}

			var lines = [];
			for (var y=0; y<gh; y++) {
				var line = "";
				for (var x=0; x<gw; x++) {
					var chip = map[y][x];
					if (chip) {
						line += charMap[chip.name];
					} else {
						line += " ";
					}
				}
				lines.push(line);
			}

			var data = {
				w: this.gw, h:this.gh, grid:this.grid,
				chars: charMapRv,
				map: lines
			};
			return data;
		}
	}
	Class.prototype.load = function(data) {
		this.grid = data.grid;
		
		with (this) {
			resize(data.w*grid, data.h*grid);
			// make char mapping
			for (var y=0; y<gh; y++) {
				for (var x=0; x<gw; x++) {
					var ch = data.map[y].charAt(x);
					if (ch != " ") {
						var name = data.chars[ch];
						var button = Buttons.instance.getByName(name);
						add(button.copy(), x*grid, y*grid);
					}
				}
			}
		}
	}
	
})(Map);

