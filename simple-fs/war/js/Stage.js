function Stage(id) {
	this.elem = document.getElementById(id);
	this.frame = this.elem.parentElement;
	this.frameHight = this.frame.clientHeight;
	this.actors = null;
	this.entities = null;
};
(function(Class) {
	Class.W = 600;
	Class.H = 600;
	const BLOCK_NIL = new BlockNil();

	Class.prototype.makeStage = function(data) {

		var lines = data.map;
		this.timelimit = data.time*1000;
		this.timelimit2 = this.timelimit*0.3;
		this.timelimit3 = this.timelimit*0.1;

		var w = data.w;
		var h = data.h;
		Class.W = w*32;
		Class.H = h*32;

		this.actors = [];
		this.entities = [];
		this.elem.innerHTML = "";
		this.elem.style.position = "relative";
		//this.elem.style.backgroundColor = "white";
		this.elem.style.width  = Stage.W+"px";
		this.elem.style.height = Stage.H+"px";

		this.map = [];
		this.idCount = 0;

		for (var y=0; y<h; y++) {
			this.map.push([]);
			for (var x=0; x<w; x++) {
				var ch = lines[y].charAt(x);

				var name = Block.RCHARS[ch];
				var block = Block.create(this, name, x,y);
				block.id = this.idCount++;
				this.map[y].push(block);
			}
		}
		for (var y=0; y<h; y++) {
			for (var x=0; x<w; x++) {
				var block = this.map[y][x];
				if (block.corrent) block.corrent();
			}
		}
		
		for (var i=0; i<data.actors.length; i++) {
			var a = data.actors[i];
			var actor = Actor.create(this, a.name, a.x,a.y);
			this.addActor(actor);
			if (actor.isMyMarble) this.marble = actor;
		}

		this.bonusActor = new Bonus(this,"");
		this.addActor(this.bonusActor);
	}


	Class.prototype.addActor = function(actor) {
		this.actors.push(actor);
		if (actor.contact) {
			this.entities.push(actor);
		}
		this.elem.appendChild(actor.elem);
	}

	Class.prototype.getBlockRaw = function(x,y) {
		if (x<0 || x>=this.map[0].length) return BLOCK_NIL;
		if (y<0 || y>=this.map.length) return BLOCK_NIL;

		return this.map[y][x];
	}
	Class.prototype.getBlock = function(x,y) {
		if (x<0 || x>Class.W) return BLOCK_NIL;
		if (y<0 || y>Class.H) return BLOCK_NIL;
		return this.map[Math.floor(y/32)][Math.floor(x/32)];
	}

	const BLOCKS=["","","","","","","","",""];
	Class.prototype.getBlocks = function(x,y) {
		var x1 = x-15.5;
		var x2 = x+15.5;
		var x3 = x-11.5;
		var x4 = x+11.5;

		var y1 = y-15.5;
		var y2 = y+15.5;
		var y3 = y-11.5;
		var y4 = y+11.5;

		with (this) {
		BLOCKS[0] =	getBlock(x1,y);
		BLOCKS[1] =	getBlock(x2,y);
		BLOCKS[2] =	getBlock(x,y1);
		BLOCKS[3] =	getBlock(x,y2);
		BLOCKS[4] =	getBlock(x3,y3);
		BLOCKS[5] =	getBlock(x3,y4);
		BLOCKS[6] =	getBlock(x4,y3);
		BLOCKS[7] =	getBlock(x4,y4);
		BLOCKS[8] =	getBlock(x,y);
		}
		return BLOCKS;
	}              
                       
	Class.prototype.scroll = function(mx,my) {
		var y = my-this.frame.scrollTop;
		if (y < 200) {
			this.frame.scrollTop = my - 200;
		} else if (y > this.frameHight-200) {
			this.frame.scrollTop = my - (this.frameHight-200);
		}
	}

})(Stage);
