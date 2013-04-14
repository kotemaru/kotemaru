

function Cable(){this.initialize.apply(this, arguments)};
(function(_class,_super){
	_class.prototype = new _super();
	
	/**
	 * コンストラクタ。
	 */
	_class.prototype.initialize = function(coorBase) {
		this.lineType  = "normal";
		this.startType = "none";
		this.endType   = "none";
		
		this.points = [];
		var coor1 = new Coor(), coor2 = new Coor();
		this.startPoint = {
			coor: coor1, 
			handle:new HandleCable(coor1, this, "setStartPoint")
		};
		this.endPoint   = {
			coor: coor2, 
			handle:new HandleCable(coor2, this, "setEndPoint")
		};
		this.startPoint.handle.color = Handle.COLOR_START;
		this.endPoint.handle.color = Handle.COLOR_END;

		this.setStartPoint(new Coor(coorBase));
		this.setEndPoint(new Coor({origin:this.startPoint.coor, x:20, y:20}));
	}
	
	_class.prototype.addPoint = function(coor) {
		var handle = new HandleCable(coor, this, "setPoint", this.points.length);
		this.points.push({coor:coor, handle:handle});
	}
	
	_class.prototype.setStartPoint = function(item) {
		setPoint(this.startPoint.coor, item);
	}
	_class.prototype.setEndPoint = function(item) {
		setPoint(this.endPoint.coor, item);
	}
	_class.prototype.setPoint = function(item, no) {
		var coor = this.points[no].coor;
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
		var r = 3;
		var rx1 = tx-r;
		var ry1 = ty-r;
		var rx2 = tx+r;
		var ry2 = ty+r;
		with (this) {
			var coor1 = startPoint.coor;
			var coor2 = points.length>0 ? points[0].coor : endPoint.coor;
			var xy = toEdge(coor1, coor2);
			var lx1 = xy.x;
			var ly1 = xy.y;
			var lx2;
			var ly2;
		
			for (var i=0; i<points.length; i++) {
				var coor = points[i].coor;
				lx2 = coor.x();
				ly2 = coor.y();
				var hits = Util.crossRectLineRaw(rx1,ry1,rx2,ry2, lx1,ly1,lx2,ly2);
				if (hits.length>0) return true;
				lx1 = lx2;
				ly1 = ly2;
			}
			var i = points.length-1;
			coor1 = points.length>0 ? points[i].coor : startPoint.coor;
			coor2 = endPoint.coor;
			xy = toEdge(coor2, coor1);
			lx2 = xy.x;
			ly2 = xy.y;
			hits = Util.crossRectLineRaw(rx1,ry1,rx2,ry2, lx1,ly1,lx2,ly2);
			if (hits.length>0) return true;
		}
		return false;
	}

	_class.prototype.draw= function(dc) {
		with (this) {
			var lines = getLines(this);
			
			dc.strokeStyle = "black";
			dc.lineWidth = 2;
			dc.beginPath();
			
			dc.moveTo(lines[0].x1, lines[0].y1);
			for (var i=0; i<lines.length; i++) {
				dc.lineTo(lines[i].x2, lines[i].y2);
			}
		
			dc.stroke();
			dc.closePath();
	
			DrawUtil.drawArrow(dc, startType, 
					lines[0].x2, lines[0].y2, lines[0].x1, lines[0].y1);
			
			var i = lines.length-1;
			DrawUtil.drawArrow(dc, endType, 
					lines[i].x1, lines[i].y1, lines[i].x2, lines[i].y2);
		}
		return this;
	}
	
	function getLines(self) {
		var lines = [];
		with (self) {
			var coor1 = startPoint.coor;
			var coor2 = points.length>0 ? points[0].coor : endPoint.coor;
			var xy = toEdge(coor1, coor2);
			var firstXy = xy;
			var beforeXy = xy;

			for (var i=0; i<points.length; i++) {
				var coor = points[i].coor;
				xy = {x:coor.x(), y:coor.y()};
				lines.push({x1:beforeXy.x, y1:beforeXy.y, x2:xy.x, y2:xy.y});
				beforeXy = xy;
			}
			
			var i = points.length-1;
			coor1 = points.length>0 ? points[i].coor : startPoint.coor;
			coor2 = endPoint.coor;
			xy = toEdge(coor2, coor1);
			lines.push({x1:beforeXy.x, y1:beforeXy.y, x2:xy.x, y2:xy.y});
		}
		return lines;
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
	
	
	_class.prototype.drawHandle = function(dc) {
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
	_class.prototype.getMenu = function(xx,yy) {
		return $("#menuCable");
	}
	_class.prototype.getDialog = function(xx,yy) {
		return "#dialogCable";
	}
	_class.prototype.doMenuItem = function($menuItem,xx,yy) {
		var cmd = $menuItem.attr("data-alt");
		if (cmd == "addPoint") {
			var coor = new Coor({
				origin:this.startPoint.coor, 
				origin2:this.endPoint.coor,
			});
			coor.xy(xx,yy);
			this.addPoint(coor);
		}
	}
	
})(Cable,Item);


//EOF
