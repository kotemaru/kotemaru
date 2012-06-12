/**
@author kotemaru@kotemaru.org
*/

/**
 * queueと同じ機能。実行タイミングを制御したいので自前で実装。
 */

enchant.gocha.ExecQueue = org.kotemaru.Class(null, function(_class, _super){
	var Util = org.kotemaru.Util;
	
	_class.prototype.entity = null;
	_class.prototype.queue = null;
	
	_class.prototype.initialize = function(entity) {
		this.entity = entity;
		this.queue = {}
	}

	_class.prototype.after = function(delay, func) {
		with (this) {
			var time = entity.age+delay;
			var list = queue[time];
			if (list == null) {
				list = [];
				queue[time] = list;
			}
			list.push(func);
		}
	}
	_class.prototype.exec = function() {
		with (this) {
			var time = entity.age;
			var list = queue[time];
			if (list) {
				for (var i=0; i<list.length; i++) {
					list[i]();
				}
				delete queue[time];
				return list.length;
			}
			return 0;
		}
	}
	
	
	
});
