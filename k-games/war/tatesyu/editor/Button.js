

function Button(){this.initialize.apply(this, arguments)};
(function(Class, Super) {
	Class.prototype = $.extend(Class.prototype, Super.prototype);

	Class.prototype.initialize = function(name, src, layer, opts) {
		//Super.prototype.initialize.apply(this, arguments);
		this.$elem = $("<div><img/><span/><div>");
		this.$elem.find("img").attr({src:src, draggable:false});
		this.$elem.attr("draggable",false);
		this.$elem.css({
			position: "absolute", display: "inline-block",
		});
		this.$elem.find("span").css({
			position: "absolute", left:0, top:0, color:"red", fontSize:"10px"
		}).text(opts.type);
		
		this.name = name;
		this.src = src;
		this.layer = layer;
		this.x = 0;
		this.y = 0;
		this.opts = opts;
		this.bindEvents();
	}
	Class.prototype.copy = function() {
		return new Class(this.name, this.src, this.layer, this.opts);
	}

})(Button, Actor);


