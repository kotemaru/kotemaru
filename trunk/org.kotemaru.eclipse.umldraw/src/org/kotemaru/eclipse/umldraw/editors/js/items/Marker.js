

function Marker(){this.initialize.apply(this, arguments)};
(function(_class, _super){
	Item.extend(_class, _super);
	//_class.properties = Lang.copy(_super.properties, {
	//	name : {type: "string", value:"EgEgEg"},
	//});
	_class.prototype.isMarker = true;
	
	var markerImg;
	$(function(){
		markerImg = $("#markerImg")[0];
	})
	
	/**
	 * コンストラクタ。
	 */
	_class.prototype.initialize = function(attrs) {
		//Lang.initAttibutes(this, _class.properties);
		_super.prototype.initialize.apply(this, arguments);
		//Lang.mergeAttibutes(this, attrs);
	}

	_class.prototype.w = function() {return 16;}
	_class.prototype.h = function() {return 16;}

	_class.prototype.draw = function(dr) {
		with (this) {
			var x1 = coor.x();
			var y1 = coor.y();
			dr.drawMarker(markerImg, x1,y1);
		}
		
		return this;
	}

	
	_class.prototype.getDialog = function() {
		return null;
	}
	_class.prototype.getMenu = function() {
		return null;
	}
	
})(Marker, Rectangle);


//EOF
