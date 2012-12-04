
function RedMine(){this.initialize.apply(this, arguments)};
(function(Class){
	Class.absPath = "/r-labs";
	Class.apiPath = "/r-labs";
	Class.apiKey = "";
	
	Class.prototype.initialize = function() {
	}

	Class.getIssues = function(callback, query, opts) {
		MyMine.waiting(true);
		MyMine.progress(50);

		var url = Class.apiPath + "/issues.json?set_filter=1&key=" + Class.apiKey;
	
		if (query) url += "&"+query;
		if (opts != null) {
			for (var k in opts) {
				if (opts[k]) url += "&"+k+"="+opts[k];
			}
		}

		console.log("Query:",url);
		getJsonAsync(url, function(data){
			callback(data);
			MyMine.waiting(false);
		});
	}
	
	Class.getIssue = function(num, callback) {
		var url = Class.apiPath + "/issues/"+num+".json"
			+ "?key=" + Class.apiKey;
		getJsonAsync(url, callback);
	}

	Class.getProjects = function(callback) {
		var url = Class.apiPath + "/projects.json"
			+ "?key=" + Class.apiKey;
		getJsonAsync(url, callback);
	}

	Class.getCurrentUser = function(callback) {
		var url = Class.apiPath + "/users/current.json"
			+ "?key=" + Class.apiKey;
		getJsonAsync(url, callback);
	}

	function getJsonAsync(url, callback) {
		$.ajax({
			async: true,
			type: "GET",
			url: url,
			dataType: "json",
			success: callback,
			error: function(xhr) {
				MyMine.waiting(false);
				alert(xhr.status+" "+xhr.statusText);
			}
		});
	}

	Class.openIsuue = function(num) {
		var url = Class.absPath+"/issues/"+num;
		window.open(url,"_blank");
	}
	
	
	Class.save = function(num) {
		var data = {
			absPath: Class.absPath,
			apiPath: Class.apiPath,
			apiKey: Class.apiKey
		};
		Storage.put("RedMine", data);
	}
	
	Class.load = function(num) {
		var data = Storage.get("RedMine");
		if (data == null) return;
		Class.absPath =	data.absPath; 
		Class.apiPath =	data.apiPath; 
		Class.apiKey  =	data.apiKey;
	}
	
	$(function(){
		Class.load();
	})

})(RedMine);

