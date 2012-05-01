function Jamper(stage, src, initval){this.initialize.apply(this, arguments)};

(function(Class) {
	Class.prototype.initialize = function(stage, src, initval) {
		this.elem = Util.createImg(src, {
			zIndex: 1,
		});
     	this.elem.width = 32;
   
		this.stage = stage;
		this.initval = initval;

		this.x = initval.x;
		this.y = initval.y;
		this.w = 32;
		this.h = 32;
		this.reflect();
	}
	Class.prototype.reflect = function() {
		with (this) {
			const st = elem.style;
			st.left = x+"px";
			st.top  = y+"px";
		}
	}

	var R = 140;
	Class.prototype.action = function() {
getBlockRaw


		var a = this, b = this.stage.marble;
		var w = b.x - a.x;
		var h = b.y - a.y;
		var l = Math.sqrt((w*w) + (h*h));

		if (l < R && l > 5 && !b.isWating && !b.isDropping  ) {
			b.gx -= (R-l)*(w>0?1:-1)/50;
			b.gy -= (R-l)*(h>0?1:-1)/50;
		}
	}

})(Jamper);
