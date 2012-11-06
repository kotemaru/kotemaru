

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

		var list = getSortedTickets(tickets);
		$section.html("");
		for (var i=0; i<list.length; i++) {
			var $art = Ticket.makeArticle(list[i], $template);
			$section.append($art);
		}
		$section.trigger("ticketsReload");
	}


	function getAssigned(issue) {
		if (issue.assigned_to == null) return null;
		return issue.assigned_to.id;
	}
	function compId(a,b,key) {
		var A = a[key]?a[key].id:-1;
		var B = b[key]?b[key].id:-1;
		return B-A;
	}
	function compName(a,b,key) {
		var A = a[key]?a[key].name:"";
		var B = b[key]?b[key].name:"";
		return (A==B?0:(A>B?-1:1));
	}

	var COMPARATOR_DESC = {
		hNum      : function(a,b){return(b.id-a.id);},
		hProject  : function(a,b){return compId(a,b,"project");},
		hTracker  : function(a,b){return compId(a,b,"tracker");},
		hAssigned : function(a,b){return compName(a,b,"assigned_to");},
		hUpDate   : function(a,b){return(Date.parse(b.updated_on)-Date.parse(a.updated_on));},
		hStartDate: function(a,b){return(Date.parse(b.start_date)-Date.parse(a.start_date));},
		hDueDate  : function(a,b){return(Date.parse(b.due_date)-Date.parse(a.due_date));},
		hDoneRate : function(a,b){return(b.done_ratio-a.done_ratio);},
		hSubject  : function(a,b){var A=a.subject,B=b.subject;return(A==B?0:(A>B?-1:1));}
	}
	var comparatorName = "hUpDate";
	var comparatorAsc = true;

	Class.setSorted = function(name, asc) {
		if (comparatorName == name) {
			comparatorAsc = !comparatorAsc;
		} else {
			comparatorName = name;
			comparatorAsc = true;
		}
		if (asc != null) comparatorAsc = asc;

		$(".THead").removeClass("Selected");
		$("#"+name).addClass("Selected")
			.find(">img").attr("src",
					"img/sort-"+(comparatorAsc?"up":"down")+".png");
	}

	Class.getSorted = function() {
		return {name:comparatorName, asc:comparatorAsc};
	}


	function getSortedTickets(tickets) {
		var list = [];
		for (var num in tickets) {
			var issue = Ticket.issue(num);
			if (issue == null) {
				issue = {id:num, subject:"<<Not found>>"};
			}
			list.push(issue);
		}
		var comparator = COMPARATOR_DESC[comparatorName];
		if (comparatorAsc == true) {
			comparator = function(a,b){return COMPARATOR_DESC[comparatorName](b,a);}
		}
		list.sort(comparator);
		return list;
	}


})(Tickets);
