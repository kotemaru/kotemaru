
function GPlace(){this.constractor.apply(this, arguments)};
(function(Class){
	var This = Class.prototype;
	This.constractor = function() {
		// not use instance.
	}

	var KEY = "AIzaSyAvupK0sGEXAAu2FPi69iJZOasSXVtvF0c";
	var URL_SEARCH =
		"https://maps.googleapis.com/maps/api/place/search/json"
/*
		+"?location=${lat},${lng}"
		+"&radius=${radius}"
		+"&types=${types}"
		+"&sensor=false"
		+"&language=ja"
		+"&key="+KEY
*/
	;
	var URL_SEARCH = "test-data/place-list.json";

	var URL_DETAIL =
		"https://maps.googleapis.com/maps/api/place/details/json"
/*
		+"?reference=${refer}"
		+"&sensor=true"
		+"&key="+KEY
*/
	;
	var URL_DETAIL = "test-data/place-detail.json";
	


	Class.getPlaces = function (params, callback) {
		params.key = KEY;
		params.sensor = false;
		$.getJSON(URL_SEARCH, params, callback);
	}

	Class.getDetail = function (ref, callback) {
		var params = {};
		params.key = KEY;
		params.sensor = true;
		params.reference = ref;
		$.getJSON(URL_DETAIL, params, callback);
	}

	Class.checkError = function (json, status) {
		if (status != "success" || json.status != "OK") {
			alert("Google place error.");
			throw "Google place error.";
		}
	}


})(GPlace)
