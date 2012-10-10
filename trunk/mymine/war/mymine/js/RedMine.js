
function RedMine(){this.initialize.apply(this, arguments)};
(function(Class){

	Class.prototype.initialize = function() {
	}

	Class.prototype.getIssues = function(callback, opts) {
		MyMine.waiting(true);
		MyMine.progress(50);

		var prjId = $("#projectSelector").val();
		var url = Config.redmineApiPath + "/issues.json"
			+ "?key=" + Config.redmineApiKey
		;
		if (Config.redmineQueryId) {
			url += "&query_id="+Config.redmineQueryId;
		} else {
			url += "&desc=updated_on";
		}

		if (opts != null) {
			for (var k in opts) {
				url += "&"+k+"="+opts[k];
			}
		}

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

	function getJsonAsync(url, callback) {
		$.ajax({
			async: true,
			type: "GET",
			url: url,
			dataType: "json",
			success: callback,
			error: function(xhr) {
				alert(xhr.status+" "+xhr.statusText);
			}
		});
	}

	Class.openIsuue = function(num) {
		var url = Config.redmineAbsPath+"/issues/"+num;
		window.open(url,"detail");
	}

})(RedMine);

