
function Font(){this.initialize.apply(this, arguments)};
(function(_class){
	_class.S = "10px sans-serif";
	_class.M = "14px sans-serif";
	_class.L = "18px sans-serif";
	
	var HEIGHT = {};
	HEIGHT[_class.S] = 12;
	HEIGHT[_class.M] = 17;
	HEIGHT[_class.L] = 22;
	_class.height = function(f) {
		return HEIGHT[f];
	};
	
	_class.prototype.initialize = function() {
	}
	
	_class.textSize = function(dc, font, str) {
		dc.font = font;
		var m = dc.measureText(str);
		if (str == null || str == "") return {w:m.width, h:0};

		var h = HEIGHT[font];
		var lines = str.split("\n");
		var width = 1;
		for (var i=0; i<lines.length; i++) {
			width = Math.max(width, dc.measureText(lines[i]).width);
		}
		return {w:width, h: h*lines.length};
	}
	
})(Font);
	