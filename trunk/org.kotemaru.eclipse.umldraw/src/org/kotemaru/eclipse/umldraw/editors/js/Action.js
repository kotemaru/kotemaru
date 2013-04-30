

function Action(){this.initialize.apply(this, arguments)};
(function(_class){

	var $menu;
	var dragItem;
	var ox;
	var oy;
	var rx;
	var ry;
	
	_class.prototype.initialize = function(targetClass) {
		this.targetClass = targetClass;
	}
	
	_class.dragStart = function(item, evx,evy) {
		dragItem = item;
		ox = evx;
		oy = evy;
		rx = evx - item.x();
		ry = evy - item.y();
		dragItem.dragStart(evx-rx,evy-ry);
	}
	
	_class.dragMove = function(evx,evy) {
		if (dragItem == null) return;
		dragItem.dragMove(evx-rx,evy-ry);
		Canvas.refresh();
	}
	_class.dragEnd = function(evx,evy) {
		if (dragItem == null) return;
		dragItem.dragEnd(evx-rx,evy-ry);
		dragItem = null;
		Canvas.refresh();
		EditBuffer.backup();
	}
	
	function selectAndDrag(ex,ey) {
		var elem = Canvas.getHandle(ex,ey);
		if (elem == null) {
			elem = Canvas.getSelectGroup(ex,ey);
		}
		if (elem == null) {
			elem = Canvas.getItem(ex,ey);
			if (elem) Canvas.select(elem);
		}
		if (elem && elem.isDraggable) {
			_class.dragStart(elem, ex,ey);
		} else if (elem == null) {
			AreaSelect.dragStart(ex,ey);
		}
		Canvas.refresh();
	}
	
	//-----------------------------------------------------------
	_class.prototype.selectMe = function() {
		if (this.targetClass) {
			Canvas.cursor("new");
		} else {
			Canvas.cursor("");
		}
	}

	_class.prototype.onMouseDown = function(ev) {
		if (this.targetClass) {
			var item = new this.targetClass({x:ev.offsetX, y:ev.offsetY});
			Canvas.addItem(item);
			Canvas.refresh();
			EditBuffer.backup();
		} else {
			selectAndDrag(ev.offsetX, ev.offsetY);
		}
	}

	_class.prototype.onMouseMove = function(ev) {
		if (dragItem) {
			_class.dragMove(ev.offsetX, ev.offsetY);
		} else {
			AreaSelect.dragMove(ev.offsetX, ev.offsetY)
		}
	}
	_class.prototype.onMouseUp  = function(ev) {
		if (dragItem) {
			_class.dragEnd(ev.offsetX, ev.offsetY);
		} else {
			AreaSelect.dragEnd(ev.offsetX, ev.offsetY)
		}
		Actions.resetAction();
	}
	
	_class.prototype.openMenu = function(ev) {
		if ($menu) $menu.hide();
		var item = Canvas.getSelectGroup(ev.offsetX, ev.offsetY);
		if (item == null) {
			item = Canvas.getItem(ev.offsetX, ev.offsetY);
		}

		if (item == null) {
			//Canvas.select(null);
			//Canvas.refresh();
			var opts = {event:ev, item:Canvas};
			PopupMenu.open("#canvasMenu", opts);
			
		} else if (item.getMenu) {
			Canvas.select(item);
			Canvas.refresh();
			var name = item.getMenu();
			var opts = {event:ev, item:item};
			PopupMenu.open(name, opts);
		}
	}
	
	_class.prototype.onDblClick  = function(ev) {
		var item = Canvas.getItem(ev.offsetX, ev.offsetY);
		if (item == null) {
			// TODO: canvas menu.
			var data = {svg: Canvas.toSVG()};
			Dialog.open("#debugDialog", data);
			//var ifr = $("#iframeSvg")[0];
			//ifr.contentDocument.body.innerHTML = data.svg;
			var data = Store.save(Canvas.getItems());
			$("#saveText").val(JSON.stringify(data,null, "\t"));
			Store.load(data);
	
		} else if (item.getDialog) {
			Canvas.select(item);
			Canvas.refresh();
			Dialog.open(item.getDialog(), item);
		}
		
	}
	
})(Action);


//EOF
