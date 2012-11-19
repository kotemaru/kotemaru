/* Copyright 2012 kotemaru.org. (http://www.apache.org/licenses/LICENSE-2.0) */

function Common(){this.initialize.apply(this, arguments)};
(function(Class){

	/**
	 * Class CSS Util
	 *
	 */
	var classCssRuleCache = {};

	Class.getCssRule = function(selector) {
		if (classCssRuleCache[selector]) return classCssRuleCache[selector];
		var sheets = document.styleSheets;
		for (var i=0; i<sheets.length; i++) {
			var rules = sheets[i].cssRules;
			if (rules == null) rules = sheets[i].rules; // ForIE
			for (var j=0; j<rules.length; j++) {
				if (selector == rules[j].selectorText) {
					classCssRuleCache[selector] = rules[j];
					return rules[j];
				}
			}
		}
		return null;
	}

	Class.getCssRuleWithDefine = function(selector) {
		var rule = Class.getCssRule(selector);
		if (rule) return rule;

		var sheet = document.styleSheets[0];
		if (sheet.insertRule) {
			sheet.insertRule(selector+"{}", sheet.cssRules.length);
		} else {
			sheet.addRule(selector,"dummy:dummy");//forIE
		}
		return Class.getCssRule(selector);
	}
	Class.setCssRule = function(selector, style) {
		var rule = Class.getCssRuleWithDefine(selector);
		if (rule == null) return;
		for (var k in style) rule.style[k] = style[k];
	}
	Class.setCssRuleImportant = function(selector, style) {
		var rule = Class.getCssRuleWithDefine(selector);
		if (rule == null) return;
		for (var k in style) rule.style.setProperty(k, style[k], 'important');
	}

	/**
	 * Waiting
	 */
	var $waiting = $("<div id='commonWaiting' ></div>");
	$(function(){
		$(document.body).append($waiting);
	})
	Class.waiting = function(callback) {
		$waiting.show();
		setTimeout(function(){
			callback();
			$waiting.hide();
		}, 1);
	}


})(Common);


