/**
@author kotemaru@kotemaru.org
*/

enchant.gocha.GochaBullet = org.kotemaru.Class(enchant.gocha.GochaEntity, function(_class, _super){
	var Util = org.kotemaru.Util;
	var CHIP_MAGIC = enchant.gocha.Chip.create("img/bullet.png", 0,0,16,16);
	var CHIP_FIRE  = enchant.gocha.Chip.create("img/effect0.gif",0,0,16,16);

	_class.prototype.isBulletClass = true;

	_class.prototype.initialize = function(shooter, target, opts) {
		_super.prototype.initialize.call(this);
		this.image = CHIP_MAGIC.image;
		this.width = CHIP_MAGIC.w;
		this.height = CHIP_MAGIC.h;
		
		Util.copy(this,{
			shooter: shooter,
			dir: shooter.dir,
			team: shooter.team,
			bx: shooter.bx, by: shooter.by,
			bw:1, bh:1,
			
			speed: 0.7,
			ar: shooter.mar, ap:shooter.map, mp: 50,
			isActive: true,
			isMagic: true,
		});
		Util.copy(this, opts);
		
		var vx = (target.bx+(target.bw/2) - this.bx); 
		var vy = (target.by - this.by);
		var len = Math.sqrt(vx*vx+vy*vy);
		
		this.vbx = vx * this.speed/len; 
		this.vby = vy * this.speed/len; 
	}

	_class.prototype.draw = function() {
		with (this) {
			_style.zIndex = 900;//TODO:
			var tw = gochaMain.map.tileWidth;
			var th = gochaMain.map.tileHeight;
			var correntH = height - (bh*th);
			moveTo(bx*tw, by*th-correntH);
		}
	};

	_class.prototype.action = function() {
		const self = this;
		with (this) {
			if (age>11) {
				dispose();
				return;
			}
			
			bx += vbx;
			by += vby;

			var maxFrame = (image.width/width)-1;
			frame = age % maxFrame;

			var tgt = gochaMain.getConflictEnemy(this,bx,by,bw,bh+1); // actorはbh-1なので。
			if (tgt != null) {
				if (isMagic) {
					tgt.gotAttackMagic(this);
				} else {
					tgt.gotAttack(this);
				}
				frame = maxFrame;
				vbx = 0;
				vby = 0;
				tl.delay(1).then(function(){self.dispose();});
			}
		}
	}

});
