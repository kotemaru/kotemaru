/**
@author kotemaru@kotemaru.org
*/



enchant.gocha.GochaAiFighter = org.kotemaru.Class(null, function(_class, _super){
	var Util = org.kotemaru.Util;
	const DIR_INFO = enchant.gocha.GochaActor.DIR_INFO;

	_class.prototype.initialize = function(actor) {
		this.actor = actor;
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
			
			// 敵の方向を得る
			var tgtDir = getEntityDir(target, "");

			// 接敵していれば攻撃する。
			if (isTouchEntity(target)) {
				if (tgtDir != "") dir = tgtDir; // 敵の方を向く
				attack(target); // 敵を攻撃する
				return;
			}
			
			//敵に無かって移動する
			if (tgtDir != "") this.doMove(tgtDir);
		}
	}
	
	_class.prototype.doMove = function(tgtDir) {
		with (this.actor) {
			var backDir = DIR_INFO[dir].b;

			// 敵に無かって移動する
			if (tgtDir != backDir && moveTest(tgtDir, true)) return true;

			// 敵に無かって移動できなければ一旦その敵は諦める。
			target = null;
			
			// 現在の進行方向に進めればとりあえず進む。
			var info = DIR_INFO[tgtDir];
			if (dir==info.l && moveTest(info.l, true)) return true;
			if (dir==info.r && moveTest(info.r, true)) return true;

			// 現在の進行方向に進めなければ左右に曲がる。
			var info = DIR_INFO[dir];
			if (isRandom(0.5)) {
				if (info.l != backDir && moveTest(info.l, true)) return true;
				if (info.r != backDir && moveTest(info.r, true)) return true;
			} else {
				if (info.r != backDir && moveTest(info.r, true)) return true;
				if (info.l != backDir && moveTest(info.l, true)) return true;
			}

			// 左右も進め無い場合は下がる。
			if (moveTest(info.b, true)) return true;
			return false;
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
				if (isTouchEntity(target)) {
					dir = getEntityDir(target, dir);// 敵の方を向く
					attack(target); // 敵を攻撃する
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

	_class.prototype.thinkEscape = function() {
		with (this.actor) {
			var enemyInfo = gochaMain.searchEnemy(this.actor,true);
			var tgt = enemyInfo.enemy;
			if (tgt == null) return;
			setTarget(tgt);
			var rbx = tgt.bx - bx;
			var rby = tgt.by - by;
			
			// 敵が近くにいれば遠ざかる
			if (enemyInfo.len < 3) {
				var escDir = getAdjustDir(-rbx, -rby);
				if (moveTest(escDir, true)) return true;
				escDir = getAdjustDir(-rby, -rbx);
				if (moveTest(escDir, true)) return true;
			}
			
			// 逃げれない場合は反撃する
			if (isTouchEntity(tgt)) {
				dir = getAdjustDir(rbx, rby);
				attack(target);
				helpMe(); // 仲間に助けを求める。
				return true;
			}
				
			{
				// 敵がいなければ回復する。
				recover(1.0);
				// 回復したら戦線復帰する。
				if (hp/max_hp > 0.5) isEscape = false;
			}
			return false;
		}
	}

});
