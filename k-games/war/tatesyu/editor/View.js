

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
		if (Actor.dragger) return;

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
		if (Actor.dragger) {
			return Actor.dragger.onMouseMove(ev);
		}
		
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
