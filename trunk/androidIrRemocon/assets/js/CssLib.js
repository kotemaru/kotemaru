function CssLib() {
	this.initialize.apply(this, arguments)
};
(function(Class) {

	/**
	 * ルールのキャッシュ
	 * 
	 * @type CSSStyleRule
	 */
	var classCssRuleCache = {};

	/**
	 * セレクタに一致するルールを取得。
	 * <li>検索は重いのキャッシュする。
	 * 
	 * @param {string} selectror セレクタ文字列
	 * @returns {CSSStyleRule} ルール
	 */
	Class.getCssRule = function(selector) {
		if (classCssRuleCache[selector]) return classCssRuleCache[selector];
		var sheets = document.styleSheets;
		for ( var i = 0; i < sheets.length; i++) {
			var rules = sheets[i].cssRules;
			if (rules == null) rules = sheets[i].rules; // ForIE
			if (rules == null) continue; // Chrome bug
			for ( var j = 0; j < rules.length; j++) {
				if (selector == rules[j].selectorText) {
					classCssRuleCache[selector] = rules[j];
					return rules[j];
				}
			}
		}
		return null;
	}

	/**
	 * セレクタに一致するルールを取得。
	 * <li>当該セレクタのルールが存在しなければ作成する。
	 * 
	 * @param {string} selectror セレクタ文字列
	 * @returns {CSSStyleRule} ルール
	 */
	Class.getCssRuleWithDefine = function(selector) {
		var rule = Class.getCssRule(selector);
		if (rule) return rule;

		var sheet = null;
		document.styleSheets[0];
		for ( var i = 0; i < document.styleSheets.length; i++) {
			if (document.styleSheets[i].cssRules != null) {
				sheet = document.styleSheets[i];
			}
		}
		if (sheet.insertRule) {
			sheet.insertRule(selector + "{}", sheet.cssRules.length);
		} else {
			sheet.addRule(selector, "dummy:dummy");// forIE
		}
		return Class.getCssRule(selector);
	}

	/**
	 * セレクタにルールを設定。
	 * <li>当該セレクタのルールが存在しなければ作成する。
	 * 
	 * @param {string} selectror セレクタ文字列
	 * @param {object} スタイルのマッピング 例：{textAling: "center",…}
	 */
	Class.setCssRule = function(selector, style) {
		var rule = Class.getCssRuleWithDefine(selector);
		if (rule == null) return;
		for ( var k in style) {
			rule.style[k] = style[k];
		}
	}

	/**
	 * セレクタにルールを important 付きで設定。
	 * <li>当該セレクタのルールが存在しなければ作成する。
	 * 
	 * @param {string} selectror セレクタ文字列
	 * @param {object} スタイルのマッピング 例：{textAling: "center",…}
	 */
	Class.setCssRuleImportant = function(selector, style) {
		var rule = Class.getCssRuleWithDefine(selector);
		if (rule == null) return;
		for ( var k in style) {
			rule.style.setProperty(k, style[k], 'important');
		}
	}

})(CssLib);