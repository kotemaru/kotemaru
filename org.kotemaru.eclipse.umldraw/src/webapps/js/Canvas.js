

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
		_class.setProperties(_class.properties);
		_class.reset();
	}
	_class.reset = function() {
		items = new Items();
		selectItem = null;
		selectGroup.clear();
	}
	_class.addItem = function(item) {
		if (item.isSelectGroup) {
			Eclipse.log("ignore Canvas.add(SelectGroup)");
			return;
		}
		return items.addItem(item);
	}
	_class.delItem = function(item) {
		item.remove();
		return items.delItem(item);
	}
	_class.getItem = function(ex,ey, ignore) {
		var item = items.getMarkerItem(ex,ey, ignore);
		if (item == null) item = items.getItem(ex,ey, ignore);
		return item;
	}
	_class.getItems = function() {
		return items;
	}
	
	_class.refresh = function() {
		var pr = _class.properties;
		context2d.clearRect(0,0,pr.width, pr.height);
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
	_class.getOutBounds = function() {
		var pr = _class.properties;
		if (pr.autoCrop) {
			var b = items.getOutBounds();
			return {x1:b.x1-4, y1:b.y1-4, w:b.w+8, h:b.h+8};
		} else {
			return {x1:0, y1:0, w:pr.width, h:pr.height};
		}
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
	function onMouseDown(ev) {
		ev = formalEvent(ev);
		var action = Actions.getAction();
		if(ev.btn == 1) {
			action.onMouseDown(ev);
		} else if(ev.btn == 3) {
			action.openMenu(ev);
		}
	}
	function onMouseMove(ev) {
		ev = formalEvent(ev);
		var action = Actions.getAction();
		if(ev.btn == 1) {
			action.onMouseMove(ev);
		}
	}
	function onMouseUp(ev) {
		ev = formalEvent(ev);
		var action = Actions.getAction();
		if(ev.btn == 1) {
			action.onMouseUp(ev);
		}
	}
	function onDblClick(ev) {
		ev = formalEvent(ev);
		var action = Actions.getAction();
		if(ev.which == 1) {
			action.onDblClick(ev);
		}
	}
	
	var event_btn = 0;
	function formalEvent(ev) {
		if (ev.buttons !== undefined) { // for IE9
			if (ev.type == "mousedown") {
				if (ev.buttons == 1) event_btn = 1;
				if (ev.buttons == 2) event_btn = 3;
			} 
			ev.btn = event_btn;
			if (ev.type == "mouseup") {
				event_btn = 0;
			}
		} else { // for chrome
			ev.btn = ev.which;
		}
		
		//console.log(ev.type," ",ev.btn," ",ev.button," ",ev.buttons," ",ev.which);
		// for FF
		if (ev.offsetX == undefined) {
			ev.offsetX = ev.originalEvent.layerX;
			ev.offsetY = ev.originalEvent.layerY;
		}
		return ev;
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
