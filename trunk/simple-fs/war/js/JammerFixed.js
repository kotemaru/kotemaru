function JammerFixed(stage, src, initval){this.initialize.apply(this, arguments)};

(function(Class, Super) {
	Util.extend(Class, Super);
	Class.prototype.action = function() {
		// nop.
	}
})(JammerFixed, Jammer);
