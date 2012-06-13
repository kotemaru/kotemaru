function Layer(){this.initialize.apply(this, arguments)};
(function(Class,Super) {
	
	Class.prototype.elem = null;
	
	Class.prototype.initialize = function(name,workSpace) {
		if (arguments.length == 0) return;
		
		this.name = name;
		this.workSpace = workSpace;

		this.elem = Util.newElem("canvas");
		this.elem.draggable = false;
		Util.css(this.elem, {
			position: "absolute", left:0, top:0, cursor:"pointer"
		});
		
		this.tiles = [];
		this.resize(workSpace.width, workSpace.height);
	}
	
	//----------------------------------------------
	// Layer control
	Class.prototype.resize = function(width,height) {
		if (height === undefined) {
			height = width.height;
			width = width.width;
		}
		
		for (var i=0; i<this.tiles.length; i++) {
			this.tiles[i].length = width;
		}
		for (var i=this.tiles.length; i<height; i++) {
			this.tiles[i] = new Array(width);
		}
		
		this.tiles.length = height;
		this.width = width;
		this.height = height;
		for (var y=0; y<this.height; y++) {
			for (var x=0; x<this.width; x++) {
				if (this.tiles[y][x] == null) this.tiles[y][x] = -1;
			}
		}
		
		this.elem.width = this.width * this.chipSet().tileWidth;
		this.elem.height = this.height * this.chipSet().tileHeight;
		return this;
	}
	
	//----------------------------------------------
	// View control
	Class.prototype.show = function(bool) {
		this.elem.style.visibility = (bool?"visible":"hidden");
		return this;
	}
	Class.prototype.visible = function(bool) {
		return (this.elem.style.visibility != "hidden");
	}
	Class.prototype.opacity = function(rate) {
		this.elem.style.opacity = rate;
	}

	Class.prototype.flush = function() {
		var ctx = this.elem.getContext('2d');
		ctx.clearRect(0,0, this.elem.width, this.elem.height);
		for (var y=0; y<this.height; y++) {
			for (var x=0; x<this.width; x++) {
				this.chipSet().draw(ctx, this.tiles[y][x], x,y);
			}
		}
		return this;
	}

	//---------------------------------------------------------
	// Edit
	Class.prototype.chipSet = function() {
		return this.workSpace.chipSet;
	}
	
	Class.prototype.cut = function(clip,sx,sy,sw,sh) {
		this.copy(clip,sx,sy,sw,sh);
		this.remove(sx,sy,sw,sh);
	}
	
	Class.prototype.copy = function(clip,sx,sy,sw,sh) {
		if (sx+sw >= this.width) sw = this.width-sx;
		if (sy+sh >= this.height) sh = this.height-sy;
		clip.offset(sx,sy);
		clip.resize(sw,sh);
		for (var y=0; y<sh; y++) {
			for (var x=0; x<sw; x++) {
				clip.tiles[y][x] = this.tiles[sy+y][sx+x];
			}
		}
	}
	
	Class.prototype.paste = function(clip,dx,dy,dw,dh) {
		dw = dw?dw:clip.width;
		dh = dh?dh:clip.height;

		if (dx+dw >= this.width) dw = this.width-dx;
		if (dy+dh >= this.height) dh = this.height-dy;

		for (var y=0; y<dh; y++) {
			for (var x=0; x<dw; x++) {
				if (dy+y>=0 && dx+x>=0) {
					this.tiles[dy+y][dx+x] = clip.tiles[y][x];
				} 
			}
		}
	}
	
	Class.prototype.remove = function(sx,sy,sw,sh) {
		if (sx+sw >= this.width) sw = this.width-sx;
		if (sy+sh >= this.height) sh = this.height-sy;

		for (var y=0; y<sh; y++) {
			for (var x=0; x<sw; x++) {
				this.tiles[sy+y][sx+x] = -1;
			}
		}
	}
	
	
	//------------------------------------------------------------
	// UI Helper
	Class.prototype.makeLabelElem = function(html) {
		var self = this;
		var $elem = $(html);
		var $cbox = $elem.find("input[data-id='check']");
		$cbox.attr("checked",this.visible());
		$elem.find("[data-id='name']").text(this.name);
		return $elem;
	}
	
	
})(Layer);

//EOF.