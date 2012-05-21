
function ImgElem(){this.initialize.apply(this, arguments)};
(function(Class) {

	Class.prototype.initialize = function(name, src, layer) {
		this.$elem = $("<img/>");
		this.$elem.attr("src",src);
		this.$elem.attr("draggable",false);
		this.$elem.css({userSelect: "none"});
		this.name = name;
		this.src = src;
		this.layer = layer;
	}

	Class.prototype.copy = function() {
		return new Class(this.name, this.src);
	}
	
	Class.prototype.setPos = function(x,y) {
		this.$elem.css({position: "absolute", left:x+"px", top:y+"px"});
	}
	Class.prototype.parge = function() {
		this.$elem.remove();
	}
	Class.prototype.appendTo = function($parent) {
		$parent.append(this.$elem);
	}
	Class.prototype.css = function(data) {
		this.$elem.css(data);
	}
	Class.prototype.bind = function(type, func) {
		this.$elem.bind(type, func);
	}
	
})(ImgElem);
