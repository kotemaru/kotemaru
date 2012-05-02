function Jammer(stage, src, initval){this.initialize.apply(this, arguments)};

(function(Class, Super) {
	Util.extend(Class, Super);

	Class.prototype.drop = function(x,y) {
		if (this._super.drop.apply(this, arguments)) {
			RollingMarble.instance.time += (this.bonusSec * 1000);
			this.stage.bonusActor.show(this.x, this.y, "+" + this.bonusSec + "sec");
		}
	}
	Class.prototype.recover = function() {
		const self = this;
		this.elem.style.display = "none";
		setTimeout(function(){
			self.reset();
			self.elem.style.display = "block";
		}, 3000);
	}

})(Jammer, Marble);
