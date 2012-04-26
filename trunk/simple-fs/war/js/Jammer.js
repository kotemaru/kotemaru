function Jammer(stage, src, initval){this.initialize.apply(this, arguments)};

(function(Class, Super) {
	Util.extend(Class, Super);

	Class.prototype.drop = function(x,y) {
		this.super.drop.apply(this, arguments);
		RollingMarble.instance.time += (this.bonusSec * 1000);
		this.stage.bonusActor.show(this.x, this.y, "+"+this.bonusSec+"sec");
	}

})(Jammer, Marble);
