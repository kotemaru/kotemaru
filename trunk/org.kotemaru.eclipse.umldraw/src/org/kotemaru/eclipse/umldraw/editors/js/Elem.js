

function Elem(){this.initialize.apply(this, arguments)};
(function(_class){
	_class.prototype.isDrawable=false;
	_class.prototype.isDraggable=false;
	_class.prototype.isRectangle=false;

	/**
	 * コンストラクタ。
	 */
	_class.prototype.initialize = function(coorBase) {
		this.coor = new Coor(coorBase);
	}
	_class.prototype.toJson = function() {
		return {coor: this.coor.toJsonRef()};
	}
	
	_class.prototype.origin = function(v) {
		return this.coor.origin(v);
	}
	_class.prototype.xy = function(xx,yy) {
		return this.coor.xy(xx,yy);
	}
	_class.prototype.x = function(v) {
		return this.coor.x(v);
	}
	_class.prototype.y = function(v) {
		return this.coor.y(v);
	}
	_class.prototype.w = function(v) {
		return 0;
	}
	_class.prototype.h = function(v) {
		return 0;
	}

	_class.prototype.onPoint = function(tx,ty) {
		with (this) {
			var x1 = coor.x();
			var y1 = coor.y();
			var x2 = x1 + w();
			var y2 = y1 + h();
			return x1<=tx && tx<=x2 && y1<=ty && ty<=y2;
		}
	}
	_class.prototype.inRect = function(tx1,ty1,tx2,ty2) {
		with (this) {
			var x1 = coor.x();
			var y1 = coor.y();
			var x2 = x1 + w();
			var y2 = y1 + h();
			return tx1<=x1 && x2<=tx2 && ty1<=y1 && y2<=ty2;
		}
	}
	_class.prototype.getOutBounds = function() {
		with (this) {
			var x1 = coor.x();
			var y1 = coor.y();
			return {x1:x1, y1:y1, x2:x1+w(), y2:y1+h()};
		}
	}

	_class.prototype.draw = function(dc) {
		throw "abstract";
	}
	
	
})(Elem);



//EOF
