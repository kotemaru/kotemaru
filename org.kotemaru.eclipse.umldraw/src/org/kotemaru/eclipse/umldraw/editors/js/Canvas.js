

function Canvas(){this.initialize.apply(this, arguments)};
(function(_class){
	var canvas;
	var context2d;
	var items;
	var selectItem;
	var selectGroup = new SelectGroup();

	_class.init = function(elem) {
		canvas = elem;
		context2d = canvas.getContext('2d');
		_class.reset();
	}
	_class.reset = function() {
		items = new Items();
		selectItem = null;
		selectGroup.clear();
	}
	_class.addItem = function(item) {return items.addItem(item);}
	_class.delItem = function(item) {return items.delItem(item);}
	_class.getItem = function(ex,ey, ignore) {return items.getItem(ex,ey, ignore);}
	_class.getItems = function() {
		return items;
	}
	
	_class.refresh = function() {
		context2d.clearRect(0,0,1000,1000);
		var drawer = new Drawer(context2d);
		items.draw(drawer);
		if (selectItem) selectItem.drawHandle(context2d);
		AreaSelect.drawOutBounds(context2d);
		drawer.close();
	}
	
	_class.toSVG = function() {
		var drawer = new DrawerSVG(context2d);
		items.draw(drawer);
		drawer.close();
		return drawer.getSVG();
	}
	
	_class.select = function(item) {
		_class.clearSelect();
		selectItem = item;
	}
	_class.clearSelect = function() {
		if (selectItem && selectItem.clear) selectItem.clear();
		selectItem = null;
	}
	//_class.addSelect = function(item) {selectGroup.addItem(item);}
	//_class.delSelect = function(item) {selectGroup.getItems().delItem(item);}
	_class.getSelectGroup = function() {
		return selectGroup;
	}
	
	_class.getHandle = function(ex,ey) {
		var handle = null;
		if (selectItem == selectGroup) {
			if (selectGroup.onPoint(ex, ey)) return selectGroup
		}
		if (selectItem) handle = selectItem.getHandle(ex, ey);
		return handle;
	}
	
	_class.cursor = function(type) {
		canvas.style.cursor = "url(img/cursor_"+type+".png),default";
	}
	
	var copyBuff;
	_class.doMenuItem = function($menuItem,xx,yy) {
		var cmd = $menuItem.attr("data-value");
		if (cmd == "copy") {
			copyBuff = Store.copy(selectGroup.getItems());
		} else if (cmd == "paste") {
			Store.paste(copyBuff, xx,yy);
		} else if (cmd == "properties") {
			Dialog.open(this.getDialog(), this);
		}
	}

	//--------------------------------------------------------------------
	// Canvas ハンドラ設定。
	function onMouseDown(ev) {
		var action = Actions.getAction();
		if(event.which == 1) {
			action.onMouseDown(ev);
		} else if(event.which == 3) {
			action.openMenu(ev);
		}
	}
	function onMouseMove(ev) {
		var action = Actions.getAction();
		if(event.which == 1) {
			action.onMouseMove(ev);
		}
	}
	function onMouseUp(ev) {
		var action = Actions.getAction();
		if(event.which == 1) {
			action.onMouseUp(ev);
		}
	}
	function onDblClick(ev) {
		var action = Actions.getAction();
		if(event.which == 1) {
			action.onDblClick(ev);
		}
	}
	
	$(function(){
		var $can = $("canvas");
		_class.init($can[0]);
		
		$can.bind("mousedown",onMouseDown);
		$can.bind("mousemove",onMouseMove);
		$can.bind("mouseup",onMouseUp);
		$can.bind("dblclick",onDblClick);
		
		$(document).bind("contextmenu",function(){return false;});
	})
	
	
})(Canvas);


//EOF
