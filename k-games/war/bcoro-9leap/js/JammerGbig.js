function JammerGbig(stage, src, initval){this.initialize.apply(this, arguments)};

(function(Class, Super) {
	Util.extend(Class, Super);
	Class.prototype.initialize = function(stage, src, initval) {
		this._super.initialize.apply(this, arguments);
		this.bonusTimeCount = 0;

		this.w = 64;
		this.h = 64;
		this.elem.width = this.w;
		this.w2 = this.w/2;
		this.h2 = this.h/2;
	}

})(JammerGbig, JammerG);
