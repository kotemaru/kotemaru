

function Canvas(){this.initialize.apply(this, arguments)};
(function(_class){
	
	_class.properties = {
		width: 1123,
		height: 794,
		size : "A4",
		dpi  : "96",
		dir  : "landscape",
		autoCrop : true
	};

	
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
	_class.delItem = function(item) {
		item.remove();
		return items.delItem(item);
	}
	_class.getItem = function(ex,ey, ignore) {
		return items.getItem(ex,ey, ignore);
	}
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
		if (selectItem != item) _class.clearSelect();
		selectItem = item;
	}
	_class.clearSelect = function() {
		if (selectItem && selectItem.clear) selectItem.clear();
		selectItem = null;
	}
	//_class.addSelect = function(item) {selectGroup.addItem(item);}
	//_class.delSelect = function(item) {selectGroup.getItems().delItem(item);}
	_class.getSelectGroup = function(ex,ey) {
		if (ex) {
			if (selectItem == selectGroup) {
				if (selectGroup.onPoint(ex, ey)) return selectGroup;
			}
			return null;
		}
		return selectGroup;
	}
	_class.hasSelectGroup = function(ex,ey) {
		return selectItem == selectGroup
			&& selectGroup.getItems().size()>0;
	}
	
	_class.getHandle = function(ex,ey) {
		var handle = null;
		if (selectItem) handle = selectItem.getHandle(ex, ey);
		return handle;
	}
	
	_class.cursor = function(type) {
		canvas.style.cursor = "url(img/cursor_"+type+".cur),default";
	}
	
	_class.getDialog = function() {
		return "#canvasDialog";
	}
	
	_class.width = function() {
		return _class.properties.width;
	}
	_class.height = function() {
		return _class.properties.height;
	}
	_class.setProperties = function(attrs) {
		if (attrs) _class.properties = attrs;
		$(canvas).attr("width", _class.properties.width);
		$(canvas).attr("height", _class.properties.height);
	}
	_class.getProperties = function(attrs) {
		return _class.properties;
	}
	$("#canvasDialog").live("saved",function(){
		_class.setProperties();
	});
	

	//--------------------------------------------------------------------
	// Canvas ハンドラ設定。
	function forIe9Event(ev) {
		if (ev.which == undefined) {
			ev.which = ev.buttons;
		}
		// for FF
		if (ev.offsetX == undefined) {
			ev.offsetX = ev.originalEvent.layerX;
			ev.offsetY = ev.originalEvent.layerY;
		}
		return ev;
	}
	function onMouseDown(ev) {
		ev = forIe9Event(ev);
		var action = Actions.getAction();
		if(ev.which == 1) {
			action.onMouseDown(ev);
		} else if(ev.which == 3) {
			action.openMenu(ev);
		}
	}
	function onMouseMove(ev) {
		ev = forIe9Event(ev);
		var action = Actions.getAction();
		if(ev.which == 1) {
			action.onMouseMove(ev);
		}
	}
	function onMouseUp(ev) {
		ev = forIe9Event(ev);
		var action = Actions.getAction();
		if(ev.which == 1) {
			action.onMouseUp(ev);
		}
	}
	function onDblClick(ev) {
		ev = forIe9Event(ev);
		var action = Actions.getAction();
		if(ev.which == 1) {
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
		
		// Defaule menu disabled.
		$(document).bind("contextmenu",function(){return false;});
	})
	
	
})(Canvas);


//EOF
