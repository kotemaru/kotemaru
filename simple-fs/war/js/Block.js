function Block(stage, src, initval){this.initialize.apply(this, arguments)};
(function(Class) {

	Class.prototype.initialize = function(stage, src, initval) {
		this.x = initval.x*32;
		this.y = initval.y*32;

		this.elem = Util.createImg(src,{
			left:this.x, top:this.y}
		);
		if (stage.elem) {
			stage.elem.appendChild(this.elem);
		}        
		this.stage = stage;
		this.initval = initval;
	}
	Class.prototype.isNil = function() {return false;}
	//Class.prototype.isWall = function() {return false;}

	//Class.prototype.rideOn = function(actor) {}
	//Class.prototype.contact = function(actor) {}

	Class.FACTORY = {
		'none':function(stage,x,y) {
			if (!BLOCK_NONE) BLOCK_NONE = new  BlockNone(stage,"img/transparent.png",{x:0,y:0});
			return BLOCK_NONE;
		},
		'wall':function(stage,x,y) {
			return new BlockWall(stage,"img/wall.png",{x:x,y:y});
		},
		'nil':function(stage,x,y) {
			return new BlockNil(stage,"img/black.png",{x:x,y:y});
		},
		'goal':function(stage,x,y) {
			return new BlockGoal(stage,"img/goal.png",{x:x,y:y});
		},
		'slow':function(stage,x,y) {
			return new BlockSlow(stage,"img/slow.png",{x:x,y:y});
		},
		
		'arrow-rD':function(stage,x,y) {
			return new BlockArrow(stage,"img/arrow-rD.png",{x:x,y:y, gx:0, gy:2.0});
		},
		'arrow-rU':function(stage,x,y) {
			return new BlockArrow(stage,"img/arrow-rU.png",{x:x,y:y, gx:0, gy:-2.0});
		},
		'arrow-rL':function(stage,x,y) {
			return new BlockArrow(stage,"img/arrow-rL.png",{x:x,y:y, gx:-2.0, gy:0});
		},
		'arrow-rR':function(stage,x,y) {
			return new BlockArrow(stage,"img/arrow-rR.png",{x:x,y:y, gx:2.0, gy:0});
		},
		
		'arrow-yD':function(stage,x,y) {
			return new BlockArrow(stage, "img/arrow-yD.png", {x:x,y:y, gx:0, gy:0.5});
		},
		'arrow-yU':function(stage,x,y) {
			return new BlockArrow(stage, "img/arrow-yU.png", {x:x,y:y, gx:0, gy:-0.5});
		},
		'arrow-yL':function(stage,x,y) {
			return new BlockArrow(stage, "img/arrow-yL.png", {x:x,y:y, gx:-0.5, gy:0});
		},
		'arrow-yR':function(stage,x,y) {
			return new BlockArrow(stage, "img/arrow-yR.png", {x:x,y:y, gx:0.5, gy:0});
		},
		'highland':function(stage,x,y) {
			return new BlockHighland(stage, "img/highland.png", {x:x,y:y});
		},
	};
	Class.CHARS = {
		"none": "o",
		"nil":  ".",
		"wall": "w",
		"goal": "g",
		"slow": "s",

		"arrow-rD": "D",
		"arrow-rU": "U",
		"arrow-rR": "R",
		"arrow-rL": "L",
		
		"arrow-yD": "d",
		"arrow-yU": "u",
		"arrow-yR": "r",
		"arrow-yL": "l",
		
		"highland": "h",
	};
	// for editor
	Class.COMMENT = {
		"none": "通常の床面",
		"nil":  "無(落ちる)",
		"wall": "壁(侵入不可)",
		"goal": "ゴール",
		"slow": "低速エリア",

		"arrow-rD": "↓↓",
		"arrow-rU": "↑↑",
		"arrow-rR": "←←",
		"arrow-rL": "→→",
		
		"arrow-yD": "↓",
		"arrow-yU": "↑",
		"arrow-yR": "→",
		"arrow-yL": "←",
		
		"highland": "台地",
	};

	
	
	Class.RCHARS = {};
	for (var name in Class.CHARS) {
		Class.RCHARS[Class.CHARS[name]] = name;
	}

	var BLOCK_NONE;

	Class.create = function(stage, name,  x, y) {
		var f = Class.FACTORY[name];
		if (f == null) f = Class.FACTORY["none"];
		return f(stage,  x, y);
	}
	

})(Block);
