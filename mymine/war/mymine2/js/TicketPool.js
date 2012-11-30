
//@Singleton
function TicketPool(){this.initialize.apply(this, arguments)};
(function(Class){
	var pool = {};
	Class.put = function(issue) {
		pool[issue.id] = issue;
	}
	Class.get = function(num) {
		return pool[num];
	}

})(TicketPool);
