

function Rating(){this.constractor.apply(this, arguments)};
(function(Class){
	var This = Class.prototype;

	var TEMPL  = $.jqmdp.absPath("Rating.html");
	var IMG_ON  = $.jqmdp.absPath("star-1.0.png");
	var IMG_OFF = $.jqmdp.absPath("star-0.0.png");

	This.constractor = function($this) {
		this.$this = $this;
		this.value = 0;
		$.jqmdp.exTemplate($this, TEMPL);
	}
	This.val = function(v){
		this.value = v;
		$.jqmdp.refresh(this.$this);
	}
	This.star = function(v){
		return (this.value>=v) ? IMG_ON : IMG_OFF;
	}
})(Rating);
