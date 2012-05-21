

function View(){this.initialize.apply(this, arguments)};
(function(Class) {
	const OPTS = {
			w: 320,
			h: 6400,
			layers: [
				{name:"map",    type:"map",   grid:32},
				{name:"ground", type:"actor", grid:1},
				{name:"air",     type:"actor", grid:1}
			]
	};


	const FACTORY = {
		map: function(self,opts,layer){return new Map(self,opts,layer);},
		actor: function(self,opts,layer){return new Actors(self,opts,layer);}
	};
	
	Class.prototype.initialize = function($elem, opts) {
		opts = $.extend(OPTS, opts);
		
		this.$elem = $elem;
		this.opts = opts;
		this.layers = new Array(opts.layers.length);
		this.w = opts.w;
		this.h = opts.h;

		for (var i=0; i<this.layers.length; i++) {
			var layerOpt = opts.layers[i];
			this.layers[i] = FACTORY[layerOpt.type](this, layerOpt, i);
		}

		//$elem.width(opts.w).height(opts.h);

		const self = this;
		this.$elem.bind("click", function(ev){self.onClick(ev)});
		this.$elem.bind("mousemove", function(ev){self.onMouseMove(ev)});
	}
	
	Class.prototype.onClick = function(ev) {
		var button = Buttons.instance.current;
		if (button == null) return;

		var x = ev.offsetX;
		var y = ev.offsetY;
		if (ev.target != ev.currentTarget) {
			x += ev.target.offsetLeft;
			y += ev.target.offsetTop;
		}

		var layer = this.layers[button.layer];
		var old = layer.get(x,y);
		if (old != null && old.name == button.name) return;

		layer.add(button.copy(), x,y);
	}
	Class.prototype.onMouseMove = function(ev) {
		if (ev.which == 0) return;
		var button = Buttons.instance.current;
		if (button == null) return;
		var layer = this.layers[button.layer];
		if (layer.grid > 1) this.onClick(ev);
		console.log("move");
	}
	
	Class.prototype.save = function() {
		var data = {};
		for (var i=0; i<this.layers.length; i++) {
			var layer = this.layers[i];
			data[layer.name] = layer.save();
		}
		return data;
	}
	Class.prototype.load = function(data) {
		for (var i=0; i<this.layers.length; i++) {
			var layer = this.layers[i];
			layer.load(data[layer.name]);
		}
	}

})(View);

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
			resize(data.gw*grid, data.gh*grid);
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



function Actors(){this.initialize.apply(this, arguments)};
(function(Class) {

	Class.prototype.initialize = function(view, opts, layer) {
		this.name = opts.name;
		this.$elem = view.$elem;
		this.list = {};
		this.idCount = 0;
		this.layer = layer;
	}
	
	Class.prototype.add = function(actor, x,y) {
		with (this) {
			var id = idCount++;
			actor.id = id;
			list[id] = actor;
			actor.appendTo($elem);
			actor.setPos(x,y);
		}
	}
	Class.prototype.remove = function(actor) {
		with (this) {
			actor.parge();
			delete list[actor.id];
		}
	}
	Class.prototype.get = function(x,y) {
		return null;
	}
	Class.prototype.save = function() {
		var data = [];
		for (var id in this.list) {
			var a = this.list[id];
			data.push({name:a.name, x:a.x, y:a.y});
		}
		return data;
	}
	Class.prototype.load = function(data) {
		for (var i=0; data.length; i++) {
			var a = data[i];
			Buttons.instance.getByName(a.name);
			this.add(button.copy(), a.x, a.y);
		}
	}
})(Actors);


function Chip(){this.initialize.apply(this, arguments)};
(function(Class) {

	Class.prototype.initialize = function(name, src, layer) {
		this.$elem = $("<img/>");
		this.$elem.attr("src",src);
		this.$elem.attr("draggable",false);
		this.name = name;
		this.src = src;
		this.layer = layer;
	}

	Class.prototype.copy = function() {
		return new Class(this.name, this.src);
	}
	
	Class.prototype.setPos = function(x,y) {
		this.$elem.css({position: "absolute", left:x+"px", top:y+"px"});
	}
	Class.prototype.parge = function() {
		this.$elem.remove();
	}
	Class.prototype.appendTo = function($parent) {
		$parent.append(this.$elem);
	}
	Class.prototype.css = function(data) {
		this.$elem.css(data);
	}
	Class.prototype.bind = function(type, func) {
		this.$elem.bind(type, func);
	}
	
})(Chip);

function Actor(){this.initialize.apply(this, arguments)};
(function(Class) {

	Class.prototype.initialize = function(name, src, layer) {
		this.$elem = $("<img/>");
		this.$elem.attr("src",src);
		this.$elem.attr("draggable",false);
		this.name = name;
		this.src = src;
		this.layer = layer;
		this.x = 0;
		this.y = 0;
	}

	Class.prototype.copy = function() {
		return new Class(this.name, this.src);
	}
	
	Class.prototype.setPos = function(x,y) {
		this.x = x;
		this.y = y;
		var left = (x-this.$elem.width()/2)+"px";
		var top = (y-this.$elem.height()/2)+"px";
		this.$elem.css({position: "absolute", left:left, top:top});
	}
	Class.prototype.parge = function() {
		this.$elem.remove();
	}
	Class.prototype.appendTo = function($parent) {
		$parent.append(this.$elem);
	}
	Class.prototype.css = function(data) {
		this.$elem.css(data);
	}
	Class.prototype.bind = function(type, func) {
		this.$elem.bind(type, func);
	}
	
})(Actor);



function Buttons(){this.initialize.apply(this, arguments)};
(function(Class) {
	const BORDER_ON  = "2px inset lightgray";
	const BORDER_OFF = "2px outset lightgray";

	Class.instance = null;

	Class.prototype.initialize = function($elem) {
		this.$elem = $elem;
		this.list = {};
		this.current = null;

		Class.instance = this;
	};
	
	Class.prototype.add = function(button) {
		const self = this;
		with (this) {
			list[button.name] = button;
			button.appendTo($elem);
			button.css({
				margin: "2px", padding: "2px",
				border: BORDER_OFF,
			});
			button.bind("click",function(ev){
				self.allOff();
				self.current = button;
				button.css({border: BORDER_ON});
			});
		};
	};
	Class.prototype.addSeparator = function() {
		this.$elem.append($("<hr/>"));
	}
	Class.prototype.remove = function(button) {
		with (this) {
			button.parge();
			delete list[button.name];
		}
	};

	Class.prototype.allOff = function(button) {
		for (var k in this.list) {
			var button = this.list[k];
			button.css({border: BORDER_OFF});
		}
	};
	
})(Buttons);





