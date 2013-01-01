function Util() {}
(function(Class) {
	Class.extend = function(dst, src) {
		if (src == null) return dst;
		dst.prototype._super = src.prototype;
		for (var k in src.prototype) {
			dst.prototype[k] = src.prototype[k];
		}
		return dst;
	}
	Class.css = function(elem, data) {
		for (var k in data) {
			elem.style[k] = data[k];
			//console.log(k,elem.style[k],data[k]);
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
	
	Class.setSelect = function(select, val) {
		var opts = select.options;
		for (var i=0; i<opts.length; i++) {
			if (val == opts[i].value) {
				select.selectedIndex = i;
				return;
			}
		}
	}
	Class.preload = function(images) {
		for (var i=0; i<images.length; i++) {
			var img = new Image();
			img.src = images[i];
		}
	}
	
	Class.move = function(elem, x,y) {
		const st = elem.st;
		st.left = (x/2)+"px";
		st.top  = (y/2)+"px";
	}
	
	Class.escapeIframe = function(){
		var IS_IPHONE = navigator.userAgent.indexOf('iPhone') > 0 
		|| navigator.userAgent.indexOf('iPod') > 0;
		var IS_IFRAME = window.parent != null && window.parent != window;
		if (IS_IPHONE && IS_IFRAME) {
			if (confirm("描画性能改善の為、iframeを脱獄します。 よろしいですか？")){
				window.parent.location = location;
			}
		}
	}
	
})(Util);