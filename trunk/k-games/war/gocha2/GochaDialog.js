/**
@author kotemaru@kotemaru.org
*/

enchant.gocha.GochaDialog = org.kotemaru.Class(enchant.Entity, function(_class, _super){
	var Util = org.kotemaru.Util;

	_class.open = function(parent, opts) {
		var dialog = new _class(parent, opts);
		parent.addChild(dialog);
	}
	
	
	_class.prototype.initialize = function(parent, opts) {
		_super.prototype.initialize.call(this);
		this.parent = parent;
		
		var w = opts.w?opts.w:256;
		var h = opts.h?opts.h:128;
		
		var html = "";
		if (opts.title) {
			var style = "text-align: center;"
				+"font: bold 16px sans-serif;"
				+"background: #333;"
				+"padding-top: 4px;";
			html += "<div style='"+style+"'>"+opts.title+"</div>";
		}
		if (opts.message) {
			html += "<div style='font:12px;margin:8px;'>"+opts.message+"</div>";
		}
		html += "<div style='text-align:center;position:absolute;top:100px;width:100%;'>";
		if (opts.buttons) {
			var btnw = (w-16)/opts.buttons.length+"px";
			for (var i=0; i<opts.buttons.length; i++) {
				html += "<button data-id='"+i+"' style='font:bold 12px sans-serif;width:"+btnw+";'>"
							+opts.buttons[i].label+"</button> ";
			}
		}
		if (opts.ok) {
			html += "<button data-id='ok' style='font:12px;width:100px;'>OK</button> ";
		}
		if (opts.cansel) {
			html += "<button data-id='cansel' style='font:12px;width:100px;'>Cansel</button> ";
		}
		html += "</div>";

		this.x = (320/2)-(w/2);
		this.y = (320/2)-(h/2);
		this.width = w;
		this.height = h;
		this.opacity = 0.8;
		
		this._style.zIndex = 1000;
		this._style.color = "white";
		this._style.background = "black";
		this._style.border = "1px solid white";

		this._element.innerHTML = html;

		// ボタンクリックイベント登録。
		var self = this;
		var buttons = this._element.getElementsByTagName("button");
		for (var i=0; i<buttons.length; i++) {
			function onclick(ev) {
				self.parent.removeChild(self);
				var id = ev.target.dataset.id;
				if (id == "ok") {
					opts.ok();
				} else if (id == "cansel") {
					opts.cansel();
				} else if (id) {
					opts.buttons[id].handler();
				}
				//self.parent.removeChild(self);
				ev.stopPropagation();
			}
			buttons[i].onclick = onclick;
			buttons[i].ontouchstart = onclick;
		}
	}
	
});
