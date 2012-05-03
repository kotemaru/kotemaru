function Marble(stage, src, initval){this.initialize.apply(this, arguments)};
(function(Class) {

	Class.prototype.initialize = function(stage, src, initval) {
		this.stage = stage;
		this.elem = Util.createImg(src, {zIndex:10});
		this.initval = initval;
		this.ignoreImpact = {};
		this.reset();
	}
		
	Class.prototype.reset = function() {
		this.isDropping = false;
		this.x = 0;
		this.y = 0;
		this.z = 0;
		this.w = 32;
		this.h = 32;
		this.nx = 0;
		this.ny = 0;
		this.nz = 0;
		this.gx = 0;
		this.gy = 0;
		this.gz = 0;
		this.friction = RollingMarble.instance ? RollingMarble.instance.params.friction : 0.94;
		this.repulsion = 1.0;
		this.weight = 1.0;
		this.spling = 0.0;
		for (var k in this.initval) {
			this[k] = this.initval[k];
		}
		this.elem.width = this.w;
		this.w2 = this.w/2;
		this.h2 = this.h/2;
		this.elem.style.display = "block";
		this.elem.style.opacity = 1.0;
		this.reflect();
	}
	Class.prototype.recover = function() {
		this.reset();
	}
	Class.prototype.accele = function(grav) {
		this.gx += grav.x/2;
		this.gy -= grav.y/2; // 上下は逆
	}
	Class.prototype.reflect = function() {
		with (this) {
			const st = elem.style;
			if (z != 0) {
				elem.width = w*(1.0+(z>-10?z:-1)/10);
				const ww = elem.width/2;
				st.left = (x-ww)+"px";
				st.top  = (y-ww)+"px";
			} else {
				if (elem.width != w) elem.width = w;
				st.left = (x-w2)+"px";
				st.top  = (y-h2)+"px";
			}
		}
	}
	Class.prototype.action = function() {
		with (this) {
			if (isDropping) return dropping();
			nx = x+gx;
			ny = y+gy;
			nz = z+gz;
			var block = stage.getBlock(x,y,z);
			if (block.rideOn) block.rideOn(this);

			var blocks = stage.getBlocks(nx,ny,nz);
			for (var i=0; i<blocks.length; i++) {
				block = blocks[i];
				if (block.contact) {
					if (block.contact(this)) break;
				}
			}

			x = nx;
			y = ny;
			z = nz;
			gx *= friction;
			gy *= friction;
			if (z>0) gz -= 1;
			
			if (!block.isNil() && z<0) z = gz = 0;
		}
	}
	Class.prototype.drop = function(x, y) {
		if (this.z>0) return false;
		const floor = this.stage.onFloor(this);
		if (floor) {
			floor.putActor(this);
			return false;
		} else {
			this.isDropping = true;
			Sound.play("drop");
			return true;
		}
		return false;
	}
	Class.prototype.dropping = function() {
		with (this) {
			z -= 1;
			if (z <= -10) recover();
		}
	}

	function DO(r) {
		return Math.floor(r * 180 / Math.PI);
	}

	const PI = Math.PI;
	const PI2 = Math.PI*2;


	/**
	 * 玉の衝突の計算。
	 * @param a 自玉
	 * @param b 敵玉
	 */
	Class.prototype.contact = function(b) {
		const a = this;
		const zz = b.z-a.z;
		if (zz < -0.1 || 0.1 < zz) return false;

		const w = b.x - a.x;
		const h = b.y - a.y;
		const l = Math.sqrt((w*w) + (h*h));
		if (l>(a.w2+b.w2) || l==0) {
			a.ignoreImpact[b.id] = false;
			b.ignoreImpact[a.id] = false;
			return false;
		}
		if (a.ignoreImpact[b.id] || b.ignoreImpact[a.id]) {
			return false;
		}

		if (a.isMyMarble) {
			Sound.play("kin");
			if (b.touch) b.touch(a);
		}


		// 自玉と敵玉の接触角
		var rTouch = (h>=0) ? Math.acos(w/l) : (PI2-Math.acos(w/l));

		// 移動量を{gx,gy}から{gl,gr}に変換
		a.correntVector();
		b.correntVector();

		// 接触角から相手に与える移動量を計算
		var la = a.getImpact(rTouch);
		var lb = b.getImpact(rTouch);

		// 移動量を{x,y}に変換
		var vxa = w/l * la;
		var vya = h/l * la;
		var vxb = w/l * lb;
		var vyb = h/l * lb;

		// 重さ補正
		var wea = a.weight*2/(a.weight+b.weight);
		var web = b.weight*2/(a.weight+b.weight);

		var repulsion = RollingMarble.instance.params.repulsion;


		// 自玉に移動量設定
		a.gx += (vxb*web - vxa*web*b.repulsion);
		a.gy += (vyb*web - vya*web*b.repulsion);

		// 敵玉に移動量設定
		b.gx += (vxa*wea - vxb*wea*a.repulsion);
		b.gy += (vya*wea - vyb*wea*a.repulsion);

		a.gLimit();
		b.gLimit();

		// 重なり状態を回避
		a.ignoreImpact[b.id] = true;
		b.ignoreImpact[a.id] = true;
		return true;
	}

	var GMAX = 24;
	Class.prototype.gLimit = function() {
		with (this) {
			var l = Math.sqrt((gx*gx) + (gy*gy));
			if (l>GMAX) {
				gx = gx/l * GMAX;
				gy = gy/l * GMAX;
			}
		}
	}

	Class.prototype.getImpact = function(rTouch) {
		if (this.gl == 0) return 0;

		// 接触角と進行方向角の差分(小さい方)
		var r = Math.abs(this.gr-rTouch);
		if (r>Math.PI) r = (PI2-r);

		// 敵玉の受け取る移動量 
		//(マイナスにする必要が有るのは上の逆向きのせいかな？)
		return Math.cos(r) * -this.gl;
	}


	Class.prototype.correntVector = function() {
		var l = Math.sqrt((this.gx*this.gx) + (this.gy*this.gy));
		if (l == 0) {
			this.gl = 0;
			this.gr = 0;
			return;
		}
		var r = (this.gy>=0) ? Math.acos(this.gx/l) : (PI2-Math.acos(this.gx/l));
		// 良く分からないが逆向きなので180度方向転換
		r += Math.PI;
		if (r>PI2) r -= PI2;
		this.gl = l;
		this.gr = r;
	}

})(Marble);
