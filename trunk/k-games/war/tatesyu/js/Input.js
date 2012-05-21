
function Input(game){this.initialize.apply(this, arguments)};
(function(Class, Super) {
	Util.extend(Class, Super);

	const KEYCODE = {
		37: "left",  //←
		38: "up",    //↑
		39: "right", //→
		40: "down",  //↓
		88: "btn1", //x
		90: "btn2",  //y
	}
	for (var k in KEYCODE) {
		Class[KEYCODE[k]] = false;
	}

	document.onkeydown = function(ev){
		var name = KEYCODE[ev.keyCode];
		if (name === undefined) return false;
		Class[name] = true;
		return false;
	};
	document.onkeyup = function(ev){
		var name = KEYCODE[ev.keyCode];
		if (name === undefined) return false;
		Class[name] = false;
		return false;
	};

	Class.onMouseMove = function(ev){
		const myShip = thisGame.myShip;
		const ox = (myShip.x-thisGame.clipX);
		const oy = (myShip.y-thisGame.clipY);
		const x = ev.clientX - ox;
		const y = ev.clientY - oy;
		Class["left"]  = (x<-10);
		Class["right"] = (x> 10);
		Class["up"]    = (y<-10);
		Class["down"]  = (y> 10);
	};
	Class.onTouchMove = function(ev){
		Class.onMouseMove(ev.touches[0]);
	};
	Class.onTouchEnd = function(ev){
		Class["left"]  = false;
		Class["right"] = false;
		Class["up"]    = false;
		Class["down"]  = false;
	};
	
	Class.on = function(name) {
		Class[name] = true;
	}
	Class.off = function(name) {
		Class[name] = false;
	}


	// for iPhone
	function onDeviceMotion(ev) {
		if (!Config.useOrient) return;
		with (ev) {
			Class.right = (gamma> 5);
			Class.left  = (gamma<-5);
			Class.up    = (beta < 20);
			Class.down  = (beta > 30);
		}
	}
	if (!IS_PC) {
		window.addEventListener("deviceorientation", onDeviceMotion, true);
	}
	
})(Input);

