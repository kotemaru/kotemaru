
function CableHandle(){this.initialize.apply(this, arguments)};
(function(_class, _super){
	_class.prototype = new _super();
	
	/**
	 * コンストラクタ。
	 */
	_class.prototype.initialize = function(origin, cable, setterName, no) {
		_super.prototype.initialize.apply(this, arguments);
		this.cable = cable;
		this.setterName = setterName;
		this.color = Color.HANDLE_VISIT;
		this.absCoor = new Coor();
		this.pointNo = no;
	}
	_class.prototype.dragMove = function(xx,yy) {
		this.absCoor.xy(xx,yy);
		this.cable[this.setterName](this.absCoor, this.pointNo);
	}
	_class.prototype.dragEnd = function(xx,yy) {
		var item = Canvas.getItem(xx,yy, this.cable);
		if (item && this.cable != item) {
			this.cable[this.setterName](item, this.pointNo);
		}
		//this.coor.origin().xy(xx,yy); // 非センター
	}
	
})(CableHandle, Handle);