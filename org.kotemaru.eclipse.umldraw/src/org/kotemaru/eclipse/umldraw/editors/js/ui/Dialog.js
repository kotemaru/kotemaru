
function Dialog(){this.initialize.apply(this, arguments)};
(function(Class){
	var current = name;
	var closureVar = null;
	var targetItem;

	Class.open = function(name, item){
		current = name;
		targetItem = item;
		
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
		Canvas.refresh();
	}
	
	Class.save = function() {
		$dialog = $(current);
		$inputs = $dialog.find("*[data-path]");
		$inputs.each(function(){
			$input = $(this);
			var path = $input.attr("data-path");
			targetItem[path] = getValue($input);
		});
		Class.close();
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
		} else if($input[0].type == "checkbox") {
			return $input.attr("checked");
		} else {
			return $input.val();
		}
	}
	function setValue($input, val) {
		if ($input[0].tagName == "IMG") {
			$input.attr("src",val);
		} else if ($input.hasClass("PulldownButton")) {
			$input.attr("data-value",val);
			$img = $input.find(">img");
			$img.attr("src", $input.find("div[data-value='"+val+"']>img").attr("src"));
		} else if($input[0].type == "checkbox") {
			$input.attr("checked", val);
		} else {
			$input.val(val);
		}
	}

	
})(Dialog);
