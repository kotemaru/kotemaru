

function Disk(){this.initialize.apply(this, arguments)};
(function(_class, _super){
	Item.extend(_class, _super);
	_class.properties = Lang.copy(_super.properties, {
		name : {type: "string", value:""},
	});
	
	/**
	 * コンストラクタ。
	 */
	_class.prototype.initialize = function(attrs) {
		Lang.initAttibutes(this, _class.properties);
		_super.prototype.initialize.apply(this, arguments);
		Lang.mergeAttibutes(this, attrs);
	}

	_class.prototype.draw = function(dr) {
		with (this) {
			var size1 = dr.textSize(Font.M, name, 1);
			
			if (isAutoSize) {
				this._w = Util.grid(Math.max(size1.w, 100)+4);
				this._h = Util.grid(size1.h + 16);
			}
			
			var w1 = w();
			var h1 = h();
			var x1 = coor.x();
			var y1 = coor.y();
			var x2 = x1+w1;
			var y2 = y1+h1;
			var cx = x1+w1/2;
			var cy = y1+h1/2;
			
			dr.clipStart(x1,y1,w1,h1);
			dr.ellipse(x1,y2-8,w1,8);
			dr.whiteBox(x1,y1+4,w1,h1-8);
			dr.drawVLine(x1,y1+4,h1-8,2);
			dr.drawVLine(x2,y1+4,h1-8,2);
			dr.ellipse(x1,y1,w1,8);
			dr.drawText(Font.M, name, x1+2, y1+10);
		
			dr.clipEnd();
		}
		
		return this;
	}
	function arc(dc, x,y,width,height){
		var radW = width/2;
		var radH = height/2;
		x = x + radW;
		y = y + radH;
		dc.lineWidth = 2;
		dc.strokeStyle = "black";
		dc.fillStyle = "white";
		dc.beginPath();
		dc.bezierCurveTo(x, y - radH, x + radW , y - radH, x + radW, y);
		dc.bezierCurveTo(x + radW, y, x + radW, y + radH, x, y + radH);
		dc.bezierCurveTo(x, y + radH, x - radW, y + radH, x - radW, y);
		dc.bezierCurveTo(x - radW, y, x - radW, y - radH, x, y - radH);
		//dc.closePath();
		dc.fill();
		dc.stroke();
	};

	
	_class.prototype.getDialog = function() {
		return "#diskDialog";
	}
	
})(Disk, Rectangle);


//EOF
