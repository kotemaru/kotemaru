/**
@author kotemaru@kotemaru.org
*/



enchant.gocha.GochaAiShooter = org.kotemaru.Class(enchant.gocha.GochaAiFighter , function(_class, _super){
	var Util = org.kotemaru.Util;
	const DIR_INFO = enchant.gocha.GochaActor.DIR_INFO;

	_class.prototype.initialize = function(actor) {
		_super.prototype.initialize.call(this, actor);
	}

	function isRandom(rate) {
		return rate>Math.random();
	}
	
	_class.prototype.thinkAttack = function() {
		with (this.actor) {
			// 敵がいなければ敵を探す。たまに別の敵を探してみる。
			if (target == null || isRandom(0.2)) {
				setTarget(gochaMain.searchEnemy(this.actor));
			}
			if (target == null) {
				this.thinkDefense();
				return;
			}
			if (target.isDead) {
				target = null;
				return;
			}
			
			// 敵との距離を得る
			var len = getDistance(target);
			if (len < 3) {
				// 近すぎなら逃げる
				this.thinkEscape();
			} else if (len < 7) {
				// 射程内なら攻撃
				dir = getEntityDir(target, dir); // 敵の方を向く
				shoot(target);
			} else {
				// 遠すぎなら近ずく
				this.doMove(getEntityDir(target, dir));
			}
		}
	}
	
	_class.prototype.thinkDefense = function() {
		with (this.actor) {
			
			// 持ち場を得る。
			var post = getPostPosition();
			var postDir = getEntityDir(post, "");// 敵の方を向く
	
			// 持ち場に居て接敵していれば攻撃する。
			if (postDir == "") {
				targetPosition = null;
				setTarget(gochaMain.searchEnemy(this.actor));
				
				var len = getDistance(target);
				if (len < 7) {
					// 射程内なら攻撃
					dir = getEntityDir(target, dir); // 敵の方を向く
					shoot(target);
				} else {
					recover(0.5); // 何もしないので回復する。
				}
			}
			// 持ち場から離れていれば持ち場へ移動する。
			else {
				this.doMove(postDir);
			}
		}
	}

});
