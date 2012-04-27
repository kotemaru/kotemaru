function Actor(stage, src, initval){this.initialize.apply(this, arguments)};
(function(Class) {

	Class.FACTORY = {
		'my': function(stage,x,y) {
			return new MyMarble(stage,"img/ball-blue.png",{
				x:x, y:y}
			);
		},
		'red': function(stage,x,y) {
			return makeJammar(stage,"ball-red",x,y,{bonusSec:1});
		},
		'black': function(stage,x,y) {
			return makeJammar(stage,"ball-black",x,y,{weight:5, friction:0.8, bonusSec:5});
		},
		'yellow': function(stage,x,y) {
			return makeJammar(stage,"ball-yellow",x,y,{weight:1, repulsion:4.0, bonusSec:3});
		},
		'green': function(stage,x,y) {
			return new JammerG(stage,"img/ball-green.png", {
				x:x, y:y, weight:0.7,
				friction:0.8, repulsion:1.1, bonusSec:3
			});
		},
		'greenBig': function(stage,x,y) {
			return new JammerG(stage,"img/ball-green.png", {
				x:x, y:y, w:64, h:64,
				weight:4,
				friction:0.8, repulsion:1.2, bonusSec:10
			});
		},
		'fixGray': function(stage,x,y) {
			return makeJammar(stage,"fix-gray",x,y,{
				w:20, h:20, weight:999999, repulsion:0.75, bonusSec:0
			});
		},
		'fixYellow': function(stage,x,y) {
			return makeJammar(stage,"fix-yellow",x,y,{
				w:20, h:20, weight:999999, repulsion:2.0, bonusSec:0
			});
		},
		'hole': function(stage,x,y) {
			return new BlackHole(stage,"img/spiral.png",
				{x:x, y:y});
		},
	};


	function makeJammar(stage,src,x,y,initval) {
		initval.x = x;
		initval.y = y;
		return new Jammer(stage,"img/"+src+".png", initval);
	};


	Class.create = function(stage, name,  x, y) {
		var f = Class.FACTORY[name];
		var actor = f(stage,  x, y);
		actor.name = name;
		return actor;
	}
	// for editor
	Class.COMMENT = {
		'my': "自玉(１個のみ)",
		'red': "普通の敵玉",
		'black': "重たい敵玉",
		'yellow': "弾ける敵玉",
		'green': "追って来る敵玉",
		'greenBig': "大きな追って来る敵玉",
		'fixGray': "動かない障害物",
		'fixYellow': "弾ける障害物",
		'hole': "吸い込まれる",
	};

})(Actor);
