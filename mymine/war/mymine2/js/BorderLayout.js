
function BorderLayout(){this.initialize.apply(this, arguments)};
(function(Class){
	var BorderLayoutHeader = ".BorderLayoutHeader";
	var BorderLayoutLeft   = ".BorderLayoutLeft";
	var BorderLayoutRight  = ".BorderLayoutRight";
	var BorderLayoutFooter = ".BorderLayoutFooter";
	var BorderLayoutMain   = ".BorderLayoutMain";

	var BorderLayoutHandle       = ".BorderLayoutHandle";

	Class.init = function(opts) {
		Class.setHeaderHeight(opts.header);
		Class.setLeftWidth(opts.left);
		Class.setRightWidth(opts.right);
		Class.setFooterHeight(opts.footer);

		if (opts.header == null) $(BorderLayoutHeader).hide();
		if (opts.left == null)   $(BorderLayoutLeft).hide();
		if (opts.right == null)  $(BorderLayoutRight).hide();
		if (opts.footer == null) $(BorderLayoutFooter).hide();

		initHandle();
	}

	function initHandle() {
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

	Class.setHeaderHeight = function(h) {
		var hpx = (h?h:0)+"px";
		classCss(BorderLayoutHeader, {height: hpx});
		classCss(BorderLayoutLeft,   {paddingTop: hpx});
		classCss(BorderLayoutRight,  {paddingTop: hpx});
		classCss(BorderLayoutMain,   {paddingTop: hpx});
		
	}
	Class.setFooterHeight = function(h) {
		var hpx = (h?h:0)+"px";
		classCss(BorderLayoutFooter, {height: hpx});
		classCss(BorderLayoutLeft,   {paddingBottom: hpx});
		classCss(BorderLayoutRight,  {paddingBottom: hpx});
		classCss(BorderLayoutMain,   {paddingBottom: hpx});
	}
	Class.setLeftWidth = function(w) {
		var wpx = (w?w:0)+"px";
		classCss(BorderLayoutLeft,   {width: wpx});
		classCss(BorderLayoutMain,   {paddingLeft: wpx});
	}
	Class.setRightWidth = function(w) {
		var wpx = (w?w:0)+"px";
		classCss(BorderLayoutRight,  {width: wpx});
		classCss(BorderLayoutMain,   {paddingRight: wpx});
	}

	var classCssRuleCache = {};
	function getCssRule(selector) {
		if (classCssRuleCache[selector]) return classCssRuleCache[selector];
		var sheets = document.styleSheets;
		for (var i=0; i<sheets.length; i++) {
			var rules = sheets[i].cssRules;
			for (var j=0; j<rules.length; j++) {
				if (selector == rules[j].selectorText) {
					classCssRuleCache[selector] = rules[j];
					return rules[j];
				}
			}
		}
		return null;
	}
	function classCss(selector, style) {
		var rule = getCssRule(selector);
		if (rule == null) return;
		for (var k in style) rule.style[k] = style[k];
	}

})(BorderLayout);


