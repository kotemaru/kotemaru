
function Coor(){this.initialize.apply(this, arguments)};
(function(_class){
	Lang.define(_class);
	_class.prototype.isCoor = true;
	_class.properties = {
		_origin : {type: "Point", value:null},
		_origin2: {type: "Point", value:null},
		_x      : {type: "number", value:0},
		_y      : {type: "number", value:0},
		mode_x  : {type: "number", value:0},
		mode_y  : {type: "number", value:0},
	};
	
	var idCount = 1;
	
	_class.prototype.initialize = function(opts) {
		if (opts == null) opts = {};
		this._origin = opts.origin; // Item
		this._origin2 = opts.origin2; // Item
		this._x = opts.x;
		this._y = opts.y;
		this.internalId = idCount++;
	}

	_class.prototype.origin = function(v) {
		if (v) {
			this._origin = v;
			return this;
		}
		return this._origin;
	}
	_class.prototype.origin2 = function(v) {
		if (v) {
			this._origin2 = v;
			return this;
		}
		return this._origin2;
	}
	_class.prototype.xy = function(xx,yy) {
		this.x(xx);
		this.y(yy);
	}
	_class.prototype.x = function(v) {
		if (v) return this.putX(v);

		this.checkRemove();
		var xx = this.calcX();
		if (xx<0) xx = 0;
		if (xx>Canvas.width()) xx = Canvas.width;
		return xx;
	}
	_class.prototype.putX = function(v) {
		if (this._origin) {
			var x1 = this._origin.x();
			if (this._origin2) {
				var x2 = this._origin2.x();
				var w = x2-x1;
				var per = (v - x1) / w;
				
				if (0.0 <= per && per<= 1.0) {
					this.mode_x = 2; // FLOATING-IN-BOUNDS
					this._x = per;
				} else {
					this.mode_x = 3; // FLOATING-OUT-BOUNDS
					this._x = v - (x1 + w/2);
				}
			} else {
				this.mode_x = 1; // RELATIVE
				this._x = v - x1 ;
			}
		} else {
			this.mode_x = 0; // ABSOLUTE
			this._x = v;
		}
		return this;
	}
	_class.prototype.calcX = function() {
		if (this._origin) {
			var x1 = this._origin.x();
			if (this._origin2) {
				var x2 = this._origin2.x();
				var w = x2 - x1;
				var per = this._x;
				
				if (0.0 <= per && per <= 1.0) {
					return x1 + w * per; // FLOATING-IN-BOUNDS
				} else {
					return x1 + w/2 + this._x; // FLOATING-OUT-BOUNDS
				}
			} else {
				return x1 + this._x; // RELATIVE
			}
		} else {
			return this._x; // ABSOLUTE
		}
	}
	_class.prototype.y = function(v) {
		if (v) return this.putY(v);
		
		var yy = this.calcY();
		if (yy<0) yy = 0;
		if (yy>Canvas.height()) yy = Canvas.height;
		return yy;
	}
	_class.prototype.putY = function(v) {
		if (this._origin) {
			var y1 = this._origin.y();
			if (this._origin2) {
				var y2 = this._origin2.y();
				var w = y2-y1;
				var per = (v - y1) / w;
				console.log(per);
				if (0.0 <= per && per<= 1.0) {
					this.mode = 2; // FLOATING-IN-BOUNDS
					this._y = per;
				} else {
					this.mode = 3; // FLOATING-OUT-BOUNDS
					this._y = v - (y1 + w/2);
				}
			} else {
				this.mode = 1; // RELATIVE
				this._y = v - y1 ;
			}
		} else {
			this.mode = 0; // ABSOLUTE
			this._y = v;
		}
		return this;
	}
	_class.prototype.calcY = function() {
		if (this._origin) {
			var y1 = this._origin.y();
			if (this._origin2) {
				var y2 = this._origin2.y();
				var w = y2 - y1;
				var per = this._y;
				
				if (0.0 <= per && per <= 1.0) {
					return y1 + w * per; // FLOATING-IN-BOUNDS
				} else {
					return y1 + w/2 + this._y; // FLOATING-OUT-BOUNDS
				}
			} else {
				return y1 + this._y; // RELATIVE
			}
		} else {
			return this._y; // ABSOLUTE
		}
	}
	
	
	
	
	_class.prototype.checkRemove = function() {
		with (this) {
			if (_origin && _origin.isRemove) setOrigin(null);
			if (_origin2 && _origin2.isRemove) setOrigin2(null);
		}
	}
	_class.prototype.setOrigin = function(v) {
		if (v == null) {
			this._x = this.calcX();
			this._y = this.calcY();
		}
		this._origin = v;
	}
	_class.prototype.setOrigin2 = function(v) {
		this._origin2 = v;
	}
	_class.prototype.setX = function(v) {
		this._x = v;
	}
	_class.prototype.setY = function(v) {
		this._y = v;
	}
	
})(Coor);

