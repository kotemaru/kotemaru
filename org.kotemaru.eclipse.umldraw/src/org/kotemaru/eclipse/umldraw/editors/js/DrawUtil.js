
function DrawUtil(){this.initialize.apply(this, arguments)};
(function(_class){
	
	_class.textSize = function(dc, font, str) {
		dc.font = font;
		var m = dc.measureText(str);
		if (str == null || str == "") return {w:m.width, h:0};

		var h = Font.height(font);
		var lines = str.split("\n");
		var width = 1;
		for (var i=0; i<lines.length; i++) {
			width = Math.max(width, dc.measureText(lines[i]).width);
		}
		return {w:width, h: h*lines.length};
	}
	
	_class.drawText = function(dc, font, str, xx, yy) {
		dc.fillStyle = "black";
		dc.font = font;
		dc.textBaseline = "top";
		var h = Font.height(font);
		var lines = str.split("\n");
		for (var i=0; i<lines.length; i++) {
			dc.fillText(lines[i], xx, yy);
			yy += h;
		}
	}
	_class.drawBox = function(dc, x,y,w,h) {
		dc.fillStyle = "white";
		dc.strokeStyle = "black";
		dc.fillRect(x, y, w, h);
		dc.lineWidth = 2;
		dc.strokeRect(x, y, w, h);
	}
	
	
	var NONE     = "none";
	var ARROW    = "arrow"; // ↑
	var TRIANGLE = "triangle"; // △
	var RHOMBI   = "rhombi"; // ◇
	_class.NONE     = NONE;
	_class.ARROW    = ARROW;
	_class.TRIANGLE = TRIANGLE;
	_class.RHOMBI   = RHOMBI;
	
	
	_class.drawArrow = function(dc, shape, x1,y1,x2,y2) {
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
	
})(DrawUtil);
	