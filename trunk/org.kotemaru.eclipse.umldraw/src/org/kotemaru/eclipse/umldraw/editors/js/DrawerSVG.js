
function DrawerSVG(){this.initialize.apply(this, arguments)};
(function(_class){
	
	_class.prototype.initialize = function(canvasCtx) {
		this.dc = canvasCtx;
		this.result = [];
		this.add("<?xml version='1.0' encoding='utf-8' ?>");
		this.add("<svg xml:space='preserve'"
				+"width='"+Canvas.width()+"' height='"+Canvas.height()+"'"
				+" xmlns='http://www.w3.org/2000/svg'"
				+" viewBox='0 0 "+Canvas.width()+" "+Canvas.height()+"'>"
		);
	}
	_class.prototype.add = function(str) {
		this.result.push(str);
	}
	_class.prototype.getSVG = function(items) {
/*
		var data = {items:{}};
		for (var i=0; i<items.length; i++) {
			var item = items[i];
			data.items[item.id] = item.toJson();
		}
		data.coors = Coor.getJsonCache();
*/		
		return this.result.join("\n");
	}

	_class.prototype.close = function() {

		this.add("</svg>");
	}
	
	_class.prototype.beginItem = function(item) {
		this.add("<g>");
	}
	_class.prototype.endItem = function() {
		this.add("</g>");
	}
	
	
	_class.prototype.clipStart = function(x1,y1,w,h) {
		this.add("<g clip='rect("+comma(x1,y1,x1+w,y1+h)+")'>");
	}
	_class.prototype.clipEnd = function() {
		this.add("</g>");
	}
	
	_class.prototype.textSize = function(font, str, minLine) {
		return Drawer.textSize(this.dc, font, str, minLine);
	}
	
	_class.prototype.drawText = function(font, str, xx, yy) {
		var lines = str.split("\n");
		yy += 2;
		for (var i=0; i<lines.length; i++) {
	 		this.add("<text x='"+xx+"' y='"+yy+"'"
	 			+" font-size='"+font.size+"px'"
	  			+" font-family='"+font.family+"'"
	  			+" text-decoration'"+font.decoration+"'"
	 			+" dominant-baseline='hanging' >"
	 			+lines[i]+"</text>");
			yy += font.height;
		}
	}
	
	_class.prototype.drawTextLine = function(font, str, xx, yy) {
		yy += 2;
 		this.add("<text stroke='white' stroke-width='2' x='"+xx+"' y='"+yy+"'"
 			+" font-size='"+font.size+"px'"
  			+" font-family='"+font.family+"'"
 			+" dominant-baseline='hanging' >"
 			+str+"</text>");
 		this.add("<text x='"+xx+"' y='"+yy+"'"
 			+" font-size='"+font.size+"px'"
  			+" font-family='"+font.family+"'"
 			+" dominant-baseline='hanging' >"
 			+str+"</text>");
	}

	_class.prototype.drawBox = function(x,y,w,h) {
		this.add("<rect fill='white' stroke='black'" 
			+" x='"+x+"'"
			+" y='"+y+"'"
			+" width='"+w+"'"
			+" height='"+h+"'"
			+" stroke-width='"+2+"'/>"
		);
	}
	
	_class.prototype.drawLines = function(lines) {
		var points = "";
		points += lines[0].x1+" "+lines[0].y1;
		for (var i=0; i<lines.length; i++) {
			points += " "+lines[i].x2+" "+lines[i].y2;
		}
		this.add("<polyline fill='white' stroke='black'" 
			+" stroke-width='"+2+"'"
			+" points='"+points+"'"
			+"/>"
		);
	}
	
	
	_class.prototype.drawLine = function(x1,y1,x2,y2) {
		this.add("<polyline fill='white' stroke='black'" 
			+" stroke-width='"+2+"'"
			+" points='"+space(x1,y1,x2,y2)+"'"
			+"/>"
		);
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

		var points = "";
		var lineWidth = (shape == ARROW)?2:1;
		points += space(x3, y3, x2, y2, x4, y4);// ∧
		
		if (shape == TRIANGLE) { // △
			// nop.
		} else if (shape == RHOMBI) { // ◇
			tag = "polygon";
			points += " "+space(x5, y5);
		}

		if (shape == ARROW) {
			this.add("<polyline fill='none' stroke='black'" 
				+" stroke-width='"+lineWidth+"'"
				+" points='"+points+"'/>"
			);
		} else {
			this.add("<polygon fill='white' stroke='black'" 
				+" stroke-width='"+lineWidth+"'"
				+" points='"+points+"'/>"
			);
		}
	}

	function comma() {
		var str = "";
		for (var i=0; i<arguments.length; i++) {
			if (i>0) str += ","
			str += arguments[i];
		}
		return str;
	}
	function space() {
		var str = "";
		for (var i=0; i<arguments.length; i++) {
			if (i>0) str += " "
			str += Math.floor(arguments[i]*100)/100;
		}
		return str;
	}

})(DrawerSVG);
	