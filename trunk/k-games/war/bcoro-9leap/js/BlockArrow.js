
function BlockArrow(stage, src, initval){this.initialize.apply(this, arguments)};
(function(Class, Super) {
	Util.extend(Class, Super);
	Class.prototype.name = "slow";

	Class.prototype.rideOn = function(actor) {
		actor.gx += this.initval.gx;
		actor.gy += this.initval.gy;
	}

})(BlockArrow, Block);

