
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
		});

		// Control
		$(".Button").live("mouseover",function(ev){
			Control.popupBalloon($(this));
		}).live("mouseout",function(){
			Control.hideBalloon();
		});

		// Folder
		$(".Folder").live("mouseup",function(){
			if (MyMine.isDrag()) {
				Folder.dropTicket(this.id);
			} else {
				Folder.select(this.id);
			}
			MyMine.isDrag(false);
		}).live("mousedown", function(){
			//event.
		}).live("mouseover", function(){
			Folder.hover(true, this);
		}).live("mouseout", function(){
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
				draggable = null;
			}
		}).live("mouseup",function(ev){
			if (draggable == this) {
				if (!ev.ctrlKey) Tickets.clearSelection();
				Tickets.toggleSelection(this.dataset.ticketNum);
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
			return false;
		})

	}

	//-------------------------------------------------------------
	
	
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
		if (Folder.isInbox()) {
			Folder.inbox();
		} else {
			Folder.updateTickets();
		}
	}
	Class.cleaning = function() {
		Ticket.cleaning();
	}

	Class.addFolder = function() {
		Dialog.open('#addFolderDialog');
	}

	Class.removeFolder = function() {
		Folder.removeFolder();
	}

	Class.search = function() {
		var kw = $("#searchKeyword").val();
		Folder.inboxOne(parseInt(kw));
	}


	Class.saveConfig = function() {
		var config = {};
		for (var i=0; i<Config.NAMES.length; i++) {
			var name = Config.NAMES[i];
			config[name] = Config.getValues($(".Config input[name='"+name+"']"));
		}
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

	Class.saveAddFolder = function() {
		var $di = $("#addFolderDialog");
		var folder = {
			name:  $di.find("input[name='folderName']").val(),
			title: $di.find("input[name='folderTitle']").val(),
			icon:  $di.find("input[name='folderIcon']").val()
		};
		Folder.add(folder);
		Dialog.close();
		Folder.refresh();
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
