
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
				Dialog.open("d_finish", function(){
					location = "/";
				});
			}
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
			var entities = stage.entities;
			for (var i=0; i<entities.length; i++) {
				for (var j=i+1; j<entities.length; j++) {
					entities[i].contact(entities[j]);
				}
			}
			var actors = stage.actors;
			for (var i=0; i<actors.length; i++) {
				actors[i].action();
			}
			stage.scroll(marble.x, marble.y);
	
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

	Class.init = function(level) {
		RollingMarble.instance = new RollingMarble();

		// 加速度センサ イベント登録
		window.addEventListener("devicemotion", function(ev){
			if (RollingMarble.instance.isStart) {
				RollingMarble.instance.marble.accele(ev.accelerationIncludingGravity);
			}
		}, true);

		// PCデバック用キー入力 イベント登録
		document.onkeydown = function(ev){
			var g = {x:0, y:0};
			switch(ev.keyCode) {
				case 37: g={x:-0.5, y:0};  break; //←
				case 38: g={x:0,   y:0.5}; break; //↑
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
			{sensitive: 0.5, repulsion:2.0, friction:0.90},
			{sensitive: 0.5, repulsion:1.5, friction:0.94},
			{sensitive: 0.8, repulsion:1.6, friction:0.97},
		]
		//RollingMarble.instance.params = levels[level-1];
		RollingMarble.instance.params = levels[1];

		RollingMarble.instance.start();
	}
	

	Class.getStageNames = function(){
		var path = getParams().path;
		var stageNames = [];

		if (path.match(/\/$/)) {
			var list = Server.list(path);
			for (var k in list) {
				if (!list[k].isDir) stageNames.push(path+k);
			}
			stageNames.sort();
		} else {
			stageNames.push(path);
		}
		
		return stageNames;
	}
	function getParams() {
		if (location.search == "") {
			return {path:"default/", level:1};
		}
		var param = decodeURIComponent(location.search.replace(/^[?]/,""));	
		var params = param.split(/,/);
		return {path:params[0], level:parseInt(params[1])};
	}

		
	Class.config = function(){
		RollingMarble.instance.stop();
	
		var params = getParams();
		var mapName = params.path.replace(/\/[0-9]*$/,"");
		var sel = Util.byId("mapSelect");
		sel.innerHTML = "";
		var list = Server.list("");
		for (var k in list) {
			if (list[k].directory) {
				var opt = document.createElement("option");
				opt.innerText = k;
				opt.value = k;
				if (k == mapName) opt.selected = "selected";
				sel.appendChild(opt);
			}
		}
		
		//var stage = Util.byId("stageSelect").value;
		//Util.byId("levelSelect").value;

		Dialog.open("d_config",{
			other: function(val){
				var name = sel.value;
				var stage = Util.byId("stageSelect").value;
				var level = Util.byId("levelSelect").value;
				location.replace("/play.html?"+name+"/"+stage+","+level);
				//location.reload(true);
			},
			ng: function() {
				RollingMarble.instance.resume();
			}
		});
	}

})(RollingMarble);
