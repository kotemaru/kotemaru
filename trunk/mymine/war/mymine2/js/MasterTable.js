
function MasterTable(){this.initialize.apply(this, arguments)};
(function(Class){


	Class.init = function(){
	}

	Class.register = function(issue) {
		put("assigned_to", issue.assigned_to);
		put("author", issue.assigned_to);
		put("assigned_to", issue.author);
		put("author", issue.author);
		put("status", issue.status);
		put("tracker", issue.tracker);
		put("priority", issue.priority);

		if (issue.custom_fields){
			for (var i=0; i<issue.custom_fields.length; i++) {
				var cf = issue.custom_fields[i];
				var type = "cf_"+cf.id;
				var data = {disp:cf.name, id:cf.value, name:cf.value};
				put(type, data);
			}
		}
	}

	var MASTER_TABLE_BASE = {
		assigned_to:{name:"担当者",  idSuf:"_id",  keySort:"name", icon:"img/led24/user_silhouette.png", values:{} },
		author:     {name:"作成者",  idSuf:"_id",  keySort:"name", icon:"img/led24/user.png", values:{} },
		tracker:    {name:"トラッカー", idSuf:"_id", keySort:"id", icon:"img/dog.png", values:{} },
		status:     {name:"進捗",      idSuf:"_id", keySort:"id", icon:"img/progress.png", values:{} },
		priority:   {name:"優先度",    idSuf:"_id", keySort:"id", icon:"img/priority.png", values:{} }
	};
	
	var masterTable = null;
	
	Class.getMasterTable = function() {
		return masterTable;
	}

	function put(type, data) {
		if (data == null) return;
		if (masterTable[type] == null) {
			masterTable[type] = {name:data.disp, idSuf:"", icon:"", values:{}};
		}
		if (masterTable[type].values[data.id]) return;
		masterTable[type].values[data.id] = data.name;
		if (data.disp) {
			masterTable[type].name = data.disp;
		}

		save();
	}

	function save() {
		Storage.put("MasterTable", masterTable);
	}
	function load() {
		masterTable = Storage.get("MasterTable", MASTER_TABLE_BASE);
	}

	$(function(){
		load();
	});
	

})(MasterTable);


