/**
 * kmaped client.
 * @author kotemru@kotemru.org (MIT license)
 */
enchant.kmaped = {
    /**
     * Make map from data.
     *
     * @param {enchant.Game} game.
     * @param {string} name localStrage name or url.
     * @return {enchant.Map}
     */
	makeMap: function(game, name) {
		var data = enchant.kmaped.getData(name);
		return enchant.kmaped.makeMapFromData(game, data);
	},
	
    /**
     * Get chipset image url from data.
     * for game.preload().
     * @param {string} localStrage name or url.
     * @return {string} url.
     */
	getChipSet: function(name) {
		var data = enchant.kmaped.getData(name);
		return data.chipSet;
	},
	
	getData: function(name) {
		var data = enchant.kmaped.fromLocalStrage(name);
		if (data != null) return data;
		data = enchant.kmaped.fromURL(name);
		if (data != null) return data;
		throw new Error("Map "+url+" not found");
	},
	
	fromLocalStrage: function(name) {
		var json = localStorage.getItem("maped:/"+name);
		if (json == null) return null;
		return JSON.parse(json);
		return enchant.kmaped.makeMapFromData(data);
	},
			
	fromURL: function(url) {
		var xhr = XMLHttpRequest();
		xhr.open("GET",url);
		xhr.send();
		if (xhr.status >= 400) return null;
		var data = JSON.parse(xhr.responseText);
		return enchant.kmaped.makeMapFromData(data);
	},
	
	makeMapFromData: function(game, data) {
		var map = new Map(data.tileWidth, data.tileHeight);
		map.image = game.assets[data.chipSet];
		var args = [];
		for (var i=0; i<data.layers.length; i++) {
			args.push(data.layers[i].tiles);
		}
		map.loadData.apply(map, args);
		if (data.collision) {
			map.collisionData = data.collision.tiles;
		}
		return map;
	}
		
};