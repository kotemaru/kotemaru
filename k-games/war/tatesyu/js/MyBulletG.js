

function MyBulletG(game){this.initialize.apply(this, arguments)};
(function(Class, Super) {
	Util.extend(Class, Super);

	var entities = [];
	Class.getInstance = function(game) {
		if (entities.length == 0) return new MyBulletG(game);
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
		if (this.state == 0 && this.count++>=4) {
			this.hitGround();
		}
		Super.prototype.action.apply(this, arguments);
	};
	
	Class.prototype.hitGround = function() {
		this.hit(this);
		const grounds = this.game.enemyGrounds;
		for (var id in grounds) {
			var coor = grounds[id].isHit(this);
			if (coor != false) {
				grounds[id].hit(this.weigh);
				//break;
			}
		}
	};
	
})(MyBulletG,Bullet);
