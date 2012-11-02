
function UI(){this.initialize.apply(this, arguments)};
(function(Class){
	var C_TICKET = ".Ticket";

	Class.init = function() {
		// MyMine
		$(document.body).bind("mouseup",function(){
			setTimeout(function() {
				MyMine.isDrag(false);
				MyMine.setDragCursor();
			}, 50);
			Folder.endDrag();
			SlideHandle.endDrag();
			PopupMenu.close();
		}).bind("mousemove", function(ev){
			SlideHandle.move(ev);
		});

		// SlideHandle
		$("#leftSlideHandle").bind("mousedown", function(ev){
			SlideHandle.startDrag(this, function(ev) {
				var px = (ev.clientX+1) +"px";
				$("#leftPanel").css({width: px});
				$("#mainPanel").css({paddingLeft: px});
				$(".TFolder").css({width: px});
			});
			return false;
		})
		$(".HeaderSlideHandle").bind("mousedown", function(ev){
			SlideHandle.startDrag(this);
			return false;
		})

		// Control
		$(".Button, .CheckButton").live("mouseover",function(ev){
			Control.popupBalloon($(this));
		}).live("mouseout",function(){
			Control.hideBalloon();
		});

		$(".CheckButton").live("click",function(ev){
			Control.toggleCheckButton(this);
		});
		$("#customQueryButtons > .CheckButton").live("change",function(ev, value, id, group){
			if (group == "custom") {
				$("#filterButtons > .CheckButton").attr("disabled", value);
			}
			Folder.inbox();
		});


		// Folder
		$(".Folder").live("mouseup",function(){
			if (MyMine.isDrag()) {
				Folder.dropTicket(this.id);
			}
			MyMine.isDrag(false);
		}).live("mousedown", function(){
			Folder.startDrag(this);
			return false;
		}).live("mousemove", function(){
			if (Folder.isDrag()) {
				$(".Folder").css({cursor:"row-resize"});
			}
		}).live("mouseover", function(ev){
			Folder.hover(true, this);
			Folder.moveDrag(this, ev)
		}).live("mouseout", function(ev){
			Folder.hover(false, this);
		});

		// Ticket
		var draggable = null;
		$(C_TICKET).live("mousedown",function(ev){
			MyMine.isDrag(true);
			draggable = this;
			return false;
		}).live("mousemove",function(ev){
			if (draggable == this) {
				MyMine.setDragCursor();
				if (MyMine.isDrag()) {
					Tickets.addSelection(this.dataset.ticketNum);
				}
			}
		}).live("mouseout",function(ev){
			if (draggable == this) {
				draggable = null;
			}
		}).live("mouseup",function(ev){
			if (draggable == this) {
				if (!ev.ctrlKey) Tickets.clearSelection();
				Tickets.toggleSelection(this.dataset.ticketNum);
				Tickets.refresh();
				draggable = null;
			}
		}).live("dblclick",function(ev){
			var num = this.dataset.ticketNum;
			RedMine.openIsuue(num);
			Ticket.checked(num);
			Tickets.refresh();
			Folder.refresh();
		});

		// Header
		$(".THead").live("click", function(){
			Tickets.setSorted(this.id);
			Folder.refresh();
		}).live("mousedown", function(){
			//return false;
		})

		// PopupMenu
		$(".SelectableIcon").live("click", function(){
			PopupMenu.open("#iconSelectMenu", {element: this});
		});
		$("#iconSelectMenu>.PopupMenuItem").live("click", function(){
			$img = $(PopupMenu.options.element);
			$img.attr("src", this.src);
			PopupMenu.close("#iconSelectMenu");
		});
		PopupMenu.makeIconMenu("#iconSelectMenu", "icons.txt");


		// 空白削除
		$("#buttons2").contents().each(function(){
			if (this.nodeType==3) this.parentNode.removeChild(this);
		});
		$("#buttons2>span").contents().each(function(){
			if (this.nodeType==3) this.parentNode.removeChild(this);
		});

	}



	//-------------------------------------------------------------
	Class.abort = function() {
		Dialog.open("#abortDialog");
	}

	Class.changeProject = function(_this) {
		$this = $(_this);
		Folder.inbox();
		Storage.saveData("projectSelector", $this.val());
	}

	Class.download = function() {
		var str = Storage.getDownloadString();
		var $a = $("#downloadLink");
		$a.attr("href","data:application/octet-stream,"+encodeURIComponent(str));
		Dialog.open("#downloadDialog");
	}

	Class.upload = function() {
		var $file = $("#uploadFile");
		$file.show().bind("change", function(){
			var reader = new FileReader();
			reader.onload = function(e) {
				Storage.setUploadString(reader.result);
				Control.setup();
				Folder.refresh();
				Ticket.refresh();
			};
            reader.readAsText($file[0].files[0]);
            Dialog.close();
		});
		Dialog.open("#uploadDialog");
	}


	Class.config = function() {
		Dialog.open("#configDialog");
	}

	Class.inbox = function() {
		Folder.inbox();
	}
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
