/**
 * kmaped client.
 * 
 * data format:
 * {
 *  width: ${width of map},
 *  height: ${height of map},
 *  tileWidth: ${width of tile (fixed 16)},
 *  tileHeight: ${width of tile (fixed 16)},
 *  layers: [
 *   {
 *     name: ${layerName},
 *     tiles:[[],[],...] // enchant.Map compatible.
 *   },
 *   :
 *  ],
 *  collision: {
 *   name:"collision", 
 *   tiles:[[],[],...] // *Not* enchant.Map compatible.
 *                     // Value of tiles: no-collision=-1, collision=1
 *  },
 * }
 * 
 * @author kotemru@kotemru.org (MIT license)
 */
enchant.kmaped = {
    /**
     * Make map from data.
     * 
     * @param {enchant.Game} game.
     * @param {string} name localStrage name or url.
     * @param {opject} opts options. {names:["name1","name2",...], collision:bool}
     * @return {enchant.Map}
     */
	makeMap: function(game, name, opts) {
		var data = enchant.kmaped.getData(name);
		return enchant.kmaped.makeMapFromData(game, data, opts);
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
		var json = localStorage.getItem("map:/"+name);
		if (json == null) return null;
		return JSON.parse(json);
	},
			
	fromURL: function(url) {
		var xhr = new XMLHttpRequest();
		xhr.open("GET",url,false);
		xhr.send();
		if (xhr.status >= 400) return null;
		var data = JSON.parse(xhr.responseText);
		return data;
	},
	
	makeMapFromData: function(game, data, opts) {
		opts = opts?opts:{};
		opts.collision = (opts.collision===undefined?true:opts.collision);

		var map = new Map(data.tileWidth, data.tileHeight);
		map.image = game.assets[data.chipSet];

		var args = [];
		if (opts.names) {
			var name2tiles = {};
			for (var i=0; i<data.layers.length; i++) {
				name2tiles[data.layers[i].name] = data.layers[i].tiles;
			}
			for (var i=0; i<opts.names.length; i++) {
				args.push(name2tiles[opts.names[i]]);
			}
		} else { // 全部
			for (var i=0; i<data.layers.length; i++) {
				args.push(data.layers[i].tiles);
			}
		}
		map.loadData.apply(map, args);

		
		if (opts.collision && data.collision) {
			var tiles = data.collision.tiles;
			// collisionは0/1なので -1=>0 に変換。
			for (var y=0; y<tiles.length; y++) {
				for (var x=0; x<tiles[y].length; x++) {
					if (tiles[y][x]<0) tiles[y][x] = 0;
				}
			}
			map.collisionData = tiles;
		}
		return map;
	}
	
		
};