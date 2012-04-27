function JammerG(stage, src, initval){this.initialize.apply(this, arguments)};

(function(Class, Super) {
	Util.extend(Class, Super);
	Class.prototype.initialize = function(stage, src, initval) {
		this._super.initialize.apply(this, arguments);
		this.bonusTimeCount = 0;
	}

	const SPEED = 2.0;
	Class.prototype.action = function() {
		with (this) {
			if (isDropping) return dropping();
			const a = this, b = stage.marble;
			const W = b.x - a.x;
			const H = b.y - a.y;
			const l = Math.sqrt((W*W) + (H*H));
			if (isNaN(l)) alert("nan");
			if (l < 200 && l > 32 && !b.isWating && !b.isDropping  ) {
				gx += (W>0?SPEED:-SPEED);
				gy += (H>0?SPEED:-SPEED);
			}
			if (bonusTimeCount>0) bonusTimeCount--;
		}
		this._super.action.apply(this, arguments);
	}
	Class.prototype.touch = function(actor) {
		if (actor.isMyMarble) {
			this.bonusTimeCount = 20;
		}
	}
	Class.prototype.drop = function(x,y) {
		this._super.drop.apply(this, arguments);
		if (this.bonusTimeCount>0) {
			RollingMarble.instance.time += (this.bonusSec * 1000);
			this.stage.bonusActor.show(this.x, this.y, "+"+this.bonusSec+"sec");
		}
	}

})(JammerG, Marble);
