
function CoorDiag(){this.initialize.apply(this, arguments)};
(function(_class, _super){
	Lang.extend(_class, _super);
	_class.properties = _super.properties;
	
	_class.prototype.initialize = function(opts) {
		_super.prototype.initialize.apply(this, arguments);
	}

	_class.prototype.x = function(v) {
		if (this.origin().w === undefined) return this.origin().x(); // TODO:
		return this.origin().x() + this.origin().w();
	}
	_class.prototype.y = function(v) {
		if (this.origin().h === undefined) return this.origin().y(); // TODO:
		return this.origin().y() + this.origin().h();
	}
	
})(CoorDiag,Coor);