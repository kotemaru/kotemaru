

function Item(){this.initialize.apply(this, arguments)};
(function(_class, _super){
	_class.prototype = new _super();
	_class.prototype.isDrawable=true;
	_class.attributes = {
		coor : new Point()
	};

	
	var idCount = 1;
	
	_class.prototype.initialize = function(coorBase) {
		_super.prototype.initialize.apply(this, arguments);
		this.internalId = idCount++;
	}
	

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
