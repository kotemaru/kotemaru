function Config(game){this.initialize.apply(this, arguments)};
(function(Class, Super) {
	Util.extend(Class, Super);

	Class.prototype.initialize = function(game) {
	};
	
	const STORAGE_NAME = "tatesyu.config";
	const DEFAULT ={
		control: "orient", muteki: false, interval: 60
	}
	
	function load() {
		var item = window.localStorage.getItem(STORAGE_NAME);
		var config = JSON.parse(item);
		Util.copy(Class, DEFAULT);
		Util.copy(Class, config);
		if (!IS_DEBUG) Config.muteki = false;
	}

	function save() {
		var config = {};
		for (var k in DEFAULT) config[k] = Class[k];
		var item = JSON.stringify(config);
		window.localStorage.setItem(STORAGE_NAME, item);
	}

	/* static */
	(function(){
		load();
	})()
	

	Config.open = function(ev) {
		var main = Util.byId("main");
		var config = Util.byId("config");
		main.style.display = "none";
		config.style.display = "block";
		
		Util.setSelect(Util.byId("c_interval"), Config.interval);
		Util.setSelect(Util.byId("c_control"), Config.control);
		Util.byId("c_muteki").checked = Config.muteki?"checked":null;
	}

	Config.close = function() {
		var main = Util.byId("main");
		var config = Util.byId("config");
		main.style.display = "block";
		config.style.display = "none";
		save();
	}

})(Config);

