
function RedMine(){this.initialize.apply(this, arguments)};
(function(Class){

	Class.prototype.initialize = function() {
	}

	Class.prototype.getIssues = function(callback, query, opts) {
		MyMine.waiting(true);
		MyMine.progress(50);

		//var prjId = $("#projectSelector").val();
		var url = Config.redmineApiPath + "/issues.json"
			+ "?set_filter=1&key=" + Config.redmineApiKey
		;
	
		
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
		var url = Config.redmineApiPath + "/issues/"+num+".json"
			+ "?key=" + Config.redmineApiKey;
		getJsonAsync(url, callback);
	}

	Class.prototype.getProjects = function(callback) {
		var url = Config.redmineApiPath + "/projects.json"
			+ "?key=" + Config.redmineApiKey;
		getJsonAsync(url, callback);
	}

	Class.prototype.getCurrentUser = function(callback) {
		var url = Config.redmineApiPath + "/users/current.json"
			+ "?key=" + Config.redmineApiKey;
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
		var url = Config.redmineAbsPath+"/issues/"+num;
		window.open(url,"_blank");
	}

})(RedMine);

