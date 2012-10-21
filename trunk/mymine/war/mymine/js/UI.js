
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
			hasFolder = null;
			sliderX = null;
			sliderTarget = null;
		}).bind("mousemove", function(ev){
			if (sliderX) {
				//Class.setLeftWidth(ev.clientX+1);
			}
			if (sliderTarget) {
				var $header = $("#"+sliderTarget.dataset.ref);
				var selector = "."+sliderTarget.dataset["class"];
				var offset = $header.offset();
				var w = (ev.clientX - offset.left);
				classCss(selector, {width: w+"px"});
			}
		});

		// LeftWidth
		var sliderTarget = null;
		var sliderX = null;
		$("#leftSlideHandle").bind("mousedown", function(ev){
			sliderX = ev.clientX;
			return false;
		})
		$(".SlideHandle").bind("mousedown", function(ev){
			sliderTarget = this;
			sliderX = ev.clientX;
			//sliderX = ev.clientX;
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


		var hasFolder = null;

		// Folder
		$(".Folder").live("mouseup",function(){
			if (MyMine.isDrag()) {
				Folder.dropTicket(this.id);
			}
			MyMine.isDrag(false);
		}).live("mousedown", function(){
			hasFolder = this;
			Folder.select(this.id);
			return false;
		}).live("mousemove", function(){
			if (hasFolder != null) {
				$(".Folder").css({cursor:"row-resize"});
			}
		}).live("mouseover", function(){
			Folder.hover(true, this);
			if (hasFolder != null && hasFolder != this) {
				Folder.insert(this.id, hasFolder.id);
				Folder.refresh();
			}
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
				TIckets.refresh();
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


	}

	
	var classCssRuleCache = {};
	function getCssRule(selector) {
		if (classCssRuleCache[selector]) return classCssRuleCache[selector];
		var sheets = document.styleSheets;
		for (var i=0; i<sheets.length; i++) {
			var rules = sheets[i].cssRules;
			for (var j=0; j<rules.length; j++) {
				if (selector == rules[j].selectorText) {
					classCssRuleCache[selector] = rules[j];
					return rules[j];
				}
			}
		}
		return null;
	}
	function classCss(selector, style) {
		var rule = getCssRule(selector);
		if (rule == null) return;
		for (var k in style) rule.style[k] = style[k];
	}
	
	//-------------------------------------------------------------
	Class.abort = function() {
		Dialog.open("#abortDialog");
	}

	Class.setLeftWidth = function(w) {
		var px = w +"px";
		$("#leftPanel").css({width: px});
		$("#mainPanel").css({paddingLeft: px});
		$(".TFolder").css({width: px});
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
