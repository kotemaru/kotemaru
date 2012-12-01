
function Dialog(){this.initialize.apply(this, arguments)};
(function(Class){
	var current = name;
	
	Class.open = function(name){
		current = name;
		var $win = $(".DialogPanel").show();
		$(".Dialog").hide();
		var $di  = $(name).show();
		$di.offset({left:($win.width()/2-$di.width()/2), top:($win.height()/2-$di.height()/2)});
		restoreDialog($di);
		$di.trigger("opened");
	}
	
	Class.close = function(){
		$(".DialogPanel").hide();
		$(current).trigger("closed");
	}
	
	Class.save = function() {
		$dialog = $(current);
		$inputs = $dialog.find("*[data-path]");
		$inputs.each(function(){
			$input = $(this);
			var path = $input.attr("data-path");
			eval(path+"=getValue($input)");
		});
		Class.close();
	}
	
	function restoreDialog($dialog) {
		$inputs = $dialog.find("*[data-path]");
		$inputs.each(function(){
			$input = $(this);
			var path = $input.attr("data-path");
			setValue($input, eval("("+path+")"));
		});
	}

	function getValue($input) {
		if ($input[0].tagName == "IMG") {
			return $input.attr("src");
		} else {
			return $input.val();
		}
	}
	function setValue($input, val) {
		if ($input[0].tagName == "IMG") {
			return $input.attr("src",val);
		} else {
			return $input.val(val);
		}
	}

})(Dialog);
