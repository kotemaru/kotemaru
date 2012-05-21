function Chip(src){this.initialize.apply(this, arguments)};
(function(Class) {
	Class.loading = 0;
	Class.cache = {};

	Class.prototype.initialize = function(src) {
		const self = this;
		var img = new Image();
		this.img = img;
		img.onload = function(){
			var tmpCvs = document.createElement("canvas");
			tmpCvs.setAttribute("width", img.width);
			tmpCvs.setAttribute("height", img.height);
			var ctx = tmpCvs.getContext("2d");
			ctx.drawImage(img, 0, 0);
			self.data = ctx.getImageData(0, 0, img.width, img.height);

			Class.loading--;
			if (Class.loading == 0) {
				Class.onload();
			}
		};
		img.src = src;
		Class.loading++;
	};
	
	Class.add = function(name, src){
		if (Chip.cache[name]) return Class.cache[name];
		Class.cache[name] = new Chip(src);
		return Class.cache[name];
	};	

	Class.onload = function(){};	
})(Chip);
