

function SlideHandle(){this.initialize.apply(this, arguments)};
(function(Class){
	
	var colmunWidth = {
			TFolder    : 200,
			TNum       : 36,
			TProject   : 80,
			TTracker   : 70,
			TPriority  : 48,
			TAssigned  : 97,
			TState     : 28,
			TUpDate    : 54,
			TStartDate : 54,
			TDueDate   : 54,
			TSubject   : 1000
	};

	Class.init = function() {
		var data = Storage.loadData("colmunWidth");
		if (data) colmunWidth = data;
		for (var k in colmunWidth) {
console.log(k,colmunWidth[k]);
			classCss("."+k, {width: colmunWidth[k]+"px"});
		}
	}
	
	
	var handle = null;
	var callback = null;
	Class.startDrag = function(elem, func) {
		handle = elem;
		callback = func;
	}
	Class.endDrag = function() {
		Storage.saveData("colmunWidth", colmunWidth);
		handle = null;
	}
	Class.move = function(ev) {
		if (handle == null) return;
		if (callback) return callback(ev);
		
		var $header = $("#"+handle.dataset.ref);
		var className = handle.dataset["class"];
		var offset = $header.offset();
		var w = (ev.clientX - offset.left);
		if (w<0) w=0;
		colmunWidth[className] = w;
		classCss("."+className, {width: w+"px"});
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
