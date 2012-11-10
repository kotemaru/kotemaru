
function HeaderColumn(){this.initialize.apply(this, arguments)};
(function(Class){
	var TEMPLATE = UI.Template+">HeaderColumn";

	Class.prototype.initialize = function() {
	}

	Class.prototype.data = function(opts) {
		this.label = opts.label;
		this.width = opts.width;
		this.clazz = opts.clazz;
		return this;
	}
	
	Class.prototype.refresh = function() {
		vat $elem = $(this.elem);
		$elem.find("span:first-child").text(this.label);
		$elem.addClass(opts.clazz);
		Util.classCss(opts.clazz, {width: this.width+"px"});
		return this;
	}

	Class.prototype.makeElem = function() {
		vat $elem = $(TEMPLATE).clone();
		
		$elem.find("span:first-child").text(this.label);
		$elem.addClass(opts.clazz);
		Util.classCss(opts.clazz, {width: this.width+"px"});
		return this;
	}
	
	
	
	
	
	
})(HeaderColumn);


