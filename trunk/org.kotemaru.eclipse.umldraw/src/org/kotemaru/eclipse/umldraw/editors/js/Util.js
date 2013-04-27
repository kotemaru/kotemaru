

function Util(){this.initialize.apply(this, arguments)};
(function(_class){

	_class.crossRectLine = function(rect, coor1, coor2) {
		if (rect == null || !rect.isRectangle ) return [];
		var rx1 = rect.x();
		var ry1 = rect.y();
		var rx2 = rect.x() + rect.w();
		var ry2 = rect.y() + rect.h();

		var lx1 = coor1.x();
		var ly1 = coor1.y();
		var lx2 = coor2.x();
		var ly2 = coor2.y();

		return _class.crossRectLineRaw(rx1,ry1,rx2,ry2, lx1,ly1,lx2,ly2);
	}
	
	_class.crossRectLineRaw = function(rx1,ry1,rx2,ry2, lx1,ly1,lx2,ly2) {
		var points = [];
		addClassPoint(points, rx1,ry1,rx2,ry1, lx1,ly1,lx2,ly2);
		addClassPoint(points, rx1,ry1,rx1,ry2, lx1,ly1,lx2,ly2);
		addClassPoint(points, rx2,ry1,rx2,ry2, lx1,ly1,lx2,ly2);
		addClassPoint(points, rx1,ry2,rx2,ry2, lx1,ly1,lx2,ly2);
		return points;
	}
	
	function addClassPoint(points, x1,y1, x2,y2, x3,y3, x4,y4) {
		var point = clossLinePoint(x1,y1, x2,y2, x3,y3, x4,y4);
		if (point) {
			points.push(point);
		}
	}
	
	function clossLinePoint(x1,y1, x2,y2, x3,y3, x4,y4){
		var ksi   = (y4-y3)*(x4-x1) - (x4-x3)*(y4-y1);
		var eta   = (x2-x1)*(y4-y1) - (y2-y1)*(x4-x1);
		var delta = (x2-x1)*(y4-y3) - (y2-y1)*(x4-x3);
	
		var ramda = ksi / delta;
		var mu    = eta / delta;
	
		if ((ramda>=0 && ramda<=1) && (mu>=0 && mu<=1))	{
			return {
				x: x1 + ramda*(x2-x1),
				y: y1 + ramda*(y2-y1)
			};
		}
		return null;
	}	
	_class.getCenter = function(line) {
		var x1 = line.x1;
		var y1 = line.y1;
		var x2 = line.x2;
		var y2 = line.y2;
		return {x:(x1+(x2-x1)/2), y:(y1+(y2-y1)/2)};
	}
	
	_class.grid = function(n) {
		var m = n%4;
		if (m == 0) return n;
		return n + (4-m);
	}
	
	_class.update = function(src, dst) {
		for (var k in dst) src[k] = dst[k];
		return src;
	}
	
	_class.formalRect = function(x1,y1,x2,y2) {
		return {
			x1: Math.min(x1,x2), y1: Math.min(y1,y2),
			x2: Math.max(x1,x2), y2: Math.max(y1,y2)
		};
	}
	_class.getOutBounds = function(items) {
		var x1 = 1000000;
		var y1 = 1000000;
		var x2 = 0;
		var y2 = 0;
		
		for (var i=0; i<items.length; i++) {
			var bounds = items[i].getOutBounds ? items[i].getOutBounds() : items[i];
			x1 = Math.min(x1, bounds.x1, bounds.x2);
			y1 = Math.min(y1, bounds.y1, bounds.y2);
			x2 = Math.max(x2, bounds.x1, bounds.x2);
			y2 = Math.max(y2, bounds.y1, bounds.y2);
		}
		return {x1:x1, y1:y1, x2:x2, y2:y2, w:(x2-x1), h:(y2-y1)};
	}
	_class.getOutBoundsEach = function(items) {
		var x1 = 1000000;
		var y1 = 1000000;
		var x2 = 0;
		var y2 = 0;
		
		for (var i in items) {
			var bounds = items[i].getOutBounds ? items[i].getOutBounds() : items[i];
			x1 = Math.min(x1, bounds.x1, bounds.x2);
			y1 = Math.min(y1, bounds.y1, bounds.y2);
			x2 = Math.max(x2, bounds.x1, bounds.x2);
			y2 = Math.max(y2, bounds.y1, bounds.y2);
		}
		return {x1:x1, y1:y1, x2:x2, y2:y2, w:(x2-x1), h:(y2-y1)};
	}
	
	_class.drawOutBounds = function(dc, x1,y1,x2,y2) {
		dc.fillStyle   = Color.GUIDE;
		dc.strokeStyle = Color.GUIDE;
		dc.globalAlpha = 0.05;
		dc.fillRect(x1, y1, (x2-x1), (y2-y1));
		dc.lineWidth = 1;
		dc.globalAlpha = 1.0;
		dc.strokeRect(x1-0.5, y1-0.5, (x2-x1)+1, (y2-y1)+1);
	}
	
	
})(Util);


//EOF
