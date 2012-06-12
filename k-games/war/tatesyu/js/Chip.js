function Chip(src){this.initialize.apply(this, arguments)};
(function(Class) {
	Class.loading = 0;
	Class.cache = {};
	
	Class.add = function(name, src){
		if (Chip.cache[name]) return Class.cache[name];
		Class.cache[name] = new Chip().load(src);
		return Class.cache[name];
	};
	
	Class.mapping = function(baseName, data) {
		for (var name in data) {
			var item = data[name];
			var chip = new Chip();
			chip.base = Class.cache[baseName];
			chip.img = chip.base.img;
			chip.data = chip.base.data;

			Util.copy(chip, item);
			Class.cache[name] = chip;
		}
	}
	Class.onload = function() {
		// nop.
	}

	Class.load = function(callback) {
		Class.onload = callback;
		if (Class.loading<=0) {
			callback();
		}
	}

	Class.prototype.initialize = function() {
		this.base = null;
		this.data = null;
	}
	
	Class.prototype.load = function(src) {
		const self = this;
		var img = new Image();
		this.img = img;
		img.onload = function(){
			Util.copy(self,{x:0,y:0, w:img.width, h:img.height});
			Class.loading--;
			if (Class.loading == 0) {
				preset();
				Class.onload();
			}
		};
		img.src = src;
		Class.loading++;
		return this;
	};

	function preset() {
		var tmpCvs = document.createElement("canvas");
		for (var name in Class.cache) {
			var chip = Class.cache[name];
			if (chip.data === null) {
				tmpCvs.setAttribute("width", chip.w);
				tmpCvs.setAttribute("height", chip.h);
				var ctx = tmpCvs.getContext("2d");
				chip.draw(ctx, 0,0);
				chip.data = ctx.getImageData(0, 0, chip.w, chip.h);
			}
		}
	}
	
	Class.prototype.draw = function(ctx, dx, dy) {
		with (this) {
			ctx.drawImage(img, x,y,w,h, dx,dy,w,h);
		}
	}

	Class.prototype.put = function(ctx, dx, dy) {
		ctx.putImageData(this.data,dx,dy);
	}
	
})(Chip);

// preload
//Chip.add("font8x12","img/font8x12.png");
Chip.add("all32","img/all32.png");
Chip.add("others","img/others.png");

//Chip.mapping("font8x12", CHIPS_FONT8x12_MAPPING);
Chip.mapping("all32", CHIPS_ALL32_MAPPING);
Chip.mapping("others", CHIPS_OTHERS_MAPPING);
