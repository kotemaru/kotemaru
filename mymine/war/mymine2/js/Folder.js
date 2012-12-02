
function Folder(){this.initialize.apply(this, arguments)};
(function(Class){
	var $TAMPLATE = $("<article class='Folder'></article>");
	var $SEPALATOR = $("<hr/>");

	Class.prototype.initialize = function() {
		this.name  = null;
		this.title = null;
		this.icon  = null;
		this.nosave = false;
		this.tickets = {};
		this.transients = {};
		this.isSeparator = false;
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
		if (this.isSeparator) return this;
		
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
			if (this.isSeparator) {
				this.transients.elem = $SEPALATOR.clone()[0];
			} else {
				this.transients.elem = $TAMPLATE.clone()[0];
			}
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
			if (!folder.tickets[num].isChecked) unchecked++;
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
	
	
	Class.prototype.dropTicket = function() {
		var ticketNums = TicketTray.getSelection();
		for (var i=0; i<ticketNums.length; i++) {
			this.addTicket(ticketNums[i]);
		}
		return this;
	}
	Class.prototype.addTicket = function(num) {
		this.tickets[num] =
			this.nosave ? TicketPool.get(num):TicketPool.getWithSave(num);
	}
	Class.prototype.clearTicket = function(num) {
		this.tickets = {};
	}

})(Folder);


