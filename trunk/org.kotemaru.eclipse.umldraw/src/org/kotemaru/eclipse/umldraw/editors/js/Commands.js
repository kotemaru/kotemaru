

function Commands(){this.initialize.apply(this, arguments)};
(function(_class){
	
	var COMMANDS = 
	[
	 	{icon:"cursor.png", action: new Action(null)},
	 	{icon:"Class.png",  action: new Action(Class)},
	 	{icon:"Cable.png",  action: new Action(Cable)},
	];
	
	var currentIdx = 0;
	
	_class.prototype.initialize = function(opts) {
	}
	
	_class.getAction = function() {
		return COMMANDS[currentIdx].action;
	}
	_class.setAction = function(idx) {
		currentIdx = idx;
		COMMANDS[currentIdx].action.commandSelect();
	}
	

	function init() {
		var $parent = $("#commands");
		for (var i=0; i<COMMANDS.length; i++) {
			$parent.append("<span class='Command' data-idx='"
					+i+"'><img src='img/"
					+COMMANDS[i].icon+"'/></span>");
		}
		
		var $btns = $(".Command");
		$btns.bind("click",function(ev){
			_class.setAction($(this).attr("data-idx"));
			$btns.removeClass("Selected");
			$(this).addClass("Selected");
		});
	}	

	$(function(){
		init();
	});
		
})(Commands);


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


	