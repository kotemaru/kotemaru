
function Lodge(game){this.initialize.apply(this, arguments)};
(function(Class, Super) {
	Util.extend(Class, Super);

	const CHIP = Chip.add("lodge", "img/lodge.png");

	Class.prototype.initialize = function(game) {
		Super.prototype.initialize.apply(this, arguments);
		this.chip = CHIP;
	};

})(Lodge, Ground);
