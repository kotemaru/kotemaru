function Actions(){this.initialize.apply(this, arguments)};
(function(Class) {
	const SPEED = 20;
	const LEN = 13;
	function shootT(me, target) {
		with (me) {
			const tx = target.x-x;
			const ty = target.y-y;
			const len = Math.sqrt(tx*tx+ty*ty);
			const bvx = tx * (SPEED / len);
			const bvy = ty * (SPEED / len);
			const lx = tx * (LEN / len);
			const ly = ty * (LEN / len);
			EnemyBullet.getInstance(game).init(x+lx,y+ly, bvx,bvy, lx,ly);
		}
	}
	function shootXY(me, bvx, bvy, lx,ly) {
		with (me) {
			EnemyBullet.getInstance(game).init(x+lx,y+ly, bvx,bvy, lx,ly);
		}
	}
	function bigBom(me) {
		with (me) {
			for (var i=0; i<16; i++) {
				var r = Math.PI*2 / 17 * i;
				var bvx = Math.cos(r) * SPEED;
				var bvy = Math.sin(r) * SPEED;
				var lx = Math.cos(r) * LEN;
				var ly = Math.sin(r) * LEN;
				EnemyBullet.getInstance(game).init(x,y, bvx,bvy, lx,ly);
			}
		}
	}

	Class.furafura1 = function() {
		with (this) {
			if( count >= 220 ){ game.delEntity(this); return;}
			if (game.myShip.isHitPlane(this)) {
				hit(100);
				game.myShip.hit(5);
			}
			
			y += 2;
			if( count % 30 == 0 ){
				this.vx = 2;
				shootT(this, game.myShip);
			}
			if( count % 60 == 0 ){
				this.vx = -2;
			}
			x += vx;
			count++;
		}
	};

	
	Class.dive = function() {
		with (this) {
			if( count >= 120 ){  game.delEntity(this); return;}
			if (game.myShip.isHitPlane(this)) {
				hit(100);
				game.myShip.hit(5);
			}
			
			y += 8;
			
			if( count % 5 == 0 ){
				shootXY(this, 0, SPEED, 0, LEN);
			}
			count++;
		}
	}
	
	Class.stalk = function() {
		with (this) {
			const tx = game.myShip.x ;
			//const ty = game.myShip.y ;
			if (game.myShip.isHitPlane(this)) {
				hit(100);
				game.myShip.hit(5);
			}

			if( count >= 120 ){  game.delEntity(this); return;}
			if( count == 0 ) this.mode = 0;
			y += 3;
			if (mode == 0) {
				x += (x<tx ? 3 : -3);
				if (Math.abs(x-tx)<5) {
					mode = 1;
					this.shootCount = 6*4;
				}
			} else {
				x -= (x<tx ? 3 : -3);
				if (this.shootCount-->0 && count%4 == 0) shootXY(this, 0, SPEED, 0, LEN);
			}
			count++;
		}
	}

	var RANGE = 40;
	Class.cannon = function() {
		with (this) {
			basicAction();
			if (hp<=0) return;
			const tx = Math.abs(x - game.myShip.x);
			const ty = Math.abs(y - game.myShip.y);

			const cc = count%75;
			if( 10 <= cc && cc <=30 && count%8 == 0 
				    && (tx>RANGE || ty>RANGE) ) {
			    shootT(this, game.myShip );
			}
			count++;
		}
	}
	Class.cannon2 = function() {
		with (this) {
			basicAction();
			if (hp<=0) return;
			const tx = Math.abs(x - game.myShip.x);
			const ty = Math.abs(y - game.myShip.y);
			if(  count%40 == 0
				    && (tx>RANGE || ty>RANGE) ) {
			    shootT(this,  game.myShip );
			}
			count++;
		}
	}
	Class.cannon3 = function() {
		with (this) {
			basicAction();
			if (hp<=0) return;
			const tx = Math.abs(x - game.myShip.x);
			const ty = Math.abs(y - game.myShip.y);
			const cc = count%150;
			
			if(10 <= cc && cc <= 20 && count%2 == 0
				    && (tx>RANGE || ty>RANGE) ) {
			    shootT(this,  game.myShip );
			}
			count++;
		}
	}

	Class.base2 = function() {
		with (this) {
			basicAction();
			if (state == 1) {
				bigBom(this);
				game.addEntity(new Item20mm(game).init(x,y));
			}
		}
	}
	
	Class.base3 = function() {
		with (this) {
			basicAction();
			if (state == 1) {
				game.addEntity(new ItemSpanner(game).init(x,y));
			}
		}
	}
	
	Class.boss = function() {
		with (this) {
			if (hp<=0) {
				game.scroll = 2;
				game.delEntity(this);
				game.boss = null;
				return;
			}
			const cc = count % 200;
			if( 20 < cc && cc < 40 && cc%5 == 0) {
			    shootT(this, game.myShip );
			}
			if(120 < cc && cc < 140){
				var c3 = cc % 4;
				if (c3 == 0) EnemyBullet.getInstance(game).init(x+5,y+30, 0,SPEED, 0,LEN);
				if (c3 == 2) EnemyBullet.getInstance(game).init(x-5,y+30, 0,SPEED, 0,LEN);
				//if (c3 == 2) shootXY(this,  4, SPEED, 1, LEN);
				//if (c3 == 4) shootXY(this, -4, SPEED, 1, LEN);
			}
			if( cc == 0 ){
			    bigBom(this);
			}
			if (count == 100) {
				game.scroll = 0;
			}
			if (count == 0) {
				game.boss = this;
			}
			count++;
			
		}
	}
	
/*

	                       
	int	a_boss( ulong count , Enemy *actor , Unit *target )
	{
		GroundEnemy	*me = (GroundEnemy*)actor;
		if( me->damage >= me->durability ) return 0;
		if( count == 1 ){
		    me->durability = 100 ;
		}

		int x = (int)actor->pos.x ;
		int y = (int)actor->pos.y ;
		if( ((count-20)%200)/40 == 0 && count%5 == 0
		    && ! target->isHit( x-RANGE , y-RANGE , x+RANGE , y+RANGE ) ){
		    actor->shoot( (int)target->pos.x , (int)target->pos.y , 5 );
		}
		if( ((count-120)%200)/30 == 0 && count%5 == 0){
		    actor->shoot( (int)actor->pos.x   , (int)actor->pos.y+10,6);
		    actor->shoot( (int)actor->pos.x+8 , (int)actor->pos.y+10,6);
		    actor->shoot( (int)actor->pos.x-8 , (int)actor->pos.y+10,6);
		}
		if( count % 400 == 0 ){
		    bigBom( actor , 6 );
		}
		return 0 ;
	}
*/                	
})(Actions);
