

function RemoveAction(){this.initialize.apply(this, arguments)};
(function(_class,_super){
	Lang.extend(_class, _super);

	_class.prototype.selectMe = function() {
		Canvas.cursor("remove");
	}
	
	_class.prototype.onMouseDown = function(ev) {
	}

	_class.prototype.onMouseMove = function(ev) {
	}
	_class.prototype.onMouseUp  = function(ev) {
		var xy = {x:ev.offsetX, y:ev.offsetY};
		var item = Canvas.getItem(xy.x, xy.y);
		if (item) {
			item.remove();
			Canvas.delItem(item);
			Canvas.refresh();
			EditBuffer.backup();
			Actions.resetAction();
		}
	}
	
})(RemoveAction, Action);


//EOF
