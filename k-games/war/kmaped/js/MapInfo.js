function MapInfo(){this.initialize.apply(this, arguments)};
(function(Class) {

	Class.prototype.initialize = function(width, height, chipSet) {
		this.tileWidth  = 16;
		this.tileHeight = 16;
		this.width      = width;
		this.height     = height;
		this.chipSet    = chipSet;
	}
	Class.prototype.clone = function(chipSet, width, height) {
		return new Class(this.width, this.height, this.chipSet);
	}
	Class.prototype.resize = function(w,h) {
		this.width = w;
		this.height = h;
		return this;
	}

})(MapInfo);
//EOF.