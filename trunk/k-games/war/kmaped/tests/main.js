
enchant();

window.onload = function(){
    var game = new Game(320, 320);
    game.fps = 5;

    var MAP_NAME = location.search.substr(1);
    game.preload(enchant.kmaped.getChipSet(MAP_NAME));

    game.onload = function(){
		var map = enchant.kmaped.makeMap(game,MAP_NAME);
		game.rootScene.addChild(map);
	}

    game.start();
};