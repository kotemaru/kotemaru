

function Actions(){this.initialize.apply(this, arguments)};
(function(_class){
	
	var ACTIONS = {
		"cursor": new Action(null),
		"config": new Action(null),
		"remove": new RemoveAction(null),
		
		"Class" : new Action(Class),
		"Object" : new Action(Object),
		"Note" : new Action(Note),
		"Cable" : new CableAction(Cable),
		//"Cable" : new CableAction(),
		"":null
	};
	
	var currentIdx = "cursor";
	var isLock = false;
	
	_class.prototype.initialize = function(opts) {
	}
	
	_class.getAction = function() {
		return ACTIONS[currentIdx];
	}
	_class.setAction = function(idx) {
		currentIdx = idx;
		ACTIONS[currentIdx].selectMe();
		
		var $btns = $(".Action");
		var $btn = $(".Action[data-value='"+idx+"']");
		$btns.removeClass("Selected");
		$btn.addClass("Selected");
	}
	_class.resetAction = function() {
		if (!isLock) _class.setAction("cursor");
	}

	function init() {
		var $btns = $(".Action");
		function onClick($this) {
			var value = $this.attr("data-value");
			_class.setAction(value);
			isLock = false;
		}
		
		$btns.bind("click",function(ev){
			onClick($(this));
		}).bind("dblclick",function(ev){
			onClick($(this));
			isLock = true;
		});
		
		$("#cablePulldownMark").bind("click",function(){
			_class.setAction("Cable");
		})
		
		
		$(".ActionGroupHeader>.OpenClose").bind("click",function(){
			var $this = $(this);
			var $tgt = $this.parent().parent().find(".ActionGroup");
			$tgt.toggle();
			this.src = $tgt.is(":visible") ? "img/pullup.png":"img/pulldown.png";

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


	