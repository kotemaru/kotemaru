
function Cannon(game){this.initialize.apply(this, arguments)};
(function(Class, Super) {
	Util.extend(Class, Super);

	const NIL = Chip.add("nil", "");
	const SMOKE = Chip.add("smoke_b", "img/smoke_b.png");
	const CHIPS = [];
	(function() {
		const names = [
			"cannonR000","cannonR045","cannonR090","cannonR135",
			"cannonR180","cannonR225","cannonR270","cannonR315"
		];
		for (var i=0; i<names.length; i++) {
			CHIPS.push(Chip.add(names[i], "img/cannon/"+names[i]+".png"));
		}
	})();

	Class.prototype.initialize = function(game) {
		Super.prototype.initialize.apply(this, arguments);

		Util.copy(this,{
			layer:1, chip:CHIPS[0], x:150, y: 6400-320, count:0, hp:3, point:500
		});
	};
	
	Class.prototype.isActive = function() {
		with (this) {
			return (game.clipY <= y);
		}
	}
	Class.prototype.paint = function(game) {
		Super.prototype.paint.apply(this, arguments);
		with (this) {
			if (hp>1 && hp<3) {
				var cc = game.count%10;
				if (cc>5) game.drawImage(SMOKE, x+1, y-10);
				else game.drawImage(SMOKE, x+8, y-20);
			}
		}
	};

	Class.prototype.basicAction = function() {
		with (this) {
			Super.prototype.action.apply(this, arguments);
		}
	};
	function getAngle(x,y) {
		if( x>0 && y<0 ){
		    if( -y/x >= 3 ) return 0;
		    if( x/-y >= 3 ) return 2;
		    return 1 ;
		}
		if( x>0 && y>0 ){
		    if( x/y >= 3 ) return 2;
		    if( y/x >= 3 ) return 4;
		    return 3;
		}
		if( x<0 && y>0 ){
		    if( y/-x >= 3 ) return 4;
		    if( -x/y >= 3 ) return 6;
		    return 5;
		}
		if( x<0 && y<0 ){
		    if( -x/-y >= 3 ) return 6;
		    if( -y/-x >= 3 ) return 0;
		    return 7;
		}
		return 0;
	}
	
	//--------------------------------------
	Class.prototype.autoAngle = function(n) {
		with (this) {
			if (hp>0) {
				var angle = getAngle(game.myShip.x-x, game.myShip.y-y);
				angle += n;
				angle = (angle>=0?angle:CHIPS.length+angle);
				chip = CHIPS[angle];
			}
		}
	}

	var RANGE = 40;
	Class.prototype.cannon = function() {
		with (this) {
			basicAction();
			if (hp<=0) return;
			const tx = Math.abs(x - game.myShip.x);
			const ty = Math.abs(y - game.myShip.y);

			const cc = count%75;
			const cc2 = (count+5)%75;
			if( 10 <= cc2 && cc2 <=20 && count%4 == 0) {
				autoAngle(0);
			}
			if( 10 <= cc && cc <=30 && count%8 == 0 
				    && (tx>RANGE || ty>RANGE) ) {
				autoAngle(0);
				EnemyBullet.shootT(this, game.myShip );
			}
			count++;
		}
	}
	Class.prototype.cannon2 = function() {
		with (this) {
			basicAction();
			if (hp<=0) return;
			const tx = Math.abs(x - game.myShip.x);
			const ty = Math.abs(y - game.myShip.y);

			const cc2 = (count+5)%40;
			if( 0 <= cc2 && cc2 <=4 && count%4 == 0) {
				autoAngle(0);
			}
			if(  count%40 == 0
				    && (tx>RANGE || ty>RANGE) ) {
				autoAngle(0);
				EnemyBullet.shootT(this,  game.myShip );
			}
			count++;
		}
	}
	Class.prototype.cannon3 = function() {
		with (this) {
			basicAction();
			if (hp<=0) return;
			const tx = Math.abs(x - game.myShip.x);
			const ty = Math.abs(y - game.myShip.y);
			const cc = count%150;
			const cc2 = (count+10)%150;
		
			if( 10 <= cc2 && cc2 <=20 && count%4 == 0) {
				autoAngle(0);
			}
			if(10 <= cc && cc <= 20 && count%2 == 0
				    && (tx>RANGE || ty>RANGE) ) {
				autoAngle(0);
				EnemyBullet.shootT(this,  game.myShip );
			}
			count++;
		}
	}

})(Cannon, Ground);
