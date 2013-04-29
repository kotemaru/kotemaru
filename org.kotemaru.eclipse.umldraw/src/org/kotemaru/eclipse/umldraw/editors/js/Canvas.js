

function Canvas(){this.initialize.apply(this, arguments)};
(function(_class){
	
	_class.attributes = {
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
	
	_class.getHandle = function(ex,ey) {
		var handle = null;
		if (selectItem) handle = selectItem.getHandle(ex, ey);
		return handle;
	}
	
	_class.cursor = function(type) {
		canvas.style.cursor = "url(img/cursor_"+type+".png),default";
	}
	
	var copyBuff;
	_class.doMenuItem = function($menuItem,xx,yy) {
		var cmd = $menuItem.attr("data-value");
		if (cmd == "cut") {
			copyBuff = Store.copy(selectGroup.getItems());
			selectGroup.getItems().each(function(item){
				Canvas.delItem(item);
			});
		} else if (cmd == "copy") {
			copyBuff = Store.copy(selectGroup.getItems());
		} else if (cmd == "paste") {
			Store.paste(copyBuff, xx,yy);
		} else if (cmd == "properties") {
			Dialog.open(this.getDialog(), _class.attributes);
		} else if (cmd == "saveLoad") {
			Dialog.open("#debugDialog", data);
			var data = Store.save(Canvas.getItems());
			$("#saveText").val(JSON.stringify(data,null, "\t"));
			Store.load(data);
		} else if (cmd == "SVG") {
			Dialog.open("#svgDialog", data);
			var data = {svg: Canvas.toSVG()};
			var ifr = $("#iframeSvg")[0];
			ifr.contentDocument.body.innerHTML = data.svg;
		} else if (cmd == "undo") {
			Canvas.undo();
		} else if (cmd == "redo") {
			Canvas.redo();
		}
	}
	_class.getDialog = function() {
		return "#canvasDialog";
	}
	
	_class.width = function() {
		return _class.attributes.width;
	}
	_class.height = function() {
		return _class.attributes.height;
	}
	_class.setAttributes = function(attrs) {
		if (attrs) _class.attributes = attrs;
		$(canvas).attr("width", _class.attributes.width);
		$(canvas).attr("height", _class.attributes.height);
	}
	_class.getAttributes = function(attrs) {
		return _class.attributes;
	}
	$("#canvasDialog").live("saved",function(){
		_class.setAttributes();
	});
	
	var undoBuff = [];
	var redoBuff = [];
	var curData = null; // todo初期データ。
	_class.backup = function(data) {
		if (data) curData = data;
		undoBuff.push(curData);
		redoBuff.length = 0;
		curData = Store.save(Canvas.getItems());
	}
	_class.undo = function() {
		if (undoBuff.length == 0) return;
		redoBuff.push(curData);
		curData = undoBuff.pop();
		Store.load(curData);
	}
	_class.redo = function() {
		if (redoBuff.length == 0) return;
		undoBuff.push(curData);
		curData = redoBuff.pop();
		Store.load(curData);
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
