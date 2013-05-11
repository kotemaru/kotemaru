

function Actions(){this.initialize.apply(this, arguments)};
(function(_class){
	
	var ACTIONS = {};
	$(function(){
		function config() {
			Actions.resetAction(true, 200);
			Dialog.open("#configDialog", Eclipse.preferences);
		}
		function undo() {
			EditBuffer.undo();
			Actions.resetAction(true, 200);
		}
		function redo() {
			EditBuffer.redo();
			Actions.resetAction(true, 200);
		}
		
		registerAction("cursor", new Action(null));
		registerAction("config", {selectMe:config});
		registerAction("remove", new RemoveAction(null));
		registerAction("undo", {selectMe:undo});
		registerAction("redo", {selectMe:redo});
	})
	function registerAction(name, action) {
		if (ACTIONS[name]) {
			Eclipse.log( "Duplicate registerAction "+name);
		}
		ACTIONS[name] = action;
	}
	_class.registerAction = registerAction;

	
	var currentIdx = "cursor";
	var isLock = false;
	
	_class.prototype.initialize = function(opts) {
	}
	
	_class.getAction = function() {
		return ACTIONS[currentIdx];
	}
	_class.setAction = function(idx) {
		currentIdx = idx;
		
		var $btns = $(".Action");
		var $btn = $(".Action[data-value='"+idx+"']");
		$btns.removeClass("Selected");
		$btns.removeClass("Locked");
		$btn.addClass("Selected");
		if (isLock) $btn.addClass("Locked");
		
		ACTIONS[currentIdx].selectMe();
	}
	_class.resetAction = function(isForce, delay) {
		if (!isLock || isForce) {
			setTimeout(function(){
				_class.setAction("cursor");
			},(delay?delay:10));
		}
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


	