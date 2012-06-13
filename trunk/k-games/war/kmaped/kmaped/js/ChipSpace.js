
function ChipSpace(){this.initialize.apply(this, arguments)};
(function(Class,Super) {
	Util.extend(Class,Super);
	
	Class.prototype.initialize = function(editor) {
		Super.prototype.initialize.apply(this, arguments);
	}

	Class.prototype.onloadChipSet = function() {
		const self = this;
		var chipSet = this.chipSet;

		self.resize(chipSet.width, chipSet.height);
		var tiles = self.getTopLayer().tiles;
		var no = 0;
		for (var y=0; y<tiles.length; y++) {
			for (var x=0; x<tiles[y].length; x++) {
				tiles[y][x] = no++;
			}
		}
		
		self.flush();
	};
	Class.prototype.selectionEnd = function(sel) {
		var layer = this.getTopLayer();
		var clip = this.clipBuffer;
		layer.copy(clip, sel.x,sel.y,sel.width,sel.height);
		clip.flush();

		UI.clickPen(); // TODO: ここで呼んでいいのか？
	}

})(ChipSpace, WorkSpace);

//EOF.