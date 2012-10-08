

function Tickets(){this.initialize.apply(this, arguments)};
(function(Class){
	var C_TICKET = ".Ticket";

	
	var selection = [];
	
	Class.getSelection = function() {
		return selection;
	}
	Class.clearSelection = function() {
		selection = [];
		Class.refresh();
	}
	Class.addSelection = function(no) {
		var idx = selection.indexOf(no);
		if (idx >= 0) {
			// nop
		} else {
			selection.push(no);
			marking("article[data-ticket-no="+no+"]", true);
		}
	}

	Class.toggleSelection = function(no) {
		var idx = selection.indexOf(no);
		if (idx >= 0) {
			selection.splice(idx, 1);
			marking("article[data-ticket-no="+no+"]", false);
		} else {
			selection.push(id);
			marking("article[data-ticket-no="+no+"]", true);
		}
	}
	
	function marking(sel, mode) {
		if (mode) {
			$(sel).css("backgroundColor", "#d0e0ff");
		} else {
			//$(sel).css("backgroundColor", "transparent");
			$(sel).css("backgroundColor", "white");
		}
	}

	Class.refresh = function() {
		var $tickets = $(C_TICKET);
		$tickets.css("backgroundColor", "white");
		$tickets.each(function(){
			var no = this.dataset.ticketNo;
			if (selection.indexOf(no)>=0) marking(this, this);
		});
	}
	
	Class.reload = function(tickets) {
		var $section = $("#tickets");
		var $template = $("#templ_ticket");

		var list = [];
		for (var no in tickets) {
			var issue = Ticket.issue(no);
			if (issue == null) {
				issue = {id:no, subject:"<<Not found>>"};
			}
			list.push(issue);
		}
		list.sort(function(a,b){
			return(Date.parse(b.updated_on)-Date.parse(a.updated_on));
		});

		$section.html("");
		for (var i=0; i<list.length; i++) {
			var $art = Ticket.makeArticle(list[i], $template);
			$section.append($art);
		}
	}

	Class.onScroll = function(_this,event) {
		var $this = $(_this);
		var $child = $this.find(">div");
		var bottom = $this.scrollTop()+$this.height();
		if (bottom >= $child.height()) {
			Folder.inboxAppend();
		}
	}	
	
})(Tickets);
