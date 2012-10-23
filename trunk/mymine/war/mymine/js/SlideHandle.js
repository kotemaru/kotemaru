

function SlideHandle(){this.initialize.apply(this, arguments)};
(function(Class){
	
	var handle = null;
	var callback = null;
	Class.startDrag = function(elem, func) {
		handle = elem;
		callback = func;
	}
	Class.endDrag = function() {
		handle = null;
	}
	Class.move = function(ev) {
		if (handle == null) return;
		if (callback) return callback(ev);
		
		var $header = $("#"+handle.dataset.ref);
		var selector = "."+handle.dataset["class"];
		var offset = $header.offset();
		var w = (ev.clientX - offset.left);
		classCss(selector, {width: w+"px"});
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



})(SlideHandle);
