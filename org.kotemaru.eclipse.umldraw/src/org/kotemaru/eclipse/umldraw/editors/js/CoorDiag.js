
function CoorDiag(){this.initialize.apply(this, arguments)};
(function(_class, _super){
	_class.prototype = new _super();
	_class.prototype.initialize = function(opts) {
		_super.prototype.initialize.apply(this, arguments);
	}
	
	_class.prototype.x = function(v) {
		return this.origin().x() + this.origin().w();
	}
	_class.prototype.y = function(v) {
		return this.origin().y() + this.origin().h();
	}
	
})(CoorDiag,Coor);