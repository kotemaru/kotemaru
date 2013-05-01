

function Item(){this.initialize.apply(this, arguments)};
(function(_class, _super){
	_class.prototype = new _super();
	_class.prototype.isDrawable=true;
	_class.properties = {
		coor : {type:"Point", value:new Coor(0,0)},
		group : {type:"Group", value:null}
	};
	
	_class.extend = function(__class, __super) {
		Lang.define(__class);
		Lang.extend(__class, __super);
		Actions.registerAction(__class.name, new Action(__class));
	}
	
	var idCount = 1;
	
	_class.prototype.initialize = function(coorBase) {
		Lang.initAttibutes(this, _class.properties);
		_super.prototype.initialize.apply(this, arguments);
		this.internalId = idCount++;
	}
	_class.prototype.remove = function() {
		this.isRemove = true;
		this.coor.isRemove = true;
	}
	_class.prototype.setGroup = function(group) {
		if (group) {
			if (this.group)	throw "Duplicate group";
			if (this.coor.origin() == null) {
				var xx=this.x(),yy=this.y();
				this.coor.setOrigin(group);
				this.xy(xx,yy);
			}
		} else { // unset
			if (this.coor.origin() == this.group) {
				var xx=this.x(),yy=this.y();
				this.coor.setOrigin(null);
				this.xy(xx,yy);
			}
		}
		this.group = group;
	}
	_class.prototype.getMenu = function() {
		return "#itemMenu";
	}
	_class.prototype.doMenuItem = function($menuItem,xx,yy) {
		var cmd = $menuItem.attr("data-value");
		if (cmd == "properties") {
			Dialog.open(this.getDialog(), this);
		}
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
