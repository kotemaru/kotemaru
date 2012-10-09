
function UI(){this.initialize.apply(this, arguments)};
(function(Class){

	Class.init = function() {
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
		// TODO:
	}


	Class.saveConfig = function() {
		var config = {};
		for (var i=0; i<Config.NAMES.length; i++) {
			var name = Config.NAMES[i];
			config[name] = getValues($(".Config input[name='"+name+"']"));
		}
		Config.save(config);
		DIalog.close();
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
