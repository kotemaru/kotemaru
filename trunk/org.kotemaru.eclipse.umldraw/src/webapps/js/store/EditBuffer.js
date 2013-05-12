

function EditBuffer(){this.initialize.apply(this, arguments)};
(function(_class){
	
	var copyBuff = null;
	var copyBuffForExport = null;
	var undoBuff = [];
	var redoBuff = [];
	var curData = null;
	var isChange = false;

	_class.getCopyBuffer = function() {
		return copyBuff;
	}
	_class.getCopyBufferForExport = function() {
		return copyBuffForExport;
	}
	
	_class.setCopyBuffer = function(data) {
		copyBuff = data;
	}
	
	_class.cut = function() {
		_class.copy();
		var selectGroup = Canvas.getSelectGroup();
		selectGroup.getItems().each(function(item){
			Canvas.delItem(item);
		});
		Canvas.clearSelect();
		Canvas.getSelectGroup().clear();
		Canvas.refresh();
	}
	_class.copy = function() {
		var selectGroup = Canvas.getSelectGroup();
		copyBuff = Store.copy(selectGroup.getItems());
		copyBuffForExport = Store.copy(selectGroup.getItems(),true);
		Eclipse.fireEvent("copyClipboard");
	}
	_class.paste = function(xx,yy) {
		Eclipse.fireEvent("pasteClipboard");
		Store.paste(copyBuff, xx,yy);
	}
	
	_class.init = function() {
		curData = Store.save(Canvas.getItems());
		undoBuff.length = 0;
		redoBuff.length = 0;
	}

	_class.notice = function() {
		isChange = true;
		return _class;
	}
	_class.noticeCancel = function() {
		isChange = false;
		return _class;
	}
	_class.backup = function(data) {
		if (!isChange) return;
		
		if (data) curData = data;
		undoBuff.push(curData);
		redoBuff.length = 0;
		curData = Store.copy(Canvas.getItems());
		Eclipse.fireEvent("change,"+undoBuff.length+","+redoBuff.length);
		Debug.change(undoBuff.length);
		isChange = false;
	}
	_class.undo = function() {
		if (undoBuff.length == 0) return;
		redoBuff.push(curData);
		curData = undoBuff.pop();
		Store.load(curData);
		Eclipse.fireEvent("change,"+undoBuff.length+","+redoBuff.length);
		Debug.change(undoBuff.length);
		isChange = false;
	}
	_class.redo = function() {
		if (redoBuff.length == 0) return;
		undoBuff.push(curData);
		curData = redoBuff.pop();
		Store.load(curData);
		Eclipse.fireEvent("change,"+undoBuff.length+","+redoBuff.length);
		Debug.change(undoBuff.length);
		isChange = false;
	}
	
})(EditBuffer);


//EOF
