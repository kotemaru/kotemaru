

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
	});
	
	Eclipse.preferences = { // for DDEBUG
		directionsBalloon: true,
		lineRouteDefault: "N",
		fontFamily: "arial,sans-serif"
	};	
	Eclipse.setContentUrl = function(url) {
		$.ajax({type:"GET", url:url, cache:false,
			success: function(xmlDoc){
				try {
					var data = xmlDoc.getElementById("umldraw-data").childNodes[0].nodeValue;
					Store.load(JSON.parse(data));
					EditBuffer.init();
				} catch (e) {
					alert("Bad data: "+e);
				}
			},
			error: function(xreq,state,err){alert("content loader:"+url+"\n"+err);},
			dataType: "xml"
		});
	};
	Eclipse.getContent = function() {
		return Canvas.toSVG();
	};
	Eclipse.startup = function(pref) {
		Eclipse.preferences = pref;
		Debug.disable();
	};
	Eclipse.print = function() {
		setTimeout(function(){
		$("#borderLayoutMain").hide();
		$("#borderLayoutLeft").hide();
		
		Eclipse.log("printer");
		var $print = $("#printer");
		$print[0].innerHTML = (Canvas.toSVG());
		$print.show();
		//$print.width(100);
		//$print.height(100);
			window.print();	
		},10);
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
	
	var contents =[];
	Eclipse.openContentBase64 = function() {
		contents.length = 0;
	};
	Eclipse.addContentBase64 = function(base64) {
		contents.push(Base64.decode(base64));
	};
	Eclipse.closeContentBase64 = function() {
		var xml = contents.join();
		contents.length = 0;
		var xmlParser = new DOMParser();
 		var xmlDoc = xmlParser.parseFromString(xml,"text/xml");
		var data = xmlDoc.getElementById("umldraw-data").childNodes[0].nodeValue;
		Store.load(JSON.parse(data));
		EditBuffer.init();
	};
	Eclipse.failContentBase64 = function(msg) {
		alert(msg);
		contents.length = 0;
	};
	
})(Editor);


//EOF
