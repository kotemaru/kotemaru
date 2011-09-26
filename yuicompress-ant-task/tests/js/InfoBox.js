function InfoBox(map, opts) {
	google.maps.OverlayView.call(this, map);
	this.map = map;
	this.box = null;
	this.innerBox = null;
	this.marker = null;
	this.setMap(map);
}
InfoBox.prototype = new google.maps.OverlayView();

InfoBox.prototype.open = function(m, html) {
	this.close();
	this.marker = m;
	this.innerBox.innerHTML = html;
	this.draw();
	if (this.box) {
		this.box.style.visibility = "visible";
	}

}
InfoBox.prototype.close = function() {
	if (this.box) {
		this.box.style.visibility = "hidden";
	}
}

InfoBox.prototype.addEventListener = function(type, func, cap) {
	if (this.innerBox) {
		this.innerBox.addEventListener(type, func, cap);
	} else {
		this.reserveListener = {type:type, func:func, cap:cap};
	}
}

InfoBox.prototype.onAdd = function() {

	var div = document.createElement('DIV');
	div.style.position = "absolute";
	div.style.width = "200px";
	div.style.height = "60px";
	div.style.cursor = "pointer";

	var div2 = document.createElement('DIV');
	div2.style.position = "absolute";
	div2.style.top = "4px";
	div2.style.left = "4px";
	div2.style.width = "180px";
	div2.style.height = "40px";
	div2.style.overflow = "hidden";
	div2.style.color = "black";
	div2.style.textOverflow = "ellipsis";

	var img2 = document.createElement("img");
	img2.src = "/images/balloon-shadow.png";
	img2.style.position = "absolute";
	img2.style.top = "4px";
	img2.style.left = "5px";

	var img = document.createElement("img");
	img.src = "/images/balloon.png";
	img.style.position = "absolute";

	div.appendChild(img2);
	div.appendChild(img);
	div.appendChild(div2);


	this.box = div;
	this.innerBox = div2;
	this.close();

	if (this.reserveListener) {
		var p = this.reserveListener;
		this.innerBox.addEventListener(p.type, p.func, p.cap);
	}


	// レイヤーにこのdivを張り込む
	var panes = this.getPanes();
	panes.floatPane.appendChild(this.box);
}


InfoBox.prototype.draw = function() {
	if (!this.box || !this.marker) return;

	// 経度緯度から画面座標系に変換
	var pos = this.getProjection().fromLatLngToDivPixel(this.marker.getPosition());
	if (!pos) return;

	this.box.style.left = (pos.x-99)  + "px";
	this.box.style.top = (pos.y-48-24) + "px";
	//this.box.style.display = 'block';
}

