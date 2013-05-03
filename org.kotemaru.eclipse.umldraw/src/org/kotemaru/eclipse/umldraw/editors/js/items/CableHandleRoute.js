
function CableHandleRoute(){this.initialize.apply(this, arguments)};
(function(_class, _super){
	_class.prototype = new _super();
	
	/**
	 * コンストラクタ。
	 */
	_class.prototype.initialize = function(origin, cable, no) {
		_super.prototype.initialize.apply(this, arguments);
		this.cable = cable;
		this.color = Color.HANDLE_VISIT;
		this.pointNo = no;
	}
	_class.prototype.dragMove = function(xx,yy) {
		var coor = this.cable.getPoint(this.pointNo);
		coor.xy(xx,yy);
		
		var item = Canvas.getItem(xx, yy, this.cable);
		var xy = CableUtil.edgePoint(item,xx,yy);
		Canvas.cursor(item?(xy?"connect2":"connect"):"");
	}
	_class.prototype.dragEnd = function(xx,yy) {
		var item = Canvas.getItem(xx,yy, this.cable);
		if (item && this.cable != item) {
			this.setPoint(item,xx,yy, this.pointNo);
		}
		//this.coor.origin().xy(xx,yy); // 非センター
	}
	
	_class.prototype.isFixed = function() {
		var coor = this.cable.getPoint(this.pointNo);
		return coor.origin();
	}
	_class.prototype.fixed = function() {
		var coor = this.cable.getPoint(this.pointNo);
		coor.setOrigin(null);
		coor.setOrigin2(null);
	}
	_class.prototype.unfixed = function() {
		var coor = this.cable.getPoint(this.pointNo);
		var xx=coor.x(),yy=coor.y();
		coor.setOrigin(this.cable.startPoint);
		coor.setOrigin2(this.cable.endPoint);
		coor.xy(xx,yy);
	}
	
})(CableHandleRoute, Handle);