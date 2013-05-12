
function Dialog(){this.initialize.apply(this, arguments)};
(function(_class){
	var current = name;
	var closureVar = null;
	var targetItem;

	_class.open = function(name, item){
		current = name;
		targetItem = item;
		
		$(".Dialog").hide();
		var $di  = $(name).show();
		if ($di.length == 0) return;
		
		var $win = $(".DialogPanel").show();
		$di.offset({left:($win.width()/2-$di.width()/2), top:($win.height()/2-$di.height()/2)});
		restoreDialog($di);
		$di.trigger("opened");
	}
	
	_class.close = function(){
		$(".DialogPanel").hide();
		$(current).trigger("closed");
		Canvas.refresh();
	}
	
	_class.save = function() {
		$dialog = $(current);
		$inputs = $dialog.find("*[data-path]");
		$inputs.each(function(){
			$input = $(this);
			var path = $input.attr("data-path");
			targetItem[path] = getValue($input);
		});
		$dialog.trigger("saved", [targetItem]);
		_class.close();
	}
	
	function restoreDialog($dialog) {
		$inputs = $dialog.find("*[data-path]");
		$inputs.each(function(){
			$input = $(this);
			var path = $input.attr("data-path");
			setValue($input, targetItem[path]);
		});
	}

	function getValue($input) {
		if ($input[0].tagName == "IMG") {
			return $input.attr("src");
		} else if ($input.hasClass("PulldownButton")) {
			return $input.attr("data-value");
		} else if ($input.hasClass("Selector")) {
			return $input.attr("data-value");
		} else if($input[0].type == "checkbox") {
			return $input.attr("checked") != null;
		} else {
			return $input.val();
		}
	}
	function setValue($input, val) {
		if ($input[0].tagName == "IMG") {
			$input.attr("src",val);
		} else if ($input.hasClass("PulldownButton")) {
			PopupMenu.setValue($input,val);
		} else if ($input.hasClass("Selector")) {
			Selector.setValue($input,val);
		} else if($input[0].type == "checkbox") {
			$input.attr("checked", val);
		} else {
			$input.val(val);
		}
	}

	
})(Dialog);
