

function Debug(){this.initialize.apply(this, arguments)};
(function(_class){

	_class.prototype.initialize = function(targetClass) {
	}
	_class.disable = function() {
		$("#debugActionGroup").hide();
	}
	function save() {
		var data = Canvas.toSVG();
		
		$.ajax({
			"url" : "/webdav/test.udr",
			"data" : data,
			"type" : "PUT",
			"cache" : false,
			"success" : function(){alert("OK");},
			"error" : function(e){alert(e);},
			"contentType" : "application/octet-stream"
		});
		Actions.resetAction(true);
/*
		Dialog.open("#debugDialog", data);
		var data = Store.save(Canvas.getItems());
		$("#saveText").val(JSON.stringify(data,null, "\t"));
		Store.load(data);
		Actions.resetAction(true);
*/
	}
	function load() {
		Eclipse.setContentUrl("/webdav/test.udr");
		Actions.resetAction(true);
	}
	function print() {
		var win = window.open("","SVG");
		win.document.body.innerHTML = Canvas.toSVG();
		Actions.resetAction(true);
		
		/*
		Dialog.open("#svgDialog", data);
		var data = {svg: Canvas.toSVG()};
		var ifr = $("#iframeSvg")[0];
		ifr.contentDocument.body.innerHTML = data.svg;
		Actions.resetAction(true);
		*/
	}

	$(function(){
		Actions.registerAction("load", {selectMe:load});
		Actions.registerAction("save", {selectMe:save});
		Actions.registerAction("print", {selectMe:print});
	});
	
	
})(Debug);


//EOF
