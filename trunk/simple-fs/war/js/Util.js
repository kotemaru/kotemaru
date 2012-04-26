function Util() {}
(function(Class) {
	Class.extend = function(dst, src) {
		if (src == null) return dst;
		dst.prototype.super = src.prototype;
		for (var k in src.prototype) {
			dst.prototype[k] = src.prototype[k];
		}
		return dst;
	}
	Class.css = function(elem, data) {
		for (var k in data) {
			elem.style[k] = data[k];
		}
	}
	Class.createImg = function(src, css) {
		var img = document.createElement("img");
		img.src = src;
		Util.css(img, {
			position: "absolute",
		});
		Util.css(img, css);
		return img;
	}
	Class.createDiv = function(css) {
		var div = document.createElement("img");
		Util.css(div, {
			position: "absolute",
		});
		Util.css(div, css);
		return div;
	}
	Class.byId = function(id) {
		return document.getElementById(id);
	}
	Class.createElem = function(name) {
		return document.createElement(name);
	}
})(Util);