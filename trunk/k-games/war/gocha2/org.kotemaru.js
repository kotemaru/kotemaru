
Array.prototype.shuffle = function() {
		var i = this.length;
	    while(i){
	        var j = Math.floor(Math.random()*i);
	        var t = this[--i];
	        this[i] = this[j];
	        this[j] = t;
	    }
	    return this;
}
Array.prototype.remove = function(obj) {
	for (var i=0; i<this.length; i++) {
		if (obj == this[i]) {
			return this.splice(i,1);
		}
	}
    return this;
}

window.org = window.org ? window.org : {kotemaru:{}};

(function(PACKAGE){
	PACKAGE.Class = function(_super, definidion) {
		var thisClass = _super 
			? enchant.Class.create(_super)
			: function(){
				this.initialize.apply(this, arguments);
			}
		;
		definidion(thisClass, _super);
		return thisClass;
	}

	PACKAGE.Util = {};
	(function(_class){
		_class.addListeners = function(self) {
			for (var name in self) {
				if (typeof self[name] == "function" 
					&& name.match(/^on_/)) {
					var etype = name.replace(/^on_/,"");
					self.addEventListener(etype, self[name]);
				}
			}
		}
		_class.copy = function(dst, src) {
			if (src != null) {
				for (var k in src) dst[k] = src[k];
			}
			return dst;
		}
	})(PACKAGE.Util);
	

})(org.kotemaru);

