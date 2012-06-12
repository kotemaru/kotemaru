function MagicBuffer(){this.initialize.apply(this, arguments)};
(function(Class,Super) {
	Util.extend(Class,Super);
	
	Class.prototype.initialize = function() {
		Super.prototype.initialize.apply(this, arguments);
		this.elem.style.position = "absolute";
	}
	Class.prototype.flush = function() {
		const tw = this.workSpace.chipSet.tileWidth;
		const th = this.workSpace.chipSet.tileHeight;
		Super.prototype.flush.apply(this, arguments);

		Util.css(this.elem, {
			left:(this.x*tw)+"px", top:(this.y*th)+"px"
		});
		this.elem.style.zoom = 1.0;
	}
})(MagicBuffer, ClipBuffer);

//EOF.