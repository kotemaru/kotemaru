
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
	var FAMILY = "'arial',sans-serif";
	
	_class.S = new _class(10, FAMILY);
	_class.M = new _class(12, FAMILY);
	_class.L = new _class(14, FAMILY);
	_class.LL = new _class(18, FAMILY);
	_class.BIG = new _class(24, FAMILY);
	
	_class.MU = new _class(12, FAMILY, {decoration:"underline"});
		
})(Font);
	