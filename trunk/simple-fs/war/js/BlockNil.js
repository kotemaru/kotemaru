function BlockNil(stage, src, initval){this.initialize.apply(this, arguments)};
(function(Class, Super) {
	Util.extend(Class, Super);
	Class.prototype.name = "none";

	Class.prototype.initialize = function(stage, src, initval) {
		if (stage == null) return; // for dummy
		this._super.initialize.apply(this, arguments);
		//this.elem.style.display = "none";
	}

	Class.prototype.isNil = function() {
		return true;
	}
	Class.prototype.rideOn = function(actor) {
		with (actor) {
			drop();
			gz = -1;
		}
	}

})(BlockNil, Block);
