

function Items(){this.initialize.apply(this, arguments)};
(function(_class){
	_class.prototype.initialize = function() {
		this.children = {};
	}
	_class.prototype.size = function() {
		var size = 0;
		for (var i in this.children) size++;
		return size;
	}
	_class.prototype.getItems = function() {
		return this.children;
	}
	
	_class.prototype.clear = function() {
		this.children = {};
		return this;
	}
	_class.prototype.addItem = function(item) {
		if (this.children[item.internalId]) {
			throw "Confrict";
		}
		this.children[item.internalId] = item;
	}
	_class.prototype.delItem = function(item) {
		delete this.children[item.internalId];
	}
	_class.prototype.getItem = function(ex,ey, ignore) {
		for (var i in this.children) {
			if (this.children[i] != ignore && this.children[i].onPoint(ex, ey)) {
				return this.children[i];
			}
		}
		return null;
	}
	
	_class.prototype.draw = function(drawer) {
		for (var i in this.children) {
			drawer.beginItem(this.children[i]);
			this.children[i].draw(drawer);
			drawer.endItem(this.children[i]);
		}
	}
	_class.prototype.drawHandle = function(context2d) {
		for (var i in this.children) {
			this.children[i].drawHandle(context2d);
		}
	}
	
	_class.prototype.getOutBounds = function() {
		return Util.getOutBoundsEach(this.children);
	}
	
	_class.prototype.each = function(callback, deep) {
		for (var i in this.children) {
			var done = callback(this.children[i]);
			if (done) return;
		}
	}
	
	
	
})(Items);


//EOF
