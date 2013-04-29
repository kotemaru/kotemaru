
function PopupMenu(){this.initialize.apply(this, arguments)};
(function(_class){
	_class.options = null;

	_class.open = function(name, opts){
		_class.close();
		_class.options = opts;
		
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

	$(function(){
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
			if (PopupMenu.options.item) {
				var ev = PopupMenu.options.event;
				PopupMenu.options.item.doMenuItem($(this),
									ev.offsetX, ev.offsetY);
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
