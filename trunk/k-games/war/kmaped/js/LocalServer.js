
function LocalServer(){this.initialize.apply(this, arguments)};
(function(Class) {
	const PREFIX = "map:/";
	Class.prototype.initialize = function() {
	}
	Class.prototype.save = function(name, json) {
		var key = (PREFIX + name).replace(/\/\//g,"/");
		localStorage.setItem(key, json);
	}
	Class.prototype.load = function(name) {
		var key = (PREFIX + name).replace(/\/\//g,"/");
		return localStorage.getItem(key);
	}
	Class.prototype.remove = function(name) {
		var key = (PREFIX + name).replace(/\/\//g,"/");
		return localStorage.removeItem(key);
	}
	Class.prototype.list = function(dir) {
		var list = [];
		var prefix = (PREFIX + dir+"/").replace(/\/\//g,"/");
		
		for (var i=0; i<localStorage.length; i++) {
			var key = localStorage.key(i);
			if (key.indexOf(prefix) == 0) {
				list.push(key.substr(prefix.length));
			}
		}
		return list;
	}
	
})(LocalServer, Server);
//EOF.