

function Class(){this.initialize.apply(this, arguments)};
(function(_class, _super){
	Lang.extend(_class, _super);
	_class.attributes = Lang.copy(_super.attributes, {
		ptype      : "",
		name       : "",
		attrs      : "",
		methods    : "",
		isAutoSize : true
	});

	/**
	 * コンストラクタ。
	 */
	_class.prototype.initialize = function(coorBase) {
		_super.prototype.initialize.apply(this, arguments);
		
		this.isAutoSize = true;
		this.ptype = "protoEg-type";
		this.name = "XxxxEgClass\nEgEGnEgEG";
		this.attrs = "";
		this.methods = "";
	}

	_class.prototype.setW = function(v) {
		this._w = v;
		this.isAutoSize = false;
	}
	_class.prototype.setH = function(v) {
		this._h = v;
		this.isAutoSize = false;
	}
	

	_class.prototype.draw = function(dr) {
		with (this) {
			var size1 = dr.textSize(Font.S, ptype);
			var size2 = dr.textSize(Font.M, name);
			var size3 = dr.textSize(Font.M, attrs);
			var size4 = dr.textSize(Font.M, methods);
			
			if (isAutoSize) {
				this._w = Util.grid(Math.max(size1.w, size2.w, size3.w, size4.w,100)+4);
				this._h = Util.grid(size1.h + size2.h + size3.h + size4.h + 16);
			}
			
			var w1 = w();
			var h1 = h();
			var x1 = coor.x();
			var y1 = coor.y();
			var x2 = x1+w1;
			var y2 = y1+h1;
			dr.clipStart(x1,y1,w1,h1);
			
			dr.drawBox(x1, y1, w1, h1);
			
			var yy = y1 + 2;
			dr.drawText(Font.S, ptype, x1+2, yy);
			yy += size1.h;
			
			dr.drawText(Font.M, name, x1+2, yy);
			yy += size2.h+4;
			dr.drawLine(x1, yy, x2, yy);

			dr.drawText(Font.M, attrs, x1+2, yy);
			yy += size3.h+4;
			dr.drawLine(x1, yy, x2, yy);
			
			dr.drawText(Font.M, methods, x1+2, yy);
			dr.clipEnd();
		}
		
		return this;
	}
	
	_class.prototype.drawHandle = function(dc) {
		this.handle.begin.draw(dc);
		this.handle.end.draw(dc);
	}
	_class.prototype.getHandle = function(xx,yy) {
		with (this.handle) {
			if (begin.onPoint(xx,yy)) return begin;
			if (end.onPoint(xx,yy)) return end;
		}
		return null;
	}
	
	_class.prototype.getDialog = function() {
		return "#dialogClass";
	}
	
})(Class, Rectangle);



//EOF
