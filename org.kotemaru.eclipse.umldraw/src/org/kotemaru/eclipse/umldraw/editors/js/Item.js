

function Item(){this.initialize.apply(this, arguments)};
(function(_class, _super){
	_class.prototype = new _super();
	_class.prototype.isDrawable=true;
	_class.attributes = {
		coor : new Point(),
		group : {isGroup: true}
	};

	
	var idCount = 1;
	
	_class.prototype.initialize = function(coorBase) {
		_super.prototype.initialize.apply(this, arguments);
		this.internalId = idCount++;
		this.group = null;
	}
	
	_class.prototype.setGroup = function(group) {
		if (group) {
			if (this.group)	throw "Duplicate group";
			if (this.coor.origin() == null) {
				var xx=this.x(),yy=this.y();
				this.coor.setOrigin(group);
				this.xy(xx,yy);
				console.log(this.internalId,xx,yy,this.x(),this.y());
			}
		} else { // unset
			if (this.coor.origin() == this.group) {
				var xx=this.x(),yy=this.y();
				this.coor.setOrigin(null);
				this.xy(xx,yy);
				console.log("clear",this.internalId,xx,yy,this.x(),this.y());
			}
		}
		this.group = group;
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
