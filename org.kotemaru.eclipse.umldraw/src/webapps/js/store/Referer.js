

function Referer(){this.initialize.apply(this, arguments)};
(function(_class){
	var idCount = 1;
	var jsons = {};
	var objs = {};
	
	_class.prototype.initialize = function() {
		this.reset();
	}

	_class.prototype.reset = function() {
		this.idCount = 1;
		this.jsons = {};
		this.objs = {};
	}
	
	_class.prototype.getJsons = function() {
		var dst = {};
		for (var i in this.jsons) {
			var json = this.jsons[i];
			dst[json.id] = json;
		}
		return dst;
	}
	
	_class.prototype.getJson = function(internalId) {
		return this.jsons[internalId];
	}
	_class.prototype.putJson = function(internalId, json) {
		json.id = this.idCount++;
		this.jsons[internalId] = json;
	}
	
	_class.prototype.preLoad = function(jsons) {
		for (var i in jsons) {
			var cls = Lang.classForName(jsons[i]._class);
			var obj = new cls();
			this.objs[i] = obj;
		}
	}
	_class.prototype.load = function(func, jsons) {
		for (var i in jsons) {
			func(this.objs[i], jsons[i]);
		}
	}
	
	_class.prototype.getObj = function(id) {
		return this.objs[id];
	}
	
})(Referer);
