
function Chip(){this.initialize.apply(this, arguments)};
(function(Class, Super) {
	Class.prototype = $.extend(Class.prototype, Super.prototype);
	
	Class.prototype.initialize = function(game) {
		Super.prototype.initialize.apply(this, arguments);
	};

})(Chip, ImgElem);
