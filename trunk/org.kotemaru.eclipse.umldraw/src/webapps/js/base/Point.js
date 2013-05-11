

function Point(){this.initialize.apply(this, arguments)};
(function(_class){
	_class.prototype.initialize = function(xx,yy) {
		this._x = 0;
		this._y = 0;
		this.xy(xx,yy);
	}
	
	_class.prototype.xy = function(xx,yy) {
		this.x(xx);
		this.y(yy);
	}
	
	
	_class.prototype.x = function(v) {
		if (v) this._x = v
		return this._x;
	}
	_class.prototype.y = function(v) {
		if (v) this._y = v
		return this._y;
	}	
})(Point);
