

function Editor(){this.initialize.apply(this, arguments)};
(function(_class){
	$(function(){
		Eclipse.fireEvent("load");
		Actions.resetAction(true);
	
		$(".Dialog").live("saved",function(ev, targetItem){
			if (targetItem.isCanvasItem) {
				EditBuffer.notice().backup();
			}
		});
		$("#meshImg").hide();
		$("#printer").live("click",function(){
			$(".BorderLayoutFrame").show();
			$(this).hide();
		})
	});
	
	Eclipse.preferences = { // for DDEBUG
		directionsBalloon: true,
		lineRouteDefault: "N",
		fontFamily: "arial,sans-serif"
	};	
	Eclipse.getContent = function() {
		return Canvas.toSVG();
	};
	Eclipse.startup = function(pref) {
		Eclipse.preferences = pref;
		Debug.disable();
	};
	Eclipse.print = function() {
		Eclipse.log("printer");
		var $print = $("#printer");
		$print[0].innerHTML = (Canvas.toSVG());
		$(".BorderLayoutFrame").hide();
		$print.show();
		window.print();	
		$(".BorderLayoutFrame").show();
		$print.hide();
	};
	
	Eclipse.undo = function() {
		EditBuffer.undo();
	};
	Eclipse.redo = function() {
		EditBuffer.redo();
	};
	Eclipse.config = function() {
		Dialog.open("#configDialog", Eclipse.preferences);
	};
	
	Eclipse.setContent = function(content) {
		var xmlParser = new DOMParser();
 		var xmlDoc = xmlParser.parseFromString(content,"text/xml");
		var data = xmlDoc.getElementById("umldraw-data").childNodes[0].nodeValue;
		Store.load(JSON.parse(data));
		EditBuffer.init();
	};
	
	Eclipse.getClipboard = function() {
		var data = EditBuffer.getCopyBufferForExport();
		return JSON.stringify(data);
	};
	
	Eclipse.setClipboard = function(base64) {
		var json = Base64.decode(base64);
		Eclipse.log(json);
		var data = JSON.parse(json);
		data.isExternal = true;
		EditBuffer.setCopyBuffer(data);
	};
	
})(Editor);


//EOF
