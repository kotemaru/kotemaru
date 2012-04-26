function Dialog() {}
(function(Class){
	Class.opts = null;
	Class.open = function(id, opts) {
		Class.close();
		var div = document.getElementById(id);
		div.style.display = "block";
		Class.current = div;
		Class.opts = opts;
	}
	Class.ok = function() {
		Class.close();
		if (typeof Class.opts == "function") {
			Class.opts();
		} else {
			Class.opts.ok();
		}
	}
	Class.ng = function() {
		Class.close();
		Class.opts.ng();
	}
	Class.other = function(val) {
		Class.close();
		if (typeof Class.opts == "function") {
			Class.opts(val);
		} else {
			Class.opts.other(val);
		}
	}
	Class.close = function() {
		if (Class.current) {
			Class.current.style.display = "none";
		}
		Class.current = null;
	}

})(Dialog);
