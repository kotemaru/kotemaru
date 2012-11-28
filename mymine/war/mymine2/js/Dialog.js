
function Dialog(){this.initialize.apply(this, arguments)};
(function(Class){
	
	Class.open = function(name){
		var $win = $(".DialogPanel").show();
		$(".Dialog").hide();
		var $di  = $(name).show();
		$di.offset({left:($win.width()/2-$di.width()/2), top:($win.height()/2-$di.height()/2)});
	}
	
	Class.close = function(){
		$(".DialogPanel").hide();
	}

})(Dialog);
