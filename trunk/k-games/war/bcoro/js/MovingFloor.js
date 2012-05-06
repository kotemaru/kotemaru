function MovingFloor(stage, src, initval){this.initialize.apply(this, arguments)};

(function(Class) {
	Class.prototype.initialize = function(stage, src, initval) {
		var elem = Util.createImg(src,{zIndex:1});
		elem.src = src;
     	elem.width = 64;
   
		this.stage = stage;
		this.initval = initval;
		this.elem = elem;

		initval.x = to32(initval.x);
		initval.y = to32(initval.y);

		this.x = initval.x;
		this.y = initval.y;
		
		this.dir = initval.dir;
		this.gx = DIRS[this.dir].gx;
		this.gy = DIRS[this.dir].gy;

		this.count = SLEEP;
		this.w = 64;
		this.h = 64;
		
		this.x1 = initval.x;
		this.x2 = initval.x + LEN;
		this.y1 = initval.y;
		this.y2 = initval.y + LEN;

		this.onActors = {};

		this.reflect();
	}

	function to32(n) {
		return Math.floor(n/32)*32;
	}

	Class.prototype.reflect = function() {
		with (this) {
			const st = elem.style;
			st.left = x+"px";
			st.top  = y+"px";
		}
	}

	const SPEED = 3;
	const SLEEP = 30;
	const LEN = 32*4;
	const DIRS = {
		"U":{rv:"D", gx:0, gy:-SPEED},
		"D":{rv:"U", gx:0, gy:SPEED},
		"R":{rv:"L", gx:SPEED, gy:0},
		"L":{rv:"R", gx:-SPEED, gy:0},
	};

	Class.prototype.action = function() {
		with (this) {
			if (count-- > 0) return;

			x += gx;
			y += gy;
			for (var id in onActors) {
				var a = onActors[id];
				a.x += gx;
				a.y += gy;
				if (!onFloor(a)) {
					delete onActors[id];
				}
			}

			if (x1 > x || x > x2 || y1 > y || y > y2) {
				if (x1>x) x=x1;	
				if (x>x2) x=x2;	
				if (y1>y) y=y1;	
				if (y>y2) y=y2;	

				dir = DIRS[dir].rv;
				gx = DIRS[dir].gx;
				gy = DIRS[dir].gy;
				count = SLEEP;
			}
		}
	}
	

	Class.prototype.putActor = function(actor){
		this.onActors[actor.id] = actor;
	}
	Class.prototype.onFloor = function(actor){
		const ax = actor.x;
		const ay = actor.y;
		with (this) {
			return (x <= ax && ax <= x + 64 && y <= ay && ay <= y + 64);
		}
	}
})(MovingFloor);
