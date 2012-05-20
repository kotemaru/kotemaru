
function RollingMarble() {
	this.stage  = new Stage("stage");
	this.timer = Util.byId("timer");
	this.time = 0;
	this.isStart = false;
	this.stageNo = 0;
	this.stageNames = RollingMarble.getStageNames();
	this.goalTime = 0;
	this.totalTime = 0;
}
(function(Class) {
	const INTERVAL = 50; //ms
	function ticker() {
		if (Class.instance.isStart) setTimeout(ticker, INTERVAL);
		Class.instance.action();
		//if (Class.instance.isStart) setTimeout(ticker, INTERVAL);
	}

	Class.prototype.start = function() {
		var stageName = this.stageNames[this.stageNo];
		var data = Server.file(stageName);
		if (data == null) {
			alert(stageName+"が有りません。");
			return;
		}
	
		this.stage.makeStage(data);
		this.marble = this.stage.marble;
		if (this.marble == null) {
			this.stageNo++;
			return this.start();
		}

		this.startTime = (new Date()).getTime();
		this.goalTime = 0;
		this.time += this.stage.timelimit;
		this.isStop = false;
		this.timeview();

		var self = this;
		setTimeout(function() {
			Util.byId("stageNo").innerText = self.stageNames[self.stageNo];
			Util.byId("timelimit").innerText = toSecStr(self.time)+" sec";
			Dialog.open("d_start",function(){
				self.isStart = true;
				self.marble.recover(15);
				ticker();
			});
		},200);
	}
	Class.prototype.goal = function() {
		this.stop();
		Sound.play("goal");
		var self = this;
		self.stageNo++;
		Util.byId("goalTime").innerText = toSecStr(self.goalTime);
		Util.byId("totalTime").innerText = toSecStr(self.totalTime);
		Dialog.open("d_goal", function(){
			if (self.stageNo < self.stageNames.length) {
				self.start();
			} else {
				var config = getConfig();
				Util.byId("scoreName").value = config.name;
				Dialog.open("d_finish", function(){
					config.name = Util.byId("scoreName").value;
					setConfig(config);
					var stageName = config.map+"/"+config.stage;
					Server.postScore(config.name, stageName, self.totalTime);
					listScores();
				});
			}
		});
	}

	function listScores() {
		var config = getConfig();
		var stageName = config.map+"/"+config.stage;
		Util.byId("scoreStage").innerText = stageName;
		
		var list = Server.listScore(config.name, stageName, self.totalTime);
		if (list == null) return;
		var tbl = Util.byId("scoreTable");
		var tds = tbl.getElementsByTagName("td");

		for (var i=0; i<tds.length/2; i++) {
			if (i < list.length-1) {
				tds[i*2+0].innerText = list[i].name;
				tds[i*2+1].innerText = toSecStr(list[i].score);
			} else {
				tds[i*2+0].innerText = "-";
				tds[i*2+1].innerText = "-";
			}
		}
		Dialog.open("d_score", function(){
			RollingMarble.init();
		});
	}

	
	Class.prototype.stop = function() {
		this.isStart = false;
	}
	Class.prototype.resume = function() {
		this.isStart = true;
		ticker();
	}
	Class.prototype.action = function() {
		const self = this;
		//Sound.autoStop();
		with (this) {
			if (!isStart) return;
			const entities = stage.entities;
			const floors = stage.floors;
			for (var i=0; i<entities.length; i++) {
				for (var j=i+1; j<entities.length; j++) {
					entities[i].contact(entities[j]);
				}
			}
			var actors = stage.actors;
			for (var i=0; i<actors.length; i++) {
				var a = actors[i];
				a.action();
				//if (a.contact) {
				//	for (var k = 0; k < floors.length; k++) {
				//		floors[k].onThe(a);
				//	}
				//}
				if (a.reflect) a.reflect();
			}
			stage.scroll(marble.x, marble.y);

			//-----

			time -= INTERVAL;
			goalTime += INTERVAL;
			totalTime += INTERVAL;
			timeview();
	
			if (time <= 0) {
				self.isStart = false;
				setTimeout(function(){
					Dialog.open("d_retry",{
						ok:function(){self.start();},
						ng:function(){}
					});
				},200);
				return false;
			} else {
				return true;
			}
		}
	}
	Class.prototype.timeview = function() {
		// time view
		//var time = (new Date()).getTime() - this.startTime;
		with (this) {
			timer.style.color = "#88ff88";
			if (time < stage.timelimit2) timer.style.color = "#ffff00";
			if (time < stage.timelimit3) timer.style.color = "#ff2222";
			if (time <= 0) {
				timer.style.color = "#2222ff";
				time = 0;
				stop();
			}
			timer.innerText = toSecStr(this.time);
		}
	}
	function toSecStr(time) {
		var ms = "000000"+time;
		return ms.substr(ms.length-6,3)+"."+ms.substr(ms.length-3,2);
	}

	function onDeviceMotion(ev) {
		if (RollingMarble.instance && RollingMarble.instance.isStart) {
			RollingMarble.instance.marble.accele(ev.accelerationIncludingGravity);
		}
	}
	function onDeviceMotionAndroid(ev) {
		if (RollingMarble.instance && RollingMarble.instance.isStart) {
			RollingMarble.instance.marble.accele({
				x: ev.gamma*0.15, y: ev.beta* -0.15, 
			});
		}
	}
	// 加速度センサ イベント登録
	const is_iPhone  = (navigator.userAgent.indexOf("iPhone")>0);
	const is_android = (navigator.userAgent.indexOf("Android")>0);
	const is_pc      = true;
	//const is_iPhone  = false;
	//const is_android = true;
	
	if (is_iPhone){
		window.addEventListener("devicemotion", onDeviceMotion, true);
	} else if (is_android){
		window.addEventListener("deviceorientation", onDeviceMotionAndroid, true);
	} else { // PC
		
	}

	Class.init = function() {
		RollingMarble.instance = new RollingMarble();

		// PCデバック用キー入力 イベント登録
		document.onkeydown = function(ev){
			var g = {x:0, y:0};
			switch(ev.keyCode) {
				case 37: g={x:-0.5,y:0};    break; //←
				case 38: g={x:0,   y:0.5};  break; //↑
				case 39: g={x:0.5, y:0};    break; //→
				case 40: g={x:0,   y:-0.5}; break; //↓
				default:
			}
			RollingMarble.instance.marble.accele(g);
		};

		document.getElementById("stage").onclick = function(ev){
			var m = RollingMarble.instance.marble;
			var x = ev.x - ev.currentTarget.offsetLeft;
			var y = ev.y - ev.currentTarget.offsetTop;

			if (ev.button == 1) {
				m.x = x;
				m.y = y;
				m.gx = 0;
				m.gy = 0;
			} else {
				//m.accele({x:(x-m.x)*0.01,y:-(y-m.y)*0.01});
				m.gx = (x - m.x)*0.05;
				m.gy = (y - m.y)*0.05;
			}
		}

		var levels = [
			//感度        ,  反発力,        摩擦係数
			{sensitive: 0.5, repulsion:1.3, friction:0.92},
			{sensitive: 0.5, repulsion:1.5, friction:0.94},
			{sensitive: 0.7, repulsion:1.6, friction:0.96},
		]
		RollingMarble.instance.params = levels[getConfig().level];

		RollingMarble.instance.start();
	}
	

	Class.getStageNames = function(){
		var config = getConfig();
		
		var stageNames = [];
		if (config.stage == "") {
			var path = config.map+"/";
			var list = Server.list(path);
			for (var k in list) {
				if (!list[k].isDir) stageNames.push(path+k);
			}
			stageNames.sort();
		} else {
			stageNames.push(config.map+"/"+config.stage);
		}
		// cacheに読み込み
		//for (var i=0; i<stageNames.length; i++) {
		//	Server.file(stageNames[i]);
		//}
		return stageNames;
	}
	
	function getConfig() {
		const DEFAULT = {map:"default", stage:"", level: 1, name:"no name"};
		var config;
		var json = window.localStorage.getItem("config");
		try {
			if (json) {
				config = JSON.parse(json);
			} else {
				config = DEFAULT;
			}
		} catch (e) {
			config = DEFAULT;
		}
		return config;		
	}
	function setConfig(config) {
		window.localStorage.setItem("config",JSON.stringify(config));
	}
		
	Class.config = function(){
		RollingMarble.instance.stop();

		var config = getConfig();

		var sel = Util.byId("mapSelect");
		sel.innerHTML = "";
		var list = Server.list("");
		for (var k in list) {
			if (list[k].directory) {
				var opt = document.createElement("option");
				opt.innerText = k;
				opt.value = k;
				sel.appendChild(opt);
			}
		}
		
		Util.setSelect(Util.byId("mapSelect"), config.map);
		Util.setSelect(Util.byId("stageSelect"), config.stage);
		Util.setSelect(Util.byId("levelSelect"), config.level);

		Dialog.open("d_config",{
			other: function(val){
				config.map   = sel.value;
				config.stage = Util.byId("stageSelect").value;
				config.level = Util.byId("levelSelect").value;
				setConfig(config);
				//location.reload();
				RollingMarble.init();
			},
			ng: function() {
				RollingMarble.instance.resume();
			}
		});
	}

})(RollingMarble);
