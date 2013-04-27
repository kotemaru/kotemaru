

function Rectangle(){this.initialize.apply(this, arguments)};
(function(_class, _super){
	Lang.extend(_class, _super);
	_class.prototype.isDraggable=true;
	_class.prototype.isRectangle=true;
	_class.attributes = Lang.copy(_super.attributes, {
		_w : 0,
		_h : 0
	});

	/**
	 * コンストラクタ。
	 */
	_class.prototype.initialize = function(origin, cable, setterName) {
		_super.prototype.initialize.apply(this, arguments);
		this._w = 0;
		this._h = 0;
		
		this.coorDiag = new CoorDiag({origin:this});
		this.handle = {
			begin: new Handle(this),
			end:   new Handle(this.coorDiag),
		};
		var self = this;
		this.handle.end.color = Color.HANDLE_END;
		this.handle.end.dragMove = function(xx,yy) {
			self.setW(xx-self.x());
			self.setH(yy-self.y());
		}
	}
	_class.prototype.getHandle = function(xx,yy) {
		with (this.handle) {
			if (begin.onPoint(xx,yy)) return begin;
			if (end.onPoint(xx,yy)) return end;
		}
		return null;
	}
	
	_class.prototype.w = function() {
		return this._w;
	}
	_class.prototype.h = function() {
		return this._h;
	}
	
	_class.prototype.setW = function(v) {
		this._w = v;
	}
	_class.prototype.setH = function(v) {
		this._h = v;
	}
	
	// implements Draggable
	_class.prototype.dragStart = function(tx,ty, ev) {
		// nop
	}
	_class.prototype.dragMove = function(tx,ty, ev) {
		this.xy(tx,ty);
	}
	_class.prototype.dragEnd = function(tx,ty, ev) {
		// nop.
	}


})(Rectangle, Item);

//EOF
