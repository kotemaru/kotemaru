

function Handle(){this.initialize.apply(this, arguments)};
(function(_class, _super){
	_class.prototype = new _super();
	_class.prototype.isDraggable=true;

	_class.COLOR_START = "#ff8888";
	_class.COLOR_VISIT = "#88ff88";
	_class.COLOR_END   = "#88aaff";

	var SIZE = 8;
	
	/**
	 * コンストラクタ。
	 */
	_class.prototype.initialize = function(origin) {
		_super.prototype.initialize.apply(this, [{origin:origin, x:-(SIZE/2), y:-(SIZE/2)}]);
		this.color = _class.COLOR_START;
	}
	_class.prototype.w = function() {
		return SIZE;
	}
	_class.prototype.h = function() {
		return SIZE;
	}
	
	_class.prototype.draw = function(dc) {
		with (this) {
			var x1 = Math.ceil(coor.x());
			var y1 = Math.ceil(coor.y());
			dc.globalAlpha = 0.5;
			dc.fillStyle = color;
			dc.fillRect(x1, y1, SIZE, SIZE);
			dc.globalAlpha = 1.0;
			dc.strokeStyle = color;
			dc.lineWidth = 1;
			dc.strokeRect(x1-0.5, y1-0.5, SIZE, SIZE);
		}
		return this;
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

})(Handle, Elem);
