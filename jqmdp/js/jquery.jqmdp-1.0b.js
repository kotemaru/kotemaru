/*
* jQuery Mobile Dynamic Page plugin
*
* Copyright 2011 (c) kotemaru@kotemaru.org
* Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
*/
/*
 * Warn: My English is very doubtful.
 * Note: JQM and $.data() combination has a bug? 
 */
(function($) {
	var isDebug = false;

	var PRE = "dp-";
	var SCOPE = PRE+"scope";
	var SHOW  = PRE+"show";
	var SRC   = PRE+"src";
	var HREF  = PRE+"href";
	var HTML  = PRE+"html";
	var TEXT  = PRE+"text";
	var VALUE = PRE+"value";
	var TEMPLATE = PRE+"template";
	var IF    = PRE+"if";
	var IFSELF= PRE+"if-self";
	var FOR   = PRE+"for";

	var XP_SCOPE = "*["+SCOPE+"]";
	var XP_SHOW  = "*["+SHOW+"]";
	var XP_SRC   = "*["+SRC+"]";
	var XP_HREF  = "*["+HREF+"]";
	var XP_HTML  = "*["+HTML+"]";
	var XP_TEXT  = "*["+TEXT+"]";
	var XP_VALUE = "*["+VALUE+"]";
	var XP_TEMPLATE = "*["+TEMPLATE+"]";
	var XP_IF    = "*["+IF+"]";
	var XP_IFSELF= "*["+IFSELF+"]";
	var XP_FOR   = "*["+FOR+"]";
	
	var exTemplates = {};

	/**
	 * The function sets an event handler of jqmdp in all JQM Pages.
	 * @param root Usually appoint document.body.
	 */
	function init(root) {
		var $pages = $(root).find("div[data-role='page']");
		for (var i=0; i<$pages.length; i++) {
			setEventListener("#"+$pages[i].id);
		};
	}

	/**
	 * The function sets an event handler of jqmdp in a JQM Page.
	 * Suppoted events id init/beforeshow/show/hide.
	 * @param pageId Page element id.
	 */
	function setEventListener(pageId) {
		var $page = $(pageId);
		$page.live('pageinit', function(ev) {
			doScopes(ev, $page, onPageInit);
		}).live('pagebeforeshow', function(ev) {
			// The 'pagebeforeshow' event is accompanied by DynamicPage processing.
			doScopes(ev, $page, onBeforeShow, processPage);
		}).live('pageshow', function(ev) {
			doScopes(ev, $page, onShow);
		}).live('pagehide', function(ev) {
			doScopes(ev, $page, onHide);
		})
		;
	}

	/**
	 * A convenient function to transmit an event to the scope of subordinates.
	 * The exception captures it and displays warning.
	 * @param ev original event.
	 * @param $elem Page or other jQuery object.
	 * @param hander event handler.
	 * @param afterHander Finally the handler which is called once.
	 */
	function doScopes(ev, $elem, hander, afterHander) {
		try {
			hander(ev, $elem);
			$elem.find(XP_SCOPE).each(function(){
				hander(ev, $(this));
			});
			if (afterHander) afterHander($elem, ev);
		} catch(e) {
			// Because JQM stops when I throw an exception.
			console.error(e.stack);
			alert(e.message+"\n"+e.stack);
		}
	}

	/**
	 * Event handler of 'pageinit'.
	 * This is evaluate the scope attribute of descendant and stick it on an element.
	 * Because $.data() is a dune buggy, I set scope instance directly in Element.jqmdp_scope.
	 * @param ev original event.
	 * @param $elem Page or other jQuery object.
	 */
	function onPageInit(ev, $elem) {
		var scope = $elem.get(0).jqmdp_scope;
		if (scope !== undefined) return;
		
		var scopeSrc = $elem.attr(SCOPE);
		if (scopeSrc != null) {
			scope = localEval(scopeSrc, [{$this:$elem}]);
		}
		if (scope != null) {
			$elem.get(0).jqmdp_scope = scope;
			if (scope.onPageInit) {
				scope.onPageInit(ev,$elem);
			}
		}
	}
	
	/**
	 * If scope instance has the function of the handler, I call it.
	 * @param ev original event.
	 * @param $elem Page or other jQuery object.
	 * @param mname Hander function name.
	 */
	function onOther(ev, $elem, mname) {
		var scope = $elem.get(0).jqmdp_scope;
		if (scope && scope[mname]) {
			scope[mname](ev,$elem);
		}
	}
	function onBeforeShow(ev, $elem) {onOther(ev, $elem, "onBeforeShow");}
	function onShow(ev, $elem) {onOther(ev, $elem, "onShow");}
	function onHide(ev, $elem) {onOther(ev, $elem, "onHide");}


	/**
	 * Dynamic page attributes processes all scopes of the descendant of the page.
	 * The range of the descendant is removed from the DOM tree temporarily.
	 * After having handled it independently, each scope is put back.
	 * TODO: This implementation is not stylish.
	 * @param $page Page jQuery object.
	 */
	function processPage($page) {
		// Take off and backup scope elements.
		var backups = [];
		var scopes = [];
		var $locals = $page.find(XP_SCOPE);
		for (var i = 0; i < $locals.length; i++) {
			scopes.push(getScopes($locals[i]));	
		}
		for (var i = 0; i < $locals.length; i++) {
			backups.push($locals[i]);
			$($locals[i]).replaceWith("<div dp-backup-no="+i+">backup-"+i+"</div>");
		}

		// Processing DynamicPage attributs.
		process($page, getScopes($page.get(0)));
		for (var i = 0; i < scopes.length; i++) {
			try {
				process($(backups[i]), scopes[i]);
			} catch (e) {
				console.error(e.stack);
				alert(e+"\n"+e.stack);
			}
		}

		// Put back scope elements.
		var $marks = $page.find("div[dp-backup-no]");
		while ($marks.length > 0) {
			for (var i = 0; i < $marks.length; i++) {
				var $mark = $($marks[i]);
				var no = $mark.attr("dp-backup-no");
				$mark.replaceWith(backups[no]);
			}
			$marks = $page.find("div[dp-backup-no]");
		}
	}
	
	function getScopes(node) {
		var scopes = [];
		while (node != null && node != window) {
			if (node.jqmdp_scope) scopes.push(node.jqmdp_scope);
			node = node.parentNode;
		}
		return scopes;
	}

	/**
	 * DynamicPage attributes processing in one scope.
	 * 
	 * 
	 * 
	 * @param $elem Page or scope element jQuery object.
	 * @param scopes scope instance array.
	 */
	function process($elem, scopes) {
		// Predisposal to handle health of "if" and "for".
		preProcess($elem);
		
		// Various substituted processing.
		$elem.find(XP_SHOW).each(function(){
			var $e = $(this);
			var bool = localEval($e.attr(SHOW), scopes);
			bool ? $e.show() : $e.hide();
		});
		$elem.find(XP_SRC).each(function(){
			var $e = $(this);
			$e.attr("src",localEval($e.attr(SRC), scopes));
		});
		$elem.find(XP_HREF).each(function(){
			var $e = $(this);
			$e.attr("href",localEval($e.attr(HREF), scopes));
		});
		$elem.find(XP_VALUE).each(function(){
			var $e = $(this);
			$e.val(localEval($e.attr(VALUE), scopes));
		});
		$elem.find(XP_TEXT).each(function(){
			var $e = $(this);
			$e.text(""+localEval($e.attr(TEXT), scopes));
		});
		$elem.find(XP_HTML).each(function(){
			var $e = $(this);
			$e.html(localEval($e.attr(HTML), scopes));
		});
		$elem.find(XP_TEMPLATE).each(function(){
			var $e = $(this);
			$.jqmdp.template($e, $($e.attr(TEMPLATE)));
		});

		// Control sentence structure processing.
		processCond($elem, XP_IF, "if", IF, scopes);
		processCond($elem, XP_IFSELF, "if", IFSELF, scopes);
		processCond($elem, XP_FOR, "for", FOR, scopes);
		
		return $elem;
	}
	
	/**
	 * Control sentence structure processing.
	 * The control sentence is composed as character string and is execute by eval() function.
	 * The body of the control sentence is stored by preprocessing.
	 * Behavior of "if" or "for" is realized by adding the clone to DOM tree when a 
	 * control sentence is  carried out.
	 * 
	 * @param $parent   Page or scope element jQuery object.
	 * @param xpath     XPath to search an attribute.
	 * @param cmd       Control sentence token "if" or "for".
	 * @param attr		Attribute name.
	 * @param scope     scope instance.
	 */
	function processCond(_$parent, _xpath, _cmd, _attr, _scopes) {
		_$parent.find(_xpath).each(function(){
			var _$elem = $(this);
			var _$body = this.jqmdp_body;
			if (isDebug) console.log(_cmd+"-body="+_$body.html());

			_$elem.html("");
			var _script = _cmd+_$elem.attr(_attr)+"{"
				+"_processClone(_$elem, _$body, _scopes);"
				+"}"
			;
			if (_attr == IFSELF) {
				_script += "else {_$elem.remove();}";
			}
			if (isDebug) console.log("cond-Eval:"+_script);
			eval(wrapScopes(_script, _scopes));
		});
	}

	/**
	 * The body of the control sentence is reproduced and is added to an element.
	 * The original body must not be modified.
	 * 
	 * @param $elem   scope element jQuery object.
	 * @param $body   Stored Control sentence.
	 * @param scopes   scope instance array.
	 */
	function _processClone($elem, $body, scopes) {
		var $clone = $body.clone();
		process($clone, scopes);
		$elem.append($clone.contents());
	}


	/**
	 * JavaScript character string is execute in scope instance.
	 * 
	 * @param _script  javascript code string.
	 * @param _scopes  scope instance array.
	 * @return result value of javascript code.
	 */
	function localEval(_script, _scopes){
		if (isDebug) console.log("localEval:"+_script);
		var _res;
		if (_scopes.length == 0) {
			_res = eval(_script);
		} else if (_scopes.length == 1) {
			with (_scopes[0]) {
				_res = eval(_script);
			}
		} else {
			if (isDebug) console.log("localEval:::"+_script);
			_res = eval(wrapScopes(_script, _scopes));
		}
		if (isDebug) console.log("localEval=" + _res);
		return _res;
	}
	
	/**
	 * Wrapping scopes javascript code.
	 * @param script  javascript code string.
	 * @param scopes  scope instance array.
	 * @return result Wrapping scopes javascript code.
	 */
	function wrapScopes(script, scopes) {
		for (var i=0; i<scopes.length; i++) {
			script = "with(_scopes["+i+"]){"+script+"}";
		}
		if (isDebug) console.log("wrapScopes:"+script);
		return script;	
	}
	
	/**
	 * The predisposal of the control sentence structure.
	 * if and for suppot.
	 * @param $elem   scope element jQuery object.
	 */
	function preProcess($elem){
		preProcess0($elem, XP_FOR);
		preProcess0($elem, XP_IF);
		preProcess0($elem, XP_IFSELF);
		return $elem;
	}
	
	/**
	 * The predisposal of the control sentence structure.
	 * Subroutine.
	 * It is cut, and the body is stored.
	 * The control sentence structure will have an empty body.
	 * @param $elem   scope element jQuery object.
	 * @param xpath   XPath to search an attribute.
	 */
	function preProcess0($elem, xpath) {
		$elem.find(xpath).each(function(){
			if (this.jqmdp_body == null) {
				var $body = $(this).clone();
				this.jqmdp_body = $body;
			}
		})
		$elem.find(xpath).html("");
	}
	
	/**
	 * Ancestors element having the nearest scope is returned.
	 * @param elem   Any element or jQuery object.
	 * @return scope element.
	 */
	function getScopeNode(elem) {
		var $this = elem.length ? elem : $(elem);
		while ($this != null && $this.get(0) != window) {
			if ($this.attr(SCOPE)) return $this;
			$this = $this.parent();
		}
		return null;
	}

	$.jqmdp = function Jqmdp(){};
	$.jqmdp.getScopeNode = getScopeNode;
	
	/**
	 * The scope instance to belong to of the element is returned.
	 * If a value is appointed, I replace scope instance.
	 * @param elem   Any element or jQuery object.
	 * @param val    scope instance.
	 * @return scope instance or $this.
	 */
	$.jqmdp.scope = function($this, val) {
		var $scopeNode = getScopeNode($this);
		if ($scopeNode == null) return null;
		if (val) {
			$scopeNode.get(0).jqmdp_scope = val;
			return $this;
		} else {
			if (undefined === $scopeNode.get(0).jqmdp_scope) {
				doScopes(null, $this, onPageInit);
			}
			return $scopeNode.get(0).jqmdp_scope;
		}
	}

	/**
	 * A supporting function to make a part.
	 * The reproduction which applied conversion handling of JQM is added.
	 * @param $this  template target jQuery object.
	 * @param $src   Template jQuery object.
	 */
	$.jqmdp.template = function($this, $src) {
		$src.page();
		$this.append($src.clone().contents());
		return $this;
	}
	/**
	 * An outside template is applied.
	 * Because it is load by async, the outside template may be behind with the real application.
	 * If it has been already loaded, it is applied immediately.
	 * @param $this  template target jQuery object.
	 * @param url    outside template url.
	 */
	$.jqmdp.exTemplate = function($this, url) {
		if (exTemplates[url] === undefined) {
			exTemplates[url] = {q:[$this]};
			$.get(url, null, function(data){onLoadTempl(data,url);});
		} else if (exTemplates[url].node === undefined) {
			exTemplates[url].q.push($this);
		} else {
			$.jqmdp.template($this, exTemplates[url].node);
		}
		return $this;
	}
	function onLoadTempl(data, url) {
		var $t = $(data);
		$(document.body).append($t); // JQM requires it.
		exTemplates[url].node = $t;
		var q = exTemplates[url].q;
		for (var i=0; i<q.length; i++) {
			$.jqmdp.template(q[i], $t);
			$.jqmdp.refresh(q[i]);
		};
	}
	
	/**
	 * The inside of the scope is drawn again.
	 * @param $this  Any jQuery object.
	 */
	$.jqmdp.refresh = function($this, delay) {
		if (delay == null) {
			processPage(getScopeNode($this));
			return;
		}

		var $elem = getScopeNode($this);
		var elem = $elem.get(0);
		elem.jqmdp_refresh = (elem.jqmdp_refresh==null) ? 1 : elem.jqmdp_refresh++;

		setTimeout(function(){
			if (--(elem.jqmdp_refresh) > 0) return;
			processPage($elem);
		}, delay);

		return $this;
	}
	/**
	 * Scope instance can call this function before 'pageinit' event, if necessary.
	 * @param $this  Any jQuery object.
	 */
	$.jqmdp.init = function($this) {
		doScopes(null, $this, onPageInit);
		return $this;
	}
	/**
	 * debug mode on/off;
	 */
	$.jqmdp.debug = function(b) {
		isDebug = b;
	}
	
	/**
	 * The relative path from a JavaScript source file is converted into an absolute path.
	 * @param path relative path
	 */
	$.jqmdp.absPath = function(path) {
		if (!(path.match(/^\//) || path.match(/^https?:/i))) {
			var scripts = document.getElementsByTagName("script");
			path = (scripts[scripts.length-1].src).replace(/[^\/]*$/,path);
		}
		return path;
	}
	

	/**
	 * A bridge function.
	 * @param method  $.jqmdp.* function name string.
	 * @param a?      Any arguments.
	 */
	$.fn.jqmdp = function(method,a1,a2,a3,a4,a5,a6){
		if (this.get(0) === window) {
			alert("JQMDP alert!\nThis is window. \nhref='javascript:$(this)' is not usable.\nPlease use onclick.");
			return;
		}
		return $.jqmdp[method](this,a1,a2,a3,a4,a5,a6);
	}

	// Auto init.
	// TODO: $(document).bind("mobileinit", function(){init(document.body);});
	if ($.mobile != null) alert("You must load 'jqmdp' before than 'jQuery mobile'.");
	$(function(){init(document.body);});

})(jQuery);
//EOF.