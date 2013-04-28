

function Class(){this.initialize.apply(this, arguments)};
(function(_class, _super){
	Lang.extend(_class, _super);
	_class.attributes = Lang.copy(_super.attributes, {
		stype      : {type: "string", value:""},
		name       : {type: "string", value:""},
		attrs      : {type: "string", value:""},
		methods    : {type: "string", value:""},
	});

	/**
	 * コンストラクタ。
	 */
	_class.prototype.initialize = function(attrs) {
		Lang.initAttibutes(this, _class.attributes);
		_super.prototype.initialize.apply(this, arguments);
		Lang.mergeAttibutes(this, attrs);
	}

	_class.prototype.draw = function(dr) {
		with (this) {
			var sType = stype!="" ? "<<"+stype+">>" : "";
			var size1 = dr.textSize(Font.S, sType, 1);
			var size2 = dr.textSize(Font.M, name, 1);
			var size3 = dr.textSize(Font.M, attrs);
			var size4 = dr.textSize(Font.M, methods);
			
			if (isAutoSize) {
				this._w = Util.grid(Math.max(size1.w, size2.w, size3.w, size4.w,100)+4);
				this._h = Util.grid(size1.h + size2.h + size3.h + size4.h + 14);
			}
			
			var w1 = w();
			var h1 = h();
			var x1 = coor.x();
			var y1 = coor.y();
			var x2 = x1+w1;
			var y2 = y1+h1;
			dr.clipStart(x1,y1,w1,h1);
			
			dr.drawBox(x1, y1, w1, h1);
			
			var yy = y1;
			var xx = x1+w1/2 - size1.w/2;
			dr.drawText(Font.S, sType, xx, yy);
			yy += size1.h;
			
			dr.drawText(Font.M, name, x1+2, yy);

			//以下したから

			yy = y1+h1 - 2 - size4.h;
			dr.drawText(Font.M, methods, x1+2, yy);
			yy = yy - 4;
			dr.drawLine(x1, yy, x2, yy);

			yy = yy - 2 - size3.h;
			dr.drawText(Font.M, attrs, x1+2, yy);
			yy = yy - 4;
			dr.drawLine(x1, yy, x2, yy);
			
			dr.clipEnd();
		}
		
		return this;
	}
	
	
	_class.prototype.getDialog = function() {
		return "#classDialog";
	}
	
})(Class, Rectangle);



//EOF
