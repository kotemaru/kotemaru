

function CableUtil(){this.initialize.apply(this, arguments)};
(function(_class){

	
	/**
	 * コンストラクタ。
	 */
	_class.prototype.initialize = function(coorBase) {
	}
	
	
	_class.edgePoint = function(item,xx,yy) {
		if (item == null || xx==null || yy==null || !item.isRectangle) return null;
		
		var w=item.w(), h=item.h();
		var x1=item.x(), y1=item.y();
		var x2=x1+w, y2=y1+h;
		var edgeW = w>12 ? 4 : w/3;
		var edgeH = h>12 ? 4 : h/3;

		if (x1+edgeW<xx && xx<x2-edgeW && y1+edgeH<yy && yy<y2-edgeH) {
			return null; // center;
		}

		var res = {x:xx, y:yy};
		if (x1+edgeW>xx) res.x = x1;
		if (xx>x2-edgeW) res.x = x2;
		if (y1+edgeH>yy) res.y = y1;
		if (yy>y2-edgeH) res.y = y2;
		return res;
	}

	
	_class.getLines = function(self) {
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
	
	function toEdge(coor1, coor2) {
		var points = Util.crossRectLine(coor1.origin(), coor1, coor2);
		if (points.length == 0) return {x:coor1.x(), y:coor1.y()};
		return points[0];
	}
	
})(CableUtil);


//EOF
