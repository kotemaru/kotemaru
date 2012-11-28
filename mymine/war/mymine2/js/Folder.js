
function Folder(){this.initialize.apply(this, arguments)};
(function(Class){
	var $TAMPLATE = $("<article class='Folder'></article>");

	Class.prototype.initialize = function() {
		this.name  = null;
		this.title = null;
		this.icon  = null;
		this.nosave = false;
		this.tickets = {};
		this.transients = {};
	}

	Class.prototype.setParams = function(params) {
		for (var k in params) this[k] = params[k];
		return this;
	}
	Class.prototype.getParams = function() {
		var params = {};
		for (var k in params) {
			if (this[k] != this.prototype && this[k] != this.transients){
				params[k] = this[k];
			}
		}
		return params;
	}
	
	Class.prototype.refresh = function() {
		var $elem = $(this.transients.elem);
		$elem.attr("id","folder_"+this.name);
		$elem.data("name",this.name);
		$elem.data("folder",this);
		$elem.html(getTitleWithCount(this));
		$elem.css("backgroundImage","url("+this.icon+")");
		return this;
	}

	Class.prototype.build = function(isForce) {
		if (this.transients.elem == null || isForce) {
			this.transients.elem = $TAMPLATE.clone()[0];
		}
		this.refresh();
		return $(this.transients.elem);
	}
	Class.prototype.get$Elem = function() {
		return $(this.transients.elem);
	}
	
	function getTitleWithCount(folder) {
		if (folder.nosave) return folder.title;
		var unchecked = 0;
		var total = 0;
		for (var num in folder.tickets) {
			if (!Ticket.isChecked(num)) unchecked++;
			total++;
		}
		if (total == 0) return folder.title;
		if (unchecked == 0) {
			return "<span class='Count'>("+total+") </span>"+folder.title;
		}
		return "<span class='Count'>("
			+"<b>"+unchecked+"</b>/"+total
			+") </span>"+folder.title;
	}
	
})(Folder);


