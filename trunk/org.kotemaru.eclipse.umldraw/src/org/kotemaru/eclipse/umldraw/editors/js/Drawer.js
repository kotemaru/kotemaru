
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
	
	
	function setLineStyle(dc, style) {
		if (style == null) style = "normal-2";
		
		var parts = style.split("-");
		var patt = null;
		if (parts[0] == "dotted") patt = [4,4];

		if (dc.setLineDash) {
			dc.setLineDash(patt);
		} else {
			dc.mozDash = patt;
		}
		
		dc.lineWidth = 2;
		if (parts.length>=2) {
			dc.lineWidth = parseInt(parts[1]);
		}
	}
	
	_class.prototype.drawLines = function(lines, style) {
		var dc = this.dc;
		dc.fillStyle = "transparent";
		dc.strokeStyle = "black";
		setLineStyle(dc, style);
		dc.beginPath();
		
		//dc.moveTo(lines[0].x1, lines[0].y1);
		for (var i=0; i<lines.length; i++) {
			dc.moveTo(lines[i].x1, lines[i].y1);
			dc.lineTo(lines[i].x2, lines[i].y2);
		}

		dc.stroke();
		dc.closePath();
	}
	_class.prototype.drawLinesS = function(lines, style) {
		var dc = this.dc;
		dc.fillStyle = "transparent";

		dc.lineWidth = 0.1;
		dc.strokeStyle = Color.GUIDE;
		dc.beginPath();
		
		var centers = [];
		for (var i=0; i<lines.length; i++) {
			centers.push(Util.getCenter(lines[i]));
			// Guid line
			dc.moveTo(lines[i].x1, lines[i].y1);
			dc.lineTo(lines[i].x2, lines[i].y2);
		}
		dc.stroke();
		
		
		setLineStyle(dc, style);
		dc.strokeStyle = "black";
		dc.beginPath();

		dc.moveTo(lines[0].x1, lines[0].y1);
		dc.lineTo(centers[0].x, centers[0].y);
		for (var i=0; i<lines.length-1; i++) {
			dc.moveTo(centers[i].x, centers[i].y);
			dc.quadraticCurveTo(lines[i].x2, lines[i].y2, 
					centers[i+1].x, centers[i+1].y);
		}
		var i = lines.length-1;
		dc.moveTo(centers[i].x, centers[i].y);
		dc.lineTo(lines[i].x2, lines[i].y2);

		dc.stroke();
		dc.closePath();
	}

	
	
	_class.prototype.drawLine = function(x1,y1,x2,y2, style) {
		var dc = this.dc;
		dc.strokeStyle = "black";
		setLineStyle(dc, style);
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
		setLineStyle(dc, null);
		
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
	