
function BlockWall(stage, src, initval){this.initialize.apply(this, arguments)};
(function(Class, Super) {
	Util.extend(Class, Super);
	Class.prototype.name = "wall";

	Class.prototype.initialize = function(stage, src, initval) {
		this._super.initialize.apply(this, arguments);
		this.elem.style.zIndex = 5;
/*
		this.x = initval.x*32;
		this.y = initval.y*32;

		this.elem = document.createElement("div");
		Util.css(this.elem, {
			position: "absolute",
			width: "30px", height: "30px", 
			border: "1px solid gray",
			background: "gray",
			left:this.x, top:this.y,
		});
		stage.elem.appendChild(this.elem);
        
		this.stage = stage;
		this.initval = initval;
*/
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

	with (actor) {
		const x1 = this.x-w2;
		const x2 = this.x+32+w2;
		const y1 = this.y-w2;
		const y2 = this.y+32+w2;
		
		var min = 999999, m, hit=null;
		if (this.hasU) {
			const xu = calc_1(y, y1, gx, gy, x);
			m = calc_2(y, y1, gx, xu, x1,x2);
			if (m < min) {min = m; hit="U"}
		}

		if (this.hasD) {
			const xd = calc_1(y, y2, gx, gy, x);
			m = calc_2(y, y2, gx, xd, x1,x2);
			if (m < min) {min = m; hit="D"}
		}

		if (this.hasL) {
			const yl = calc_1(x, x1, gy, gx, y);
			m = calc_2(x, x1, gy, yl, y1,y2);
			if (m < min) {min = m; hit="L"}
		}

		if (this.hasR) {
			const yr = calc_1(x, x2, gy, gx, y);
			m = calc_2(x, x2, gy, yr, y1,y2);
			if (m < min) {min = m; hit="R"}
		}

		if (hit == null) return false;

		if (hit == "U" && gy>0) {
			ny = ny-(ny-y1);
			//nx = x;
			gy = -gy*0.7;
		} else if (hit == "D" && gy<0) {
			ny = ny+(y2-ny);
			//nx = x;
			gy = -gy*0.7;
		} else if (hit == "L" && gx>0) {
			nx = nx-(nx-x1);
			//ny = y;
			gx = -gx*0.7;
		} else if (hit == "R" && gx<0) {
			nx = nx+(x2-nx);
			//ny = y;
			gx = -gx*0.7;
		}
	}
		return true;
	}


	//Class.prototype.isWall = function() {return true;}
	Class.prototype.isWall = true;

	Class.prototype.corrent = function() {
		this.x1 = this.x-16;
		this.x2 = this.x+32+16;
		this.y1 = this.y-16;
		this.y2 = this.y+32+16;

		const x = this.initval.x;
		const y = this.initval.y;
		this.hasU = !(this.stage.getBlockRaw(x,y-1).isWall);
		this.hasD = !(this.stage.getBlockRaw(x,y+1).isWall);
		this.hasL = !(this.stage.getBlockRaw(x-1,y).isWall);
		this.hasR = !(this.stage.getBlockRaw(x+1,y).isWall);
/*
		const st = this.elem.style;
		if (this.hasU)	st.borderTop    = "1px solid black";
		if (this.hasD)	st.borderBottom = "1px solid black";
		if (this.hasL)	st.borderLeft   = "1px solid black";
		if (this.hasR)	st.borderRight  = "1px solid black";
*/
	}

})(BlockWall, Block);

