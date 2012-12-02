

function Control(){this.initialize.apply(this, arguments)};
(function(Class){
	Class.prototype.initialize = function() {
	}

	var checkButtons = {
		filter_user: false
	};

	Class.userId = null;

	Class.init = function() {
		new RedMine().getCurrentUser(function(data){
			Class.userId = data.user.id;
		});
	}

	
	Class.getProjectId = function() {
		return $("#projectSelector").val();
	}
	Class.getCustomQuery = function() {
		var $custom = CheckButton.getGroupCheck("custom");
		if ($custom)
		return $custon.data("query");
	}
	Class.getFilterUser = function() {
		return CheckButton.isChecked($("#filter_user"))?Class.userId:null;
	}
	Class.getFilterClosed = function() {
		return CheckButton.isChecked($("#filter_closed"))?"*":null;
	}
	Class.getFilterMasters = function(opts) {
		var masterTable = MasterTable.getMasterTable();
		for (var k in masterTable) {
			var val = PulldownButton.getValue($("#filter_"+k));
			if (val) opts[k+masterTable[k].idSuf] = val;
		}
		return opts;
	}

	//-----------------------------------------------------
	Class.customImg = [];
	Class.customName = [];
	Class.customQuery = [];

	Class.reconfig = function(){
		initProjects();
		initMasterTable();
		initCustomQuery();
	}
	
	function initProjects() {
		new RedMine().getProjects(function(data){
			var projects = data.projects;
			
			var $sel = $("#projectSelector").html("<option value=''>*</option>");
			for (var i=0; i<projects.length; i++) {
				var project = projects[i];
				var $opt = $("<option/>");
				$opt.val(project.id);
				$opt.text(project.name);
				$sel.append($opt);
			}
			$sel.val(Storage.get("projectSelector"));
		});
		
	}
	function initCustomQuery() {
		var $btns = $("#customQueryButtons");
		$btns.html("");
		for (var i=0; i<Class.customQuery.length; i++) {
			if (Class.customQuery[i] == "") continue;

			$btn = $("<img class='CheckButton' />");
			$btn.attr("id", "custom_"+i);
			$btn.attr("data-group", "custom");
			$btn.attr("src", Class.customImg[i])
			$btn.attr("alt", Class.customName[i]);
			$btn.data("query", Class.customQuery[i]);
			$btns.append($btn);
		}
	}
	function initMasterTable() {
		var masterTable = MasterTable.getMasterTable();
		if (masterTable == null) return;

		var $filters = $("#filterButtons").html("");
		for (var k in masterTable) {
			if (k.indexOf("cf_")==0) continue; // TODO:カスタムフィールドの扱い
			var $btn = PulldownButton.makeElement("filter_"+k, masterTable[k]);
			$filters.append($btn);
		}
	}
	function refreshMasterTable() {
		var masterTable = MasterTable.getMasterTable();
		if (masterTable == null) return;

		for (var k in masterTable) {
			if (k.indexOf("cf_")==0) continue; // TODO:カスタムフィールドの扱い
			PulldownButton.makeMenu($("#filter_"+k), masterTable[k]);
		}
	}
	Class.refreshMasterTable = refreshMasterTable;
	
	
	
	$(function(){
		$("#projectSelector").live("change", function(){
			//Folder.inbox();
			Storage.put("projectSelector",  $(this).val());
		});
		
		// 空白削除
		function removeSpace(){
			if (this.nodeType==3) this.parentNode.removeChild(this);
		}
		$("#filterPack").contents().each(removeSpace);
		$("#filterPack>span").contents().each(removeSpace);
		$("#configPack").contents().each(removeSpace);
		
		Class.reconfig();
	});

	//--------------------------------------------------------------------------
	// 検索関連
	Class.search = function() {
		var kw = $("#searchKeyword").val();
		Inbox.inboxOne(parseInt(kw));
	}
	Class.searchKeyPress = function(ev,_this) {
		if (ev.keyCode == 13) { // ReturnKey
			Class.search();
			return false;
		}
	}

})(Control);
