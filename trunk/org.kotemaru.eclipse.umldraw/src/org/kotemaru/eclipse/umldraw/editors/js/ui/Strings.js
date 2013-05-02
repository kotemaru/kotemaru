

function Strings(){this.initialize.apply(this, arguments)};
(function(_class){

	var common1 = "<br/>(The double click locks this action.)";
	var common2 = "<br/>Please click a canvas after choice in this." + common1;
	var common3 = "<br/>Please click a canvas after choice in this."
	
	var map_en = {
		"Action.undo": "UNDO",
		"Action.redo": "REDO",
		"Action.cursor": "This performs the move, connection, copy, etc... of the item.",
		"Action.remove": "This deletes one item."
			+"<br/>Please click a item after choice in this." + common1,
			
		"Action.Class": "This creates a class item."+common2,
		"Action.Object": "This creates a object item."+common2,
		"Action.Cable": "This creates a cable item.<br/>"
			+"You can choose the kind of the cable among a menu."+common2,
			
		"Action.Note": "This creates a note item."+common2,
		"Action.Disk": "This creates a database item."+common2,
		"Action.Marker": "This creates a marker item."
			+"<br/>The marker is not printed."
			+"<br/>It is for a course and the combination of the cable."
			+common1
			,
	};
	
	_class.get = function(name) {
		if (map[name]) return map[name];
		return name;
	}
	

})(Strings);
