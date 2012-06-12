/**
@author kotemaru@kotemaru.org
*/

enchant.gocha.GochaGroup = org.kotemaru.Class(enchant.Group, function(_class, _super){
	var Util = org.kotemaru.Util;
	
	_class.prototype.isGroupClass = true;
	_class.prototype.gochaMain = null;
	_class.prototype.parent = null;
	_class.prototype.entities = null;

	_class.prototype.initialize = function() {
		_super.prototype.initialize.call(this);
		this.uid = enchant.gocha.uniqId++;
		this.entities = {};
	};
	
	_class.prototype.addGocha = function(entity) {
		entity.setParent(this);
		this.entities[entity.uid] = entity;
		this.addChild(entity);
		this.isUpdate = true;
	}

	_class.prototype.removeGocha = function(entity) {
		delete this.entities[entity.uid];
		this.removeChild(entity);
		this.isUpdate = true;
	}
	
	_class.prototype.setParent = function(group) {
		this.parent = group;
		this.gochaMain = group.gochaMain;

		for (var i in this.entities) {
			var entity = this.entities[i];
			entity.setParent(this);
		}
	}
	
	_class.prototype.getActionList = function() {
		var list = [];
		for (var i in this.entities) {
			var entity = this.entities[i];
			if (entity.isGroupClass) {
				var subList = entity.getActionList();
				Array.prototype.push.apply(list, subList);
			} else if (entity.action) {
				list.push(entity);
			}
		}
		return list;
	}
	
	_class.prototype.getConflicters = function() {
		var list = [];
		for (var i in this.entities) {
			var entity = this.entities[i];
			if (entity.isGroupClass) {
				var subList = entity.getConflicters();
				Array.prototype.push.apply(list, subList);
			} else if (entity.isActorClass) {  // 現状Actorのみ
				list.push(entity);
			}
		}
		return list;
	}
	
	_class.prototype.draw = function() {
		with (this) {
			for (var i in entities) {
				if (entities[i].draw) entities[i].draw();
			}
		}
	}
	
	_class.prototype.command = function(_team, cmd, opts) {
		with (this) {
			for (var i in entities) {
				if (entities[i].command) entities[i].command(_team, cmd, opts);
			}
		}
	}
	
	_class.prototype.searchEnemy = function(me, isInfoResult) {
		var minLen = 1000000;
		var enemy = null;

		for (var i in this.entities) {
			var entity = this.entities[i];
			if (entity.isGroupClass) {
				var info = entity.searchEnemy(me, true);
				if (info.len < minLen) {
					minLen = info.len;
					enemy = info.enemy;
				}
			} else if (entity.isActorClass) {
				if (!entity.isDead && entity.team != me.team) {
					var len = me.getDistance(entity);
					if (len < minLen) {
						minLen = len;
						enemy = entity;
					}
				}
			}
		}
		if (isInfoResult) return {len:minLen, enemy:enemy};
		return enemy;
	}

	
	
});
