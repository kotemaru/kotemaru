
function Selector(){this.initialize.apply(this, arguments)};
(function(Class) {
	const CSS = {
		position: "absolute",
		border: "2px dashed yellow",
		margin: 0, padding: 0, opacity:1.0,
		zIndex: ZIndex.SELECTOR, mouse:"arrow"
	};

	Class.prototype.initialize = function(workSpace) {
		if (workSpace == null) return;
		
		this.workSpace = workSpace;
		this.elem = Util.newElem("div", CSS);
		this.elem.draggable = false;
		this.elem.onselectstart = function(){return false;};
		this.offset(0,0);
		this.resize(1,1);
		this.isMouseDown = false;
		this.isSlide = false;
		this.isDrag = false;

		this.initHandler();
	}
	Class.prototype.cloneRect = function() {
		var clone = new Class();
		clone.offset(this.x, this.y).resize(this.width,this.height);
		return clone;
	}
	Class.prototype.offset = function(x,y) {
		if (y === undefined) {
			y = x.y;
			x = x.x;
		}
		this.x = x;
		this.y = y;
		return this;
	}
	Class.prototype.resize = function(w,h) {
		if (h === undefined) {
			h = w.height;
			w = w.width;
		}
		this.width = w;
		this.height = h;
		return this;
	}
	Class.prototype.show = function(bool) {
		bool = (bool===undefined)?true:bool;
		this.elem.style.visibility = (bool?"visible":"hidden");
		return this;
	}
	Class.prototype.fadeOut = function(ms) {
		const self = this;
		setTimeout(function(){self.show(false)},ms);
		return this;
	}
	
	Class.prototype.flush = function() {
		const tw = this.workSpace.chipSet.tileWidth;
		const th = this.workSpace.chipSet.tileHeight;
		Util.css(this.elem, {
			left:(this.x*tw-1)+"px", top:(this.y*th-1)+"px",
			width:(this.width*tw-2)+"px", height:(this.height*tw-2)+"px"
		});
	}
	Class.prototype.canselDrag = function() {
		this.isDragging = false;
		return this;
	}
	Class.prototype.setMode = function(mode) {
		this.mode = mode;
		if (mode != "slide") this.anime(false);
		return this;
	}

	Class.prototype.initHandler = function() {
		const self = this;
		var origin;
		var rx,ry;
 		function onMouseDown(ev) {
	 
 			var coor = getCoor(ev, self.workSpace);
			self.isMouseDown = true;
			self.isDragged = false;

	 		if (ev.which == 3) { // 右ボタン
				self.show();
				onMouseMove(ev);
	 		} else if (self.mode == "slide" && ev.target == self.elem) { // スライド開始
				self.isSlide = true;
				rx = self.x - coor.x;
				ry = self.y - coor.y;
	 		} else {
				origin = coor;
				self.isDragging = true;
				self.offset(coor.x, coor.y).resize(1,1).show().flush();
				self.workSpace.selectionStart(self);
			}
		}
		function onMouseMove(ev) {
			if (self.isMouseDown == true && ev.which == 0) {
				onMouseUp(ev); // 外でマウスUPされている場合。
				return;
			}
			if (self.isMouseDown == false) return;
			self.isDragged = true;

			if (self.isSlide) {
				var coor = getCoor(ev, self.workSpace);
				self.offset(coor.x+rx, coor.y+ry);

			} else if (self.isDragging) {
				var coor = getCoor(ev, self.workSpace, "endPoint");
				self.resize(coor.x-self.x, coor.y-self.y);

			} else if (ev.target != self.elem) { // ペンモードドラッグ
				var coor = getCoor(ev, self.workSpace);
				var x = origin.x + Math.floor((coor.x-origin.x)/self.width)*self.width;
				var y = origin.y + Math.floor((coor.y-origin.y)/self.height)*self.height;
				self.offset(x, y).show().flush();
			}
			self.workSpace.selectionDrag(self);
			self.flush();
		}

		function onMouseUp(ev) {
			self.isMouseDown = false;
			self.workSpace.selectionEnd(self);
			self.isSlide = false;
		}
		
		var elem = this.workSpace.elem;
		$(elem).bind("mousedown",onMouseDown)
			.bind("mousemove",onMouseMove)
			.bind("mouseup",onMouseUp);
	}
	
	function getCoor(ev, workSpace, mode) {
		const tw = workSpace.chipSet.tileWidth;
		const th = workSpace.chipSet.tileHeight;
		var x = ev.offsetX;
		var y = ev.offsetY;
		var e = ev.target;

		if (x == undefined) { // for FireFox
			var offset = $(e).offset();
			x = ev.pageX - offset.left;
			y = ev.pageY - offset.top;
		}
		
		while (e != null && e != workSpace.elem) {
			x += e.offsetLeft;
			y += e.offsetTop;
			e = e.offsetParent;
		}
		if (mode == "endPoint") {
			return {x:Math.ceil(x / tw), y:Math.ceil(y / th)};
		} else {
			return {x:Math.floor(x / tw), y:Math.floor(y / th)};
		}
	}

	Class.prototype.anime = function(b) {
		if (this.isAnimation == b) return;
		this.isAnimation = b;
		this.animeFlag = false;
		anime(this);
		return this;
	}
	function anime(self) {
		if (!self.isAnimation) {
			self.elem.style.border = "2px dashed yellow"; 
			return;
		}
		setTimeout(function(){anime(self);}, 500);
		self.elem.style.border = self.animeFlag ? "2px dashed yellow" : "2px dotted yellow"; 
		self.animeFlag = !self.animeFlag;
	}
	
	
})(Selector);
//EOF.