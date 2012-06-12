
(function($, undefined) {

	const NAVI_CSS = {
		diaplay: "block",
		listStyleType: "none",
		margin: 0, padding: 0,
		height: "28px"
	};

	const TAB_CSS = {
		diaplay: "inline-block",
		cursor: "pointer",
		font: "12px bold sans-serif",
		float:"left",
		//width: 100px,
		height: "24px",
	  	margin: "0px 0px",
	  	padding: "2px 8px",
	};
	const TAB_UNSELECT_CSS = {
		background: "#ddd",
		border:    "1px solid #fff",
		borderTop: "2px solid #fff",
		height: "21px",
	};
	const TAB_SELECT_CSS = {
		background: "#f0f0f0",
		border:       "1px solid #f0f0f0",
		borderLeft:   "1px solid white",
		borderRight:  "1px solid #cccccc",
		height: "22px",
	};
	const PANEL_CSS = {
		background: "#f0f0f0",
		padding: "4px"
	};

	function TabPanel(){this.initialize.apply(this, arguments)};
	(function(Class, Super) {
		//Class.prototype = $.extend(Class.prototype, Super.prototype);
	
		Class.prototype.initialize = function($elem) {
			//Super.prototype.initialize.apply(this, arguments);
			const self = this;
			
			self.$elem = $elem;
			self.tabs = {};
			self.panels = {};

			var $ul = $elem.find("> ul");
			$ul.css(NAVI_CSS);
			$elem.find("div[data-tab]").each(function(){
				const $this = $(this);
				const tabName = $this.attr("data-tab");
				const tabIcon = $this.attr("data-tab-icon");
				self.panels[tabName] = $this;
				$this.css(PANEL_CSS);
				
				self.tabs[tabName] = self.makeTabLi(tabName, tabIcon);
				$ul.append(self.tabs[tabName]);
			});
		}
		
		Class.prototype.select = function(name) {
			with (this) {
				for (var k in tabs) {
					tabs[k].css(TAB_UNSELECT_CSS);
					panels[k].hide();
				}
				tabs[name].css(TAB_SELECT_CSS);
				panels[name].show();
			}
		}
		
		Class.prototype.makeTabLi = function(name, icon) {
			const self = this;
			var $li = $("<li></li>");
			if (icon) {
				var $icon = $("<img src='"+icon+"'/>");
				$icon.css({verticalAlign:"text-bottom"});
				$li.append($icon);
			}
			$li.append($("<b> "+name+"</b>"));

			$li.css(TAB_CSS);
			$li.bind("click", function(){
				self.select(name);
			});
			return $li;
		}

	})(TabPanel);
	

	function tabPanel($this) {
		var tabPanel = $this.data("tabPanel");
		if (!tabPanel) {
			tabPanel = new TabPanel($this);
			$this.data("tabPanel", tabPanel);
		}
		return $.extend($this, {
			select: function(name){tabPanel.select(name); return $this;}
		});
	}
	
	//-------------------------------------------------------------------------
	// jQuery extends.
	$.extend({tabPanel: tabPanel});
	$.fn.extend({
		tabPanel: function(a1,a2,a3,a4,a5){return tabPanel(this, a1,a2,a3,a4,a5);},
	});

})(jQuery);
//EOF.