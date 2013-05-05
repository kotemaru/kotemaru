

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
				var data = xmlDoc.getElementById("umldraw-data").childNodes[0].nodeValue;
				Store.load(JSON.parse(data));
				EditBuffer.init();
			},
			error: function(xreq,state,err){alert(err);},
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
	
})(Editor);


//EOF
