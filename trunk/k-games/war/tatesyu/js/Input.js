
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
		if (Config.control == "pad") {
			padTouch(ev);
		} else {
			directTouch(ev); 
		}
	};

	function directTouch(ev) {
		if (ev.which == 0) return Class.onTouchEnd();
		const myShip = thisGame.myShip;
		const ox = (myShip.x-thisGame.clipX);
		const oy = (myShip.y-thisGame.clipY);
		const x = ev.clientX - ox;
		const y = ev.clientY - oy;
		Class["left"]  = (x<-1);
		Class["right"] = (x> 1);
		Class["up"]    = (y<-1);
		Class["down"]  = (y> 1);
	}
	
	function padTouch(ev) {
		if (ev.which == 0) return Class.onTouchEnd();
		const PAD_OX = 320-80+32;
		const PAD_OY = thisGame.clipH-32;
		const x = ev.clientX - PAD_OX;
		const y = ev.clientY - PAD_OY;
		Class["left"]  = (x<-8);
		Class["right"] = (x> 8);
		Class["up"]    = (y<-8);
		Class["down"]  = (y> 8);
	}
	
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
		if (Config.control != "orient") return;
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

