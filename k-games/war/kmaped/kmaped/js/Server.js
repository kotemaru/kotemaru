
function Server(){this.initialize.apply(this, arguments)};
(function(Class) {
	Class.prototype.initialize = function() {
	}
	Class.prototype.save = function(json) {
		// abstract
	}
	Class.prototype.load = function() {
		// abstract
		return json;
	}
	Class.prototype.list = function(dir) {
		// abstract
		return [];
	}
	
})(Server);
//EOF.