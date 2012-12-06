
function Folder(){this.initialize.apply(this, arguments)};
(function(Class){
	var $TAMPLATE = $("<article class='Folder'></article>");
	var $SEPALATOR = $("<hr/>");
	var TRASH = "trash";

	Class.prototype.initialize = function() {
		this.name  = null;
		this.title = null;
		this.icon  = null;
		this.nosave = false;
		this.tickets = [];
		this.transients = {elem:null, tickets:{}};
		this.isSeparator = false;
	}

	Class.prototype.setParams = function(params) {
		for (var k in params) this[k] = params[k];

		for (var i=0; i<this.tickets.length; i++) {
			var num = this.tickets[i];
			this.transients.tickets[num] = TicketPool.get(num);
		}
		return this;
	}
	Class.prototype.getParams = function() {
		this.syncTickets();
		var params = {};
		for (var k in this) {
			if (this[k] != this.prototype && this[k] != this.transients){
				params[k] = this[k];
			}
		}
		if (this.nosave) params.tickets = [];
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
		for (var num in folder.transients.tickets) {
			if (!TicketPool.isChecked(num)) unchecked++;
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
	function isCheck(issue) {
		return issue.checked > Date.parse(issue.updated_on);
	}
	
	
	Class.prototype.dropTicket = function(dstFolder) {
		var ticketNums = TicketTray.getSelection();
		for (var i=0; i<ticketNums.length; i++) {
			this.addTicket(ticketNums[i]);
			dstFolder.delTicket(ticketNums[i]);
		}
		return this;
	}
	Class.prototype.addTicket = function(num) {
		this.transients.nosync = true;
		if (this.nosave) {
			this.transients.tickets[num] = TicketPool.get(num);
			if (this.name == TRASH) {
				TicketPool.removeFromStorage(num);
			}
		} else {
			this.transients.tickets[num] = TicketPool.getWithSave(num);
		}
	}
	Class.prototype.delTicket = function(num) {
		this.transients.nosync = true;
		delete this.transients.tickets[num];
	}
	Class.prototype.clearTicket = function(num) {
		this.tickets = [];
		this.transients.tickets = {};
	}
	Class.prototype.getTickets = function(num) {
		return this.transients.tickets;
	}
	Class.prototype.getTicketNums = function(num) {
		this.syncTickets();
		return this.tickets;
	}
	Class.prototype.syncTickets = function() {
		if (!this.transients.nosync) return; 
		this.transients.nosync = false;
		this.tickets = [];
		for (var num in this.transients.tickets) {
			this.tickets.push(num);
		}
	}
	Class.prototype.updateTickets = function() {
		this.syncTickets();
		TicketPool.update(this.tickets);
	}
	
	
})(Folder);


