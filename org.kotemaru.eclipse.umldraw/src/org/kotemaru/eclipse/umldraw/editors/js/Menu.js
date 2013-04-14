

function Menu(){this.initialize.apply(this, arguments)};
(function(_class){
	
	_class.open = function(items) {
		
		for (var i=0; i<items.length; i++) {
			
		}
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
	
	_class.grid = function(n) {
		var m = n%4;
		if (m == 0) return n;
		return n + (4-m);
	}
	
})(Menu);


//EOF
