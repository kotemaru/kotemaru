

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

	Class.setValue = function(name, val) {
		checkButtons[name] = val
	}
	Class.getValue = function(name) {
		return checkButtons[name];
	}

	Class.setup = function() {
		setupCustomQuery();
		setupProjects();
		setupMasterTable();
	}
	function setupCustomQuery() {
		var querys = Config.redmineCustomQuery;
		if (querys == null) return;
		if (Config.redmineCustomImg == null) return;

		var $btns = $("#customQueryButtons");
		$btns.html("");
		for (var i=0; i<querys.length; i++) {
			if (Config.redmineCustomQuery[i] == "") continue;

			$btn = $("<img class='CheckButton' />");
			$btn.attr("id", "custom_"+i);
			$btn.attr("data-group", "custom");
			$btn.attr("src", Config.redmineCustomImg[i])
			$btn.attr("alt", Config.redmineCustomName[i]);
			$btns.append($btn);
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

	function setupMasterTable() {
		var masterTable = MasterTable.getMasterTable();
		if (masterTable == null) return;

		var $filters = $("#filterButtons").html("");
		for (var k in masterTable) {
			if (k.indexOf("cf_")==0) continue; // TODO:カスタムフィールドの扱い
			var $btn = Class.makePulldownButton("filter_"+k, masterTable[k]);
			$filters.append($btn);
		}
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

		if ($elem.attr("disabled")) return;

		var curFlag = checkButtons[elem.id];
		var group = $elem.attr("data-group");
		if (group) {
			var $group = $(".CheckButton[data-group='"+group+"']");
			$group.each(function(){
				Class.offCheckButton(this);
			})
			//Class.onCheckButton(elem);
		} //else {
			if (curFlag) {
				Class.offCheckButton(elem);
			} else {
				Class.onCheckButton(elem);
			}
		//}
		$elem.trigger("change", [checkButtons[elem.id], elem.id, group]);
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

	//-----------------------------------------------------
	// Pulldown Button functions
	//-----------------------------------------------------

	Class.makePulldownButton = function(id,opts) {
		var $elem = $("#templ_pulldownButton>span").clone();
		var $img = $elem.find("img:first-child");

		var icon = opts.icon;
		if (icon==null||icon=="") icon="img/funnel.png";
		$elem.attr("id", id);
		$img.attr("src", icon);
		$img.attr("alt", opts.name);

		var $menu = $elem.find(".PopupMenu").html("");
		var list = [];
		for (var k in opts.values) list.push({id:k, name:opts.values[k]});
		if (opts.keySort == "name") {
			list.sort(function(a,b){return a.name>b.name?1:-1;});
		} else if (opts.keySort == "id") {
			list.sort(function(a,b){return a.id-b.id;});
		}


		for (var i=0; i<list.length; i++) {
			var $item = $("<div class='PopupMenuItem' ></div>");
			$item.attr("data-value", list[i].id);
			$item.text(list[i].name);
			$menu.append($item);
		}
		return $elem;
	}

	//-----------------------------------------------------
	Class.customImg = [];
	Class.customName = [];
	Class.customQuery = [];

	Class.reconfig = function(){
		initProjects();
		initCustomQuery();
		initMasterTable();
	}
	$(function(){
		// Control
		$(".Button, .CheckButton, .PulldownButton>img:first-child").live("mouseover",function(ev){
			Control.popupBalloon($(this));
		}).live("mouseout",function(){
			Control.hideBalloon();
		});

		$(".CheckButton").live("click",function(ev){
			Control.toggleCheckButton(this);
		});
		$("#customQueryButtons > .CheckButton").live("change",function(ev, value, id, group){
			if (group == "custom" && Control.checkButtonGroup(group) >= 0) {
				$(".FilterButtons").addClass("Disabled");
			} else {
				$(".FilterButtons").removeClass("Disabled");
			}
			Folder.inbox();
		});
		// PulldownButton
		$(".PulldownButton").live("click", function(){
			var val = Control.getValue(this.id);
			if (val) {
				$(this).removeClass("PulldownButtonOn");
				Control.setValue(this.id, null);
			} else {
				var opts = {element: this, corrent:{x:0,y:6}};
				PopupMenu.open($(this).find(".PopupMenu")[0], opts);
			}
		});
		$(".PulldownButton .PopupMenuItem").live("click", function(){
			var val = $(this).attr("data-value");
			Control.setValue(PopupMenu.options.element.id, val);
			if (val) {
				$(PopupMenu.options.element).addClass("PulldownButtonOn");
			} else {
				$(PopupMenu.options.element).removeClass("PulldownButtonOn");
			}
			PopupMenu.close();
			return false;
		});

		// 空白削除
		function removeSpace(){
			if (this.nodeType==3) this.parentNode.removeChild(this);
		}
		$("#filterPack").contents().each(removeSpace);
		$("#filterPack>span").contents().each(removeSpace);
		$("#configPack").contents().each(removeSpace);
		
		Class.reconfig();
	});
	
	function initProjects() {
		new RedMine().getProjects(function(data){
			var projects = data.projects;
			
			var $sel = $("#projectSelector").html("<option value=''>*</option>");
			for (var i=0; i<projects.length; i++) {
				var project = projects[i];
				var $opt = $("<option/>");
				$opt.val(project.id);
				$opt.text(project.name);
				$sel.append($opt);
			}
			//$sel.val(Storage.loadData("projectSelector"));
		});
		
	}
	function initCustomQuery() {
		var $btns = $("#customQueryButtons");
		$btns.html("");
		for (var i=0; i<Class.customQuery.length; i++) {
			if (Class.customQuery[i] == "") continue;

			$btn = $("<img class='CheckButton' />");
			$btn.attr("id", "custom_"+i);
			$btn.attr("data-group", "custom");
			$btn.attr("src", Class.customImg[i])
			$btn.attr("alt", Class.customName[i]);
			$btns.append($btn);
		}
	}
	function initMasterTable() {
		var masterTable = MasterTable.getMasterTable();
		if (masterTable == null) return;

		var $filters = $("#filterButtons").html("");
		for (var k in masterTable) {
			if (k.indexOf("cf_")==0) continue; // TODO:カスタムフィールドの扱い
			var $btn = Class.makePulldownButton("filter_"+k, masterTable[k]);
			$filters.append($btn);
		}
	}
	Class.initMasterTable = initMasterTable;

})(Control);
