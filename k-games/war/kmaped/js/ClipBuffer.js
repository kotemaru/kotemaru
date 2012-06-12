function ClipBuffer(){this.initialize.apply(this, arguments)};
(function(Class,Super) {
	Util.extend(Class,Super);
	
	Class.prototype.initialize = function() {
		Super.prototype.initialize.apply(this, arguments);
		this.offset(0,0);
		this.resize(1,1);
		this.elem.style.zIndex = ZIndex.CLIP_BUFFER;
		this.elem.style.position = "relative";
	}
	Class.prototype.offset = function(x,y) {
		if (y === undefined) {
			y = x.y;
			x = x.x;
		}
		this.x = x;
		this.y = y;
		return this;
	}
	Class.prototype.flush = function() {
		const tw = this.workSpace.chipSet.tileWidth;
		const th = this.workSpace.chipSet.tileHeight;
		Super.prototype.flush.apply(this, arguments);

		// offset 無くなった
		//Util.css(this.elem, {
		//	left:(this.x*tw)+"px", top:(this.y*th)+"px"
		//});

		var pw = $(this.elem.parentNode).width();
		var ph = $(this.elem.parentNode).height();
		var zw = pw/(this.width*tw);
		var zh = ph/(this.height*th);
		var zoom = Math.min(zw,zh);
		//if (zoom >= 1.0) zoom = 1.0
		this.elem.style.zoom = zoom;
	}
})(ClipBuffer, Layer);

//EOF.