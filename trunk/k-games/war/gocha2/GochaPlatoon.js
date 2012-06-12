/**
@author kotemaru@kotemaru.org
*/

if (!enchant.gocha) enchant.gocha = {};

enchant.gocha.GochaPlatoon = org.kotemaru.Class(enchant.gocha.GochaGroup, function(_class, _super){
	var Util = org.kotemaru.Util;

	const MAX_ACTOR_COUNT = 16;
	
	_class.prototype.isPlatoonClass = true;
	_class.prototype.leader = null;
	_class.prototype.team = 0;
	_class.prototype.dir = null;
	_class.prototype.isFirstLines = true;

	_class.prototype.initialize = function(team) {
		_super.prototype.initialize.call(this);
		this.team = team;
		this.lines = new enchant.gocha.GochaLines();

		const self = this;
		Util.addListeners(this);
	};
	
	_class.prototype.addGocha = function(entity) {
		_super.prototype.addGocha.call(this, entity);
		entity.team = this.team;

		// メンバー数の上限チェック
		var count = this.getMemberCount();
		if (count > MAX_ACTOR_COUNT) {
			throw new Error("Platoon max actor "+MAX_ACTOR_COUNT);
		}
	}
	_class.prototype.getMemberCount = function() {
		var count = 0;
		for (var i in this.entities) count++;
		return count;
	}

	_class.prototype.doLines = function() {
		this.lines.setPositions(this);
		this.isFirstLines = false;
	}

	_class.prototype.setLeader = function(actor){
		this.leader = actor;
		this.dir = actor.dir;
		this.addGocha(actor);
	}

	_class.prototype.getPostPosition = function(actor){
		this.leader = actor;
		this.addGocha(actor);
	}
	
	_class.prototype.command = function(_team, cmd, opts) {
		if (_team != this.team) return;

		if (cmd == "moveLeader") {
			this.leader.targetPosition = opts;
		} else if (cmd == "lines") {
			this.doLines();
		} else {
			for (var i in this.entities) {
				var ent = this.entities[i];
				if (opts == null) {
					if (ent != this.leader) ent.command(_team, cmd, opts);
				} else if (opts["for"] == "all") {
					ent.command(_team, cmd, opts);
				} else if (opts["for"] == "leader") {
					if (ent == this.leader) ent.command(_team, cmd, opts);
				} else {
					if (ent != this.leader) ent.command(_team, cmd, opts);
				}
			}
		}
	}

});

