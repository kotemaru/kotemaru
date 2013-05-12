

function Strings(){this.initialize.apply(this, arguments)};
(function(_class){

	var common1 = "<br/>(It is locked by double click.)";
	var common2 = "<br/>Please click the place that wants to make an element after choosing this button." + common1;
	
	var map = {
		"Action.undo": "UNDO",
		"Action.redo": "REDO",
		"Action.cursor": "Select and movement of element.",
		"Action.remove": "Delete of element."
			+"<br/>Please click the element which is deleted after choosing this button." + common1,
			
		"Action.Class": "Create class element."+common2,
		"Action.Object": "Create object element."+common2,
		"Action.Cable": "Create assosiation cable element.<br/>"
			+"The menu can choose the assosiation of the cable."+common2,
			
		"Action.Note": "Create note element."+common2,
		"Action.Disk": "Create DataBase element."+common2,
		"Action.Marker": "Create marker element."+common2
			+"<br/>The marker is not printed."
			+"<br/>It controls the course of the cable."
			,
	};
	
	_class.get = function(name) {
		if (map[name]) return map[name];
		return name;
	}
	
	$(function(){
		var lang = Util.browserLanguage("en");
		if (window["Strings_"+lang]) {
			window.Strings = window["Strings_"+lang];
		}
	});

})(Strings);

