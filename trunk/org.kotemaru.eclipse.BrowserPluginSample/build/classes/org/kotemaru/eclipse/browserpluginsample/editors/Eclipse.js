var Eclipse = {};
Eclipse.setContent = function(content) {
	alert("Abstract function Eclipse.setContent() not implemented.");
};
Eclipse.getContent = function() {
	alert("Abstract function Eclipse.getContent() not implemented.");
};

Eclipse.fireEvent = function(type) {
	window.status = type;
};

window.onload = function() {
	Eclipse.fireEvent("load");
}
