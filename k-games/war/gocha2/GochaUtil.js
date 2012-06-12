/**
@author kotemaru@kotemaru.org
*/

enchant.gocha.GochaUtil = org.kotemaru.Class(null, function(_class, _super){
	var Util = org.kotemaru.Util;
	
	_class.makePlainMap = function(chip, w,h, bw,bh,n) {
		var map = new Map(w,h);
		map.image = chip.image;
		map.loadData(_class.makePlainMapData(bw,bh,n));
		map.collisionData = [];
		return map;
	}

	_class.makePlainMapData = function(w,h,n) {
		var data = [];
		for (var y=0; y<h; y++) {
			var line = [];
			for (var x=0; x<w; x++) {
				line.push(n);
			}
			data.push(line);
		}
		return data;
	}
	
	_class.debugSetClassName = function(pkg) {
		for (var name in pkg) {
			var func = pkg[name];
			if (typeof func == "function") {
				func.__name = name;
			}
		}
	}
	_class.copy = function(dst, src) {
		if (src != null) {
			for (var k in src) dst[k] = src[k];
		}
		return dst;
	}
	
});
