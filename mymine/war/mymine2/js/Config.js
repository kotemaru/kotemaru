
function Config(){this.initialize.apply(this, arguments)};
(function(Class){

	var NAMES = [
		"redmineAbsPath",
		"redmineApiPath",
		"redmineApiKey",
		"redmineQueryId",
		"redmineCustomImg",
		"redmineCustomName",
		"redmineCustomQuery",
		"redmineProjectName",
		"redmineProjectId"
	];
	Class.NAMES = NAMES;

	Class.init = function(){
	}

	Class.getProjects = function() {
		MyMine.waiting(true);
		new RedMine().getProjects(function(data){
			var projects = [];
			var names = [];
			for (var i=0; i<data.projects.length; i++) {
				var project = data.projects[i];
				projects.push(project.id);
				names.push(project.name);
			}
			setupProjects({
				redmineProjectId: projects,
				redmineProjectName: names
			});
			MyMine.waiting(false);
		});
	}

	Class.save = function(config) {
		Storage.saveConfig(config);
		//setup(config);
	}

	// TODO: UIか？
	function setup(config) {
		for (var name in config) {
			Class[name] = config[name];
			if (name.indexOf("redmineProject") != 0) {
				setValues($("input[name='"+name+"']"),config[name]);
			}
		}
		setupCustomQuery(config);
		setupProjects(config);
		Control.setup();
	}
	function setupCustomQuery(config) {
		var querys = config.redmineCustomQuery;
		var names = config.redmineCustomName;
		var icons = config.redmineCustomImg;
		if (querys == null) return;

		var $icons = $("img.redmineCustomImg");
		var $names = $("input[name='redmineCustomName']");
		var $querys = $("input[name='redmineCustomQuery']");

		for (var i=0; i<querys.length; i++) {
			if (icons) $($icons[i]).attr("src",icons[i]);
			if (names) $($names[i]).val(names[i]);
			$($querys[i]).val(querys[i]);
		}
	}
	function setupProjects(config) {
		var $list = $("#configProjectList").html("");
		var $templ = $("#templ_project");

		var projects = config.redmineProjectId;
		var names = config.redmineProjectName;
		if (projects == null) return;

		for (var i=0; i<projects.length; i++) {
			var $row = $templ.clone();
			$row.find("input[name='redmineProjectName']").val(names[i]);
			$row.find("input[name='redmineProjectId']").val(projects[i]);
			$list.append($row);
		}
	}

	Class.setup = setup;

	function getValues($inputs) {
		if ($inputs.length <= 1) return $inputs.val();
		var list = [];
		for (var i=0; i<$inputs.length; i++) {
			var $input = $($inputs[i]);
			if ($input[0].tagName == "IMG") {
				list.push($input.attr("src"));
			} else {
				list.push($input.val());
			}

		}
		return list;
	}
	Class.getValues = getValues;
	function setValues($inputs, vals) {
		if ($inputs.length <= 1) return $inputs.val(vals);
		for (var i=0; i<$inputs.length; i++) {
			$($inputs[i]).val(vals[i]);
		}
	}


})(Config);


