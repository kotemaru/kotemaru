function MyShipDummy(game){this.initialize.apply(this, arguments)};
(function(Class, Super) {
	Util.extend(Class, Super);

	Class.prototype.action = function() {
		this.y -= this.game.scroll;
	}
	Class.prototype.isHit = function(bullet) {
		return false;
	};
	Class.prototype.isHitPlane = function(plane) {
		return false;
	};
	Class.prototype.paint = function(plane) {
	};
	Class.prototype.hit = function(waigh){
	}
	
})(MyShipDummy, MyShip);

