

function CheckButton(){this.initialize.apply(this, arguments)};
(function(Class){
	var CheckButtonOn = "CheckButtonOn";
	
	Class.prototype.initialize = function(selector) {
	}

	Class.toggle = function(elem) {
		var $elem = $(elem);
		if ($elem.attr("disabled")) return;

		if ($elem.hasClass(CheckButtonOn)) {
			$elem.removeClass(CheckButtonOn);
		} else {
			resetGroup($elem);
			$elem.addClass(CheckButtonOn);
		}
		$elem.trigger("change", [$elem.hasClass(CheckButtonOn), $elem.attr("id")]);
	};
	function resetGroup($elem) {
		var group = $elem.attr("data-group");
		if (group) {
			var $group = $(".CheckButton[data-group='"+group+"']");
			$group.removeClass(CheckButtonOn);
		}
	}
	
	Class.prototype.off = function(elem) {
		$(elem).removeClass(CheckButtonOn);
	}
	Class.prototype.on = function(elem) {
		$(elem).addClass(CheckButtonOn);
	}
	
	Class.isChecked = function($elem) {
		return $elem.hasClass(CheckButtonOn);
	}

	Class.getGroupCheck = function(group) {
		var res = null;
		var $group = $(".CheckButton[data-group='"+group+"']");
		$group.each(function(){
			if ($(this).hasClass(CheckButtonOn)) res = $(this);
		});
		return res;
	}


	//-----------------------------------------------------
	$(function(){
		$(".CheckButton").live("click",function(ev){
			Class.toggle(this);
		});
	});

})(CheckButton);
