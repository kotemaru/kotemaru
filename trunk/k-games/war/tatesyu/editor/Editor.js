
function Editor(){this.initialize.apply(this, arguments);};
(function(Class) {

	const OPTS = {
		w: 320,
		h: 6400,
		layers: [
			{name:"map",    type:"map",   grid:32},
			{name:"ground", type:"actor", grid:1},
			{name:"air",    type:"actor", grid:1}
		]
	};
	
	Class.prototype.initialize = function($elem, opts) {
		this.$elem = $elem;
		this.opts = $.extend(OPTS, opts);
		
		
		Util.css();
		
		
		
	};

})(Editor);
