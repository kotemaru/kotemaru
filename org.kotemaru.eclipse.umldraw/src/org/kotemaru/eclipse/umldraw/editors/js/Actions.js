

function Actions(){this.initialize.apply(this, arguments)};
(function(_class){
	
	var ACTIONS = {
		"cursor": new Action(null),
		"config": new Action(null),
		"remove": new RemoveAction(null),
		
		"Class" : new Action(Class),
		"Cable" : new CableAction(Cable),
		//"Cable" : new CableAction(),
		"":null
	};
	
	var currentIdx = "cursor";
	
	_class.prototype.initialize = function(opts) {
	}
	
	_class.getAction = function() {
		return ACTIONS[currentIdx];
	}
	_class.setAction = function(idx) {
		currentIdx = idx;
		ACTIONS[currentIdx].selectMe();
	}

	function init() {
		var $btns = $(".Action");
		$btns.bind("click",function(ev){
			var $this = $(this);
			var value = $this.attr("data-value");

			$btns.removeClass("Selected");
			_class.setAction(value);
			$this.addClass("Selected");
			
		});
	}	

	$(function(){
		init();
	});
		
})(Actions);


/*
"Call.png",
"Disk.png",
"IF.png",
"Incall.png",
"Life.png",
"Note.png",
"Object.png",
"Package.png",
"Point.png",
"Return.png",
"State.png",
"User.png",
"delete.png",
"line1.png",
"line10.png",
"line11.png",
"line2.png",
"line3.png",
"line4.png",
"line5.png",
"line6.png",
"line7.png",
"line8.png",
"line9.png",
*/


	