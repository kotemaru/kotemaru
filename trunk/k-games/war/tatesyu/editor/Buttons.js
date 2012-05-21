

function Buttons(){this.initialize.apply(this, arguments)};
(function(Class) {
	const BORDER_ON  = "2px inset lightgray";
	const BORDER_OFF = "2px outset lightgray";

	Class.instance = null;

	Class.prototype.initialize = function($elem) {
		this.$elem = $elem;
		this.list = {};
		this.current = null;

		Class.instance = this;
	};
	
	Class.prototype.add = function(button) {
		const self = this;
		with (this) {
			list[button.name] = button;
			button.appendTo($elem);
			button.css({
				margin: "2px", padding: "2px",
				border: BORDER_OFF,
			});
			button.bind("click",function(ev){
				self.allOff();
				self.current = button;
				button.css({border: BORDER_ON});
			});
		};
	};
	Class.prototype.addSeparator = function() {
		this.$elem.append($("<hr/>"));
	}
	Class.prototype.remove = function(button) {
		with (this) {
			button.parge();
			delete list[button.name];
		}
	};

	Class.prototype.allOff = function(button) {
		for (var k in this.list) {
			var button = this.list[k];
			button.css({border: BORDER_OFF});
		}
	};
	
})(Buttons);





