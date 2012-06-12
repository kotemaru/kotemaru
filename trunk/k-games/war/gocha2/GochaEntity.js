/**
@author kotemaru@kotemaru.org
*/


enchant.gocha.GochaEntity = org.kotemaru.Class(enchant.Sprite, function(_class, _super){
	var Util = org.kotemaru.Util;
	
	_class.prototype.gochaMain = null;
	_class.prototype.parent = null;
	_class.prototype.bx = 0;
	_class.prototype.by = 0;
	_class.prototype.bw = 1;
	_class.prototype.bh = 1;
	_class.prototype.isActive = true;
	
	_class.prototype.initialize = function(opts) {
		_super.prototype.initialize.call(this, 1, 1);
		this.uid = enchant.gocha.uniqId++;
		Util.copy(this, opts);
	}
	_class.prototype.setParent = function(group){
		this.parent = group;
		this.gochaMain = group.gochaMain;
		if (this.gochaMain == null) return;
		this.bw = this.width / this.gochaMain.map.tileWidth;
		this.bh = this.height / this.gochaMain.map.tileHeight;
	}

	_class.prototype.dispose = function(delay) {
		const self = this;
		isActive = false;
		if (delay == null || delay == 0) {
			self.parent.removeGocha(self);
		} else {
			var timeLine = enchant.tl.Timeline(this);
			timeLine.delay(delay).then(function(){
				self.parent.removeGocha(self);
			});
			return timeLine;
		}
	}

	_class.prototype.draw = function() {
		with (this) {
			_style.zIndex = by;//TODO:
			var tw = gochaMain.map.tileWidth;
			var th = gochaMain.map.tileHeight;
			var correntH = height - (bh*th);
			moveTo(bx*tw, by*th-correntH);
		}
	};

	_class.prototype.isConflictEntity = function(e) {
		if (e == null) return false;
		return this.isConflict(e.bx, e.by, e.bw, e.bh);
	}
	_class.prototype.isConflict = function(tbx,tby,tbw,tbh) {
		var rbx = this.getDistanceX(tbx, tbw);
		if (rbx > 0) return false;
		var rby = this.getDistanceY(tby, tbh);
		if (rby > 0) return false;
		return (rbx < 0 && rby < 0);
	}

	_class.prototype.isTouchEntity = function(e) {
		if (e == null) return false;
		return this.isTouch(e.bx, e.by, e.bw, e.bh);
	}
	_class.prototype.isTouch = function(tbx,tby,tbw,tbh) {
		var rbx = this.getDistanceX(tbx, tbw);
		if (rbx > 0) return false;
		var rby = this.getDistanceY(tby, tbh);
		if (rby > 0) return false;
		return (rbx < 0 && rby <= 0) || (rbx <= 0 && rby < 0);
	}

	_class.prototype.getDistance = function(e) {
		if (e == null) return 0;
		var rbx = this.getDistanceX(e.bx, e.bw);
		var rby = this.getDistanceY(e.by, e.bh);
		return Math.sqrt(rbx*rbx+rby*rby);
	}

	_class.prototype.getDistanceX = function(tbx, tbw) {
		with (this) {
			if (bx <= tbx) {
				return tbx-(bx+bw);
			} else {
				return bx-(tbx+tbw);
			}
		}
	}
	_class.prototype.getDistanceY = function(tby, tbh) {
		with (this) {
			if (by <= tby) {
				return tby-(by+bh);
			} else {
				return by-(tby+tbh);
			}
		}
	}

	
	
});
