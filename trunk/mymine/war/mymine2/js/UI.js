
function UI(){this.initialize.apply(this, arguments)};
(function(Class){
	Class.refresh = function() {
		var folderName = Folder.getCurrentName();
		if (Folder.isInbox()) {
			Folder.inbox();
		} else {
			if (event.shiftKey) {
				Folder.updateTickets(); // All
			} else {
				Folder.updateTickets(folderName);
			}
		}
	}
	Class.cleaning = function() {
		Ticket.cleaning();
	}

	Class.addFolder = function() {
		Dialog.open('#addFolderDialog');
	}

	Class.removeFolder = function() {
		if (window.confirm("本当にフォルダを削除しますか？")) {
			Folder.removeFolder();
		}
	}

	Class.search = function() {
		var kw = $("#searchKeyword").val();
		Folder.inboxOne(parseInt(kw));
	}
	Class.searchKeyPress = function(ev,_this) {
		if (ev.keyCode == 13) { // ReturnKey
			Class.search();
			return false;
		}
	}


	Class.saveConfig = function() {
		var config = {};
		for (var i=0; i<Config.NAMES.length; i++) {
			var name = Config.NAMES[i];
			config[name] = Config.getValues($(".Config input[name='"+name+"']"));
		}
		config["redmineCustomImg"] = Config.getValues($(".Config img.redmineCustomImg"));

		Config.save(config);
		Dialog.close();
	}

	Class.getProjects = function() {
		var config = {};
		for (var i=0; i<Config.NAMES.length; i++) {
			var name = Config.NAMES[i];
			config[name] = Config.getValues($(".Config input[name='"+name+"']"));
		}
		Config.save(config);
		Config.getProjects();
	}

	Class.setLeftWidth = function(w) {
		var px = w +"px";
		$("#leftPanel").css({width: px});
		$("#mainPanel").css({paddingLeft: px});
		$(".TFolder").css({width: px});
	}

	Class.saveAddFolder = function() {
		var $di = $("#addFolderDialog");
		var folder = {
			name:  $di.find("input[name='folderName']").val(),
			title: $di.find("input[name='folderTitle']").val(),
			icon:  $di.find("img#folderIcon").attr("src")
		};
		Folder.add(folder);
		Dialog.close();
		Folder.refresh();
	}

	Class.editFolder = function() {
		var folder = Folder.getCurrentFolder();
		var $di = $("#addFolderDialog");
		$di.find("input[name='folderName']").val(folder.name);
		$di.find("input[name='folderTitle']").val(folder.title);
		$di.find("img#folderIcon").attr("src", folder.icon);
		Dialog.open("#addFolderDialog");
	}


	Class.onScroll = function(_this,event) {

		var $this = $(_this);
		var $child = $this.find(">div");
		var scrollTop =  $this.scrollTop();
		var bottom = scrollTop+$this.height();
		if (bottom >= $child.height()) {
			var $section = $("#tickets");
			$section.bind("ticketsReload", function(){
				$this.scrollTop(scrollTop);
				console.log(scrollTop);
			});
			Folder.inboxAppend();
		}

	}
})(UI);
