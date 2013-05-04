

function CableAction(){this.initialize.apply(this, arguments)};
(function(_class,_super){
	Lang.extend(_class, _super);
	
	var STYLES = {
		0: {startType:"none",   lineType:"normal", endType:"none"},
		1: {startType:"none",   lineType:"normal", endType:"rhombi"},
		2: {startType:"none",   lineType:"normal", endType:"rhombi-B"},
		3: {startType:"none",   lineType:"normal", endType:"triangle"},
		4: {startType:"none",   lineType:"dotted", endType:"triangle"},
		5: {startType:"none",   lineType:"dotted", endType:"arrow"},
		6: {startType:"rhombi", lineType:"dotted", endType:"arrow"},
	};
	
	_class.dialogSave = function() {
		var $di = $("#cableDialog");
		var isDef = $di.find("input[name='routeDefault']").attr("checked");
		if (isDef) {
			var lineRouteDefault =
				$di.find("*[data-path='lineRoute']").attr("data-value");
			Eclipse.setPreferences("lineRouteDefault", lineRouteDefault);
		}
	}
	
	_class.prototype.onMouseDown = function(ev) {
		var asso = $("#association").attr("data-value");
		var xy = {x:ev.offsetX, y:ev.offsetY};
		this.cable = new this.targetClass(xy);
		Util.update(this.cable, STYLES[asso]);
		this.cable.lineRoute = Eclipse.getPreferences("lineRouteDefault");;
		
		Canvas.addItem(this.cable);
		Canvas.select(this.cable);
		
		var item = Canvas.getItem(xy.x, xy.y);
		if (item) {
			this.cable.setStartPoint(item);
		} else {
			this.cable.setStartPoint(new Coor(xy));
		}
		Canvas.refresh();
	}

	_class.prototype.onMouseMove = function(ev) {
		var xy = {x:ev.offsetX, y:ev.offsetY};
		this.cable.setEndPoint(new Coor(xy));
		Canvas.refresh();

		var item = Canvas.getItem(xy.x, xy.y, this.cable);
		var xy = CableUtil.edgePoint(item,xy.x, xy.y);
		Canvas.cursor(item?(xy?"connect2":"connect"):"");
	}
	_class.prototype.onMouseUp  = function(ev) {
		var xy = {x:ev.offsetX, y:ev.offsetY};
		var item = Canvas.getItem(xy.x, xy.y, this.cable);
		if (item) {
			this.cable.setEndPoint(item);
		} else {
			this.cable.setEndPoint(new Coor(xy));
		}
		Canvas.refresh();
		Actions.resetAction();
	}
	
})(CableAction, Action);


//EOF
