
function Inbox(){this.initialize.apply(this, arguments)};
(function(Class){
	var inboxPage = 1;
	var isInboxFin = false;
	
	Class.first = function() {
		inboxPage = 1;
		isInboxFin = false;
		Folders.getInbox().clearTicket();
		inbox();
	}
	Class.next = function() {
		if (isInboxFin) return;
		inboxPage++ ;
		inbox();
	}


	var SORT_NAME = {
		hNum      : "id",
		hAssigned : "assigned_to",
		hUpDate   : "updated_on",
		hDueDate  : "due_date",
		hDoneRate : "done_rate",
		hSubject  : "subject"
	}

	function inbox() {
		//var prjId = $("#projectSelector").val();
		var prjId = "";
		//var opts = {page:inboxPage, project_id:prjId};
		var opts = {page:inboxPage};

		var custom = Control.checkButtonGroup("custom");
		var query = null;
		if (custom>=0) query = Config.redmineCustomQuery[custom];

		if (Control.checkButtons.filter_user) opts.assigned_to_id=Control.userId;
		if (Control.checkButtons.filter_closed) opts.status_id="*";
		var masterTable = MasterTable.getMasterTable();
		for (var k in masterTable) {
			var val = Control.getValue("filter_"+k);
			if (val) opts[k+masterTable[k].idSuf] = val;
		}
		
		/*
		var sorted = Tickets.getSorted();
		if (sorted.name) {
			opts.sort = SORT_NAME[sorted.name];
			if (!sorted.asc) {
				opts.sort += ":desc";
			}
		}
		*/

		Config.redmineApiPath = "/r-labs"; // TODO:Config
		
		new RedMine().getIssues(function(resData){
			var issues = resData.issues;
			var inbox = Folders.getInbox();
			for (var i=0; i<issues.length; i++) {
				var issue = issues[i];
				TicketPool.put(issue);
				inbox.addTicket(issue.id);
				MasterTable.register(issue);
			}
			Folders.select(inbox);
			isInboxFin = (issues.length==0);
		}, query, opts);
	}


})(Inbox);


