

function SelectGroup(){this.initialize.apply(this, arguments)};
(function(_class, _super){
	Lang.extend(_class, _super);

	/**
	 * コンストラクタ。
	 */
	_class.prototype.initialize = function(coorBase) {
		_super.prototype.initialize.apply(this, arguments);
	}
	
	_class.prototype.isValid = function() {
		return this.items.size()>0;
	}
	
	_class.prototype.drawHandle = function(dc) {

		this.items.each(function(item){
			item.drawHandle(dc);
		});
		
		if (this.items.size()<=0) return;
		var b = this.items.getOutBounds();
		Util.drawOutBounds(dc, b.x1, b.y1, b.x2, b.y2);
		//this.handle.begin.draw(dc);
		//this.handle.end.draw(dc);
	}

	
})(SelectGroup, Group);



//EOF
