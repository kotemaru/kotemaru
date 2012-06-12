
function EditAction(){editor.initialize.apply(this, arguments)};
(function(Class) {
	
	Class.pen = {};
	Class.erase = {};
	Class.selection = {};
	Class.magic = {};

	Class.pen.free = {
		start: function(sel, workSpace, editor) {
			var layer = workSpace.getTopLayer();
			var clip = editor.clipBuffer;
			layer.paste(clip, sel.x, sel.y);
			sel.canselDrag();
			sel.resize(clip.width, clip.height);
			layer.flush();
		},
		drag: function(sel, workSpace, editor) {
			Class.pen.free.start(sel, workSpace, editor)
		},
		end: function(sel, workSpace, editor) {
			sel.fadeOut(500);
			editor.edited();
		}
	};
	
	Class.pen.rect = {
		start: function(sel, workSpace, editor) {/*nop*/},
		drag: function(sel, workSpace, editor) {/*nop*/},
		end: function(sel, workSpace, editor) {
			var layer = workSpace.getTopLayer();
			var clip = editor.clipBuffer;
			fill(clip, sel, layer);
			sel.fadeOut(500);
			editor.edited();
		}
	};
	
	Class.erase.free = {
		start: function(sel, workSpace, editor) {
			var layer = workSpace.getTopLayer();
			var clip = editor.clipBuffer;
			layer.remove(sel.x, sel.y, 1, 1);
			sel.canselDrag();
			layer.flush();
		},
		drag: function(sel, workSpace, editor) {
			Class.erase.free.start(sel, workSpace, editor)
		},
		end: function(sel, workSpace, editor) {
			sel.fadeOut(500);
			editor.edited();
		}
	};
	
	Class.erase.rect = {
		start: function(sel, workSpace, editor) {/*nop*/},
		drag: function(sel, workSpace, editor) {/*nop*/},
		end: function(sel, workSpace, editor) {
			var layer = workSpace.getTopLayer();
			layer.remove(sel.x,sel.y,sel.width,sel.height);
			sel.fadeOut(500);
			editor.edited();
		}
	};
	
	
	Class.magic = {
		start: function(sel, workSpace, editor) {/*nop*/},
		drag: function(sel, workSpace, editor) {
			if (sel.isSlide) {
				var clip = workSpace.clipBuffer;
				sel.anime(true);
				clip.offset(sel).resize(sel).show(true).flush();
			}
		},
		end: function(sel, workSpace, editor) {
			var layer = workSpace.getTopLayer();
			var clip = workSpace.clipBuffer;
			if (!sel.isSlide) {
				clip.offset(sel).resize(sel);
				if (editor.editMagicMode == "copy") {
					layer.copy(clip, sel.x,sel.y,sel.width,sel.height);
				} else {
					editor.edited();
					layer.cut(clip, sel.x,sel.y,sel.width,sel.height);
				}
				clip.show(true).flush();
				sel.anime(true);
			} else if (sel.isSlide && !sel.isDragged) {
				layer.paste(clip, sel.x,sel.y,sel.width,sel.height);
				layer.flush();
				editor.edited();
				clip.show(false);
				sel.anime(false);
			}
		}
	};
		

	function fill(clip, sel, layer) {
		for (var y=0; y<sel.height; y+=clip.height) {
			for (var x=0; x<sel.width; x+=clip.width) {
				var dw = Math.min(clip.width, (sel.width-x));
				var dh = Math.min(clip.height, (sel.height-y));
				layer.paste(clip, sel.x+x, sel.y+y, dw,dh);
			}
		}
	}
	
	//---------------------------------------------------------
	// cut&paste
	Class.copy = function(editor) {
		var sel = editor.workSpace.selector;
		var clip = editor.clipBuffer;
		var layer = editor.workSpace.getTopLayer();
		layer.copy(clip, sel.x,sel.y,sel.width,sel.height);
		editor.chipSpace.selector.show(false);
		clip.flush();
	}
	Class.cut = function(editor) {
		var sel = editor.workSpace.selector;
		var clip = editor.clipBuffer;
		var layer = editor.workSpace.getTopLayer();
		layer.cut(clip, sel.x,sel.y,sel.width,sel.height);
		editor.chipSpace.selector.show(false);
		layer.flush();
		clip.flush();
		editor.edited();
	}
	Class.remove = function(editor) {
		var sel = editor.workSpace.selector;
		var clip = editor.clipBuffer;
		var layer = editor.workSpace.getTopLayer();
		layer.remove(sel.x,sel.y,sel.width,sel.height);
		layer.flush();
		clip.flush();
	}
	Class.paste = function(editor) {
		var sel = editor.workSpace.selector;
		var clip = editor.clipBuffer;
		sel.resize(clip).flush();
		var layer = editor.workSpace.getTopLayer();
		fill(clip, sel, layer);
		layer.flush();
		editor.edited();
	}
	
})(EditAction);
//EOF.
