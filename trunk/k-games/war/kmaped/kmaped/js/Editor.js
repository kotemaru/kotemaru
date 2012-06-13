
function Editor(){this.initialize.apply(this, arguments)};
(function(Class) {
	Class.prototype.initialize = function() {
		const self = this;
		
		var chipSet = new ChipSet(16,16);
		
		this.chipSet = chipSet;
		this.workSpace = new WorkSpace(this);
		this.workSpace.addLayer();
		this.workSpace.grid(false);
		
		var chipSpace = new ChipSpace(this);
		chipSpace.addLayer("chipSet");
		chipSpace.grid(false);
		chipSet.onload = function() {
			chipSpace.onloadChipSet();
			self.workSpace.flush();
		};
		this.chipSpace = chipSpace;

		this.clipBuffer = new ClipBuffer("_editor", this.workSpace);
		this.clipBuffer.resize(1,1).tiles[0][0] = 0;
		// TODO:無駄、整理が必要
		this.chipSpace.clipBuffer = this.clipBuffer;
		this.workSpace.clipBuffer = new MagicBuffer("_slide", this.workSpace);
		this.workSpace.elem.appendChild(this.workSpace.clipBuffer.elem);
		
		
		this.editMode = "pen";
		this.editPenMode = "free";
		this.editMagicMode = "copy";
		this.history = [];
		this.historyPos = -1;

		this.server = new LocalServer();
		this.edited();
	}
	
	//-------------------------------------------------------------------
	// Editor ctrol
	
	Class.prototype.sizeFlush = function() {
		this.chipSpace.sizeFlush();
		this.workSpace.sizeFlush();
	}

	Class.prototype.setChipSet = function(src) {
		var chipSet = new ChipSet(16, 16, src);
		this.chipSet = chipSet;
		this.workSpace.chipSet = chipSet;
		this.workSpace.flash();
		this.edited();
	}
	Class.prototype.setMode = function(mode) {
		this.editMode = mode;
		var sel = this.workSpace.selector;
		sel.setMode(mode=="magic"?"slide":"normal");
	}
	Class.prototype.setPenMode = function(mode) {
		this.editPenMode = mode;
	}
	Class.prototype.setMagicMode = function(mode) {
		this.editMagicMode = mode;
	}

	//---------------------------------------------------------
	// History control
	
	Class.prototype.backup = function() {
		if (this.historyPos < this.history.length-1) {
			this.history.length = this.historyPos+1;
		}
		var json = this.workSpace.saveJSON();
		//this.source(json)
		this.history.push(json);

		if (this.history.length>50) {
			this.history.shift();
		}
		this.historyPos = this.history.length-1;
	}
	Class.prototype.edited = function() {
		this.backup();
	}
	
	Class.prototype.undo = function() {
		if (this.historyPos > 0) {
			var json = this.history[--this.historyPos];
			this.workSpace.loadJSON(json);
			this.workSpace.flush();
		} else {
			alert("No history");
		}
	}
	Class.prototype.redo = function() {
		if (this.historyPos < this.history.length-1) {
			var json = this.history[++this.historyPos];
			this.workSpace.loadJSON(json);
			this.workSpace.flush();
		} else {
			alert("No history");
		}
	}
	

	//---------------------------------------------------------------
	// 編集操作
	Class.prototype.addLayer = function() {
		this.workSpace.addLayer();
		this.edited();
	}
	Class.prototype.resize = function(w,h) {
		this.workSpace.resize(w,h);
		this.edited();
	}

	Class.prototype.selectionStart = function(sel, workSpace) {
		if (this.workSpace != workSpace) return;
		sel.anime(false);
		var emode = this.editMode;
		if (emode == "pen" || emode == "erase") {
			EditAction[emode][this.editPenMode].start(sel, workSpace, this);
		} else if (emode == "magic") {
			EditAction.magic.start(sel, workSpace, this);
		} 
	}
	Class.prototype.selectionDrag = function(sel, workSpace) {
		if (this.workSpace != workSpace) return;
		var emode = this.editMode;
		if (emode == "pen" || emode == "erase") {
			EditAction[emode][this.editPenMode].drag(sel, workSpace, this);
		} else if (emode == "magic") {
			EditAction.magic.drag(sel, workSpace, this);
		} 
	}
	Class.prototype.selectionEnd = function(sel, workSpace) {
		if (this.workSpace != workSpace) return;
		var emode = this.editMode;
		
		if (emode == "pen" || emode == "erase") {
			EditAction[emode][this.editPenMode].end(sel, workSpace, this);
		} else if (emode == "magic") {
			EditAction.magic.end(sel, workSpace, this);
		}
		workSpace.flush();
	}

		
	//------------------------------------------------
	// File IO
	Class.prototype.save = function(name) {
		this.workSpace.name = name;
		var json = this.workSpace.saveJSON(" ");
		this.server.save(this.workSpace.name, json);
	}
	Class.prototype.load = function(name) {
		var json = this.server.load(name);
		this.apply(json);
		this.workSpace.name = name;
	}
	Class.prototype.apply = function(json) {
		var data = JSON.parse(json);
		
		this.chipSet.load(data.chipSet);
		this.workSpace.loadJSON(json);
		this.workSpace.flush();
		this.edited();
	}
	
})(Editor);
//EOF.
