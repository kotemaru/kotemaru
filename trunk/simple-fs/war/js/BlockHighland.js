
function BlockHighland(stage, src, initval){this.initialize.apply(this, arguments)};
(function(Class, Super) {
	Util.extend(Class, Super);
	Class.prototype.name = "highland";

	Class.prototype.initialize = function(stage, src, initval) {
		this._super.initialize.apply(this, arguments);

	}

	function calc_1(a,b,ga,gb,c) {
		if (gb == 0) return -1;
		return (a-b) * (ga/gb) + c;
	}
	function calc_2(a,b,c,d,min,max) {
		if (min > d || d > max) return 999999;
		return Math.abs((d-c)*(b-a));
	}

	Class.prototype.contact = function(actor) {
		const x1 = this.x;
		const x2 = this.x+32;
		const y1 = this.y;
		const y2 = this.y+32;

	with (actor) {
		if (z != 0) return false;
		if (x1 > nx || nx > x2 || y1 > ny || ny > y2) return false;
		if (x1 <= x && x <= x2 && y1 <= y && y <= y2) return false;

		var min = 999999, m, hit=null;
			const xu = calc_1(y, y1, gx, gy, x);
			m = calc_2(y, y1, gx, xu, x1,x2);
			if (m < min) {min = m; hit="U"}

			const xd = calc_1(y, y2, gx, gy, x);
			m = calc_2(y, y2, gx, xd, x1,x2);
			if (m < min) {min = m; hit="D"}

			const yl = calc_1(x, x1, gy, gx, y);
			m = calc_2(x, x1, gy, yl, y1,y2);
			if (m < min) {min = m; hit="L"}
			
			const yr = calc_1(x, x2, gy, gx, y);
			m = calc_2(x, x2, gy, yr, y1,y2);
			if (m < min) {min = m; hit="R"}

		if (hit == null) return false;

		if (hit == "U" || hit == "D") {
			var agy = Math.abs(gy);
			if (agy>5) {
				gz = agy *0.3;
				gy = gy *0.7;
				nz = 1;
			} else {
				ny = y;
				gy = -gy;			
			}
		} else  {
			var agx = Math.abs(gx);
			if (agx>5) {
				gz = agx *0.3;
				gx = gx *0.7;
				nz = 1;
			} else {
				nx = x;
				gx = -gx;			
			}
		}
		if (gz>5) gz = 5;
	}
		return true;
	}


})(BlockHighland, Block);

