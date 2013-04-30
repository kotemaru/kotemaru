

function MenuManager(){this.initialize.apply(this, arguments)};
(function(_class){

	_class.prototype.initialize = function(targetClass) {
	}
	
	var ENABLE = {
		cut: function(){return Canvas.hasSelectGroup();},
		copy: function(){return Canvas.hasSelectGroup();},
		paste: function(){return EditBuffer.getCopyBuffer() != null;},
		
		delPoint: function(item,xx,yy){return Canvas.getHandle(xx,yy) != null;},
		fixPoint: function(item,xx,yy){return Canvas.getHandle(xx,yy) != null;},
	};
	
	
	_class.isEnable = function(cmd,item,xx,yy) {
		if (ENABLE[cmd]) return ENABLE[cmd](item,xx,yy);
		return true;
	}
	
	_class.doMenuItem = function(item, $menuItem,xx,yy) {
		var cmd = $menuItem.attr("data-value");
		if (cmd == "cut") {
			EditBuffer.cut();
		} else if (cmd == "copy") {
			EditBuffer.copy();
		} else if (cmd == "paste") {
			EditBuffer.paste(xx,yy);
		} else if (cmd == "properties") {
			Dialog.open(item.getDialog(), item);
		} else if (cmd == "canvas-properties") {
			Dialog.open(Canvas.getDialog(), Canvas.properties);

		// Cables
		} else if (cmd == "addPoint") {
			var coor = new Coor({
				origin:this.startPoint, 
				origin2:this.endPoint,
			});
			coor.xy(xx,yy);
			item.addPoint(coor);
		} else if (cmd == "fixPoint") {
			var handle = Canvas.getHandle(xx,yy);
			if (handle && handle.fixed) {
				handle.fixed(xx,yy);
			}
		}
	}
	
})(MenuManager);


//EOF
