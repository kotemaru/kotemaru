function MyMarble(stage, src, initval){this.initialize.apply(this, arguments)};

(function(Class, Super) {
	Util.extend(Class, Super);

	Class.prototype.initialize = function(stage, src, initval) {
		this.super.initialize.apply(this, arguments);
	}
	Class.prototype.isMyMarble = true;

	Class.prototype.reset = function(stage, src, initval) {
		this.super.reset.apply(this);
		this.log = [];
		this.count = 0;
		for (var i=0; i<6; i++) this.log.push({x:this.x, y:this.y});
		this.isWating = false;
	}

	Class.prototype.accele = function(grav) {
		this.gx += grav.x * RollingMarble.instance.params.sensitive;
		this.gy -= grav.y * RollingMarble.instance.params.sensitive;
		this.gLimit();
	}

	Class.prototype.action = function() {
		with (this) {
			if (isWating) return blink();
			if (count++>5 && !isDropping) {
				log.shift();
				log.push({x:x, y:y});
				count = 0;
			}
		}
		this.super.action.apply(this);
	}
	Class.prototype.recover = function(maxCount) {
		if (!maxCount) maxCount = 30;
		Sound.play("boyon");
		this.super.reset.apply(this);

		with (this) {
			for (var i=log.length-1; i>=0; i--) {
				if (!stage.getBlock(log[i].x,log[i].y).isNil()) {
					this.x = log[i].x;
					this.y = log[i].y;
				}
			}
			this.reflect();
		}

		this.count = 0;
		this.elem.style.opacity = 0;
		this.isWating = true;
		this.maxCount = maxCount;
	}
	Class.prototype.blink = function() {
		with (this) {
			count++;
			elem.style.opacity = count/maxCount*0.7;
			if (count > maxCount*0.8) {
				elem.style.display = "none";
			} 
			if (count > maxCount) {
				elem.style.display = "block";
				elem.style.opacity = 1.0;
				isWating = false;
				gx = 0;
				gy = 0;
			}
		}
	}

})(MyMarble, Marble);
