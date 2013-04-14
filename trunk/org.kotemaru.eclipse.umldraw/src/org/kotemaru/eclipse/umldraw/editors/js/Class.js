

function Class(){this.initialize.apply(this, arguments)};
(function(_class, _super){
	_class.prototype = new _super();

	/**
	 * コンストラクタ。
	 */
	_class.prototype.initialize = function(coorBase) {
		_super.prototype.initialize.apply(this, arguments);
		this.isAutoSize = true;
		
		this.ptype = "protoEg-type";
		this.name = "XxxxEgClass\nEgEG";
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
	

	_class.prototype.draw = function(dc) {
		with (this) {
			var size1 = Font.textSize(dc, Font.S, ptype);
			var size2 = Font.textSize(dc, Font.M, name);
			var size3 = Font.textSize(dc, Font.M, attrs);
			var size4 = Font.textSize(dc, Font.M, methods);
			
			if (isAutoSize) {
				this._w = Util.grid(Math.max(size1.w, size2.w, size3.w, size4.w,100)+4);
				this._h = Util.grid(size1.h + size2.h + size3.h + size4.h + 16);
			}
			
			var x1 = coor.x();
			var y1 = coor.y();
			dc.save();
			dc.rect(x1-1,y1-1,_w+2,_h+2);
			dc.clip();
			
			DrawUtil.drawBox(dc, x1, y1, _w, _h);
			
			var yy = y1 + 2;
			DrawUtil.drawText(dc, Font.S, ptype, x1+2, yy);
			yy += size1.h;
			
			DrawUtil.drawText(dc, Font.M, name, x1+2, yy);
			yy += size2.h+4;
			dc.lineWidth = 1;
			dc.strokeRect(x1, yy, _w, 0);

			DrawUtil.drawText(dc, Font.M, attrs, x1+2, yy);
			yy += size3.h+4;
			dc.strokeRect(x1, yy, _w, 0);
			
			DrawUtil.drawText(dc, Font.M, methods, x1+2, yy);
			
			dc.restore();		
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
