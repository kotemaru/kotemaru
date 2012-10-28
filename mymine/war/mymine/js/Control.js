

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
		setupCustomQuery();
		setupProjects();
	}
	function setupCustomQuery() {
		var querys = Config.redmineCustomQuery;
		if (querys == null) return;

		var $btns = $("#customQueryButtons>img");
		for (var i=0; i<querys.length; i++) {
			$btn = $($btns[i]);
			$btn.attr("id", "custom_"+i);
			$btn.attr("data-group", "custom");
			$btn.attr("alt", Config.redmineCustomName[i]);
		}
	}
	
	function setupProjects() {
		var projects = Config.redmineProjectId;
		if (projects == null) return;

		var $sel = $("#projectSelector").html("<option value=''>*</option>");
		for (var i=0; i<projects.length; i++) {
			var opt = "<option value='"+projects[i]+"'>"
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

	//-----------------------------------------------------
	// Check Button functions
	//-----------------------------------------------------
	Class.toggleCheckButton = function(elem) {
		var $elem = $(elem);
		var group = $elem.attr("data-group");
		if (group) {
			var $group = $(".CheckButton[data-group='"+group+"']");
			$group.each(function(){
				Class.offCheckButton(this);
			})
			Class.onCheckButton(elem);
		} else {
			if (checkButtons[elem.id]) {
				Class.offCheckButton(elem);
			} else {
				Class.onCheckButton(elem);
			}
		}
	};
	Class.offCheckButton = function(elem) {
		checkButtons[elem.id] = false;
		$(elem).removeClass("CheckButtonOn");
	}
	Class.onCheckButton = function(elem) {
		checkButtons[elem.id] = true;
		$(elem).addClass("CheckButtonOn");
	}
	Class.checkButtonGroup = function(group) {
		// TODO:手抜き
		for (var i=0; i<10; i++) {
			if (checkButtons[group+"_"+i]) return i;
		}
		return -1;
	}

})(Control);
