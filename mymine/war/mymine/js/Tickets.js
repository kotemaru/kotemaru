

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
	Class.addSelection = function(num) {
		var idx = selection.indexOf(num);
		if (idx >= 0) {
			// nop
		} else {
			selection.push(num);
			marking("article[data-ticket-num="+num+"]", true);
		}
	}

	Class.toggleSelection = function(num) {
		var idx = selection.indexOf(num);
		if (idx >= 0) {
			selection.splice(idx, 1);
			marking("article[data-ticket-num="+num+"]", false);
		} else {
			selection.push(num);
			marking("article[data-ticket-num="+num+"]", true);
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
			var num = this.dataset.ticketNum;
			if (selection.indexOf(num)>=0) marking(this, true);
		});
	}

	Class.reload = function(tickets) {
		var $section = $("#tickets");
		var $template = $("#templ_ticket");

		var list = [];
		for (var num in tickets) {
			var issue = Ticket.issue(num);
			if (issue == null) {
				issue = {id:num, subject:"<<Not found>>"};
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
		$section.trigger("ticketsReload");
	}



})(Tickets);
