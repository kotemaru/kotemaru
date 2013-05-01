
function PopupMenu(){this.initialize.apply(this, arguments)};
(function(_class){
	_class.options = null;

	_class.open = function(name, opts){
		_class.close();
		_class.options = opts;

		var $menu  = $(name);
		setupDisabled($menu,opts);
		
		$(name).show();
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

	_class.close = function(){
		// TODO: 複数Popup
		$(".PulldownMenu").hide();
		$(".PopupMenu").hide();
	}
	
	_class.setValue = function($btn, val){
		$btn.attr("data-value",val);
		$img = $btn.find(">img");
		$img.attr("src", $btn.find("div[data-value='"+val+"']>img").attr("src"));
	}
	
	function setupDisabled($menu,opts) {
		var $items = $menu.find(".MenuItem");
		$items.removeClass("Disabled");
		$items.each(function(){
			var $item = $(this);
			var cmd = $item.attr("data-value");
			var item = PopupMenu.options.item;
			var x=opts.event.offsetX, y=opts.event.offsetY;
			if (!MenuManager.isEnable(cmd,item,x,y)) {
				$item.addClass("Disabled");
			}
		});
	}

	$(function(){
		var reserve = null;

		$(document.body).live("click", function(){
			PopupMenu.close();
/*
			// trigger first
			reserve = PopupMenu.close;
			setTimeout(function(){
				if (reserve) reserve();
				reserve = null;
			}, 100);
*/
		});
		
		$(".PulldownButton").live("click", function(){
			var optsAttr = $(this).attr("data-opts");
			var opts = optsAttr ? eval("("+optsAttr+")") : {};
			opts.element = this;
			PopupMenu.open($(this).find(".PulldownMenu"), opts);
			return false;
		});
		$(".PulldownMenu > .MenuItem").live("click", function(){
			var $elem = $(PopupMenu.options.element);
			var $sel = $(this);
			var dataImg = $elem.attr("data-img");
			
			var $img = dataImg ? $(dataImg) : $elem.find(">img");
			$img.attr("src", $sel.find(">img").attr("src"));
			$elem.attr("data-value", $sel.attr("data-value"));
			
			PopupMenu.close();
			return false;
		});
		$(".PopupMenu > .MenuItem").live("click", function(){
			reserve = null;
			if ($(this).hasClass("Disabled")) return false;
			
			if (PopupMenu.options.item) {
				var ev = PopupMenu.options.event;
				MenuManager.doMenuItem(
					PopupMenu.options.item,
					$(this),
					ev.offsetX, ev.offsetY
				);
			}
			PopupMenu.close();
			Canvas.refresh();
			return false;
		});
		//PopupMenu.makeIconMenu("#iconSelectMenu", "icons.txt");
	});

})(PopupMenu);
