
function RollingMarble() {
	this.stage  = new Stage("stage");
	this.timer = Util.byId("timer");
	this.time = 0;
	this.isStart = false;
	this.stageNo = 0;
	this.stageNames = RollingMarble.getStageNames();
}
(function(Class) {
	var INTERVAL = 50; //ms
	function ticker() {
		if (Class.instance.isStart) setTimeout(ticker, INTERVAL);
		Class.instance.action();
		//if (Class.instance.isStart) setTimeout(ticker, INTERVAL);
	}

	Class.prototype.start = function() {
		this.stage.makeStage(this.stageNames[this.stageNo]);
		this.marble = this.stage.marble;
		if (this.marble == null) {
			this.stageNo++;
			return this.start();
		}

		this.startTime = (new Date()).getTime();
		this.time += this.stage.timelimit;
		this.isStop = false;
		this.timeview();

		var self = this;
		setTimeout(function() {
			Util.byId("stageNo").innerText = (self.stageNo+1);
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
		Dialog.open("d_goal", function(){
			self.stageNo++;
			self.start();
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
		//Sound.autoStop();
		if (!this.isStart) return;
		var entities = this.stage.entities;
		for (var i=0; i<entities.length; i++) {
			for (var j=i+1; j<entities.length; j++) {
				entities[i].contact(entities[j]);
			}
		}
		var actors = this.stage.actors;
		for (var i=0; i<actors.length; i++) {
			actors[i].action();
		}
		this.stage.scroll(this.marble.x, this.marble.y);

		this.time -= INTERVAL;
		this.timeview();

		if (this.time <= 0) {
			var self = this;
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
	Class.prototype.timeview = function() {
		// time view
		//var time = (new Date()).getTime() - this.startTime;
		this.timer.style.color = "#88ff88";
		if (this.time < this.stage.timelimit2) this.timer.style.color = "#ffff00";
		if (this.time < this.stage.timelimit3) this.timer.style.color = "#ff2222";
		if (this.time <= 0) {
			this.timer.style.color = "#2222ff";
			this.time = 0;
			this.stop();
		}
		this.timer.innerText = toSecStr(this.time);
	}
	function toSecStr(time) {
		var ms = "000000"+time;
		return ms.substr(ms.length-6,3)+"."+ms.substr(ms.length-3,2);
	}

	Class.getStageNames = function(){
		var dir = location.search!="" ? location.search.replace(/^[?]/,"") : "default";
		var stageNames = [];

		if (location.hash == "") {
			var list = Server.list(dir);
			for (var k in list) {
				if (!list[k].isDir) stageNames.push(dir+ "/"+k);
			}
			stageNames.sort();
		} else {
			stageNames.push(dir+"/"+location.hash.replace(/^#/,""));
		}
		
		return stageNames;
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
			var x = ev.x;
			var y = ev.y - ev.currentTarget.offsetTop;

			if (ev.button == 1) {
				m.x = x;
				m.y = y;
				m.gx = 0;
				m.gy = 0;
			} else {
				m.gx = (x - m.x)*0.05;
				m.gy = (y - m.y)*0.05;
			}
		}

		var levels = [
			//感度        ,  反発力,        摩擦係数
			{sensitive: 0.3, repulsion:1.2, friction:0.93},
			{sensitive: 0.5, repulsion:1.5, friction:0.96},
			{sensitive: 0.8, repulsion:1.6, friction:0.97},
		]
		RollingMarble.instance.params = levels[level-1];

		RollingMarble.instance.start();
	}

})(RollingMarble);
