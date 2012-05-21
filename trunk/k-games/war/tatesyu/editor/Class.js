
var Class = {};
Class.def = function(cls, arg2, arg3) {
	var parent = arg3==null?null:arg2;
	var body   = arg3==null?arg2:arg3;
	
	cls.constructor = function(){this.initialize.apply(this, arguments)};
	body(cls, parent);
}

Class.def.