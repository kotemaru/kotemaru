

function Control(){this.initialize.apply(this, arguments)};
(function(Class){
	Class.prototype.initialize = function() {
	}

	var checkButtons = {
		filter_user: false
	};

	Class.userId = null;
	Class.checkButtons = checkButtons;

	Class.init = function() {
		new RedMine().getCurrentUser(function(data){
			Class.userId = data.user.id;
		});

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

	Class.popupBalloon = function($button) {
		var alt = $button.attr("alt");
		var $balloon = $("#balloon");
		$balloon.offset({top:0,left:0}).css("display","inline-block").html(alt);

		var offset = $button.offset();
		offset.top += $button.width() + 8 ;
		offset.left += $button.height() + 8;
		if (offset.left+$balloon.width()>$(document.body).width()) {
			offset.left = $(document.body).width()-$balloon.width()-12;
		}
		$balloon.offset(offset);
	}
	Class.hideBalloon = function() {
		$("#balloon").hide();
	}

	Class.toggleCheckButton = function(elem) {
		var $elem = $(elem);
		checkButtons[elem.id] = !checkButtons[elem.id];
		$elem.removeClass("CheckButtonOn");
		if (checkButtons[elem.id]) {
			$elem.addClass("CheckButtonOn")
		}
	};


})(Control);
