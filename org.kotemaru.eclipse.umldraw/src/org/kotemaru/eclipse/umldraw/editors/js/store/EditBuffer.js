

function EditBuffer(){this.initialize.apply(this, arguments)};
(function(_class){
	
	var copyBuff = null;
	var undoBuff = [];
	var redoBuff = [];
	var curData = null; // todo初期データ。
	
	_class.getCopyBuffer = function() {
		return copyBuff;
	}
	
	_class.cut = function() {
		var selectGroup = Canvas.getSelectGroup();
		copyBuff = Store.copy(selectGroup.getItems());
		selectGroup.getItems().each(function(item){
			Canvas.delItem(item);
		});
	}
	_class.copy = function() {
		var selectGroup = Canvas.getSelectGroup();
		copyBuff = Store.copy(selectGroup.getItems());
	}
	_class.paste = function(xx,yy) {
		Store.paste(copyBuff, xx,yy);
	}
	
	_class.init = function() {
		curData = Store.save(Canvas.getItems());
		undoBuff.length = 0;
		redoBuff.length = 0;
	}

	_class.backup = function(data) {
		if (data) curData = data;
		undoBuff.push(curData);
		redoBuff.length = 0;
		curData = Store.save(Canvas.getItems());
		Eclipse.fireEvent("change,"+undoBuff.length);
	}
	_class.undo = function() {
		if (undoBuff.length == 0) return;
		redoBuff.push(curData);
		curData = undoBuff.pop();
		Store.load(curData);
		Eclipse.fireEvent("change,"+undoBuff.length);
	}
	_class.redo = function() {
		if (redoBuff.length == 0) return;
		undoBuff.push(curData);
		curData = redoBuff.pop();
		Store.load(curData);
		Eclipse.fireEvent("change,"+undoBuff.length);
	}
	
})(EditBuffer);


//EOF
