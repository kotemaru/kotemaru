
function RedMine(){this.initialize.apply(this, arguments)};
(function(Class){
	Class.absPath = "/r-labs";
	Class.apiPath = "/r-labs";
	Class.apiKey = "";
	
	Class.prototype.initialize = function() {
	}

	Class.prototype.getIssues = function(callback, query, opts) {
		MyMine.waiting(true);
		MyMine.progress(50);

		var url = Class.apiPath + "/issues.json?set_filter=1&key=" + Class.apiKey;
	
		
		//if (Config.redmineQueryId) {
		//	url += "&query_id="+Config.redmineQueryId;
		//} else {
		//	url += "&desc=updated_on";
		//}
		//if (Control.checkButtons.filter_user) {
			//url += "&assigned_to_id="+Control.userId;
			//url += "&query_id=72";
		//}

		if (query) url += "&"+query;
		if (opts != null) {
			for (var k in opts) {
				url += "&"+k+"="+opts[k];
			}
		}

		console.log("Query:",url);
		getJsonAsync(url, function(data){
			callback(data);
			MyMine.waiting(false);
		});
	}
	Class.prototype.getIssue = function(num, callback) {
		var url = Class.apiPath + "/issues/"+num+".json"
			+ "?key=" + Class.apiKey;
		getJsonAsync(url, callback);
	}

	Class.prototype.getProjects = function(callback) {
		var url = Class.apiPath + "/projects.json"
			+ "?key=" + Class.apiKey;
		getJsonAsync(url, callback);
	}

	Class.prototype.getCurrentUser = function(callback) {
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

})(RedMine);

