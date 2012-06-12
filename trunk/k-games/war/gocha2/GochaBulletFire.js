/**
@author kotemaru@kotemaru.org
*/

enchant.gocha.GochaBulletFire = org.kotemaru.Class(enchant.gocha.GochaBullet, function(_class, _super){
	var Util = org.kotemaru.Util;
	var CHIP_FIRE = enchant.gocha.Chip.create("img/bulletFire.png");

	_class.prototype.isMagic = true;

	_class.prototype.initialize = function(shooter, target, opts) {
		_super.prototype.initialize.call(this, shooter, target, opts);
		this.image = CHIP_FIRE.image;
		this.width = 16;
		this.height = 16;
		this.mp = 50;
	}
});
