
function BlockGoal(stage, src, initval){this.initialize.apply(this, arguments)};
(function(Class, Super) {
	Util.extend(Class, Super);

	Class.prototype.rideOn = function(actor) {
		if (actor.isMyMarble) {
			RollingMarble.instance.goal();
		}
	}

})(BlockGoal, Block);

