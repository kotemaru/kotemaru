
(function(){
	var Package = window;
	var Class = function Rating(){this.initialize.apply(this, arguments)};
	var This = Class.prototype;
	Package[Class.name] = Class;

	var TEMPL   = $.jqmdp.absPath("Rating.html");
	var IMG_ON  = $.jqmdp.absPath("star-1.0.png");
	var IMG_HALF= $.jqmdp.absPath("star-0.5.png");
	var IMG_OFF = $.jqmdp.absPath("star-0.0.png");

	This.initialize = function($this) {
		this.$this = $this;
		this.value = 0;
		$.jqmdp.exTemplate($this, TEMPL);
	}
	This.val = function(v){
		this.value = v;
		$.jqmdp.refresh(this.$this);
	}
	This.star = function(v){
		var sub = this.value+1.0 - v;
		if (sub >= 1.0) return IMG_ON;
		if (sub >= 0.5) return IMG_HALF;
		return IMG_OFF;
	}
})();
