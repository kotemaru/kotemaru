

function Control(){this.initialize.apply(this, arguments)};
(function(Class){
	Class.prototype.initialize = function() {
	}
	Class.init = function() {

		$(".Button").live("mouseover",function(ev){
			var $this = $(this);
			var alt = $this.attr("alt");
			var $balloon = $("#balloon");
			$balloon.offset({top:0,left:0}).css("display","inline-block").text(alt);

			var offset = $this.offset();
			offset.top += 20;
			offset.left += 20;
			if (offset.left+$balloon.width()>$(document.body).width()) {
				offset.left = $(document.body).width()-$balloon.width()-12;
			}
			$balloon.offset(offset);

		}).live("mouseout",function(){
			$("#balloon").hide();
		});

		$("#projectSelector").bind("change", function() {
			$this = $(this);
			Folder.inbox();
			Storage.saveData("projectSelector", $this.val());
		})


		Class.setup();
	}

	Class.setup = function() {
		var querys = Config.redmineProjectId;
		if (querys == null) return;

		var $sel = $("#projectSelector").html("");
		for (var i=0; i<querys.length; i++) {
			var opt = "<option value='"+querys[i]+"'>"
				+Config.redmineProjectName[i]
				+"</option>"
			$sel.append($(opt));
		}
		$sel.val(Storage.loadData("projectSelector"));
	}



})(Control);
