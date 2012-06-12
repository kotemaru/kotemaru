
enchant();
enchant('tl');
enchant('gocha');

window.onload = function(){
    var game = new Game(320, 320);
    game.fps = 5;
    Chip.preload(game);

    var stages = [null,stage1,stage2,stage3,stage4,stage5];
	var stageNo = 1;

    game.onload = function(){
    	Chip.onload(game);
    	var main = null;

    	
    	function runStage(next) {
    		stageNo += next;
    		if (stageNo>=stages.length) stageNo = 1;
    		
	    	game.rootScene.removeChild(main);
	    	main = stages[stageNo](game);
	    	game.rootScene.addChild(main);
	    	if (game._intervalID == null) game.resume();
    	}
    	
    	function nextDialog(ev,title) {
	    	game.pause();
			GochaDialog.open(game.rootScene, {
				title:title, message:ev.message,
				buttons: [
				    {label:"Retry", handler:function(){runStage(0)}},
				    {label:"Next Stage", handler:function(){runStage(1)}},
				]
			});
    	}
    	
     	game.addEventListener("gocha.win", function(ev){nextDialog(ev,"You WIN!");});
    	game.addEventListener("gocha.lost", function(ev){nextDialog(ev,"You LOST!");});
    	runStage(0);
    }

    game.start();
};