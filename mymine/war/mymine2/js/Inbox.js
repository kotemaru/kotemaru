
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
		var opts = {page:inboxPage};
		// プロジェクト
		var prjId = $("#projectSelector").val();
		if (prjId != "") opts.project_id = prjId;

		// カスタムクエリ
		var custom = Control.checkButtonGroup("custom");
		var query = null;
		if (custom>=0) query = Config.redmineCustomQuery[custom];

		// 自分担当のみ
		if (Control.checkButtons.filter_user) opts.assigned_to_id=Control.userId;
		// 終了チケット含む
		if (Control.checkButtons.filter_closed) opts.status_id="*";

		// マスタ
		var masterTable = MasterTable.getMasterTable();
		for (var k in masterTable) {
			var val = Control.getValue("filter_"+k);
			if (val) opts[k+masterTable[k].idSuf] = val;
		}
		
		// ソート条件
		var sortInfo = TicketTray.getSortInfo();
		if (sortInfo) {
			opts.sort = sortInfo.name + (sortInfo.desc?":desc":"");
		}

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
			
			Control.initMasterTable();//TODO:このタイミング？
		}, query, opts);
	}


})(Inbox);


