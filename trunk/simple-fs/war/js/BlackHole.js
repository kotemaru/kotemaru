function BlackHole(stage, src, initval){this.initialize.apply(this, arguments)};

(function(Class) {
	Class.prototype.initialize = function(stage, src, initval) {
		var elem = document.createElement("img");
		elem.style.position = "absolute";
		elem.style.zIndex = 9;
		elem.src = src;
     		elem.width = 128;
   
		this.stage = stage;
		this.initval = initval;
		this.elem = elem;

		this.x = initval.x;
		this.y = initval.y;
		this.w = 128;
		this.h = 128;
		this.w2 = this.w/2;
		this.h2 = this.h/2;
		this.reflect();
	}
	Class.prototype.reflect = function() {
		with (this) {
			const st = elem.style;
			st.left = Math.floor(x-w2)+"px";
			st.top  = Math.floor(y-h2)+"px";
		}
	}

	var R = 130;
	Class.prototype.action = function() {
		var a = this, b = this.stage.marble;
		var w = b.x - a.x;
		var h = b.y - a.y;
		var l = Math.sqrt((w*w) + (h*h));

		if (l < R && l > 5 && !b.isWating && !b.isDropping  ) {
			b.gx -= (R-l)*(w>0?1:-1)/50;
			b.gy -= (R-l)*(h>0?1:-1)/50;
		}
	}

})(BlackHole);
