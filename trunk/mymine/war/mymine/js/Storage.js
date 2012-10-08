

function Storage(){this.initialize.apply(this, arguments)};
(function(Class){
	var BASE = "MyMine"
	var FOLDER = BASE+"/folder/";
	var ISSUE = BASE+"/issue/";
	var CONFIG = BASE+"/config";
	
	Class.prototype.initialize = function() {
	}
	Class.init = function() {
		Class.loadAll();
	}

	Class.saveFolder = function(folder) {
		var backup = folder.tickets;
		if (folder.nosave) folder.tickets = {};
		localStorage[FOLDER+folder.id] = JSON.stringify(folder);
		folder.tickets = backup;
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
			}
		}
		
		if (!hasFolder) {
			Folder.resetAll();
		}
	}
	
	Class.download = function(_this){
		var str = "{";
		for (var k in localStorage) {
			if (k.indexOf(BASE) == 0) {
				str += '"'+k+'":'+localStorage[k]+",\n";
			}
		}
		str += '"":0}';

		var $a = $("#downloadLink");
		$a.attr("href","data:application/octet-stream,"+encodeURIComponent(str));
		Dialog.open("#downloadDialog");
	}

	Class.upload = function() {
		var $file = $("#uploadFile");
		$file.show().bind("change", function(){
			var reader = new FileReader();
			reader.onload = function(e) {onLoad(reader);};
            reader.readAsText($file[0].files[0]);
            Dialog.close();
		});
		Dialog.open("#uploadDialog");
	}
	function onLoad(reader) {
		for (var k in localStorage) {
			if (k.indexOf(BASE) == 0) delete localStorage[k];
		}
		//console.log(reader.result);
        var data = JSON.parse(reader.result);
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
