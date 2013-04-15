
function PopupMenu(){this.initialize.apply(this, arguments)};
(function(Class){
	Class.options = null;

	Class.open = function(name, opts){
		Class.close();
		Class.options = opts;
		
		var $menu  = $(name).show();
		var offset = {left:0, top:0};
		if (opts.offset) {
			offset = opts.offset;
		} else if (opts.element) {
			var $elem = $(opts.element);
			offset = $elem.offset();
			offset.top += $elem.height();
		} else if (opts.event) {
			offset = {left:opts.event.clientX, top:opts.event.clientY};
		}

		if (opts.corrent) {
			offset.left += opts.corrent.x;
			offset.top  += opts.corrent.y;
		}

		$menu.offset(offset);
	}

	Class.close = function(){
		// TODO: 複数Popup
		$(".PulldownMenu").hide();
		$(".PopupMenu").hide();
	}

	var isMakedIconMenu = false;
	Class.makeIconMenu = function(name, url) {
		if (isMakedIconMenu) return;
		isMakedIconMenu = true;
		jQuery.get(url, null, function(data){
			var $menu = $(name);
			var icons = data.split("\n");

			for (var i=0; i<icons.length; i++) {
				var $img = $("<img class='MenuItem'/>");
				$img.attr("src", icons[i]);
				$menu.append($img);
			}
		});
	}

	$(function(){
		$(".PulldownButton").live("click", function(){
			PopupMenu.open($(this).find(".PulldownMenu"), {element: this});
			return false;
		});
		$(".PulldownMenu > .MenuItem").live("click", function(){
			var $elem = $(PopupMenu.options.element);
			var $sel = $(this);
			
			$img = $elem.find(">img");
			$img.attr("src", $sel.find(">img").attr("src"));
			$elem.attr("data-value", $sel.attr("data-value"));
			
			PopupMenu.close();
			return false;
		});
		$(".PopupMenu > .MenuItem").live("click", function(){
			if (PopupMenu.options.item) {
				PopupMenu.options.item.doMenuItem($(this));
			}
			PopupMenu.close();
			Canvas.refresh();
			return false;
		});
		$(document.body).live("click", function(){
			PopupMenu.close();
		});
		//PopupMenu.makeIconMenu("#iconSelectMenu", "icons.txt");
	});

})(PopupMenu);
