

function Debug(){this.initialize.apply(this, arguments)};
(function(_class){

	_class.prototype.initialize = function(targetClass) {
	}
	_class.disable = function() {
		$("#debugActionGroup").hide();
	}

	var saveCount=0;
	var currentCount=0;
	_class.change = function(count) {
		currentCount=count;
		var $btn = $("span.Action[data-value='save']");
		if (saveCount == currentCount) {
			$btn.addClass("Disabled");
		} else {
			$btn.removeClass("Disabled");
		}
	}
	
	function save() {
		var data = Canvas.toSVG();
		$.ajax({
			"url" : "/webdav/test.udr",
			"data" : data,
			"type" : "PUT",
			"cache" : false,
			"success" : function(){},
			"error" : function(xhr,st,e){alert(e);},
			"contentType" : "text/plain; charset=utf8"
		});
		Actions.resetAction(true);
		
		saveCount = currentCount;
		$("span.Action[data-value='save']").addClass("Disabled");
	}
	function load() {
		$.ajax({
			"url" : "/webdav/test.udr",
			"type" : "GET",
			"cache" : false,
			"success" : function(data){Eclipse.setContent(data);},
			"error" : function(xhr,st,e){alert(e);},
			"contentType" : "text/plain; charset=utf8"
		});
		Actions.resetAction(true);
	}
	function print() {
		Eclipse.print();
		Actions.resetAction(true);
	}

	$(function(){
		Actions.registerAction("load", {selectMe:load});
		Actions.registerAction("save", {selectMe:save});
		Actions.registerAction("print", {selectMe:print});
	});
	
	
})(Debug);


//EOF
