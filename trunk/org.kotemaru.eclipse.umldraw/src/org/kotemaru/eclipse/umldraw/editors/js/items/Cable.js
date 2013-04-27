

function Cable(){this.initialize.apply(this, arguments)};
(function(_class,_super){
	Lang.extend(_class, _super);
	_class.attributes = Lang.copy(_super.attributes, {
		lineType   : "normal",
		lineRoute  : "N",
		startType  : "none",
		endType    : "none",
		startText  : "",
		centerText : "",
		endText    : "",
		points     : [new Point()],
		startPoint : new Point(),
		endPoint   : new Point(),
	});

	
	/**
	 * コンストラクタ。
	 */
	_class.prototype.initialize = function(coorBase) {
		_super.prototype.initialize.apply(this, arguments);
		
		this.lineType  = "normal";
		this.lineRoute  = "N";
		this.startType = "none";
		this.endType   = "none";
		
		this.startText  = "startText";
		this.centerText = "centerText";
		this.endText    = "endText";
		
		this.points = [];
		this.startPoint = new Coor();
		//this.startPoint.handle = 
		//	new CableHandle(this.startPoint, this, "setStartPoint")
		this.endPoint = new Coor();
		//this.endPoint.handle = 
		//	new CableHandle(this.endPoint, this, "setEndPoint")
		//this.startPoint.handle.color = Color.HANDLE_START;
		//this.endPoint.handle.color = Color.HANDLE_END;

		this.setStartPoint(new Coor(coorBase));
		this.setEndPoint(new Coor({origin:this.startPoint, x:20, y:20}));
	}
	
	_class.prototype.addPoint = function(coor) {
		var idx = this.onPointIndex(coor.x(),coor.y());
		if (idx >= 0) {
			this.points.splice(idx,0,coor);
			// clear handle.
			for (var i=0; i<this.points.length; i++) {
				this.points[i].handle = null;
			}
		}
	}
	
	_class.prototype.setStartPoint = function(item) {
		setPoint(this.startPoint, item);
	}
	_class.prototype.setEndPoint = function(item) {
		setPoint(this.endPoint, item);
	}
	_class.prototype.setPoint = function(item, no) {
		var coor = this.points[no];
		coor.xy(item.x(), item.y());
	}
	function setPoint(coor, item) {
		if (item) {
			coor.origin(item);
			coor.origin2(item.coorDiag);
			if (item.coorDiag) {
				// TODO:
				coor._x = 0.5;
				coor._y = 0.5;
			} else {
				coor._x = 0;
				coor._y = 0;
			}
		} else {
			var x0 = coor.x();
			var y0 = coor.y();
			coor.origin(null);
			coor._origin = null; // TOOD;
			coor.origin2(null);
			coor.xy(x0,y0);
		}
	}
	_class.prototype.onPoint = function(tx,ty) {
		return this.onPointIndex(tx,ty)>=0;
	}
	_class.prototype.inRect = function(tx1,ty1,tx2,ty2) {
		var b = Util.getOutBounds(getLines(this));
		return tx1<=b.x1 && b.x2<=tx2 && ty1<=b.y1 && b.y2<=ty2;
	}
	_class.prototype.getOutBounds = function() {
		return Util.getOutBounds(getLines(this));
	}
	_class.prototype.onPointIndex = function(tx,ty) {
		var r = 3;
		var rx1 = tx-r;
		var ry1 = ty-r;
		var rx2 = tx+r;
		var ry2 = ty+r;
		var lines = getLines(this);
		
		for (var i=0; i<lines.length; i++) {
			var hits = Util.crossRectLineRaw(rx1,ry1,rx2,ry2, 
					lines[i].x1, lines[i].y1, lines[i].x2, lines[i].y2);
			if (hits.length>0) return i;
		}
		return -1;
	}

	_class.prototype.draw= function(dr) {
		with (this) {
			var lines = getLines(this);
			if (lineRoute == "S") {
				dr.drawLinesS(lines, lineType);
			} else {
				dr.drawLines(lines, lineType);
			}
	
			dr.drawArrow(startType, 
					lines[0].x2, lines[0].y2, lines[0].x1, lines[0].y1);
			
			var i = lines.length-1;
			dr.drawArrow(endType, 
					lines[i].x1, lines[i].y1, lines[i].x2, lines[i].y2);

			drawText(dr, lines[0], 0);
			drawText(dr, lines[Math.floor(lines.length/2)], 1);
			drawText(dr, lines[i], 2);
		}
		return this;
	}
	
	
	
	_class.prototype.drawText = function(dr, line, mode) {
		with (this) {
			var x1 = line.x1;
			var y1 = line.y1;
			var x2 = line.x2;
			var y2 = line.y2;
			var x3 = x1 + (x2 - x1)/2;
			var y3 = y1 + (y2 - y1)/2;
	
			var size1 = dr.textSize(Font.M, startText);
			var size2 = dr.textSize(Font.M, endText);
			var size3 = dr.textSize(Font.M, centerText);
			var w1 = size1.w;
			var w2 = size2.w;
			var w3 = size3.w;
	
			var PADDING = 8;
			if (x1 < x2) {
				x1 = x1 + PADDING;
				x2 = x2 - w2 - PADDING;
			} else {
				x1 = x1 - w1 - PADDING;
				x2 = x2 + PADDING;
			}
			if (y1 < y2) {
				y2 = y2 - size1.h;
			} else {
				y1 = y1 - size1.h;
			}
			x3 = x3 - w3/2;
			y3 = y3 - size1.h/2;

			if (mode == 0) {
				dr.drawTextLine(Font.M, startText,  x1, y1);
			} else if (mode == 1) {
				dr.drawTextLine(Font.M, centerText, x3, y3);
			} else {
				dr.drawTextLine(Font.M, endText,    x2, y2);
			}
		}
	}
	
	function getLines(self) {
		if (self.lineRoute == "L") {
			return getLinesL(self.startPoint, self.points, self.endPoint);
		} else {
			return getLinesN(self.startPoint, self.points, self.endPoint);
		}
	}
	
	function getLinesN(startPoint,points,endPoint) {
		var lines = [];
		var coor1 = startPoint;
		var coor2 = points.length>0 ? points[0] : endPoint;
		var xy = toEdge(coor1, coor2);
		var firstXy = xy;
		var beforeXy = xy;

		for (var i=0; i<points.length; i++) {
			var coor = points[i];
			xy = {x:coor.x(), y:coor.y()};
			lines.push({x1:beforeXy.x, y1:beforeXy.y, x2:xy.x, y2:xy.y});
			beforeXy = xy;
		}
		
		var i = points.length-1;
		coor1 = points.length>0 ? points[i] : startPoint;
		coor2 = endPoint;
		xy = toEdge(coor2, coor1);
		lines.push({x1:beforeXy.x, y1:beforeXy.y, x2:xy.x, y2:xy.y});
		return lines;
	}

	
	function getLinesL(startPoint,points,endPoint) {
		var lines = [];
		function pt(vertical, pt1, pt2) {
			if (vertical) {
				return new Point(pt1.x(), pt2.y());
			} else {
				return new Point(pt2.x(), pt1.y());
			}
		}
		function initVertical(pt1, pt2) {
			var ww = pt1.x()-pt2.x();
			var hh = pt1.y()-pt2.y();
			return ww>hh;
		}
		var Lpoints = [];
	
		var pt1 = startPoint;
		var pt2 = points.length>0 ? points[0] : endPoint;
		var vertical = initVertical(pt1,pt2);
		Lpoints.push(pt(vertical,pt1,pt2));
		vertical = !vertical;
		pt1 = pt2;
		
		for (var i=1; i<points.length; i++) {
			pt2 = points[i];
			Lpoints.push(pt(vertical,pt1,pt2));
			vertical = !vertical;
			pt1 = pt2;
		}
		if (pt1 != endPoint) {
			pt2 = endPoint;
			Lpoints.push(pt(vertical,pt1,pt2));
		}
			
		return getLinesN(startPoint,Lpoints,endPoint);
	}
	
	_class.prototype.getHandle = function(xx,yy) {
		with (this) {
			for (var i=0; i<points.length; i++) {
				var handle = points[i].handle;
				if (handle.isOnCoor(xx,yy)) return handle;
			}
		}
		return null;
	}
	
	function toEdge(coor1, coor2) {
		var points = Util.crossRectLine(coor1.origin(), coor1, coor2);
		if (points.length == 0) return {x:coor1.x(), y:coor1.y()};
		return points[0];
	}
	
	function makeHandle(coor, self, method, no) {
		if (coor.handle) return;
		var color = Color.HANDLE_VISIT;
		if (method == "setStartPoint") color = Color.HANDLE_START;
		if (method == "setEndPoint") color = Color.HANDLE_END;
		coor.handle = new CableHandle(coor, self, method, no);
		coor.handle.color = color;
	}
	_class.prototype.makeHandle = function(coor) {
		makeHandle(this.startPoint,this,"setStartPoint");
		makeHandle(this.endPoint,this,"setEndPoint");
		for (var i=0; i<this.points.length; i++) {
			makeHandle(this.points[i],this,"setPoint", i);
		}
	}	
	_class.prototype.drawHandle = function(dc) {
		this.makeHandle();
		this.startPoint.handle.draw(dc);
		this.endPoint.handle.draw(dc);
		for (var i=0; i<this.points.length; i++) {
			this.points[i].handle.draw(dc);
		}
	}
	_class.prototype.getHandle = function(xx,yy) {
		with (this) {
			if (startPoint.handle.onPoint(xx,yy)) return startPoint.handle;
			if (endPoint.handle.onPoint(xx,yy)) return endPoint.handle;
			for (var i=0; i<points.length; i++) {
				if (points[i].handle.onPoint(xx,yy)) return points[i].handle;
			}
		}
		return null;
	}
	_class.prototype.getMenu = function() {
		return "#cableMenu";
	}
	_class.prototype.getDialog = function() {
		return "#dialogCable";
	}
	_class.prototype.doMenuItem = function($menuItem,xx,yy) {
		var cmd = $menuItem.attr("data-value");
		if (cmd == "addPoint") {
			var coor = new Coor({
				origin:this.startPoint, 
				origin2:this.endPoint,
			});
			coor.xy(xx,yy);
			this.addPoint(coor);
		} else if (cmd == "fixPoint") {
			// TODO:

		} else if (cmd == "properties") {
			Dialog.open(this.getDialog(), this);
		}
	}
	
})(Cable,Item);


//EOF
