
function DrawerSVG(){this.initialize.apply(this, arguments)};
(function(_class){
	
	_class.prototype.initialize = function(canvasCtx) {
		this.dc = canvasCtx;
		this.result = [];
		
	}
	_class.prototype.add = function(str) {
		this.result.push(str);
	}
	_class.prototype.getSVG = function(items) {
		var b = Canvas.getOutBounds();
		var header = "<?xml version='1.0' encoding='utf-8' ?>\n"
			+"<svg xml:space='preserve'"
			+" width='"+b.w+"' height='"+b.h+"'"
			+" xmlns='http://www.w3.org/2000/svg'"
			+" viewBox='"+space(b.x1, b.y1, b.w, b.h)+"'>\n";

		var data = Store.save(Canvas.getItems());
		var json = JSON.stringify(data,null,"\t");
		json = json.replace(/\]\]>/,"]]&gt;");
		//json = json.replace(/[&]/,"&amp;").replace(/</,"&lt;").replace(/>/,"&gt;");
		
		return header
			+ "<metadata id='umldraw-data'><![CDATA["
				+json
			+ "]]></metadata>"
			+ this.result.join("\n")
		;
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
		if (str == "") return;
		
		var isUnderLine = (font.decoration == "underline");
		var lines = str.split("\n");
		yy += font.acender;
		for (var i=0; i<lines.length; i++) {
	 		this.add("<text x='"+xx+"' y='"+yy+"'"
	 			+" font-size='"+font.size+"px'"
	  			+" font-family='"+font.family+"'"
	 			//+" dominant-baseline='text-before-edge'" +
	 			+" >"+esc(lines[i])+"</text>");
	 		if (isUnderLine) {
				var m = this.dc.measureText(lines[i]);
				this.drawHLine(xx, yy, m.width, 0.5);
	 		}
			yy += font.height;
		}
	}
	
	_class.prototype.drawTextLine = function(font, str, xx, yy) {
		if (str == "") return;
		yy += font.acender;
 		this.add("<text stroke='white' stroke-width='2' x='"+xx+"' y='"+yy+"'"
 			+" font-size='"+font.size+"px'"
  			+" font-family='"+font.family+"'"
 			//+" dominant-baseline='text-before-edge'"
 			+" >"+esc(str)+"</text>");
 		this.add("<text x='"+xx+"' y='"+yy+"'"
 			+" font-size='"+font.size+"px'"
  			+" font-family='"+font.family+"'"
 			//+" dominant-baseline='text-before-edge"
 			+" >"+esc(str)+"</text>");
	}
	function esc(str) {
		return str.replace(/[&]/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;");
	}
	
	_class.prototype.drawHLine = function(xx,yy,ww, lw) {
		yy = Math.floor(yy)+0.5;
		this.add("<polyline fill='white' stroke='black'" 
			+" stroke-width='"+(lw?lw:1)+"'"
			+" points='"+space(xx,yy,xx+ww,yy)+"'"
			+"/>"
		);
	}
	_class.prototype.drawVLine = function(xx,yy,hh, lw) {
		this.add("<polyline fill='white' stroke='black'" 
			+" stroke-width='"+(lw?lw:1)+"'"
			+" points='"+space(xx,yy,xx,yy+hh)+"'"
			+"/>"
		);
	}
	_class.prototype.whiteBox = function(xx,yy,ww,hh) {
		this.add("<rect fill='white' stroke='white'" 
			+" x='"+xx+"'"
			+" y='"+yy+"'"
			+" width='"+ww+"'"
			+" height='"+hh+"'"
			+" stroke-width='"+0+"'/>"
		);
	}
	_class.prototype.ellipse = function(x,y,width,height) {
		var rw = width/2, rh = height/2;
 		this.add("<ellipse stroke='black' fill='white'"
	 		+" cx='"+(x+rw)+"' cy='"+(y+rh)+"' rx='"+rw+"' ry='"+rh+"'"
			+" stroke-width='"+2+"'/>"
		);
	};
	_class.prototype.drawMarker = function(image, xx, yy) {
		// no print.
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
	
	_class.prototype.drawPoly = function(points) {
		var pointsStr = "";
		pointsStr += points[0].x+" "+points[0].y;
		for (var i=0; i<points.length; i++) {
			pointsStr += " "+points[i].x+" "+points[i].y;
		}
		this.add("<polyline fill='white' stroke='black'" 
			+" stroke-width='"+1+"'"
			+" points='"+pointsStr+"'"
			+"/>"
		);
	}
	_class.prototype.drawPolyGuide = function(points) {
		// no print.
	}
	
	_class.prototype.drawLines = function(lines, style) {
		var styleAttr = getLineStyle(style);
		var points = "";
		points += lines[0].x1+" "+lines[0].y1;
		for (var i=0; i<lines.length; i++) {
			points += " "+lines[i].x2+" "+lines[i].y2;
		}
		this.add("<polyline fill='white' stroke='black'" 
			+" points='"+points+"'"
			+ styleAttr
			+"/>"
		);
	}
	
	
	_class.prototype.drawLine = function(x1,y1,x2,y2, style) {
		this.add("<polyline fill='white' stroke='black'" 
			+ getLineStyle(style)
			+" points='"+space(x1,y1,x2,y2)+"'"
			+"/>"
		);
	}
	function getLineStyle(style) {
		var attr="";
		if (style == null) style = "normal-2";
		
		var parts = style.split("-");
		var patt = null;
		if (parts[0] == "dotted") {
			attr+= " stroke-dasharray='4 4'";
		}

		var lw = parts.length>=2?parts[1]:2;
		attr+= " stroke-width='"+lw+"'";
		
		return attr;
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
	