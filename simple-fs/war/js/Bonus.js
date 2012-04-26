function Bonus(stage, src, initval){this.initialize.apply(this, arguments)};
(function(Class) {

	Class.prototype.initialize = function(stage, text, initval) {
		var elem = document.createElement("div");
		elem.style.position = "absolute";
		elem.style.color = "#ff8844";
		elem.style.font = "bold 24px sans-serif";
		elem.style.position = "absolute";
		elem.style.webkitTextStroke = "2px gray";
		elem.innerText = text;
        
		this.stage = stage;
		this.initval = initval;
		this.elem = elem;
		this.hide();
	}
	Class.prototype.show = function(x,y,text) {
		with (this) {
			elem.innerText = text;
			elem.style.display = "block";
			elem.style.opacity = 1.0;
			elem.style.left = (x-elem.clientWidth/2)+"px";
			elem.style.top  = (y-elem.clientHeight/2)+"px";

			this.isVisible = true;
			this.count = 0;
		}
	}
	Class.prototype.hide = function(x,y,text) {
		this.isVisible = false;
		this.elem.style.display = "none";
	}

	var STEP = 20;
	Class.prototype.action = function() {
		if (!this.isVisible) return;
		with (this) {
			if (count++ > STEP) return hide();
			elem.style.opacity = 1.5-count/STEP;
		}
	}

})(Bonus);
