

function AreaSelect(){this.initialize.apply(this, arguments)};
(function(_class){

	_class.prototype.initialize = function() {
	}
	
	var isVisible = false;
	var isDrag = false;
	var x1,y1,x2,y2;

	_class.dragStart = function(evx,evy) {
		isVisible = true;
		isDrag = false;
		x1 = evx;
		y1 = evy;
		x2 = evx;
		y2 = evy;
		Canvas.select(null);
		Canvas.refresh();
	}
	
	_class.dragMove = function(evx,evy) {
		isDrag = true;
		x2 = evx;
		y2 = evy;
		Canvas.refresh();
	}
	_class.dragEnd = function(evx,evy) {
		if (isDrag) {
			var f = Util.formalRect(x1,y1,x2,y2);
			var selectGroup = Canvas.getSelectGroup();
			selectGroup.clear();
			Canvas.getItems().each(function(item){
				if (item.inRect(f.x1,f.y1,f.x2,f.y2)) {
					selectGroup.getItems().addItem(item);
				}
			});
			selectGroup.fixed();
			if (selectGroup.isValid()) Canvas.select(selectGroup);
		}
		
		isVisible = false;
		Canvas.refresh();
	}
	_class.drawOutBounds = function(dc) {
		if (isVisible) Util.drawOutBounds(dc, x1, y1, x2, y2);
	}

})(AreaSelect);


//EOF
