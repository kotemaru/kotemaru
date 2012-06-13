
function WorkSpace(){this.initialize.apply(this, arguments)};
(function(Class) {
	
	const CSS = {
			position: "absolute", 
			background: "url(kmaped/img/gray-check.png)",
	};
	const GRID_CSS = {
			position: "absolute",
			top:0, left:0, zIndex: ZIndex.GRID,
			background: "url(kmaped/img/grid16.png)", opacity: 0.7,
	};
	const COLLISION = "collision";
	
	Class.prototype.initialize = function(editor) {
		this.width = 20;
		this.height =20;
		this.editor = editor;
		this.chipSet = editor.chipSet;
		this.elem = Util.newElem("div", CSS);
		this.gridElem = Util.newElem("div", GRID_CSS);
		this.selector = new Selector(this);

		this.layers = [];
		this.opacityMode = "no";

		this.elem.appendChild(this.gridElem);
		this.elem.appendChild(this.selector.elem);
		this.addLayer(COLLISION);

		this.sizeFlush();
	}
	//----------------------------------------------------------
	// WrokSpace controlls
	Class.prototype.grid = function(b) {
		var b = b?b:(this.gridElem.style.visibility == "hidden");
		this.gridElem.style.visibility = (b?"visible":"hidden");
		return this;
	}
	
	Class.prototype.resize = function(width,height) {
		this.width = width;
		this.height = height;
		for (var i=0; i<this.layers.length; i++) {
			this.layers[i].resize(width,height);
		}
		this.collisionLayer.resize(width,height);
		this.sizeFlush();
		return this;
	}
	
	Class.prototype.sizeFlush = function() {
		var w = (this.width * this.chipSet.tileWidth)+"px";
		var h = (this.height * this.chipSet.tileHeight)+"px";
	
		Util.css(this.elem,{width:w,  height:h});
		Util.css(this.gridElem,{width:w,  height:h});
		if (this.elem.parentNode) {
			Util.css(this.elem.parentNode,{width:w,  height:h});
		}
		return this;
	}
	Class.prototype.flush = function() {
		this.sizeFlush();
		this.opacity();
		this.selector.flush();
		this.collisionLayer.flush();
		for (var i=0; i<this.layers.length; i++) {
			this.layers[i].elem.style.zIndex = ZIndex.layer(i);
			this.layers[i].flush();
		}
	}

	//----------------------------------------------------------
	// Layer controlls
	Class.prototype.addLayer = function(name) {
		name = name?name:("layer"+this.layers.length);
		var layer;
		if (name == COLLISION) {
			this.removeLayer(99);
			layer = new CollisionLayer(name,this);
			this.collisionLayer = layer;
		} else {
			layer = new Layer(name,this,this.chipSet);
			this.layers.push(layer);
		}
		this.elem.appendChild(layer.elem);
		return layer;
	}
	Class.prototype.getTopLayer = function() {
		if (this.collisionLayer.visible()) {
			return this.collisionLayer;
		}
		
		for (var i=this.layers.length-1; i>=0; i--) {
			if (this.layers[i].visible()) return this.layers[i];
		}
		this.layers[0].show(true);
		return this.layers[0];
	}
	Class.prototype.showLayer = function(idx, bool) {
		var layer = (idx==99 ? this.collisionLayer : this.layers[idx]);
		layer.show(bool);
	}
	Class.prototype.removeLayer = function(idx) {
		var layer = (idx==99 ? this.collisionLayer : this.layers[idx]);
		if (layer == null) return;
		this.layers.splice(idx, 1);
		this.elem.removeChild(layer.elem);
	}
	Class.prototype.moveLayer = function(idx, delta) {
		var tgtIdx = idx + delta;
		if (tgtIdx<0 || tgtIdx>=this.layers.length) return;

		var layer = this.layers[idx];
		this.layers[idx] = this.layers[tgtIdx];
		this.layers[tgtIdx] = layer;
	}
	
	Class.prototype.opacity = function(mode) {
		this.opacityMode = mode?mode:this.opacityMode;

		var rate = (this.opacityMode == "no") ? 1.0 : 0.5;
		for (var i=0; i<this.layers.length; i++) {
			this.layers[i].opacity(rate);
		}
		
		if (this.opacityMode == "back") {
			this.getTopLayer().opacity(1.0);
		} else if (this.opacityMode == "front") {
			this.layers[0].opacity(1.0);
		}
		return this;
	}
	
	
	//----------------------------------------------------------
	// IO Helper
	
	Class.prototype.saveJSON = function(indent) {
		var data = {
			width:  this.width,
			height: this.height,
			tileWidth:  this.chipSet.tileWidth,
			tileHeight: this.chipSet.tileHeight,
			chipSet: this.chipSet.src,
			layers: [],
		};
		
		for (var i=0; i<this.layers.length; i++) {
			var layer = this.layers[i];
			data.layers.push({name:layer.name, tiles:layer.tiles});
		}
		layer = this.collisionLayer;
		data.collision = {name:layer.name, tiles:layer.tiles};

		if (indent) {
			return toJSON(data,indent);
		} else {
			return JSON.stringify(data);
		}
	}

	function toJSON(data,indent) {
		var str =  JSON.stringify(data);
		str = str.replace(/\[/g, "\n[")
			.replace(/\{/g, "\n{")
			.replace(/\]\]}/g, "]\n]}")
			.replace(/,"/g, ",\n\"")
			.replace(/{"/g, "{\n\"")
		;
		return str;
	}
	
	
	Class.prototype.loadJSON = function(json) {
		var data = JSON.parse(json);

		this.resize(data.width, data.height);
		this.layers = [];
		for (var i=0; i<data.layers.length; i++) {
			var layerInfo = data.layers[i];
			var layer = this.addLayer(layerInfo.name);
			layer.tiles = layerInfo.tiles;
		}
		
		var layer = this.addLayer(COLLISION);
		if (data.collision) {
			layer.tiles = data.collision.tiles;
		}
	
		return this;
	}
	
	
	//-------------------------------------------------------------
	// UI helper
	Class.prototype.makeLabelElem = function(templ) {
		var $elem = $("<div></div>")
		for (var i=this.layers.length-1; i>=0; i--) {
			var $label = this.layers[i].makeLabelElem(templ);
			$label.attr("data-idx",i);
			$label.data("idx",i);
			$label[0].data_idx = i;
			$elem.append($label);
		}
		return $elem;
	}
	
	//-------------------------------------------------------
	// Event handlers
	Class.prototype.selectionStart = function(sel) {
		this.editor.selectionStart(sel, this);
	}
	Class.prototype.selectionDrag = function(sel) {
		this.editor.selectionDrag(sel, this);
	}
	Class.prototype.selectionEnd = function(sel) {
		this.editor.selectionEnd(sel, this);
	}

})(WorkSpace);

//EOF.