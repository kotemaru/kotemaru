
function BorderLayout(){this.initialize.apply(this, arguments)};
(function(Class){
	var BorderLayoutHeader = ".BorderLayoutHeader";
	var BorderLayoutLeft   = ".BorderLayoutLeft";
	var BorderLayoutRight  = ".BorderLayoutRight";
	var BorderLayoutFooter = ".BorderLayoutFooter";
	var BorderLayoutMain   = ".BorderLayoutMain";
	var BorderLayoutHandle = ".BorderLayoutHandle";

	var SELECTOR = {
		header: BorderLayoutHeader,
		footer: BorderLayoutFooter,
		left  : BorderLayoutLeft,
		right : BorderLayoutRight
	};
	
	Class.setHeaderHeight = function(h) {
		sizes.header = h;
		var hpx = (h?h:0)+"px";
		setStyle(BorderLayoutHeader, {height: hpx});
		setStyle(BorderLayoutLeft,   {paddingTop: hpx});
		setStyle(BorderLayoutRight,  {paddingTop: hpx});
		setStyle(BorderLayoutMain,   {paddingTop: hpx});
	}
	Class.setFooterHeight = function(h) {
		sizes.footer = h;
		var hpx = (h?h:0)+"px";
		setStyle(BorderLayoutFooter, {height: hpx});
		setStyle(BorderLayoutLeft,   {paddingBottom: hpx});
		setStyle(BorderLayoutRight,  {paddingBottom: hpx});
		setStyle(BorderLayoutMain,   {paddingBottom: hpx});
	}
	Class.setLeftWidth = function(w) {
		sizes.left = w;
		var wpx = (w?w:0)+"px";
		setStyle(BorderLayoutLeft,   {width: wpx});
		setStyle(BorderLayoutMain,   {paddingLeft: wpx});
	}
	Class.setRightWidth = function(w) {
		sizes.right = w;
		var wpx = (w?w:0)+"px";
		setStyle(BorderLayoutRight,  {width: wpx});
		setStyle(BorderLayoutMain,   {paddingRight: wpx});
	}
	
	var RESIZE_FUNC = {
		header: Class.setHeaderHeight,
		footer: Class.setFooterHeight,
		left  : Class.setLeftWidth,
		right : Class.setRightWidth
	};

	var OPTIONS = {
		header:{size:0, resizable:false},
		footer:{size:0, resizable:false},
		left  :{size:0, resizable:false},
		right :{size:0, resizable:false},
		strageName: location.path+"/BorderLayout"
	};
	
	var sizes = {};
	Class.init = function(opts) {
		opts = $.extend(true, OPTIONS, opts);
		for (var name in SELECTOR) {
			initOne(name, opts);
		}
		initHandling();
	}
	function initOne(name, opts) {
		RESIZE_FUNC[name](opts[name].size);
		var $elem = $(SELECTOR[name]);
		$elem.toggle(opts[name].size>0);
		if (opts[name].resizable) {
			var cursor = (name=="left"||name=="right")?"col-resize":"row-resize";
			$elem.find(BorderLayoutHandle).css("cursor", cursor);
		}
	}

	function initHandling() {
		var handle = null;
		$(BorderLayoutHandle).live("mousedown",function(){
			handle = this;
			return false;
		});
		$(document.body).live("mousemove",function(ev){
			if (handle == null) return;
			var frame = handle.parentNode;
			var $frame = $(frame);
			var offset = $frame.offset();
			if (frame == $(BorderLayoutHeader)[0]) {
				Class.setHeaderHeight(ev.clientY - offset.top+2);
			} else if (frame == $(BorderLayoutFooter)[0]) {
				Class.setFooterHeight(offset.top + $frame.height() - ev.clientY+1);
			} else if (frame == $(BorderLayoutLeft)[0]) {
				Class.setLeftWidth(ev.clientX - offset.left+2);
			} else if (frame == $(BorderLayoutRight)[0]) {
				Class.setRightWidth(offset.left + $frame.width() - ev.clientX+1);
			}
		}).live("mouseup", function(){
			handle = null;
		});
	}

	                 	
	
	function setStyle(selector, style) {
		$(selector).css(style);
	}

	
})(BorderLayout);


