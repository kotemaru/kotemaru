

function Item(){this.initialize.apply(this, arguments)};
(function(_class, _super){
	_class.prototype = new _super();
	_class.prototype.isDrawable=true;

	_class.prototype.getHandle = function(xx,yy) {
		throw "abstract";
	}
	
	_class.prototype.toSvg = function() {
		throw "abstract";
	}
	_class.prototype.fromSvg= function(svg) {
		throw "abstract";
	}
	_class.prototype.getHandle= function(xx,yy) {
		throw "abstract";
	}
	
	_class.prototype.draw = function(ctx2d) {
		throw "abstract";
	}
	_class.prototype.drawHandles= function(ctx2d) {
		throw "abstract";
	}

})(Item, Elem);

//EOF
