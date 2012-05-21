

function Actor(){this.initialize.apply(this, arguments)};
(function(Class, Super) {
	Class.prototype = $.extend(Class.prototype, Super.prototype);

	Class.prototype.initialize = function(name, src, layer) {
		Super.prototype.initialize.apply(this, arguments);
		this.x = 0;
		this.y = 0;
		this.bindEvents();
	}
	Class.prototype.bindEvents = function() {
		const self = this;
		this.$elem.bind("click",function(ev){self.onClick(ev);});
		this.$elem.bind("mousedown",function(ev){self.onMouseDown(ev);});
		//this.$elem.bind("mousemove",function(ev){self.onMouseMove(ev);});
		this.$elem.bind("mouseup",function(ev){self.onMouseUp(ev);});
	}
	Class.prototype.copy = function() {
		return new Class(this.name, this.src);
	}

	Class.prototype.setPos = function(x,y) {
		this.x = x;
		this.y = y;
		var left = (x-this.$elem.width()/2)+"px";
		var top = (y-this.$elem.height()/2)+"px";
		this.$elem.css({position: "absolute", left:left, top:top});
	}

	Class.dragger = null;
	Class.prototype.onClick = function(ev) {
		event.preventDefault();
		event.stopPropagation();
	}
	Class.prototype.onMouseDown = function(ev) {
		Class.dragger = this;
	}
	Class.prototype.onMouseMove = function(ev) {
		var x = ev.offsetX;
		var y = ev.offsetY;
		if (ev.target != ev.currentTarget) {
			x += ev.target.offsetLeft;
			y += ev.target.offsetTop;
		}
		this.setPos(x,y);
	}
	Class.prototype.onMouseUp = function(ev) {
		Class.dragger = null;
	}
	
})(Actor, ImgElem);


