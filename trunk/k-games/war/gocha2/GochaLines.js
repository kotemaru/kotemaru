/**
@author kotemaru@kotemaru.org
*/

if (!enchant.gocha) enchant.gocha = {};

enchant.gocha.GochaLines = org.kotemaru.Class(null, function(_class, _super){
	var Util = org.kotemaru.Util;
	
	// dir=U の相対座標
	const POS_FIGHTER = [
	    {x: 0, y:-1},
	    {x:-1, y:-1},
	    {x: 1, y:-1},
	    {x:-2, y:-1},
	    {x: 2, y:-1},
	    {x:-3, y:-1},
	    {x: 3, y:-1},
	    
	    {x: 0, y:1},
	    {x:-1, y:1},
	    {x: 1, y:1},
	    {x:-2, y:1},
	    {x: 2, y:1},
	    {x:-3, y:1},
	    {x: 3, y:1},
	    
	    {x: 3, y:0},
	];
	const POS_SHOOTER = [
 	    {x: 0, y:1},
	    {x:-1, y:1},
	    {x: 1, y:1},
	    {x:-2, y:1},
	    {x: 2, y:1},
	    {x:-3, y:1},
	    {x: 3, y:1},
	    
	    {x: 0, y:-1},
	    {x:-1, y:-1},
	    {x: 1, y:-1},
	    {x:-2, y:-1},
	    {x: 2, y:-1},
	    {x:-3, y:-1},
	    {x: 3, y:-1},

 	    {x: 3, y:0},
	];
	
	_class.prototype.initialize = function() {
		this.positions = {
			fighter:POS_FIGHTER, shooter:POS_SHOOTER
		};
	};
	

	_class.prototype.setPositions = function(platoon) {
		var dir = platoon.dir;
		var entities = platoon.entities;

		var fighterNo = 0;
		var shooterNo = 0;
		for (var i in entities) {
			var entity = entities[i];
			if (entity == platoon.leader) {
				entity.postPosition = {x:0, y:0};
			} else if (entity.isActorClass) {
				var pos = entity.isShooter
						?this.positions.shooter[shooterNo++]
						:this.positions.fighter[fighterNo++]
				;
				var x = pos.x;
				var y = pos.y;
				if (dir == "D") {
					y = -y;
				} else if (dir == "L") {
					x = pos.y;
					y = pos.x;
				} else if (dir == "R") {
					x = -pos.y;
					y = pos.x;
				}
				entity.postPosition = {x:x*2, y:y}; // TODO:
				if (platoon.isFirstLines) {
					entity.bx = platoon.leader.bx;
					entity.by = platoon.leader.by;
				}
				//console.log(entity.team, entity.by);
			}
		}

	};

});