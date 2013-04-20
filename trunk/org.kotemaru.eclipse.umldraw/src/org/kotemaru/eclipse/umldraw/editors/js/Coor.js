
function Coor(){this.initialize.apply(this, arguments)};
(function(_class){
	Lang.define(_class);
	_class.prototype.isCoor = true;
	_class.attributes = {
		_origin : new Point(),
		_origin2: new Point(),
		_x      : 0,
		_y      : 0,
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
		if (v) {
			if (this._origin) {
				var x1 = this._origin.x();
				if (this._origin2) {
					var w = this._origin2.x() - x1;
					this._x = (v - x1) / w ;
				} else {
					this._x = v - x1 ;
				}
			} else {
				this._x = v;
			}
			return this;
		}
		
		if (this._origin) {
			var x1 = this._origin.x();
			if (this._origin2) {
				var w = this._origin2.x() - x1;
				return x1 + w * this._x;
			} else {
				return x1 + this._x;
			}
		} else {
			return this._x;
		}
	}
	_class.prototype.y = function(v) {
		if (v) {
			if (this._origin) {
				var y1 = this._origin.y();
				if (this._origin2) {
					var w = this._origin2.y() - y1;
					this._y = (v - y1) / w ;
				} else {
					this._y = v - y1 ;
				}
			} else {
				this._y = v;
			}
			return this;
		}
		
		if (this._origin) {
			var y1 = this._origin.y();
			if (this._origin2) {
				var w = this._origin2.y() - y1;
				return y1 + w * this._y;
			} else {
				return y1 + this._y;
			}
		} else {
			return this._y;
		}
	}
	_class.prototype.setOrigin = function(v) {
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

