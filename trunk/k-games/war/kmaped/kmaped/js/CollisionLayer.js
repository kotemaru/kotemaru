function CollisionLayer(){this.initialize.apply(this, arguments)};
(function(Class,Super) {
	Util.extend(Class,Super);

	const CHIPSET = new ChipSet(16,16,"img/collision-map.png");
	
	Class.prototype.elem = null;
	
	Class.prototype.initialize = function(name,workSpace) {
		Super.prototype.initialize.apply(this, arguments);
		this.elem.style.zIndex = ZIndex.COLLISION_LAYER;
		//this.elem.style.opacity = 0.5;
		this.show(false);
	}
	Class.prototype.chipSet = function() {
		return CHIPSET;
	}
	
	
	Class.prototype.paste = function(clip,dx,dy,dw,dh) {
		dw = dw?dw:clip.width;
		dh = dh?dh:clip.height;

		if (dx+dw >= this.width) dw = this.width-dx;
		if (dy+dh >= this.height) dh = this.height-dy;

		for (var y=0; y<dh; y++) {
			for (var x=0; x<dw; x++) {
				if (dy+y>=0 && dx+x>=0) {
					this.tiles[dy+y][dx+x] = (clip.tiles[y][x]== -1?-1:1);
				} 
			}
		}
	}
	
	
	
})(CollisionLayer, Layer);

//EOF.