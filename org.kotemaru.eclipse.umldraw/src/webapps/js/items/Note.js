

function Note(){this.initialize.apply(this, arguments)};
(function(_class, _super){
	Item.extend(_class, _super);
	_class.properties = Lang.copy(_super.properties, {
		comment : {type: "string", value:""},
		isStrip: {type: "boolean", value:false},
		fontSize: {type: "string", value:"M"}
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
			var size1 = dr.textSize(Font[fontSize], comment, 1);
			
			if (isAutoSize) {
				this._w = Util.grid(Math.max(size1.w, 100)+4);
				this._h = Util.grid(size1.h + 4);
			}
			
			var w1 = w();
			var h1 = h();
			var x1 = coor.x();
			var y1 = coor.y();
			var x2 = x1+w1;
			var y2 = y1+h1;
			var x3 = x2-6;
			var y3 = y1+6;
			dr.clipStart(x1,y1,w1,h1);
			
			var points = [
				{x:x3,y:y1}, {x:x2,y:y3}, {x:x2,y:y2},
				{x:x1,y:y2}, {x:x1,y:y1}, {x:x3,y:y1},
				{x:x3,y:y3}, {x:x2,y:y3}
			];
			if (isStrip) {
				dr.drawPolyGuide(points);
			} else {
				dr.drawPoly(points);
			}
			dr.drawText(Font[fontSize], comment, x1+2, y1+2);
			dr.clipEnd();
		}
		
		return this;
	}
	
	
	_class.prototype.getDialog = function() {
		return "#noteDialog";
	}
	
})(Note, Rectangle);


//EOF
