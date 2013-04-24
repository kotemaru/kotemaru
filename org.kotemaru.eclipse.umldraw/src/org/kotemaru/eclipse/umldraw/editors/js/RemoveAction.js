

function RemoveAction(){this.initialize.apply(this, arguments)};
(function(_class,_super){
	Lang.extend(_class, _super);
	
	_class.prototype.onMouseDown = function(ev) {
	}

	_class.prototype.onMouseMove = function(ev) {
	}
	_class.prototype.onMouseUp  = function(ev) {
		var xy = {x:ev.offsetX, y:ev.offsetY};
		var item = Canvas.getItem(xy.x, xy.y);
		item.isRemove = true;
		Canvas.delItem(item);
		Canvas.refresh();
	}
	
})(RemoveAction, Action);


//EOF
