

function Group(){this.initialize.apply(this, arguments)};
(function(_class, _super){
	Lang.extend(_class, _super);
	_class.prototype.isGroup = true;

	/**
	 * コンストラクタ。
	 */
	_class.prototype.initialize = function(coorBase) {
		_super.prototype.initialize.apply(this, arguments);
		this.items = new Items();
	}
	
	_class.prototype.clear = function() {
		this.items.each(function(item){
			item.setGroup(null);
		});
		this.items.clear();
		return this;
	}
	_class.prototype.fixed = function() {
		var b = this.items.getOutBounds();
		this.xy(b.x1,b.y1);
		var self = this;
		this.items.each(function(item){
			item.setGroup(self);
		});
	}
	
	_class.prototype.getItems = function() {
		return this.items;
	}

	_class.prototype.w = function(v) {
		return this.items.getOutBounds().w;
	}
	_class.prototype.h = function(v) {
		return this.items.getOutBounds().h;
	}
	

	_class.prototype.draw = function(dr) {
		this.items.draw(dr);
		return this;
	}
	
	_class.prototype.drawHandle = function(dc) {
		var b = this.items.getOutBounds();
		Uril.drawOutBounds(dc, b.x1, b.y1, b.x2, b.y2);
		this.handle.begin.draw(dc);
		this.handle.end.draw(dc);
	}

})(Group, Rectangle);


//EOF
