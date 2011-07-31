$(function(){
	initMap();
});

var map;
var marker;
var center;
var balloon;
var balloonMarker;
var markers = {};

function initMap() {
	center = new google.maps.LatLng(35.684699,139.753897);
	var mapopts = {
		zoom: 14,
		center: center,
		scaleControl: true,
		mapTypeId: google.maps.MapTypeId.ROADMAP
	}
	map = new google.maps.Map(document.getElementById("mapCanvas"),mapopts);
	map.controls[google.maps.ControlPosition.TOP_RIGHT].push(
$("<div id='menuButton'>検索条件</div>")[0]);

	marker = new google.maps.Marker({position: center, map: map});
	balloon = new google.maps.InfoWindow({
		content: $("#balloon").html().replace(/[$][{]formName[}]/g,"balloonForm"),
		size: new google.maps.Size(100, 80)
	});
	google.maps.event.addListener(balloon, 'domready', onLoadBalloon);

	google.maps.event.addListener(map, 'click', onMapClick);

	navigator.geolocation.watchPosition(geoUpdate, function(e){waitingIcon(false);});
	//waitingIcon(true);
}

function onLoadBalloon(ev) {
	var form = document.getElementById("balloonForm");
	var pos = balloonMarker.getPosition();
	form.lat.value = pos.lat();
	form.lng.value = pos.lng();

	var geocoder = new google.maps.Geocoder();
	geocoder.geocode({latLng: pos}, function(results, status){
		if(status == google.maps.GeocoderStatus.OK){
			form.address.value = results[0].formatted_address;
		} else {
			form.address.value = "???"; // error
		}
	});

	var img = document.getElementById("balloonImg");
	if (balloonMarker.masterData) {
		var md = balloonMarker.masterData;
		form.id.value = md.id;
		form.comment.value = md.comment;
		form.tags.value = md.tags;
		var img = document.getElementById("balloonImg");
		img.src = "/image?id="+md.images[0];
		//$("#balloonImgInput").hide();
		$("input[name='level']").val([md.level]);
		$("input[name='appraise']").val([md.appraise]);
	} else {
		form.comment.value = "";
		form.tags.value = "";
		img.src = "http://maps.google.co.jp/mapfiles/ms/icons/blue-pushpin.png";
		$("#balloonImgInput").show();
	}

}
function onImageSelect(_this, ev) {
	var reader = new FileReader();
	reader.onload = function(e) {
		var img = document.getElementById("balloonImg");
		img.src = reader.result;
	};
	reader.readAsDataURL(_this.files[0]); 	
}

function onMapClick(ev) {
	//alert(uneval(ev));
	//center = ev.latLng;
	//map.setCenter(center);
	marker.setPosition(ev.latLng);
	marker.setVisible(true);
	balloonMarker = marker;
	balloon.open(map,marker);

	var rect = map.getBounds();
	if (rect == null) return;
	var latNE = rect.getNorthEast().lat();
	var lngNE = rect.getNorthEast().lng();
	var latSW = rect.getSouthWest().lat();
	var lngSW = rect.getSouthWest().lng();

	IchiMemo.listAsync(protMarkers, {
		username:"kotemaru27@gmail.com",
		maxLat: Math.max(latNE, latSW),
		minLat: Math.min(latNE, latSW),
		maxLng: Math.max(lngNE, lngSW),
		minLng: Math.min(lngNE, lngSW),
	});
};

function geoUpdate(position) {
	var lat = position.coords.latitude;
	var lng = position.coords.longitude;
	center = new google.maps.LatLng(lat, lng);
	map.setCenter(center);
	marker.setPosition(center);
	waitingIcon(false);

	onMapClick({latLng: center});
}

var pinImage = new google.maps.MarkerImage(
    "http://maps.google.co.jp/mapfiles/ms/icons/blue-pushpin.png", // url
    new google.maps.Size(32,32), // size
    new google.maps.Point(0,0),  // origin
    new google.maps.Point(10,30) // anchor
);
var pinShadowImage = new google.maps.MarkerImage(
    "http://maps.google.co.jp/mapfiles/ms/icons/pushpin_shadow.png", // url
    new google.maps.Size(32,32), // size
    new google.maps.Point(0,0),  // origin
    new google.maps.Point(8,31) // anchor
);


var protMarkers = {
	send: function(list) {
		//for (var key in markers) markers[key].setVisible(false);
		for (var i=0; i<list.length; i++) {
			var id = list[i].id;
			if (markers[id] == null) {
				var pos = new google.maps.LatLng(list[i].lat, list[i].lng);
				var m = new google.maps.Marker({position: pos, map: map,
						icon:pinImage, shadow:pinShadowImage
				});
				google.maps.event.addListener(m, 'click', onMarkerClick);
				m.masterData = list[i];
				markers[id] = m;
			} else {
				markers[id].setVisible(true);
			}
		}
	},
	_throw: function(e) {
		alert(e);
	}
}

function onMarkerClick(ev) {
	marker.setVisible(false);
	balloonMarker = this;
	balloon.open(map,this);
}

function waitingIcon(b) {
	if (b) {
		$("#waitingIcon").show();
	} else {
		$("#waitingIcon").hide();
	}
}
