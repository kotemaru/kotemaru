

function Storage(){this.initialize.apply(this, arguments)};
(function(Class){
	var BASE = "MyMine"
	var FOLDER = BASE+"/folder/";
	var ISSUE = BASE+"/issue/";
	var CONFIG = BASE+"/config";
	var MASTER = BASE+"/master";

	Class.prototype.initialize = function() {
	}
	Class.init = function() {
		Class.loadAll();
	}

	Class.saveFolder = function(folder) {
		var backup = folder.tickets;
		if (folder.nosave) folder.tickets = {};
		localStorage[FOLDER+folder.name] = JSON.stringify(folder);
		folder.tickets = backup;
	}
	Class.removeFolder = function(folder) {
		delete localStorage[FOLDER+folder.name];
	}

	Class.saveTicket = function(issue) {
		localStorage[ISSUE+issue.id] = JSON.stringify(issue);
	}
	Class.removeTicket = function(issue) {
		delete localStorage[ISSUE+issue.id];
	}
	Class.saveConfig = function(config) {
		localStorage[CONFIG] = JSON.stringify(config);
	}
	Class.saveMaster = function(data) {
		localStorage[MASTER] = JSON.stringify(data);
	}
	Class.saveData = function(name, data) {
		localStorage[BASE+"/"+name] = JSON.stringify(data);
	}
	Class.loadData = function(name) {
		var data = localStorage[BASE+"/"+name];
		if (data == null) return null;
		return JSON.parse(data);
	}

	Class.loadAll = function(){
		Config.setup(JSON.parse(localStorage[CONFIG]));

		var hasFolder = false;
		for (var k in localStorage) {
			if (k.indexOf(FOLDER) == 0) {
				hasFolder = true;
				Folder.put(JSON.parse(localStorage[k]));
			} else if (k.indexOf(ISSUE) == 0) {
				Ticket.register(JSON.parse(localStorage[k]));
			} else if (k == MASTER) {
				MasterTable.load(JSON.parse(localStorage[k]));
			}
		}

		if (!hasFolder) {
			Folder.resetAll();
		}
	}

	Class.getDownloadString = function(){
		var str = "{";
		for (var k in localStorage) {
			if (k.indexOf(BASE) == 0) {
				str += '"'+k+'":'+localStorage[k]+",\n";
			}
		}
		str += '"":0}';
		return str;
	}


	Class.cleanup = function(subname) {
		for (var k in localStorage) {
			if (k.indexOf(BASE+subname) == 0) delete localStorage[k];
		}
	}
	Class.setUploadString = function(str) {
		Class.cleanup();
        var data = JSON.parse(str);
		for (var k in data) {
			if (k == CONFIG) {
				Class.saveConfig(data[k]);
			} else if (k.indexOf(FOLDER) == 0) {
				Class.saveFolder(data[k]);
			} else  if (k.indexOf(ISSUE) == 0) {
				Class.saveTicket(data[k]);
			}
		}
		Class.loadAll();
	}

})(Storage);
