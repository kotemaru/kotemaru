
function Drawer(){this.initialize.apply(this, arguments)};
(function(_class){
	
	_class.prototype.initialize = function(canvasCtx) {
		this.dc = canvasCtx;
	}

	_class.prototype.close = function() {
		// nop.
	}
	
	_class.prototype.beginItem = function(item) {
		this.dc.save();
	}
	_class.prototype.endItem = function() {
		this.dc.restore();
	}
	
	_class.prototype.clipStart = function(x1,y1,w,h) {
		var dc = this.dc;
		dc.rect(x1-1,y1-1,w+2,h+2);
		dc.clip();
	}
	_class.prototype.clipEnd = function(x1,y1,w,h) {
		var dc = this.dc;
		dc.rect(0,0,10000,10000);
		dc.clip();
	}
	
	_class.prototype.textSize = function(font, str) {
		var dc = this.dc;
		dc.font = font.name;
		var m = dc.measureText(str);
		if (str == null || str == "") return {w:m.width, h:0};

		var h = font.height;
		var lines = str.split("\n");
		var width = 1;
		for (var i=0; i<lines.length; i++) {
			width = Math.max(width, dc.measureText(lines[i]).width);
		}
		return {w:width, h: h*lines.length};
	}
	
	_class.prototype.drawText = function(font, str, xx, yy) {
		var dc = this.dc;
		dc.fillStyle = "black";
		dc.font = font.name;
		dc.textBaseline = "top";
		var h = font.height;
		var lines = str.split("\n");
		for (var i=0; i<lines.length; i++) {
			dc.fillText(lines[i], xx, yy);
			yy += h;
		}
	}
	
	_class.prototype.drawTextLine = function(font, str, xx, yy) {
		var dc = this.dc;
		dc.strokeStyle = "white";
		dc.lineWidth = 2;
		dc.fillStyle = "black";
		dc.font = font;
		dc.textBaseline = "top";
		dc.strokeText(str, xx, yy);
		dc.fillText(str, xx, yy);
	}

	_class.prototype.drawBox = function(x,y,w,h) {
		var dc = this.dc;
		dc.fillStyle = "white";
		dc.strokeStyle = "black";
		dc.fillRect(x, y, w, h);
		dc.lineWidth = 2;
		dc.strokeRect(x, y, w, h);
	}
	
	_class.prototype.drawLines = function(lines) {
		var dc = this.dc;
		dc.strokeStyle = "black";
		dc.lineWidth = 2;
		dc.beginPath();
		
		dc.moveTo(lines[0].x1, lines[0].y1);
		for (var i=0; i<lines.length; i++) {
			dc.lineTo(lines[i].x2, lines[i].y2);
		}
	
		dc.stroke();
		dc.closePath();
	}
	_class.prototype.drawLine = function(x1,y1,x2,y2) {
		var dc = this.dc;
		dc.strokeStyle = "black";
		dc.lineWidth = 2;
		dc.beginPath();
		dc.moveTo(x1, y1);
		dc.lineTo(x2, y2);
		dc.stroke();
		dc.closePath();
	}
	
	var NONE     = "none";
	var ARROW    = "arrow"; // ↑
	var TRIANGLE = "triangle"; // △
	var RHOMBI   = "rhombi"; // ◇
	_class.NONE     = NONE;
	_class.ARROW    = ARROW;
	_class.TRIANGLE = TRIANGLE;
	_class.RHOMBI   = RHOMBI;
	
	
	_class.prototype.drawArrow = function(shape, x1,y1,x2,y2) {
		var dc = this.dc;
		if (shape == NONE) return;
		var isFill = shape.match(/-B$/);
		shape = shape.replace(/-B$/,"");

		var len = 10;
		var angle = 30 * (Math.PI / 180); // toRadian
		var theta = Math.atan2(y2 - y1, x2 - x1);
		if (shape == RHOMBI) {
			len = 7;
		}

		var x3 = x2 + len*Math.cos(theta+Math.PI-angle);
		var y3 = y2 + len*Math.sin(theta+Math.PI-angle);
		var x4 = x2 + len*Math.cos(theta+Math.PI+angle);
		var y4 = y2 + len*Math.sin(theta+Math.PI+angle);
		var x5 = x2 + len*(1.73)*Math.cos(theta+Math.PI);
		var y5 = y2 + len*(1.73)*Math.sin(theta+Math.PI);

		dc.beginPath();
		dc.lineWidth = (shape == ARROW)?2:1;
		dc.moveTo(x3, y3);
		dc.lineTo(x2, y2);
		dc.lineTo(x4, y4); // ∧
		
		if (shape == TRIANGLE) { // △
			dc.closePath();
		} else if (shape == RHOMBI) { // ◇
			dc.lineTo(x5, y5);
			dc.closePath();
		}
		dc.strokeStyle = "black";

		if (shape == ARROW) {
			dc.stroke();
		} else {
			dc.fillStyle = isFill ? "black" : "white";
			dc.fill();
			dc.stroke();
		}
	}


})(Drawer);
	