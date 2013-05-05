
function Font(){this.initialize.apply(this, arguments)};
(function(_class){
	_class.prototype.initialize = function(size, family, opts) {
		this.size   = size;
		this.family = family;
		this.name   = size+"px "+family;
		this.height = Math.floor(size * 1.2);
		this.acender = size;
		for (var k in opts) this[k] = opts[k];
	}
	
	
	function init(family) {
		_class.S = new _class(10, family);
		_class.M = new _class(12, family);
		_class.L = new _class(14, family);
		_class.LL = new _class(18, family);
		_class.BIG = new _class(24, family);
		_class.MU = new _class(12, family, {decoration:"underline"});
	}

	$(function(){
		init(Eclipse.preferences.fontFamily);
		$("#configDialog").live("saved",function(){
			init(Eclipse.preferences.fontFamily);
		});
	});

})(Font);
	