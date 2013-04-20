

function Lang(){this.initialize.apply(this, arguments)};
(function(_class){
	var CLASSES = {};

	_class.classForName = function(name) {
		var cls = CLASSES[name];
		if (cls == null) {
			throw "Class not found: "+name;
		}
		return cls;
	}
	
	_class.define = function(__class) {
		CLASSES[__class.name] = __class;
		__class.prototype._class = __class;
	}
	
	_class.extend = function(__class, __super) {
		__class.prototype = new __super();
		__class._super = __super;
		__class.prototype._super = __super;
		__class.prototype._class = __class;
		CLASSES[__class.name] = __class;
	}
	
	_class.copy = function(src, opts) {
		var dst = {};
		for (var k in src) dst[k] = src[k];
		for (var k in opts) dst[k] = opts[k];
		return dst;
	}
	
})(Lang);
