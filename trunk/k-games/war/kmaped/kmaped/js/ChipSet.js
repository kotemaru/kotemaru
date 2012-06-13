
function ChipSet(){this.initialize.apply(this, arguments)};
(function(Class) {
	Class.prototype.initialize = function(tileWidth,tileHeight,src) {
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.elem = Util.newElem("img");
		if (src) this.load(src);
	}
	Class.prototype.load = function(src) {
		const self = this;
		this.elem.onload = function() {
			self.width = self.elem.width/self.tileWidth;
			self.height = self.elem.height/self.tileHeight;
			if (self.onload) self.onload();
		};
		this.src = src;
		this.elem.src = src;
	}
	
	Class.prototype.getFrame = function(x, y) {
		return this.width * y + x;
	}
	
	Class.prototype.draw = function(ctx, frame, dx,dy) {
		const tw = this.tileWidth;
		const th = this.tileHeight;
		if (frame == undefined || frame == -1) {
			//ctx.clearRect(dx*th, dy*th, tw, th); // 全体クリア済のはず。
		} else {
			var x = frame % this.width;
			var y = Math.floor(frame / this.width);
			ctx.drawImage(this.elem, x*tw, y*th, tw,th, dx*tw,dy*th, tw,th);
		}
	}
	
})(ChipSet);
//EOF.