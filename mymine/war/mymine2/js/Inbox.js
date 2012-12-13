
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
	Class.next = function(callback) {
		if (isInboxFin) return;
		inboxPage++ ;
		inbox(callback);
	}

	Class.inboxOne = function(num) {
		MyMine.waiting(true);
		isFirstInbox= false;
		var inbox = Folders.getInbox();
		RedMine.getIssue(num, function(data){
			var issue = data.issue;
			TicketPool.put(issue);
			inbox.addTicket(issue.id);
			MasterTable.register(issue);

			Folders.select(inbox);
			isInboxFin = true;
			MyMine.waiting(false);
		});
	}

	function inbox(callback) {
		var opts = {page:inboxPage};

		// Control のフィルタ条件設定
		opts.project_id     = Control.getProjectId();
		var query = Control.getCustomQuery();
		if (query == null) {
			opts.assigned_to_id = Control.getFilterUser();
			opts.status_id      = Control.getFilterClosed();
			Control.getFilterMasters(opts);
			
			// ソート条件
			var sortInfo = TicketTray.getSortInfo();
			if (sortInfo) {
				opts.sort = sortInfo.name + (sortInfo.desc?":desc":"");
			}
		}
		
		RedMine.getIssues(function(resData){
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
			
			Control.refreshMasterTable();//TODO:このタイミング？
			if (callback) callback(issues);
		}, query, opts);
	}


})(Inbox);


