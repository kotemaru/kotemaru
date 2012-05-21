

function MyBullet(game){this.initialize.apply(this, arguments)};
(function(Class, Super) {
	Util.extend(Class, Super);
	
	var entities = [];
	Class.getInstance = function(game) {
		if (entities.length == 0) return new MyBullet(game);
		return entities.pop();
	}
	
	Class.prototype.initialize = function(game) {
		Super.prototype.initialize.apply(this, arguments);
		//this.color = "#ffff00";
	};
	
	Class.prototype.close = function() {
		entities.push(this);
		this.game.delEntity(this);
	}

	Class.prototype.action = function() {
		const planes = this.game.enemyPlanes;
		for (var id in planes) {
			var coor = planes[id].isHit(this);
			if (coor != false) {
				this.hit(coor);
				planes[id].hit(this.weigh);
				break;
			}
		}
		Super.prototype.action.apply(this, arguments);
	};
	
})(MyBullet,Bullet);
