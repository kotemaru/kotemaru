/**
@author kotemaru@kotemaru.org
*/

if (!enchant.gocha) enchant.gocha = {};

enchant.gocha.Chip = org.kotemaru.Class(null, function(_class, _super){
	var Util = org.kotemaru.Util;
	_class.prototype.initialize = function(src, x,y,w,h) {
		x = x?x:0;
		y = y?y:0;
		Util.copy(this,{img:null,src:src,x:x,y:y,w:w,h:h});
	};
	
	_class.prototype.getImage = function() {
		with (this) {
			if (img != null) return img;
			if (base) return base.getImage();
			throw new Error("Not image source "+src);
		}
	}

	_class.prototype.draw = function(ctx, dx,dy, dw,dh) {
		with (this) {
			if (img == null) img = getImage();
			dw = dw?dw:w;
			dh = dh?dh:h;
			ctx.drawImage(img, x,y,w,h, dx,dy,dw,dh);
		}
	}
	
	_class.prototype.sub = function(sx,sy,sw,sh) {
		with (this) {
			var chip = new _class(src,x+sx,y+sy,sw,sh);
			chip.base = this;
			chip.img = this.img;
			return chip;
		}
	}

	_class.cache = {};
	_class.create = function(src, x,y, w,h) {
		var name = src+","+x+","+y+","+w+","+h;
		if (_class.cache[name]) {
			var chip = _class.cache[name];
			if (chip.src != src || chip.w != w || chip.h != h) {
				throw new Error("Duplacate chip definition");
			}
		} else {
			_class.cache[name] = new _class(src,x,y,w,h);
		}
		return _class.cache[name];
	}
	_class.preload = function(game) {
		var cache = _class.cache;
		for (var name in cache) {
			game.preload(cache[name].src);
		}
		game.addEventListener("load", function(){
			_class.onload(game);
		});
	}
	
	_class.onload = function(game) {
		var cache = _class.cache;
		for (var name in cache) {
			var chip = cache[name];
			chip.image = game.assets[chip.src];
			chip.img = chip.image._element;
			chip.w = chip.w?chip.w:chip.img.width;
			chip.h = chip.h?chip.h:chip.img.height;
		}
	}
	
});
enchant.gocha.GochaChip = org.kotemaru.Class(null, function(_class, _super){
	_class.prototype.initialize = function(base) {
		//_super.prototype.initialize.call(this, chip.w, chip.h);
		this.baseChip = base;
		this.w = base.w/6;
		this.h = base.h/5;

		function subChip(bx,by) {
			return base.sub(this.w*bx,this.h*by,this.w,this.h);
		}
		this.chips = {
			D:[subChip(0,0),subChip(1,0),subChip(2,0)],
			L:[subChip(0,1),subChip(1,1),subChip(2,1)],
			R:[subChip(0,2),subChip(1,2),subChip(2,2)],
			U:[subChip(0,3),subChip(1,3),subChip(2,3)],

			aD:[subChip(3,0),subChip(4,0),subChip(5,0)],
			aL:[subChip(3,1),subChip(4,1),subChip(5,1)],
			aR:[subChip(3,2),subChip(4,2),subChip(5,2)],
			aU:[subChip(3,3),subChip(4,3),subChip(5,3)],
			
			r:[subChip(0,4),subChip(1,4)],
			y:[subChip(2,4),subChip(3,4)],
			d:[subChip(4,4),subChip(5,4)],
		};
	}
	
	_class.prototype.draw = function(ctx, dir, frame) {
		var motion = this.chips[dir];
		motion[frame%motion.length].draw(ctx, 0,0);
	};
});

enchant.gocha.VXChip = org.kotemaru.Class(null, function(_class, _super){
	const BLEED = enchant.gocha.Chip.create("img/bleed.png");
	const ATTACK = enchant.gocha.Chip.create("img/attack.png");

	_class.prototype.initialize = function(base, attack) {
		//_super.prototype.initialize.call(this, chip.w, chip.h);
		attack = (attack?attack:ATTACK);
		
		this.baseChip = base;
		this.attackChip = attack;
		var w = base.w/3;
		var h = base.h/4;
		
		function subChip(bx,by) {
			return base.sub(w*bx,h*by,w,h);
		}
		this.chips = {
			D:[subChip(0,0),subChip(1,0),subChip(2,0)],
			L:[subChip(0,1),subChip(1,1),subChip(2,1)],
			R:[subChip(0,2),subChip(1,2),subChip(2,2)],
			U:[subChip(0,3),subChip(1,3),subChip(2,3)],

			aD:[subChip(0,0),subChip(1,0),subChip(2,0)],
			aL:[subChip(0,1),subChip(1,1),subChip(2,1)],
			aR:[subChip(0,2),subChip(1,2),subChip(2,2)],
			aU:[subChip(0,3),subChip(1,3),subChip(2,3)],

			r:[subChip(1,0),subChip(1,0)],
			y:[subChip(1,0),subChip(1,0)],
			d:[subChip(1,2),subChip(1,2)],
		};
		
		function subAttack(bx,by) {
			return attack.sub(w*bx,h*by,w,h);
		}
		this.effects = {
			aD:[subAttack(0,0),subAttack(1,0),subAttack(2,0)],
			aL:[subAttack(0,1),subAttack(1,1),subAttack(2,1)],
			aR:[subAttack(0,2),subAttack(1,2),subAttack(2,2)],
			aU:[subAttack(0,3),subAttack(1,3),subAttack(2,3)],
			 y:[BLEED.sub(0,0,32,32),BLEED.sub(32,0,32,32)]
		};
		
		
		this.chips.r[0].draw = drawRecover0;
		this.chips.r[1].draw = drawRecover1;
		
		this.chips.d[0].draw = drawDead0;
		this.chips.d[1].draw = drawDead1;
		this.w = w;
		this.h = h;
	}
	
	_class.prototype.draw = function(ctx, dir, frame) {
		var motion = this.chips[dir];
		var n = frame%motion.length;
		var effect =  this.effects[dir]? this.effects[dir][n]:null;

		if (effect && dir=="aU") {
			effect.draw(ctx, 0,0,this.w,this.h);
		}
		motion[n].draw(ctx, 0,0);
		
		if (effect && dir!="aU") {
			effect.draw(ctx, 0,0,this.w,this.h);
		}
	};
	
	function drawRecover0(ctx, dx,dy) {
		with (this) { // this==chip
			ctx.drawImage(img, x,y,w,h, dx,dy+(h*0.3),w,h);
		}
	}
	function drawRecover1(ctx, dx,dy) {
		with (this) { // this==chip
			ctx.drawImage(img, x,y,w,h, dx,dy+(h*0.25),w,h);
		}
	}
	
	function drawDead0(ctx, dx,dy) {
		with (this) { // this==chip
			ctx.rotate( 45 * Math.PI / 180 );
			ctx.drawImage(img, x,y,w,h, dx+(w*0.3),dy-w/2,w*1.0 ,h);
			ctx.rotate( -45 * Math.PI / 180 );
		}
	}
	function drawDead1(ctx, dx,dy) {
		with (this) { // this==chip
			ctx.rotate( 90 * Math.PI / 180 );
			ctx.drawImage(img, x,y,w,h, dx+(w*0.3),dy-w,w*1.0 ,h);
			ctx.rotate( -90 * Math.PI / 180 );
		}
	}
	
});
