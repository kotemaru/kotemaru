
function BlockSlow(stage, src, initval){this.initialize.apply(this, arguments)};
(function(Class, Super) {
	Util.extend(Class, Super);
	Class.prototype.name = "slow";

	Class.prototype.rideOn = function(actor) {
		actor.gx *= 0.5;
		actor.gy *= 0.5;
	}

})(BlockSlow, Block);

