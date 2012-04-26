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

		this.elem.style.left = (this.x-64)+"px";
		this.elem.style.top  = (this.y-64)+"px";
	}

	var R = 200;
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
