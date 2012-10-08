
function Folder(){this.initialize.apply(this, arguments)};
(function(Class){
	var FOLDERS = [
	    {name:"news",    title:"新着",       icon:"img/inbox.png", nosave:true},
	    {name:"trash",   title:"ゴミ箱",     icon:"img/bin_closed.png", nosave:true},
	    {name:"sepa1",   title:"---",       icon:"---"},
	    {name:"now",     title:"今すぐ",     icon:"img/alarm.png"},
	    {name:"play",    title:"現行作業",   icon:"img/hand.png"},
	    {name:"reserve", title:"次予定作業", icon:"img/bookmark_folder.png"},
	    {name:"todo",    title:"後回し",     icon:"img/folder.png"},
	    {name:"other",   title:"その他",     icon:"img/folder.png"},
	    {name:"ovserve", title:"相談のみ",   icon:"img/comment.png"}
	];

	var folders = {};
	var currentName = null;
	
	Class.prototype.initialize = function() {
	}
	Class.resetAll = function() {
		folders = {};
		for (var i=0; i<FOLDERS.length; i++) {
			var folder = FOLDERS[i]
			folder.seq = i;
			folder.tickets = {};
			folders[folder.name] = folder;
			Storage.saveFolder(folder);
		}
	}

	Class.init = function() {
		//Class.resetAll();

		Class.refresh();

		$(".Folder").live("mouseup",function(){
			if (MyMine.isDrag()) {
				Class.dropTicket(this.id);
			} else {
				Class.select(this.id);
			}
		}).live("mousedown", function(){
			//event.
		}).live("mouseup", function(){
			$(this).css("cursor", "pointer");
		}).live("mouseover", function(){
			hover(true, this);
		}).live("mouseout", function(){
			hover(false, this);
		});
	}
	function hover(isIn, _this) {
		var cursor = "pointer";
		if (MyMine.isDrag()) {
			var sels = Tickets.getSelection();
			var img = (sels.length>=2) ? "tickets":"ticket";
			if (_this.id == currentName) img += "-no";
			cursor = "url(img/"+img+".png) 16 8, pointer";
		}
		var $this = $(_this);
		$this.css("cursor", cursor);
		
		if (_this.id != currentName) {
			$this.css("border", isIn?"1px solid #ddd":"0");
		}
	}
	Class.put = function(folder) {
		folders[folder.name] = folder;
		//Class.refresh();
	}
	Class.dropTicket = function (name) {
		var curFolder = folders[currentName];
		var selects = Tickets.getSelection();
		for (var i=0; i<selects.length; i++) {
			var ticketNo = selects[i];
			Class.register(name, Ticket.issue(ticketNo));
			Class.remove(currentName, curFolder.tickets[ticketNo]);
		}
		Class.select(currentName);
	}
	
	Class.register = function (name, issue) {
		var folder = folders[name];
		issue.folder = name;
		Ticket.register(issue);
		folder.tickets[issue.id] = 1;
		Storage.saveFolder(folder);
	}
	Class.remove = function (name, issue) {
		var folder = folders[name];
		issue.folder = null;
		Ticket.register(issue);
		delete folder.tickets[issue.id];
		Storage.saveFolder(folder);
	}
	Class.isNoSave = function (name) {
		var folder = folders[name];
		if (folder == null) return true;
		return folder.nosave;
	}
	
	var inboxPage = 1;
	Class.inbox = function() {
		inboxPage = 1;
		folders.news.tickets = {};
		Tickets.reload(folders.news.tickets);
		inbox();
	}
	Class.inboxAppend = function() {
		inboxPage++ ;
		inbox();
	}
	
	function inbox() {
		new RedMine().getIssues(function(data){
			for (var i=0; i<data.issues.length; i++) {
				var issue = Ticket.register(data.issues[i]);
				Folder.register("news", issue);
			}
			Folder.refresh();
			Folder.select("news");
		},{page: inboxPage});
	}
	
	Class.refresh = function () {
		var $section = $("#folders");
		var $template = $("#templ_folder");

		$section.html("");
		var list = [];
		for (var name in folders) {
			list.push(folders[name]);
		}
		list.sort(function(a,b){return a.seq-b.seq;});
		
		for (var i=0; i<list.length; i++) {
			var folder = list[i];
			if (folder.title=="---") {
				$section.append($("<hr/>"));
			} else {
				var $article = $template.clone();
				$article.attr("id",folder.name);
				$article.html(getTitleWithCount(folder));
				$article.css("background","url("+folder.icon+") no-repeat  2px 2px");
				$section.append($article);
			}
		}
	}
	
	function getTitleWithCount(folder) {
		if (folder.nosave) return folder.title;
		var unchecked = 0;
		for (var num in folder.tickets) {
			if (!Ticket.isChecked(num)) unchecked++;
		}
		if (unchecked==0) return folder.title;
		return "<b class='Count'>("+unchecked+") </b>"+folder.title;
	}
	

	Class.select = function(name) {
		$(".Folder").css({backgroundColor:"transparent", border:"0"});
		$("#"+name).css({backgroundColor:"white", border:"1px solid #aaa"});
		currentName = name;

		Tickets.reload(folders[name].tickets);
		Tickets.clearSelection();
	}
	
	Class.updateTickets = function() {
		MyMine.waiting(true);
		var allTickets = getAllTickets();
		var redmine = new RedMine();
		var count = 0;
		var total = 0;
		for (var num in allTickets) {
			count++;
			redmine.getIssue(num, function(data){
				Ticket.register(data.issue);
				if (--count <= 0) {
					MyMine.waiting(false);
				}
				MyMine.progress(100*(total-count)/total);
			});
		}
		total = count;
		if (count == 0) {
			MyMine.waiting(false);
		}
	}
	
	function getAllTickets() {
		var all = {};
		for (var name in folders) {
			var tickets = folders[name].tickets;
			for (var num in tickets) all[num] = 1;
		}
		return all;
	}
	

})(Folder);
