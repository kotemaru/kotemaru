

function PulldownButton(){this.initialize.apply(this, arguments)};
(function(Class){
	Class.prototype.initialize = function() {
		
	}

	//-----------------------------------------------------
	// Pulldown Button functions
	//-----------------------------------------------------

	Class.makeElement = function(id,opts) {
		var $elem = $("#templ_pulldownButton>span").clone();
		var $img = $elem.find("img:first-child");

		var icon = opts.icon;
		if (icon==null||icon=="") icon="img/funnel.png";
		$elem.attr("id", id);
		$img.attr("src", icon);
		$img.attr("alt", opts.name);
		Class.makeMenu($elem,opts);
		return $elem
	}
	
	Class.makeMenu = function($elem,opts) {
		var $menu = $elem.find(".PopupMenu").html("");
		var list = [];
		for (var k in opts.values) list.push({id:k, name:opts.values[k]});
		if (opts.keySort == "name") {
			list.sort(function(a,b){return a.name>b.name?1:-1;});
		} else if (opts.keySort == "id") {
			list.sort(function(a,b){return a.id-b.id;});
		}

		for (var i=0; i<list.length; i++) {
			var $item = $("<div class='PopupMenuItem' ></div>");
			$item.attr("data-value", list[i].id);
			$item.text(list[i].name);
			$menu.append($item);
		}
		return $elem;
	}
	Class.getValue = function($elem) {
		return $elem.data("value");
	}
	Class.onClick = function(elem) {
		var $elem = $(elem);
		var val = $elem.data("value");
		if (val) {
			$(this).removeClass("PulldownButtonOn");
			$elem.data("value",null);
		} else {
			var opts = {element:elem, corrent:{x:0,y:6}};
			PopupMenu.open($elem.find(".PopupMenu")[0], opts);
		}
	}
	Class.onChange = function(item) {
		var $elem = $(PopupMenu.options.element);
		var val = $(item).attr("data-value");
		$elem.data("value", val);
		if (val) {
			$elem.addClass("PulldownButtonOn");
		} else {
			$elem.removeClass("PulldownButtonOn");
		}
		PopupMenu.close();
	}

	//-----------------------------------------------------
	$(function(){
		// PulldownButton
		$(".PulldownButton").live("click", function(){
			Class.onClick(this);
		});
		$(".PulldownButton .PopupMenuItem").live("click", function(){
			Class.onChange(this);
			return false;
		});

	});
	

})(PulldownButton);
