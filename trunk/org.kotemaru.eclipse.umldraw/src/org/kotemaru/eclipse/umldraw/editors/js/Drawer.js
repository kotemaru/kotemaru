
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
	
	_class.prototype.textSize = function(font, str, minLine) {
		return _class.textSize(this.dc, font, str, minLine);
	}
	_class.textSize = function(dc, font, str, minLine) {
		minLine = minLine ? minLine : 0;
		dc.font = font.name;
		var h = font.height;
		if (str == null || str == "") {
			return {w:0, h:h*minLine};
		}

		var m = dc.measureText(str);
		var lines = str.split("\n");
		var width = 1;
		for (var i=0; i<lines.length; i++) {
			width = Math.max(width, dc.measureText(lines[i]).width);
		}
		return {w:width, h: h*lines.length};
	}
	
	_class.prototype.drawText = function(font, str, xx, yy) {
		var dc = this.dc;
		var isUnderLine = (font.decoration == "underline");

		dc.fillStyle = "black";
		dc.font = font.name;
		dc.textBaseline = "alphabetic";
		
		var h = font.height;
		var lines = str.split("\n");
		for (var i=0; i<lines.length; i++) {
			dc.fillText(lines[i], xx, yy+font.acender);
			if (isUnderLine) {
				var m = dc.measureText(lines[i]);
				this.drawHLine(xx, yy+font.acender, m.width);
			}
			yy += h;
		}
	}
	_class.prototype.drawTextLine = function(font, str, xx, yy) {
		var dc = this.dc;
		dc.strokeStyle = "white";
		dc.lineWidth = 2;
		dc.fillStyle = "black";
		dc.font = font;
		dc.textBaseline = "alphabetic";
		dc.strokeText(str, xx, yy+font.acender);
		dc.fillText(str, xx, yy+font.acender);
	}
	_class.prototype.drawHLine = function(xx,yy,ww, lw) {
		yy = Math.floor(yy)+0.5;
		var dc = this.dc;
		dc.strokeStyle = "black";
		dc.lineWidth = lw?lw:1;
		dc.beginPath();
		dc.moveTo(xx, yy);
		dc.lineTo(xx+ww, yy);
		dc.stroke();
	}
	_class.prototype.drawVLine = function(xx,yy,hh, lw) {
		xx = Math.floor(xx)+0.5;
		var dc = this.dc;
		dc.strokeStyle = "black";
		dc.lineWidth = lw?lw:1;
		dc.beginPath();
		dc.moveTo(xx, yy);
		dc.lineTo(xx, yy+hh);
		dc.stroke();
	}
	_class.prototype.whiteBox = function(xx,yy,ww,hh) {
		var dc = this.dc;
		dc.fillStyle= "white";
		dc.fillRect(xx,yy,ww,hh);
	}
	_class.prototype.ellipse = function(x,y,width,height) {
		var dc = this.dc;
		var radW = width/2;
		var radH = height/2;
		x = x + radW;
		y = y + radH;
		dc.lineWidth = 2;
		dc.strokeStyle = "black";
		dc.fillStyle = "white";
		dc.beginPath();
		dc.bezierCurveTo(x, y - radH, x + radW , y - radH, x + radW, y);
		dc.bezierCurveTo(x + radW, y, x + radW, y + radH, x, y + radH);
		dc.bezierCurveTo(x, y + radH, x - radW, y + radH, x - radW, y);
		dc.bezierCurveTo(x - radW, y, x - radW, y - radH, x, y - radH);
		//dc.closePath();
		dc.fill();
		dc.stroke();
	};
	_class.prototype.drawMarker = function(image, xx, yy) {
		this.dc.drawImage(image, xx, yy);
	}


	_class.prototype.drawBox = function(x,y,w,h) {
		var dc = this.dc;
		dc.fillStyle = "white";
		dc.strokeStyle = "black";
		dc.fillRect(x, y, w, h);
		dc.lineWidth = 2;
		dc.strokeRect(x, y, w, h);
	}
	
	_class.prototype.drawPoly = function(points) {
		var dc = this.dc;
		dc.fillStyle = "white";
		dc.strokeStyle = "black";
		drawPoly(dc, points);
	}
	_class.prototype.drawPolyGuide = function(points) {
		var dc = this.dc;
		dc.fillStyle = "transparent";
		dc.strokeStyle = Color.GUIDE;
		dc.lineWidth = 0.25;
		drawPoly(dc, points);
	}
	function drawPoly(dc, points) {
		dc.beginPath();
		
		dc.moveTo(points[0].x-0.5, points[0].y-0.5);
		for (var i=1; i<points.length; i++) {
			dc.lineTo(points[i].x-0.5, points[i].y-0.5);
		}
		dc.closePath();
		dc.fill();
		dc.stroke();
	}
	
	function setLineStyle(dc, style) {
		if (style == null) style = "normal-2";
		
		var parts = style.split("-");
		var patt = null;
		if (parts[0] == "dotted") {
			patt = [4,4];
			
			if (dc.setLineDash) {
				dc.setLineDash(patt);
			} else if (dc.mozDash) {
				dc.mozDash = patt;
			} else { // for IE9
				//dc.strokeStyle = "#888888";
				dc.strokeStyle = dc.createPattern($("#meshImg")[0], 'repeat');
			}
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
		
		
		dc.strokeStyle = "black";
		setLineStyle(dc, style);
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
	